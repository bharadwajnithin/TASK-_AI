# Task AI — Frontend

React + Vite frontend with **JWT authentication**, **Google OAuth**, and a responsive app shell.

## Features

- Login & Register pages
- JWT stored in localStorage + Axios interceptors
- Protected routes & guest routes
- Google OAuth callback (`/oauth/callback`)
- Task management (CRUD)
- AI-powered task extraction
- Gmail integration
- WhatsApp chat import
- Analytics dashboard
- Sidebar + Navbar layout (responsive)

## Tech stack

- React 18 + Vite 6
- Tailwind CSS 3
- React Router 6
- Axios
- Lucide React icons

## Prerequisites

- Node.js 18+
- Backend running on `http://localhost:8080` (Phase 1)

## Setup

```powershell
cd taskflow-frontend
npm install
copy .env.example .env
npm run dev
```

App: **http://localhost:5173**

## Environment variables

| Variable | Description |
|----------|-------------|
| `VITE_API_BASE_URL` | Backend URL. Leave empty in dev to use Vite proxy. |
| `VITE_GOOGLE_OAUTH_URL` | Google OAuth start URL on backend |

**Dev `.env` example:**

```env
VITE_API_BASE_URL=
VITE_GOOGLE_OAUTH_URL=http://localhost:8080/oauth2/authorization/google
```

**Production (Vercel):**

```env
VITE_API_BASE_URL=https://your-backend.onrender.com
VITE_GOOGLE_OAUTH_URL=https://your-backend.onrender.com/oauth2/authorization/google
```

## Pages

| Route | Description |
|-------|-------------|
| `/login` | Email/password + Google login |
| `/register` | Create account |
| `/oauth/callback` | Google OAuth token handler |
| `/dashboard` | Task dashboard with stats |
| `/tasks` | Task management (CRUD) |
| `/ai-extract` | AI-powered task extraction |
| `/gmail` | Gmail integration |
| `/whatsapp` | WhatsApp chat import |
| `/analytics` | Analytics dashboard |
| `/profile` | User profile |

## Auth flow

1. **Register/Login** → backend returns `{ token, user, expiresIn }`
2. Token saved to `localStorage`; Axios adds `Authorization: Bearer <token>`
3. **Google OAuth** → redirect to backend → callback with `?token=...` → fetch `/api/users/me`
4. On **401**, session cleared and user sent to `/login`

## Run with backend

Terminal 1 (backend):

```powershell
cd taskflow-backend
.\run.ps1
```

Terminal 2 (frontend):

```powershell
cd taskflow-frontend
npm run dev
```

## Build for production

```powershell
npm run build
npm run preview
```

## Deploy to Vercel

1. Import repo, set root directory: `taskflow-frontend`
2. Framework: Vite
3. Add env vars (`VITE_API_BASE_URL`, `VITE_GOOGLE_OAUTH_URL`)
4. Deploy

## Security

- Never commit `.env` files
- Use HTTPS in production
- Validate all user inputs
- Keep dependencies updated
