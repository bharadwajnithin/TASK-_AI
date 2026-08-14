# TASK AI 
   
An AI-powered productivity platform that automates task extraction from text, Gmail, and WhatsApp chats, paired with smart analytics.

## 🚀 Features

- **Task Management**: Task CRUD with filtering, status tracking, and priority levels.
- **AI Task Extraction**: Automatically parse tasks from natural language text, emails, and chat exports.
- **Integrations**: Sync tasks via Gmail and WhatsApp imports.
- **Analytics & Auth**: Dashboard metrics, JWT authentication, and Google OAuth support.

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, MongoDB, Spring Security, JWT, Google OAuth.
- **Frontend**: React 18, Vite 6, Tailwind CSS 3, Lucide React.

## ⚡ Quick Start

### Prerequisites
Java 21+, Maven 3.9+, Node.js 18+, MongoDB

### 1. Backend Setup
```bash
cd taskflow-backend
cp .env.example .env
mvn spring-boot:run
```
Runs on `http://localhost:8080`

### 2. Frontend Setup
```bash
cd taskflow-frontend
npm install
npm run dev
```
Runs on `http://localhost:5173`

## ⚙️ Environment Variables

- **Backend** (`taskflow-backend/.env`): `MONGODB_URI`, `JWT_SECRET`, `AI_PROVIDER`, `AI_API_KEY`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- **Frontend** (`taskflow-frontend/.env`): `VITE_API_BASE_URL`, `VITE_GOOGLE_OAUTH_URL`

## 📄 License

[MIT](LICENSE)
