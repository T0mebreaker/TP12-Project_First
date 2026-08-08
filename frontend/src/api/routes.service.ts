import { ApiClientError, apiClient } from './client'
import type { LocationReference, RouteRequest, RouteResponse } from '@/types/domain'
import { getMockRouteResponse } from '@/mocks/routes.mock'

const useMock = import.meta.env.VITE_USE_MOCK_DATA === 'true'
let activeRouteController: AbortController | null = null

export async function getSupportedLocations(): Promise<LocationReference[]> {
  if (useMock) return getMockRouteResponse().then((r) => [r.origin, r.destination])
  const { data } = await apiClient.get<LocationReference[]>('/locations/supported')
  return data
}

export async function generateRoutes(request: RouteRequest): Promise<RouteResponse> {
  if (useMock) {
    if ((request.scenario || '').toUpperCase().replaceAll('-', '_') === 'OUTSIDE_COVERAGE') {
      throw new ApiClientError('No supported pedestrian sensor coverage is available for this prototype search. Revise the origin or destination.', 'OUTSIDE_COVERAGE')
    }
    return getMockRouteResponse(request)
  }

  activeRouteController?.abort()
  const controller = new AbortController()
  activeRouteController = controller
  try {
    const { data } = await apiClient.post<RouteResponse>('/routes', request, { signal: controller.signal })
    return data
  } finally {
    if (activeRouteController === controller) activeRouteController = null
  }
}
