import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import GuestRoute from './components/GuestRoute';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/layout/Layout';
import AiExtract from './pages/AiExtract';
import Analytics from './pages/Analytics';
import Dashboard from './pages/Dashboard';
import Gmail from './pages/Gmail';
import WhatsApp from './pages/WhatsApp';
import Login from './pages/Login';
import OAuthCallback from './pages/OAuthCallback';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Tasks from './pages/Tasks';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route
            path="/login"
            element={
              <GuestRoute>
                <Login />
              </GuestRoute>
            }
          />
          <Route
            path="/register"
            element={
              <GuestRoute>
                <Register />
              </GuestRoute>
            }
          />
          <Route path="/oauth/callback" element={<OAuthCallback />} />

          <Route
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/tasks" element={<Tasks />} />
            <Route path="/ai-extract" element={<AiExtract />} />
            <Route path="/analytics" element={<Analytics />} />
            <Route path="/gmail" element={<Gmail />} />
            <Route path="/whatsapp" element={<WhatsApp />} />
            <Route path="/profile" element={<Profile />} />
          </Route>

          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
