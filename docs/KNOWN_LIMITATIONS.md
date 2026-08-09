# Known Limitations Before Public Deployment

## Must discuss / decide before calling this production

1. **Real walking route provider** — current backend geometry is a prototype polyline. Choose an approved provider if street-accurate routing is required.
2. **Route-to-sensor mapping** — the current MVP deliberately associates Route A with origin sensor and Route B with destination sensor. A production algorithm should spatially associate route segments with nearby sensors.
3. **Live data** — packaged CSV data is historical; no live City of Melbourne refresh job exists yet.
4. **Nearby transport metadata** — currently a small contextual mapping, not a public-transport API journey planner.
5. **Authentication** — sign in is illustrative; do not present it as secure account authentication.
6. **Public map tiles** — OpenStreetMap public tiles are appropriate for light review/demo traffic but a deployment should comply with the selected tile provider's usage policy.
7. **Browser QA** — run Chrome/Safari/Edge plus narrow mobile and keyboard checks locally before cloud release.

## Deliberately not added yet

- Redis
- Spring Data JPA / PostgreSQL
- Google Maps SDK
- ML prediction
- user profiles / registration
- route ranking / “best route” claims
- live GPS

These additions are not needed for the current acceptance criteria and would increase scope before the first review.
