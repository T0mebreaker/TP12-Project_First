export type SensoryClassification = 'High' | 'Low' | 'Data unavailable'
export type RouteDataStatus = 'available' | 'unavailable'
export type DataFreshness = 'fresh' | 'stale' | 'illustrative'
export type AsyncState = 'idle' | 'loading' | 'success' | 'empty' | 'error'
export type PredictionStatus =
  | 'Higher pedestrian activity likely'
  | 'Lower pedestrian activity likely'
  | 'Prediction unavailable'

export interface Coordinates {
  latitude: number
  longitude: number
}

export interface LocationReference extends Coordinates {
  id: string
  name: string
  sensorDescription?: string
  source: 'sensor' | 'landmark' | 'prototype-reference'
}

export interface RouteRequest {
  originId: string
  destinationId: string
  departureTime: string
  scenario?: string
}

export interface RouteGeometry {
  coordinates: Coordinates[]
  geometrySource: string
  navigationAccuracy: 'prototype' | 'provider'
}

export interface PeakHourContext {
  isPeakHour: boolean
  label: string
  departureTime: string
  timezone: string
}

export interface PredictionEligibility {
  validSensor: boolean
  sameSensor: boolean
  sameHour: boolean
  sameDayType: boolean
  comparableReadingCount: number
  eligible: boolean
}

export interface PedestrianActivity {
  averagePedestriansPerMinute: number | null
  classification: SensoryClassification
  dataStatus: RouteDataStatus
  observedAt: string | null
}

export interface PredictionResult {
  eligible: boolean
  status: PredictionStatus
  comparableReadingCount: number
  predictedAveragePedestriansPerMinute: number | null
  predictedHour: string
  dayType: 'weekday' | 'weekend'
  affectedArea: string
  timeframe: string
  isIllustrative: boolean
  limitation: string
}

export interface NearbyTransportConnection {
  name: string
  type: 'Train station' | 'Tram stop'
  approximateDistanceMetres: number
}

export interface RouteOption {
  id: string
  name: string
  geometry: RouteGeometry
  walkingTimeMinutes: number
  distanceKm: number
  sensoryClassification: SensoryClassification
  averagePedestriansPerMinute: number | null
  classificationThreshold: string
  sensorId: string | null
  sensorName: string | null
  isLowerStimulationAlternative: boolean
  dataStatus: RouteDataStatus
  highCongestionSegment?: string | null
  prediction: PredictionResult
  nearbyTransport?: NearbyTransportConnection | null
  limitations: string[]
  isIllustrative: boolean
}

export interface RouteResponse {
  request: RouteRequest
  origin: LocationReference
  destination: LocationReference
  peakHour: PeakHourContext
  routes: RouteOption[]
  dataSource: string
  generatedAt: string
  limitation: string
  scenario: string
}

export interface LocationDetail {
  id: string
  name: string
  coordinates: Coordinates
  latestPedestriansPerMinute: number | null
  latestObservedAt: string | null
  dataFreshness: string
  stale: boolean
  interpretation: string
  dataSource: string
  sampleData: boolean
  sensoryLimitation: string
  nearbyTransport?: NearbyTransportConnection | null
}

export interface HistoricalDataPoint {
  hour: number
  label: string
  averagePedestriansPerMinute: number
  sampleCount: number
}

export interface QuieterTimeInsight {
  label: 'Illustrative insight'
  timeWindow: string
  explanation: string
  guidanceOnly: true
}

export interface HistoricalTrendResult {
  locationId: string
  locationName: string
  available: boolean
  points: HistoricalDataPoint[]
  higherActivityPeriod: string | null
  lowerActivityPeriod: string | null
  summary: string
  quieterTimeInsight: string | null
  limitation: string
  dataSource: string
}

export type NearbyPlaceCategory = 'Library' | 'Park' | 'Garden' | 'Reserve'

export interface NearbyPlace extends Coordinates {
  id: string
  name: string
  category: NearbyPlaceCategory
  approximateDistanceMetres: number
  sampleData: boolean
}

export interface NearbyPlacesResult {
  locationId: string
  locationName: string
  places: NearbyPlace[]
  limitation: string
  dataSource: string
  guidanceOnly: boolean
}

export interface ApiErrorResponse {
  message: string
  code?: string
  details?: string
}
