# Task AI — Backend

AI-powered productivity platform backend built with **Java 21**, **Spring Boot 3**, **MongoDB**, and **JWT** authentication.


## Features

- User registration & login with JWT authentication
- BCrypt password encryption
- MongoDB Atlas / local MongoDB integration
- Google OAuth login (optional, enabled via env)
- Provider-agnostic AI integration (Gemini, OpenAI, and more)
- Gmail integration for task extraction
- WhatsApp chat import for task extraction
- Analytics dashboard
- Global exception handling & DTO validation
- CORS for React frontend

## Project structure

```
src/main/java/com/taskflowai/
├── config/          # MongoDB, CORS, app properties
├── controller/      # REST controllers
├── dto/             # Request/response DTOs
├── exception/       # Custom exceptions & handler
├── model/           # MongoDB entities
├── repository/      # Spring Data repositories
├── security/        # JWT, OAuth2, Spring Security
├── service/         # Business logic
└── util/            # Mappers & helpers
```

## Prerequisites

- Java 21+
- Maven 3.9+
- MongoDB (local or [MongoDB Atlas](https://www.mongodb.com/atlas))

## Environment variables

Copy `.env.example` to `.env` and set values (or export as system env vars):

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/taskflow_ai` |
| `JWT_SECRET` | HMAC secret (min 256 bits recommended) | (see .env.example) |
| `JWT_EXPIRATION_MS` | Token TTL | `86400000` (24h) |
| `CORS_ALLOWED_ORIGINS` | Frontend URL(s) | `http://localhost:5173` |
| `FRONTEND_URL` | Used for OAuth redirect | `http://localhost:5173` |
| `GOOGLE_OAUTH_ENABLED` | Enable Google login | `false` |
| `GOOGLE_CLIENT_ID` | Google Cloud OAuth client ID | (required if OAuth enabled) |
| `GOOGLE_CLIENT_SECRET` | Google Cloud OAuth client secret | (required if OAuth enabled) |
| `AI_PROVIDER` | AI provider: `gemini` or `openai` | `gemini` |
| `AI_API_KEY` | AI provider API key | (required) |
| `AI_MODEL` | AI model name | `gemini-2.5-flash` |
| `AI_BASE_URL` | Optional custom API base URL | (provider default) |

## Run locally

**Windows:** Maven may use JDK 17 by default while this project needs **Java 21**. Use one of these:

```powershell
cd taskflow-backend

# Option A — helper script (recommended)
.\run.ps1

# Option B — set JAVA_HOME for this session, then run
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
mvn spring-boot:run
```

Set MongoDB if needed:

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/taskflow_ai"
```

Server: `http://localhost:8080`

## API endpoints (Phase 1)

### Health

```http
GET /api/health
```

### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securepass123"
}
```

**Response (201):**

```json
{
  "token": "eyJhbG...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "...",
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "oauthUser": false,
    "createdAt": "2026-06-04T..."
  }
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securepass123"
}
```

### Current user (protected)

```http
GET /api/users/me
Authorization: Bearer <token>
```

### Google OAuth

1. Set `GOOGLE_OAUTH_ENABLED=true`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
2. In Google Cloud Console, add redirect URI:
   `http://localhost:8080/login/oauth2/code/google`
3. Open: `http://localhost:8080/oauth2/authorization/google`
4. After login, redirects to: `{FRONTEND_URL}/oauth/callback?token=...`

## Task API (Phase 3)

All endpoints require `Authorization: Bearer <token>`.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks` | List tasks (search, filter, pagination) |
| GET | `/api/tasks/stats` | Dashboard stats + recent tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

**Query params for GET `/api/tasks`:** `search`, `status`, `priority`, `page`, `size`, `sortBy`, `sortDir`

## AI API

Requires `AI_PROVIDER` and `AI_API_KEY` in environment or `.env`.

**Supported providers:** `gemini`, `openai`

```http
POST /api/ai/extract
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Please update the login page before Friday and send deployment link.",
  "saveTasks": false
}
```

**Response:**

```json
{
  "clientName": "Acme Corp",
  "projectName": "Website",
  "tasks": [
    {
      "title": "Update Login Page",
      "description": "Update login page",
      "priority": "MEDIUM",
      "dueDate": "2026-06-06",
      "dueDateText": "Friday"
    }
  ],
  "savedTasks": []
}
```

Set `"saveTasks": true` to automatically create tasks in MongoDB.

## Chat API (Phase 6 — WhatsApp)

Import a WhatsApp `.txt` export and optionally extract tasks with AI.

```http
POST /api/chats/import
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "[6/4/26, 10:30 AM] Client: Update login page before Friday",
  "title": "Acme client chat",
  "saveTasks": false
}
```

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chats/import` | Import WhatsApp chat export |
| GET | `/api/chats` | List imported chats |
| POST | `/api/chats/process` | Run AI extraction on a saved chat |
| DELETE | `/api/chats/{id}` | Delete a chat import |

## MongoDB Atlas setup

1. Create a free cluster at [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Database Access → create user
3. Network Access → allow your IP (or `0.0.0.0/0` for dev)
4. Connect → copy connection string
5. Set `MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/taskflow_ai`

## Tests

Requires MongoDB running on `localhost:27017`:

```bash
mvn test
```

## Deploy to Render

1. New **Web Service** → connect repo
2. Root directory: `taskflow-backend`
3. Build: `mvn -DskipTests clean package`
4. Start: `java -jar target/taskflow-backend-1.0.0.jar`
5. Add environment variables from `.env.example`
6. Set `SPRING_PROFILES_ACTIVE=prod` when Google OAuth is configured

## Completed phases

| Phase | Feature | Status |
|-------|---------|--------|
| 1 | Backend authentication | ✅ |
| 2 | React frontend authentication | ✅ |
| 3 | Task CRUD | ✅ |
| 4 | AI task extraction | ✅ |
| 5 | Gmail integration | ✅ |
| 6 | WhatsApp import | ✅ |
| 7 | Analytics dashboard | ✅ |

## Security

- Never commit `.env` files
- Use strong JWT secrets in production
- Enable HTTPS in production
- Rotate API keys regularly
- Review dependencies for vulnerabilities
