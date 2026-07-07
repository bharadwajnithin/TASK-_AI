# OMNITASK

AI-powered productivity platform that helps you manage tasks, extract tasks from emails and chats, and gain insights through analytics.

## 🚀 Features

### Core Functionality
- **Task Management**: Create, read, update, and delete tasks with advanced filtering and search
- **AI-Powered Task Extraction**: Automatically extract tasks from natural language text using AI
- **Gmail Integration**: Import and process emails to extract tasks automatically
- **WhatsApp Chat Import**: Import WhatsApp chat exports to extract tasks from conversations
- **Analytics Dashboard**: Visualize task statistics and productivity metrics
- **User Authentication**: Secure JWT-based authentication with Google OAuth support

### Technical Features
- **Provider-Agnostic AI**: Support for multiple AI providers (Gemini, OpenAI, and more)
- **Real-time Updates**: Live task status updates and notifications
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Secure by Default**: Environment-based configuration, no hardcoded secrets    

## 📋 Tech Stack

### Backend
- **Java 21** - Modern Java with latest features
- **Spring Boot 3.3.5** - Enterprise-grade application framework
- **MongoDB** - NoSQL database for flexible data storage
- **Spring Security** - Comprehensive security framework
- **JWT** - Stateless authentication
- **Google OAuth2** - Third-party authentication

### Frontend
- **React 18** - Modern UI library
- **Vite 6** - Fast build tool and dev server
- **Tailwind CSS 3** - Utility-first CSS framework
- **React Router 6** - Client-side routing
- **Axios** - HTTP client
- **Lucide React** - Beautiful icon library

## 🏗️ Project Structure

```
omnitask/
├── taskflow-backend/          # Spring Boot backend
│   ├── src/main/java/com/taskflowai/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST API controllers
│   │   ├── dto/              # Data transfer objects
│   │   ├── exception/        # Custom exceptions
│   │   ├── model/            # MongoDB entities
│   │   ├── repository/       # Spring Data repositories
│   │   ├── security/         # Security configuration
│   │   ├── service/          # Business logic
│   │   └── util/             # Utility classes
│   ├── src/main/resources/
│   │   └── application.yml   # Application configuration
│   ├── pom.xml               # Maven dependencies
│   └── README.md             # Backend documentation
│
└── taskflow-frontend/        # React frontend
    ├── src/
    │   ├── api/              # API client layer
    │   ├── components/       # React components
    │   ├── context/          # React contexts
    │   ├── pages/            # Page components
    │   ├── utils/            # Utility functions
    │   ├── App.jsx           # Main app component
    │   └── main.jsx          # Entry point
    ├── public/               # Static assets
    ├── package.json          # NPM dependencies
    └── README.md             # Frontend documentation
```

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 18+
- MongoDB (local or MongoDB Atlas)

### Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd taskflow-backend
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Install dependencies**
   ```bash
   mvn install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd taskflow-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Run the development server**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173`

## 🔧 Configuration

### Backend Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `MONGODB_URI` | MongoDB connection string | Yes |
| `JWT_SECRET` | JWT signing secret (min 256 bits) | Yes |
| `JWT_EXPIRATION_MS` | JWT token expiration time | No |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | No |
| `FRONTEND_URL` | Frontend URL for OAuth redirect | No |
| `GOOGLE_OAUTH_ENABLED` | Enable Google OAuth | No |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | If OAuth enabled |
| `GOOGLE_CLIENT_SECRET` | Google OAuth client secret | If OAuth enabled |
| `AI_PROVIDER` | AI provider (gemini, openai) | Yes |
| `AI_API_KEY` | AI provider API key | Yes |
| `AI_MODEL` | AI model name | No |
| `AI_BASE_URL` | Custom AI API base URL | No |

### Frontend Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `VITE_API_BASE_URL` | Backend API URL | No (uses proxy in dev) |
| `VITE_GOOGLE_OAUTH_URL` | Google OAuth start URL | No |

## 📚 API Documentation

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

### Task Endpoints

#### Create Task
```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "priority": "HIGH",
  "status": "TODO",
  "dueDate": "2026-07-01"
}
```

#### Get Tasks
```http
GET /api/tasks?page=0&size=10&status=TODO&priority=HIGH
Authorization: Bearer <token>
```

### AI Extraction Endpoints

#### Extract Tasks from Text
```http
POST /api/ai/extract
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Please complete the login page by Friday and send the deployment link.",
  "saveTasks": false
}
```

## 🔒 Security

- **JWT Authentication**: Secure token-based authentication
- **BCrypt Password Hashing**: Secure password storage
- **CORS Configuration**: Controlled cross-origin requests
- **Environment Variables**: No hardcoded secrets
- **OAuth2 Integration**: Secure third-party authentication
- **Input Validation**: Comprehensive DTO validation

## 🧪 Testing

### Backend Tests.   
```bash
cd taskflow-backend
mvn test
```

### Frontend Tests
```bash
cd taskflow-frontend
npm test
```

## 📦 Deployment

### Backend Deployment (Render)

1. Create a new Web Service on Render
2. Connect your GitHub repository
3. Set root directory to `taskflow-backend`
4. Build command: `mvn -DskipTests clean package`
5. Start command: `java -jar target/taskflow-backend-1.0.0.jar`
6. Add environment variables from `.env.example`

### Frontend Deployment (Vercel)

1. Import your repository on Vercel
2. Set root directory to `taskflow-frontend`
3. Framework preset: Vite
4. Add environment variables
5. Deploy

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines before submitting pull requests.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the amazing UI library
- MongoDB for the flexible database
- AI providers (Gemini, OpenAI) for powerful AI capabilities

## 📞 Support

For support, email support@omnitask.com or open an issue on GitHub.

## 🗺️ Roadmap

### Completed
- ✅ User authentication with JWT
- ✅ Task management (CRUD)
- ✅ AI-powered task extraction
- ✅ Gmail integration
- ✅ WhatsApp chat import
- ✅ Analytics dashboard
- ✅ Google OAuth authentication

### Planned
- ⏳ Mobile app (React Native)
- ⏳ Team collaboration features
- ⏳ Calendar integration
- ⏳ Advanced analytics
- ⏳ Custom AI model training
- ⏳ Dark mode
- ⏳ Multi-language support

---

**Built with ❤️ by the OMNITASK team**
