import { apiClient } from './client'
import type { HistoricalTrendResult } from '@/types/domain'
import { getMockHistoricalTrend } from '@/mocks/content.mock'

const useMock = import.meta.env.VITE_USE_MOCK_DATA === 'true'

export async function getHistoricalTrend(id: string): Promise<HistoricalTrendResult> {
  if (useMock) return getMockHistoricalTrend(id)
  const { data } = await apiClient.get<HistoricalTrendResult>(`/locations/${encodeURIComponent(id)}/history`)
  return data
}
