import type { HistoricalTrendResult, LocationDetail, NearbyPlacesResult } from '@/types/domain'

const locations: Record<string, { name: string; latitude: number; longitude: number; latest: number; observed: string }> = {
  '5': { name: 'Princes Bridge', latitude: -37.81874249, longitude: 144.96787656, latest: 328, observed: '2026-08-05T16:15:00+10:00' },
  '3': { name: 'Melbourne Central', latitude: -37.81101524, longitude: 144.96429485, latest: 74, observed: '2026-08-06T11:52:00+10:00' },
}

export async function getMockLocationDetail(id: string): Promise<LocationDetail> {
  const l = locations[id] || locations['5']
  return {
    id,
    name: l.name,
    coordinates: { latitude: l.latitude, longitude: l.longitude },
    latestPedestriansPerMinute: l.latest,
    latestObservedAt: l.observed,
    dataFreshness: 'Illustrative frontend fallback · not live',
    stale: true,
    interpretation: 'This is an illustrative one-minute pedestrian count, not an official sensory or crowd-level classification.',
    dataSource: 'Frontend Mock mode',
    sampleData: true,
    sensoryLimitation: 'Pedestrian counts do not represent every sensory condition.',
    nearbyTransport: id === '3'
      ? { name: 'Melbourne Central Station', type: 'Train station', approximateDistanceMetres: 120 }
      : { name: 'Flinders Street Station', type: 'Train station', approximateDistanceMetres: 250 },
  }
}

const historyValues = [8, 6, 5, 4, 4, 6, 10, 23, 38, 42, 35, 31, 34, 41, 46, 50, 58, 72, 65, 48, 35, 27, 19, 12]

export async function getMockHistoricalTrend(id: string): Promise<HistoricalTrendResult> {
  const location = locations[id] || locations['5']
  const points = historyValues.map((averagePedestriansPerMinute, hour) => ({
    hour,
    label: `${String(hour).padStart(2, '0')}:00`,
    averagePedestriansPerMinute,
    sampleCount: 20,
  }))
  return {
    locationId: id,
    locationName: location.name,
    available: true,
    points,
    higherActivityPeriod: '17:00 · illustrative average 72 pedestrians/min',
    lowerActivityPeriod: '03:00 · illustrative average 4 pedestrians/min',
    summary: 'Illustrative fallback: activity is higher in the late afternoon and lower overnight in this mock profile.',
    quieterTimeInsight: 'Potentially quieter time: around 03:00 based on this illustrative historical profile. Calculation method is not final.',
    limitation: 'Historical patterns are guidance only and do not guarantee future pedestrian or sensory conditions.',
    dataSource: 'Frontend Mock mode · illustrative historical profile',
  }
}

export async function getMockNearbyPlaces(id: string): Promise<NearbyPlacesResult> {
  const location = locations[id] || locations['5']
  const places = id === '3'
    ? [
        { id: 'm1', name: 'The Melbourne Athenaeum Library', category: 'Library' as const, latitude: -37.8148855756416, longitude: 144.967291289941, approximateDistanceMetres: 504, sampleData: true },
        { id: 'm2', name: 'Parliament Reserve', category: 'Reserve' as const, latitude: -37.809852620638, longitude: 144.973462202839, approximateDistanceMetres: 816, sampleData: true },
        { id: 'm3', name: 'Carlton Gardens South', category: 'Garden' as const, latitude: -37.8060684577258, longitude: 144.971266479841, approximateDistanceMetres: 823, sampleData: true },
      ]
    : [
        { id: 'p1', name: 'Alexandra Gardens', category: 'Garden' as const, latitude: -37.8206051404251, longitude: 144.971796067365, approximateDistanceMetres: 402, sampleData: true },
        { id: 'p2', name: 'Queen Victoria Gardens', category: 'Garden' as const, latitude: -37.8216381244891, longitude: 144.971049530478, approximateDistanceMetres: 426, sampleData: true },
        { id: 'p3', name: 'The Melbourne Athenaeum Library', category: 'Library' as const, latitude: -37.8148855756416, longitude: 144.967291289941, approximateDistanceMetres: 432, sampleData: true },
      ]
  return {
    locationId: id,
    locationName: location.name,
    places,
    limitation: 'These places are suggested using public category data and have not been verified as quiet or sensory-friendly.',
    dataSource: 'Frontend Mock mode · City of Melbourne category-shaped sample',
    guidanceOnly: true,
  }
}
