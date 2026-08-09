# Melbourne Sensory-Aware Travel — FIT5120 Review Build

A review-ready full-stack MVP for the first FIT5120 project. It implements the approved route / pedestrian-activity / historical-pattern / nearby-place flow with a Vue 3 frontend and Spring Boot backend, and packages the corrected cleaned dataset so the team can inspect, discuss and debug locally before cloud deployment.

## What this build contains

- Vue 3 + Vite + TypeScript SPA
- Vue Router + Pinia
- Axios service layer
- Tailwind utility support plus project CSS tokens/components
- Leaflet map with OpenStreetMap tiles (configurable through environment variables)
- Java 21 + Spring Boot REST backend
- Cleaned FIT5120 pedestrian/sensor/landmark CSV files loaded by the backend at startup
- Route sensory classification: `> 60 pedestrians/min = High`, `<= 60 = Low`, no usable data = `Data unavailable`
- Melbourne weekday peak-hour context: `07:00–10:00` and `16:00–19:00`
- Historical next-hour prediction using same sensor + same hour + same weekday/weekend type, requiring at least 4 comparable readings
- Historical trend page with accessible SVG chart + text summary
- Nearby public places filtered to Library / Park / Garden / Reserve, maximum three
- Development demo scenarios for acceptance-criteria states
- Frontend-only Mock mode as a presentation fallback
- - Production deployment:
  - Frontend: Vercel
  - Backend: Render
- Docker configuration is retained as an optional alternative deployment method.

## Important prototype boundaries

This version deliberately distinguishes real dataset logic from prototype-only behaviour:

**Data-backed in this build**
- Cleaned sensor locations
- Hourly and minute pedestrian counts
- High/Low threshold calculation
- Peak-hour evaluation
- Historical comparable-reading prediction
- Historical hourly trend aggregation
- Nearby public-place filtering and distance calculation

**Still prototype / illustrative**
- Walking route geometry is generated as a backend prototype polyline, not turn-by-turn road navigation
- Route-to-sensor association is simplified (Route A uses the origin sensor, Route B uses the destination sensor)
- Nearby public-transport connection is contextual prototype metadata
- Sign in is illustrative only; guest access is the primary usable path
- Packaged data is a historical snapshot, not live City of Melbourne data
- Query-string demo scenarios intentionally override selected values and are visibly labelled as illustrative

These boundaries are intentional for the first review build so that the UI, API contract and acceptance-criteria states can be tested before choosing a production routing provider or database architecture.

## Prerequisites

- Node.js 20+ (22 recommended)
- npm 10+
- Java 21
- Maven 3.9+ (or IntelliJ IDEA's bundled Maven)

## Run locally — integrated backend + frontend

### 1. Start Spring Boot

```bash
cd backend
mvn spring-boot:run
```

Backend health check:

```text
http://localhost:8080/api/health
```

### 2. Start Vue

Open a second terminal:

```bash
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

On Windows PowerShell:

```powershell
cd frontend
Copy-Item .env.example .env.local
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

## Fastest review flow

1. Main Page → `Get started`
2. Login → `Continue as guest`
3. On Home keep the default values:
   - From: **Princes Bridge** (sensor 5)
   - To: **Melbourne Central** (sensor 3)
   - Depart: **2025-10-21 17:30**
4. Select `Generate routes`
5. The data-driven result should show Route A as High (about 91.9/min) and Route B as Low (about 51.1/min), based on exact cleaned hourly records for 17:00 that day.
6. Select different route cards and verify map/list selected state.
7. Open a location detail → Historical Patterns / Nearby Public Places.

## Acceptance-criteria demo scenarios

When the backend is running with `APP_DEMO_MODE=true` (default for local review), these URLs provide deterministic states. They are labelled as illustrative in the UI.

```text
http://localhost:5173/home?scenario=HIGH_LOW
http://localhost:5173/home?scenario=NO_ALTERNATIVE
http://localhost:5173/home?scenario=DATA_UNAVAILABLE
http://localhost:5173/home?scenario=PREDICTION_HIGH
http://localhost:5173/home?scenario=PREDICTION_UNAVAILABLE
http://localhost:5173/home?scenario=OUTSIDE_COVERAGE
```

For production-like review, use `/home` with no `scenario` query parameter and set:

```text
APP_DEMO_MODE=false
```

## Frontend-only fallback (no backend)

If you need the UI for a presentation while the backend is unavailable:

```text
VITE_USE_MOCK_DATA=true
```

Then restart Vite. This fallback is explicitly illustrative and is not evidence that backend algorithms or database logic are correct.

## API endpoints

```http
GET  /api/health
GET  /api/locations/supported
POST /api/routes
GET  /api/locations/{id}
GET  /api/locations/{id}/history
GET  /api/locations/{id}/nearby-places
```

Example route request:

```json
{
  "originId": "5",
  "destinationId": "3",
  "departureTime": "2025-10-21T17:30:00"
}
```

## Why Leaflet rather than Google Maps in this review build?

The supplied implementation documents explicitly establish Leaflet as the project's map framework and instruct later phases not to introduce a second map framework. Leaflet also avoids a Google Maps API key/billing dependency during classroom review. The tile provider is environment-configurable, so the map infrastructure is not hard-coded to a single tile service.

If the team later receives an explicit requirement for Google Maps/Google Routes, migrate the map provider deliberately rather than running two map frameworks in parallel.

## Data

Backend resources are under:

```text
backend/src/main/resources/data/
```

See [docs/DATASET_USAGE.md](docs/DATASET_USAGE.md) for the audit and how each file is used.

## Production Deployment

The current production deployment uses separate frontend and backend hosting.

```text
User Browser
     |
     v
Vercel
Vue 3 + Vite Frontend
     |
     | HTTPS REST API
     v
Render
Spring Boot Backend
     |
     v
Packaged cleaned CSV datasets
```

## Project documents

- `docs/ARCHITECTURE.md`
- `docs/DATASET_USAGE.md`
- `docs/AC_COVERAGE.md`
- `docs/DEMO_SCENARIOS.md`
- `docs/KNOWN_LIMITATIONS.md`
- `docs/DEPLOYMENT.md`

Deployment:
- Vercel production frontend verified
- Render production backend verified
- HTTPS API integration verified
- CORS verified

The source was statically checked and the dataset was audited.
