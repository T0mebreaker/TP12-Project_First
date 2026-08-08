# Acceptance Criteria Coverage — Review Build

This is an implementation mapping, not a claim that final production routing or live-data correctness has been certified.

| Requirement | Implementation evidence | Status / boundary |
|---|---|---|
| Route path, walking time, distance | `POST /api/routes` returns geometry/time/distance; `RouteMap.vue` + `RouteCard.vue` display them | Implemented; geometry is prototype, not street navigation |
| High / Low text not colour-only | backend classification + route summary/cards/map tooltip/status text | Implemented |
| >60 High; <=60 Low; show average/threshold | `RouteService.HIGH_THRESHOLD`; summary and cards | Implemented |
| No data → Data unavailable and no High/Low | route C + demo state; UI text | Implemented |
| High route clearly identified | map tooltip + route list + selected summary | Implemented |
| Low lower-stimulation alternative | backend flags first available Low alternative when primary is High | Implemented |
| Honest alternative wording | “Lower-stimulation alternative based on available pedestrian data”; no best/safest/guaranteed quiet wording | Implemented |
| No Low alternative warning/actions | `NO_ALTERNATIVE` demo + Home warning + Continue / Return actions | Implemented |
| Peak-hour detection | weekday 07:00–10:00 and 16:00–19:00 | Implemented |
| Same threshold in peak hour | one backend classification rule | Implemented |
| Peak-hour limitation | result limitation shown near route list | Implemented |
| Nearby transport connection (max one) | route/location DTO contains at most one connection | Implemented; contextual prototype metadata |
| Nearby eligible public places max 3 | `NearbyPlaceService` | Implemented |
| Only Library/Park/Garden/Reserve | backend category mapping/filter | Implemented |
| Place name/category/distance | Nearby cards | Implemented |
| Nearby limitation wording | visible notice near result list | Implemented |
| Prediction eligibility >=4 comparable readings | `RouteService.prediction` | Implemented |
| Prediction average | average of comparable historical hourly totals / 60 | Implemented |
| Higher/Lower prediction threshold | same >60 / <=60 rule | Implemented |
| Proactive higher-activity alert + Review Routes | Home predictive alert | Implemented |
| Prediction uncertainty statement | prediction limitation in selected route summary | Implemented |
| Prediction unavailable no Higher/Lower alert | route C / demo state | Implemented |
| Location latest count/timestamp/freshness | `GET /api/locations/{id}` | Implemented; snapshot marked stale |
| Historical chart + text summary | `HistoricalTrendView` + lightweight SVG | Implemented |
| Higher/lower period + illustrative quieter-time | `HistoryService` + cards | Implemented |
| Insufficient history no fabricated insight | unavailable branch | Implemented |
| Main → Login → Guest/Sign In → Home | Router + Main/Login views | Implemented |
| Historical and Nearby as parallel branches | Location Detail equal-weight action cards | Implemented |
| Outside Coverage recovery | `OUTSIDE_COVERAGE` error state + Revise Search | Implemented as deterministic review state and unsupported-ID backend response |
| Responsive + keyboard + map/list alternative | responsive CSS, focus handling, semantic controls, route list | Implemented for review; local browser QA still required |
