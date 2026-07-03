# 30-Day Commit Schedule for OMNITASK

This document provides a realistic 30-day development roadmap for manually committing the OMNITASK project to GitHub.

## Important Notes

**DO NOT COMMIT:**
- `.env` files (both backend and frontend)
- `node_modules/` folder
- `target/` folder
- `dist/` folder
- `.idea/` folder
- Any build artifacts
- Any temporary files
- Any logs

**COMMIT INSTEAD:**
- `.env.example` files (with placeholder values)
- Source code files
- Configuration files
- Documentation files

---

## Day 1: Project Initialization

**Goal:** Initialize project structure and add root documentation

**Files/Folders:**
- `LICENSE`
- `README.md`
- `.gitignore` (root)

**Reason:** Start with project licensing, overview, and basic Git configuration.

**Suggested Commit Message:**
```
chore: initialize OMNITASK project with LICENSE and README
```

---

## Day 2: Backend Project Setup

**Goal:** Initialize Spring Boot backend project

**Files/Folders:**
- `taskflow-backend/pom.xml`
- `taskflow-backend/.gitignore`
- `taskflow-backend/README.md`

**Reason:** Set up Maven project structure and dependencies.

**Suggested Commit Message:**
```
feat: initialize Spring Boot backend with Maven configuration
```

---

## Day 3: Backend Configuration

**Goal:** Add core backend configuration files

**Files/Folders:**
- `taskflow-backend/src/main/resources/application.yml`
- `taskflow-backend/.env.example`
- `taskflow-backend/src/main/java/com/taskflowai/TaskflowAiApplication.java`

**Reason:** Configure application properties and main entry point.

**Suggested Commit Message:**
```
feat: add application configuration and main entry point
```

---

## Day 4: Backend Configuration Classes

**Goal:** Add configuration classes for MongoDB, CORS, and app properties

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/config/MongoConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/config/CorsConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/config/AppProperties.java`

**Reason:** Set up database connectivity, CORS, and application properties binding.

**Suggested Commit Message:**
```
feat: add MongoDB, CORS, and app properties configuration
```

---

## Day 5: Security Configuration

**Goal:** Add Spring Security and JWT configuration

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/security/SecurityConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/security/JwtUtil.java`
- `taskflow-backend/src/main/java/com/taskflowai/security/JwtAuthenticationFilter.java`

**Reason:** Implement security layer with JWT authentication.

**Suggested Commit Message:**
```
feat: add Spring Security configuration with JWT authentication
```

---

## Day 6: User Model and Repository

**Goal:** Add user data model and repository

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/model/User.java`
- `taskflow-backend/src/main/java/com/taskflowai/model/Role.java`
- `taskflow-backend/src/main/java/com/taskflowai/repository/UserRepository.java`

**Reason:** Define user entity and database access layer.

**Suggested Commit Message:**
```
feat: add User model and repository with role support
```

---

## Day 7: Authentication DTOs

**Goal:** Add data transfer objects for authentication

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/dto/auth/LoginRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/auth/RegisterRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/auth/AuthResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/user/UserResponse.java`

**Reason:** Define API contracts for authentication endpoints.

**Suggested Commit Message:**
```
feat: add authentication DTOs for login and registration
```

---

## Day 8: Authentication Service

**Goal:** Implement authentication business logic

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/AuthService.java`
- `taskflow-backend/src/main/java/com/taskflowai/security/CustomUserDetails.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/SecurityUtils.java`

**Reason:** Implement user registration, login, and JWT generation logic.

**Suggested Commit Message:**
```
feat: implement authentication service with JWT generation
```

---

## Day 9: Authentication Controllers

**Goal:** Add authentication and user controllers

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/controller/AuthController.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/UserController.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/HealthController.java`

**Reason:** Expose authentication and user management endpoints.

**Suggested Commit Message:**
```
feat: add authentication and user management endpoints
```

---

## Day 10: Task Model and Repository

**Goal:** Add task data model and repository

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/model/Task.java`
- `taskflow-backend/src/main/java/com/taskflowai/model/TaskStatus.java`
- `taskflow-backend/src/main/java/com/taskflowai/model/Priority.java`
- `taskflow-backend/src/main/java/com/taskflowai/model/SourceType.java`
- `taskflow-backend/src/main/java/com/taskflowai/repository/TaskRepository.java`

**Reason:** Define task entity with enums and database access layer.

**Suggested Commit Message:**
```
feat: add Task model with status, priority, and source type enums
```

---

## Day 11: Task DTOs

**Goal:** Add data transfer objects for task management

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/dto/task/TaskRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/task/TaskResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/task/TaskPageResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/task/TaskStatsResponse.java`

**Reason:** Define API contracts for task CRUD operations.

**Suggested Commit Message:**
```
feat: add task DTOs for CRUD operations and pagination
```

---

## Day 12: Task Service

**Goal:** Implement task management business logic

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/TaskService.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/TaskMapper.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/DateParseUtil.java`

**Reason:** Implement task CRUD operations and mapping utilities.

**Suggested Commit Message:**
```
feat: implement task service with CRUD operations
```

---

## Day 13: Task Controller

**Goal:** Add task management endpoints

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/controller/TaskController.java`

**Reason:** Expose task management API endpoints.

**Suggested Commit Message:**
```
feat: add task management endpoints with search and pagination
```

---

## Day 14: AI Provider Interface

**Goal:** Add provider-agnostic AI interface

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/AiProvider.java`
- `taskflow-backend/src/main/java/com/taskflowai/config/RestClientConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/AiRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/AiResponse.java`

**Reason:** Define provider-agnostic AI integration interface.

**Suggested Commit Message:**
```
feat: add provider-agnostic AI provider interface
```

---

## Day 15: Gemini AI Provider

**Goal:** Implement Gemini AI provider

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/GeminiAiProvider.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/AiExtractionPayload.java`

**Reason:** Implement Gemini AI provider for task extraction.

**Suggested Commit Message:**
```
feat: implement Gemini AI provider for task extraction
```

---

## Day 16: OpenAI Provider

**Goal:** Implement OpenAI provider

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/OpenAiProvider.java`
- `taskflow-backend/src/main/java/com/taskflowai/service/AiProviderFactory.java`

**Reason:** Implement OpenAI provider and factory for provider selection.

**Suggested Commit Message:**
```
feat: implement OpenAI provider and factory pattern
```

---

## Day 17: AI Extraction Service

**Goal:** Implement AI-powered task extraction service

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/AiExtractionService.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/ExtractRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/ExtractResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/ai/ExtractedTaskDto.java`

**Reason:** Implement AI service for extracting tasks from text.

**Suggested Commit Message:**
```
feat: implement AI-powered task extraction service
```

---

## Day 18: AI Controller

**Goal:** Add AI extraction endpoints

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/controller/AiController.java`
- `taskflow-backend/src/main/java/com/taskflowai/exception/AiServiceException.java`

**Reason:** Expose AI extraction API endpoints.

**Suggested Commit Message:**
```
feat: add AI extraction endpoints
```

---

## Day 19: Email Integration Models

**Goal:** Add email and Gmail data models

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/model/EmailMessage.java`
- `taskflow-backend/src/main/java/com/taskflowai/repository/EmailMessageRepository.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/gmail/GmailListResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/gmail/GmailMessageDetail.java`

**Reason:** Define email entities and Gmail API DTOs.

**Suggested Commit Message:**
```
feat: add email and Gmail data models
```

---

## Day 20: Gmail Integration Service

**Goal:** Implement Gmail API integration

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/GmailApiClient.java`
- `taskflow-backend/src/main/java/com/taskflowai/service/GmailTokenService.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/GmailMessageParser.java`

**Reason:** Implement Gmail API client and token management.

**Suggested Commit Message:**
```
feat: implement Gmail API integration and message parsing
```

---

## Day 21: Gmail Service and Controller

**Goal:** Add Gmail service and endpoints

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/GmailService.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/GmailController.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/EmailController.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/email/EmailResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/email/EmailPageResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/email/ProcessEmailRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/email/ProcessEmailResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/email/GmailStatusResponse.java`

**Reason:** Implement Gmail service and expose email management endpoints.

**Suggested Commit Message:**
```
feat: add Gmail service and email management endpoints
```

---

## Day 22: Google OAuth Configuration

**Goal:** Add Google OAuth2 integration

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/config/GoogleOAuth2ClientConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/config/OAuth2ConditionalConfig.java`
- `taskflow-backend/src/main/java/com/taskflowai/security/OAuth2AuthenticationSuccessHandler.java`

**Reason:** Configure Google OAuth2 for authentication.

**Suggested Commit Message:**
```
feat: add Google OAuth2 configuration and authentication
```

---

## Day 23: WhatsApp Integration

**Goal:** Add WhatsApp chat import functionality

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/model/ChatImport.java`
- `taskflow-backend/src/main/java/com/taskflowai/repository/ChatImportRepository.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/WhatsAppChatParser.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ChatResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ChatPageResponse.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ImportChatRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ImportChatResponse.java`

**Reason:** Implement WhatsApp chat parsing and import.

**Suggested Commit Message:**
```
feat: add WhatsApp chat import and parsing functionality
```

---

## Day 24: Chat Service and Controller

**Goal:** Add chat service and endpoints

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/ChatService.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/ChatController.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ProcessChatRequest.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/chat/ProcessChatResponse.java`

**Reason:** Implement chat service and expose chat management endpoints.

**Suggested Commit Message:**
```
feat: add chat service and AI processing endpoints
```

---

## Day 25: Analytics Service

**Goal:** Add analytics functionality

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/AnalyticsService.java`
- `taskflow-backend/src/main/java/com/taskflowai/controller/AnalyticsController.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/analytics/AnalyticsResponse.java`

**Reason:** Implement analytics service for dashboard statistics.

**Suggested Commit Message:**
```
feat: add analytics service and dashboard endpoints
```

---

## Day 26: Exception Handling

**Goal:** Add global exception handling and common DTOs

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/exception/GlobalExceptionHandler.java`
- `taskflow-backend/src/main/java/com/taskflowai/exception/ResourceNotFoundException.java`
- `taskflow-backend/src/main/java/com/taskflowai/dto/common/ApiErrorResponse.java`

**Reason:** Implement centralized error handling.

**Suggested Commit Message:**
```
feat: add global exception handling and error responses
```

---

## Day 27: Frontend Project Setup

**Goal:** Initialize React frontend project

**Files/Folders:**
- `taskflow-frontend/package.json`
- `taskflow-frontend/package-lock.json`
- `taskflow-frontend/vite.config.js`
- `taskflow-frontend/tailwind.config.js`
- `taskflow-frontend/postcss.config.js`
- `taskflow-frontend/.gitignore`
- `taskflow-frontend/README.md`

**Reason:** Set up React project with Vite and Tailwind CSS.

**Suggested Commit Message:**
```
feat: initialize React frontend with Vite and Tailwind CSS
```

---

## Day 28: Frontend Configuration

**Goal:** Add frontend configuration and entry files

**Files/Folders:**
- `taskflow-frontend/index.html`
- `taskflow-frontend/src/main.jsx`
- `taskflow-frontend/src/App.jsx`
- `taskflow-frontend/src/index.css`
- `taskflow-frontend/.env.example`
- `taskflow-frontend/vercel.json`

**Reason:** Configure frontend entry points and environment variables.

**Suggested Commit Message:**
```
feat: add frontend configuration and entry files
```

---

## Day 29: Frontend API Layer

**Goal:** Add API utilities and authentication context

**Files/Folders:**
- `taskflow-frontend/src/api/axios.js`
- `taskflow-frontend/src/api/authApi.js`
- `taskflow-frontend/src/api/taskApi.js`
- `taskflow-frontend/src/api/aiApi.js`
- `taskflow-frontend/src/api/emailApi.js`
- `taskflow-frontend/src/api/chatApi.js`
- `taskflow-frontend/src/context/AuthContext.jsx`
- `taskflow-frontend/src/utils/storage.js`

**Reason:** Implement API client layer and authentication context.

**Suggested Commit Message:**
```
feat: add API layer and authentication context
```

---

## Day 30: Frontend Components - UI

**Goal:** Add reusable UI components

**Files/Folders:**
- `taskflow-frontend/src/components/ui/Button.jsx`
- `taskflow-frontend/src/components/ui/Input.jsx`
- `taskflow-frontend/src/components/ui/Textarea.jsx`
- `taskflow-frontend/src/components/ui/Select.jsx`
- `taskflow-frontend/src/components/ui/Modal.jsx`
- `taskflow-frontend/src/components/ui/Alert.jsx`
- `taskflow-frontend/src/components/ui/Spinner.jsx`

**Reason:** Create reusable UI component library.

**Suggested Commit Message:**
```
feat: add reusable UI components
```

---

## Day 31: Frontend Components - Layout

**Goal:** Add layout components

**Files/Folders:**
- `taskflow-frontend/src/components/layout/Layout.jsx`
- `taskflow-frontend/src/components/layout/Navbar.jsx`
- `taskflow-frontend/src/components/layout/Sidebar.jsx`

**Reason:** Implement main application layout structure.

**Suggested Commit Message:**
```
feat: add layout components (Navbar, Sidebar)
```

---

## Day 32: Frontend Components - Auth

**Goal:** Add authentication components

**Files/Folders:**
- `taskflow-frontend/src/components/auth/AuthLayout.jsx`
- `taskflow-frontend/src/components/ProtectedRoute.jsx`
- `taskflow-frontend/src/components/GuestRoute.jsx`

**Reason:** Implement authentication layout and route protection.

**Suggested Commit Message:**
```
feat: add authentication components and route protection
```

---

## Day 33: Frontend Pages - Auth

**Goal:** Add authentication pages

**Files/Folders:**
- `taskflow-frontend/src/pages/Login.jsx`
- `taskflow-frontend/src/pages/Register.jsx`
- `taskflow-frontend/src/pages/OAuthCallback.jsx`

**Reason:** Implement login, registration, and OAuth callback pages.

**Suggested Commit Message:**
```
feat: add authentication pages (Login, Register, OAuth)
```

---

## Day 34: Frontend Pages - Dashboard

**Goal:** Add dashboard page

**Files/Folders:**
- `taskflow-frontend/src/pages/Dashboard.jsx`

**Reason:** Implement main dashboard page.

**Suggested Commit Message:**
```
feat: add dashboard page with task overview
```

---

## Day 35: Frontend Pages - Tasks

**Goal:** Add task management pages

**Files/Folders:**
- `taskflow-frontend/src/pages/Tasks.jsx`
- `taskflow-frontend/src/components/tasks/TaskFormModal.jsx`

**Reason:** Implement task management interface.

**Suggested Commit Message:**
```
feat: add task management pages and form modal
```

---

## Day 36: Frontend Pages - AI Extraction

**Goal:** Add AI extraction page

**Files/Folders:**
- `taskflow-frontend/src/pages/AiExtract.jsx`

**Reason:** Implement AI-powered task extraction interface.

**Suggested Commit Message:**
```
feat: add AI task extraction page
```

---

## Day 37: Frontend Pages - Gmail

**Goal:** Add Gmail integration page

**Files/Folders:**
- `taskflow-frontend/src/pages/Gmail.jsx`

**Reason:** Implement Gmail integration interface.

**Suggested Commit Message:**
```
feat: add Gmail integration page
```

---

## Day 38: Frontend Pages - WhatsApp

**Goal:** Add WhatsApp import page

**Files/Folders:**
- `taskflow-frontend/src/pages/WhatsApp.jsx`

**Reason:** Implement WhatsApp chat import interface.

**Suggested Commit Message:**
```
feat: add WhatsApp chat import page
```

---

## Day 39: Frontend Pages - Analytics

**Goal:** Add analytics page

**Files/Folders:**
- `taskflow-frontend/src/pages/Analytics.jsx`

**Reason:** Implement analytics dashboard interface.

**Suggested Commit Message:**
```
feat: add analytics dashboard page
```

---

## Day 40: Frontend Pages - Profile

**Goal:** Add profile page

**Files/Folders:**
- `taskflow-frontend/src/pages/Profile.jsx`
- `taskflow-frontend/public/favicon.svg`

**Reason:** Implement user profile page and add favicon.

**Suggested Commit Message:**
```
feat: add user profile page and favicon
```

---

## Day 41: Backend Tests

**Goal:** Add backend test files

**Files/Folders:**
- `taskflow-backend/taskflowai/TaskflowAiApplicationTests.java`
- `taskflow-backend/taskflowai/controller/AiControllerIntegrationTest.java`
- `taskflow-backend/taskflowai/controller/AuthControllerIntegrationTest.java`
- `taskflow-backend/taskflowai/controller/TaskControllerIntegrationTest.java`
- `taskflow-backend/taskflowai/util/WhatsAppChatParserTest.java`

**Reason:** Add integration tests for backend controllers and utilities.

**Suggested Commit Message:**
```
test: add backend integration tests
```

---

## Day 42: Root Documentation

**Goal:** Add root documentation files

**Files/Folders:**
- `SECURITY.md`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `CHANGELOG.md`

**Reason:** Add comprehensive project documentation.

**Suggested Commit Message:**
```
docs: add project documentation (Security, Contributing, Code of Conduct, Changelog)
```

---

## Day 43: Backend README Update

**Goal:** Update backend README with complete documentation

**Files/Folders:**
- `taskflow-backend/README.md`

**Reason:** Update backend README with complete setup and API documentation.

**Suggested Commit Message:**
```
docs: update backend README with complete documentation
```

---

## Day 44: Frontend README Update

**Goal:** Update frontend README with complete documentation

**Files/Folders:**
- `taskflow-frontend/README.md`

**Reason:** Update frontend README with complete setup and usage documentation.

**Suggested Commit Message:**
```
docs: update frontend README with complete documentation
```

---

## Day 45: Security Cleanup

**Goal:** Ensure no sensitive data in configuration

**Files/Folders:**
- `taskflow-backend/src/main/resources/application.yml`
- `taskflow-backend/.env.example`

**Reason:** Remove any hardcoded secrets and ensure placeholder values only.

**Suggested Commit Message:**
```
security: remove hardcoded secrets from configuration files
```

---

## Day 46: Gitignore Finalization

**Goal:** Finalize .gitignore files

**Files/Folders:**
- `taskflow-backend/.gitignore`
- `taskflow-frontend/.gitignore`

**Reason:** Ensure all build artifacts, IDE files, and sensitive files are ignored.

**Suggested Commit Message:**
```
chore: finalize .gitignore files for production
```

---

## Day 47: Code Quality - Backend

**Goal:** Backend code quality improvements

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/util/TaskMapper.java`
- `taskflow-backend/src/main/java/com/taskflowai/util/UserMapper.java`

**Reason:** Refactor mapper classes for better code quality.

**Suggested Commit Message:**
```
refactor: improve mapper classes code quality
```

---

## Day 48: Code Quality - Frontend

**Goal:** Frontend code quality improvements

**Files/Folders:**
- `taskflow-frontend/src/App.jsx`
- `taskflow-frontend/src/components/layout/Layout.jsx`

**Reason:** Refactor layout components for better code quality.

**Suggested Commit Message:**
```
refactor: improve frontend layout components
```

---

## Day 49: Bug Fixes

**Goal:** Fix identified bugs

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/AiExtractionService.java`
- `taskflow-backend/src/main/java/com/taskflowai/service/ChatService.java`

**Reason:** Fix bugs in AI extraction and chat services.

**Suggested Commit Message:**
```
fix: resolve bugs in AI extraction and chat services
```

---

## Day 50: Performance Optimization

**Goal:** Optimize performance

**Files/Folders:**
- `taskflow-backend/src/main/java/com/taskflowai/service/TaskService.java`
- `taskflow-backend/src/main/java/com/taskflowai/service/AnalyticsService.java`

**Reason:** Optimize database queries and service performance.

**Suggested Commit Message:**
```
perf: optimize task and analytics service performance
```

---

## Day 51-60: Additional Polish (Optional)

**Days 51-60 are reserved for additional polish, testing, and minor improvements as needed.**

---

## Summary

**Total Days:** 50+ (extendable to 60)

**Total Commits:** 50+ (one per day minimum, can split into multiple commits per day)

**Phases:**
- Days 1-3: Project initialization
- Days 4-26: Backend development
- Days 27-40: Frontend development
- Days 41-50: Testing, documentation, and polish

## Important Reminders

1. **Never commit `.env` files** - only commit `.env.example`
2. **Never commit `node_modules/`** - it's in .gitignore
3. **Never commit `target/` or `dist/`** - build artifacts
4. **Never commit `.idea/`** - IDE files
5. **Always verify commit messages** before pushing
6. **Test builds** before committing (optional but recommended)

## Git Commands Reference

### Add files for the day
```bash
git add path/to/file1 path/to/file2 path/to/folder
```

### Commit with message
```bash
git commit -m "feat: your commit message"
```

### View status
```bash
git status
```

### View commit history
```bash
git log --oneline
```

### Push to GitHub (when ready)
```bash
git push origin main
```
