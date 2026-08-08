# Deployment Handoff — AWS / Azure

Do this only after the local review build is accepted by the team.

## Recommended first public deployment

Use one small Ubuntu VM (AWS EC2 or Azure VM) and Docker Compose:

```text
Internet :80/:443
      │
      ▼
Nginx / Vue static SPA
      │ same-origin /api
      ▼
Spring Boot container :8080 (internal only)
      │
      ▼
Packaged cleaned CSV snapshot
```

Why this is a good first course deployment:

- one public URL
- no CORS problem in normal browser traffic because Nginx proxies `/api`
- no database server needed for the review snapshot
- simple rollback/rebuild
- easy to migrate the backend repository to PostgreSQL later without rewriting Vue

## Local Docker check

```bash
docker compose up -d --build
docker compose ps
```

Open `http://localhost`.

## AWS EC2 outline

1. Create a small Ubuntu EC2 instance.
2. Security Group: allow SSH 22 from your IP; HTTP 80 and HTTPS 443 publicly.
3. Install Docker Engine + Compose plugin.
4. Copy/clone this repository to the VM.
5. Run `docker compose up -d --build`.
6. Verify `/api/health` through the public URL.
7. Add HTTPS before final submission (domain + Certbot/Caddy/ALB, depending on team choice).
8. Set `APP_DEMO_MODE=false` for the normal production endpoint. Keep presentation/demo scenarios only if the team explicitly wants them available.

## Azure VM outline

Use the same container layout on an Ubuntu Azure VM. Open 80/443 in the Network Security Group, install Docker, then run the same Compose command.

## Production environment checklist

- `VITE_USE_MOCK_DATA=false`
- frontend API base is `/api`
- no API key/secret in frontend code
- map tile URL + attribution configured
- `APP_DEMO_MODE=false` unless intentional
- SPA history fallback works on refresh
- nested routes refresh correctly
- `/api/health` responds
- browser console has no blocking error
- HTTPS has no mixed content
- CORS is not overly broad if frontend/backend are deployed on different origins

## If PostgreSQL is added later

Import the cleaned sensor/hour/minute/landmark data into normalised tables, add Spring Data JPA repositories, and replace only the current `DatasetRepository` implementation. Keep the REST DTOs and Vue service layer stable where possible.
