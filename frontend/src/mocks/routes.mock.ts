import type { RouteRequest, RouteResponse } from '@/types/domain'

const origin = {
  id: '5',
  name: 'Princes Bridge',
  latitude: -37.81874249,
  longitude: 144.96787656,
  sensorDescription: 'Princes Bridge',
  source: 'sensor' as const,
}
const destination = {
  id: '3',
  name: 'Melbourne Central',
  latitude: -37.81101524,
  longitude: 144.96429485,
  sensorDescription: 'Melbourne Central',
  source: 'sensor' as const,
}

function isPeakHour(value: string): boolean {
  const dt = new Date(value)
  if (Number.isNaN(dt.getTime())) return false
  const day = dt.getDay()
  if (day === 0 || day === 6) return false
  const minutes = dt.getHours() * 60 + dt.getMinutes()
  return (minutes >= 7 * 60 && minutes <= 10 * 60) || (minutes >= 16 * 60 && minutes <= 19 * 60)
}

export async function getMockRouteResponse(request?: RouteRequest): Promise<RouteResponse> {
  const req: RouteRequest = request || {
    originId: origin.id,
    destinationId: destination.id,
    departureTime: '2025-10-21T17:30',
    scenario: 'HIGH_LOW',
  }
  const scenario = (req.scenario || 'HIGH_LOW').toUpperCase().replaceAll('-', '_')
  const baseLimitation = 'This result is based on available pedestrian data and historical patterns only. Actual conditions may differ.'
  const response: RouteResponse = {
    request: req,
    origin,
    destination,
    peakHour: {
      isPeakHour: isPeakHour(req.departureTime),
      label: isPeakHour(req.departureTime) ? 'Peak-hour route check' : 'Route check',
      departureTime: req.departureTime,
      timezone: 'Australia/Melbourne',
    },
    dataSource: 'Illustrative mock response aligned with the City of Melbourne data contract',
    generatedAt: new Date().toISOString(),
    limitation: baseLimitation,
    scenario,
    routes: [
      {
        id: 'route-a',
        name: 'Route A · Direct walk',
        geometry: {
          coordinates: [
            { latitude: origin.latitude, longitude: origin.longitude },
            { latitude: -37.8154, longitude: 144.9661 },
            { latitude: destination.latitude, longitude: destination.longitude },
          ],
          geometrySource: 'Illustrative prototype geometry',
          navigationAccuracy: 'prototype',
        },
        walkingTimeMinutes: 12,
        distanceKm: 0.9,
        sensoryClassification: 'High',
        averagePedestriansPerMinute: 91.9,
        classificationThreshold: 'High > 60 pedestrians/min; Low ≤ 60',
        sensorId: '5',
        sensorName: 'Princes Bridge',
        isLowerStimulationAlternative: false,
        dataStatus: 'available',
        highCongestionSegment: 'Princes Bridge approach',
        prediction: {
          eligible: true,
          status: 'Lower pedestrian activity likely',
          comparableReadingCount: 40,
          predictedAveragePedestriansPerMinute: 36.1,
          predictedHour: '18:00',
          dayType: 'weekday',
          affectedArea: 'Princes Bridge approach',
          timeframe: '18:00–19:00',
          isIllustrative: true,
          limitation: 'This prediction is based on historical pedestrian patterns and is not a guarantee of future conditions.',
        },
        nearbyTransport: { name: 'Flinders Street Station', type: 'Train station', approximateDistanceMetres: 250 },
        limitations: [baseLimitation],
        isIllustrative: true,
      },
      {
        id: 'route-b',
        name: 'Route B · Lower-stimulation alternative',
        geometry: {
          coordinates: [
            { latitude: origin.latitude, longitude: origin.longitude },
            { latitude: -37.8164, longitude: 144.9636 },
            { latitude: -37.8127, longitude: 144.9628 },
            { latitude: destination.latitude, longitude: destination.longitude },
          ],
          geometrySource: 'Illustrative prototype geometry',
          navigationAccuracy: 'prototype',
        },
        walkingTimeMinutes: 15,
        distanceKm: 1.1,
        sensoryClassification: 'Low',
        averagePedestriansPerMinute: 51.1,
        classificationThreshold: 'High > 60 pedestrians/min; Low ≤ 60',
        sensorId: '3',
        sensorName: 'Melbourne Central',
        isLowerStimulationAlternative: true,
        dataStatus: 'available',
        highCongestionSegment: null,
        prediction: {
          eligible: true,
          status: 'Lower pedestrian activity likely',
          comparableReadingCount: 40,
          predictedAveragePedestriansPerMinute: 40.2,
          predictedHour: '18:00',
          dayType: 'weekday',
          affectedArea: 'Melbourne Central area',
          timeframe: '18:00–19:00',
          isIllustrative: true,
          limitation: 'This prediction is based on historical pedestrian patterns and is not a guarantee of future conditions.',
        },
        nearbyTransport: { name: 'Melbourne Central Station', type: 'Train station', approximateDistanceMetres: 120 },
        limitations: [baseLimitation],
        isIllustrative: true,
      },
      {
        id: 'route-c',
        name: 'Route C · Alternative path',
        geometry: {
          coordinates: [
            { latitude: origin.latitude, longitude: origin.longitude },
            { latitude: -37.8148, longitude: 144.9704 },
            { latitude: destination.latitude, longitude: destination.longitude },
          ],
          geometrySource: 'Illustrative prototype geometry',
          navigationAccuracy: 'prototype',
        },
        walkingTimeMinutes: 14,
        distanceKm: 1.0,
        sensoryClassification: 'Data unavailable',
        averagePedestriansPerMinute: null,
        classificationThreshold: 'No usable pedestrian data → Data unavailable',
        sensorId: null,
        sensorName: null,
        isLowerStimulationAlternative: false,
        dataStatus: 'unavailable',
        highCongestionSegment: null,
        prediction: {
          eligible: false,
          status: 'Prediction unavailable',
          comparableReadingCount: 0,
          predictedAveragePedestriansPerMinute: null,
          predictedHour: '18:00',
          dayType: 'weekday',
          affectedArea: 'Route C',
          timeframe: '18:00–19:00',
          isIllustrative: true,
          limitation: 'Prediction unavailable insufficient comparable historical data',
        },
        nearbyTransport: null,
        limitations: ['Data unavailable — no usable pedestrian data was found for this route.'],
        isIllustrative: true,
      },
    ],
  }

  if (scenario === 'NO_ALTERNATIVE') {
    const alt = response.routes[1]
    alt.name = 'Route B · Alternative walk'
    alt.sensoryClassification = 'High'
    alt.averagePedestriansPerMinute = 69
    alt.isLowerStimulationAlternative = false
    alt.highCongestionSegment = 'Melbourne Central segment'
  } else if (scenario === 'DATA_UNAVAILABLE') {
    const first = response.routes[0]
    first.sensoryClassification = 'Data unavailable'
    first.averagePedestriansPerMinute = null
    first.dataStatus = 'unavailable'
    first.highCongestionSegment = null
    first.classificationThreshold = 'No usable pedestrian data → Data unavailable'
  } else if (scenario === 'PREDICTION_HIGH') {
    const prediction = response.routes[0].prediction
    prediction.status = 'Higher pedestrian activity likely'
    prediction.predictedAveragePedestriansPerMinute = 74
    prediction.affectedArea = 'Princes Bridge approach'
  } else if (scenario === 'PREDICTION_UNAVAILABLE') {
    for (const route of response.routes) {
      route.prediction.eligible = false
      route.prediction.status = 'Prediction unavailable'
      route.prediction.comparableReadingCount = 0
      route.prediction.predictedAveragePedestriansPerMinute = null
      route.prediction.limitation = 'Prediction unavailable insufficient comparable historical data'
    }
  }

  return response
}
