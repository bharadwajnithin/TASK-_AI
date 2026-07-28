# OMNITASK

AI-powered productivity platform to automate task management, extract tasks from emails/chats (Gmail, WhatsApp), and visualize analytics.

## 🚀 Quick Start

### Prerequisites
- **Java 21** & **Maven 3.9+**
- **Node.js 18+** & **npm**
- **MongoDB**

### 1. Backend Setup
```bash
cd taskflow-backend
cp .env.example .env    # Configure MONGODB_URI, JWT_SECRET, AI_API_KEY
mvn spring-boot:run
```
> Runs at `http://localhost:8080`

### 2. Frontend Setup
```bash
cd taskflow-frontend
cp .env.example .env    # Configure VITE_API_BASE_URL
npm install
npm run dev
```
> Runs at `http://localhost:5173`

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.3, Spring Security (JWT, Google OAuth2), MongoDB
- **Frontend**: React 18, Vite 6, Tailwind CSS 3, Axios
- **AI Integration**: Gemini, OpenAI (Provider-agnostic task extraction)

---

## ✨ Features

- 📝 **Task Management**: Full CRUD operations, filtering, and priority tracking.
- 🤖 **AI Task Extraction**: Auto-extract actionable tasks from raw text.
- 📧 **Gmail & WhatsApp Integration**: Auto-sync tasks from emails and chat exports.
- 📊 **Analytics Dashboard**: Visual metrics and productivity insights.
- 🔐 **Authentication**: JWT authentication & Google OAuth2 integration.

---

## 📄 License
Licensed under the [MIT License](LICENSE).
