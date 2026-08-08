import { apiClient } from './client'
import type { NearbyPlacesResult } from '@/types/domain'
import { getMockNearbyPlaces } from '@/mocks/content.mock'

const useMock = import.meta.env.VITE_USE_MOCK_DATA === 'true'

export async function getNearbyPlaces(id: string, scenario?: string): Promise<NearbyPlacesResult> {
  if (useMock) return getMockNearbyPlaces(id, scenario)
  const { data } = await apiClient.get<NearbyPlacesResult>(`/locations/${encodeURIComponent(id)}/nearby-places`)
  return data
}
