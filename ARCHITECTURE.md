# Architecture

## Goal

Keep the first review build simple enough to run locally, while preserving the service/API boundaries needed for later cloud deployment and a real route provider/database.

```text
Browser
  │
  │ Vue 3 / Axios
  ▼
Frontend SPA
  │
  │ REST / JSON
  ▼
Spring Boot API
  ├── RouteService
  ├── LocationService
  ├── HistoryService
  ├── NearbyPlaceService
  └── DatasetRepository
          │
          ▼
  packaged cleaned CSV snapshot
```

## Frontend responsibilities

- Inputs and accessible validation
- Loading/error/outside-coverage states
- Route map/list synchronisation
- High / Low / Data unavailable text presentation
- Peak-hour and prediction context presentation
- Location / history / nearby-place navigation
- Responsive layout and keyboard focus
- Development Mock fallback through the service layer

The frontend does **not** calculate production route classification or historical prediction in HTTP mode.

## Backend responsibilities

- Supported sensor location validation
- Loading the cleaned dataset
- Route result preparation
- Pedestrian-count aggregation and High/Low classification
- Peak-hour evaluation
- Historical comparable-reading selection and prediction average
- Historical trend aggregation
- Nearby category filtering and straight-line distance calculation
- Data-source / limitation metadata

## Why no PostgreSQL or Redis in v0.1?

The first goal is a zero-infrastructure review build using a small cleaned snapshot (~1.6 MB). Adding PostgreSQL or Redis now would create extra failure modes without improving the acceptance-criteria demonstration.

For cloud release we can choose either:

1. keep the immutable cleaned snapshot packaged in the backend for maximum demo reliability; or
2. import the same cleaned tables into PostgreSQL and replace `DatasetRepository` with JPA/repository queries while keeping controllers/DTOs/frontend unchanged.

Redis would only become useful if the project later has expensive route/provider calls or live data that benefits from caching.

## Route geometry boundary

`RouteService` currently creates prototype polylines in the **backend**. This satisfies the UI/API contract while making the limitation visible. It is not street routing.

A later implementation can introduce a `RouteGeometryProvider` backed by Google Routes, OSRM, OpenRouteService, or another approved source without moving route calculation into Vue.

## Map choice

Leaflet is the only frontend map framework in this build. The tile URL and attribution are environment variables. Google Maps is intentionally not added in parallel.
