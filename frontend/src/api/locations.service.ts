import { apiClient } from './client'
import type { LocationDetail } from '@/types/domain'
import { getMockLocationDetail } from '@/mocks/content.mock'

const useMock = import.meta.env.VITE_USE_MOCK_DATA === 'true'

export async function getLocationDetail(id: string): Promise<LocationDetail> {
  if (useMock) return getMockLocationDetail(id)
  const { data } = await apiClient.get<LocationDetail>(`/locations/${encodeURIComponent(id)}`)
  return data
}
