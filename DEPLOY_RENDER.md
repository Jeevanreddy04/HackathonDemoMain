# Deploy on Render

This project is now ready for Render with:
- Dockerized Spring Boot backend
- Render-managed PostgreSQL
- Static frontend build

## Files added
- `render.yaml`
- `backend/Dockerfile`
- `frontend/Dockerfile`
- `docker-compose.yml`

## 1) Deploy with Blueprint
1. Push this repository to GitHub.
2. In Render, click **New +** -> **Blueprint**.
3. Select this repository.
4. Render detects `render.yaml` and creates:
   - PostgreSQL: `hackathon-postgres`
   - Web service: `hackathon-backend`
   - Static site: `hackathon-frontend`

## 2) Update service URLs
After first deploy, set real URLs in `render.yaml` or Render dashboard:
- `CORS_ALLOWED_ORIGINS` on backend should be your frontend Render URL.
- `VITE_API_BASE_URL` on frontend should be your backend URL + `/api`.

Example:
- Frontend URL: `https://hackathon-frontend.onrender.com`
- Backend URL: `https://hackathon-backend.onrender.com`

Then set:
- `CORS_ALLOWED_ORIGINS=https://hackathon-frontend.onrender.com`
- `VITE_API_BASE_URL=https://hackathon-backend.onrender.com/api`

## 3) Redeploy
After env changes, trigger manual redeploy for both frontend and backend.

## Local Docker run
From project root:

```bash
docker compose up --build
```

App URLs:
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080/api`
- Postgres: `localhost:5432` (db: `hackathon`)
