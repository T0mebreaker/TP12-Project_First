# Production Deployment

## 1. Deployment Architecture

User
  |
  v
Vercel
Vue Frontend
  |
  | HTTPS REST API
  v
Render
Spring Boot Backend
  |
  v
Cleaned Historical Dataset

## 2. GitHub Repository

Repository structure:

melbourne-sensory-aware-travel/
├── frontend/
├── backend/
├── docs/
└── README.md

The same GitHub repository is connected independently to Vercel and Render.

## 3. Frontend Deployment — Vercel

Root Directory:
frontend

Framework:
Vite

Build Command:
npm run build

Output Directory:
dist

Environment Variables:

VITE_API_BASE_URL=https://<backend>.onrender.com
VITE_USE_MOCK_DATA=false

## 4. Backend Deployment — Render

Root Directory:
backend

Runtime:
Java 21 / Spring Boot

Environment Variables:

APP_DEMO_MODE=false
APP_ALLOWED_ORIGINS=https://<frontend>.vercel.app

Health endpoint:

GET /api/health

## 5. CORS

The Spring Boot backend only allows approved frontend origins.

Local:
http://localhost:5173

Production:
https://<frontend>.vercel.app

## 6. Deployment Validation

Verify:

Frontend root URL loads
Route search works
Backend health endpoint responds
Frontend can call backend
CORS succeeds
Leaflet tiles load
Direct Vue routes refresh correctly
HTTPS is enabled
Browser back/forward works
Production does not use Mock mode

## 7. Update Workflow

Local development
→ Git commit
→ GitHub push
→ Vercel automatic frontend deployment
→ Render automatic backend deployment

## 8. Alternative Deployment

Docker Compose remains available for AWS EC2 / Azure VM deployment but is not the primary deployment for the current FIT5120 build.
