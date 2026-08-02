import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { CheckCircle2, ExternalLink, RefreshCw, Server } from 'lucide-react';
import { authApi, getGoogleOAuthUrl } from '../api/authApi';
import AuthLayout from '../components/auth/AuthLayout';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [loading, setLoading] = useState(false);
  const [googleOAuth, setGoogleOAuth] = useState({ enabled: false, message: '' });
  const [wakingServer, setWakingServer] = useState(false);
  const [wakeSuccess, setWakeSuccess] = useState(false);

  const sessionExpired = searchParams.get('session') === 'expired';

  const checkOAuthStatus = async () => {
    try {
      const { data } = await authApi.getGoogleOAuthInfo();
      setGoogleOAuth(data);
      return true;
    } catch {
      setGoogleOAuth({
        enabled: false,
        message: 'Unable to reach backend server. If using Vercel, ensure VITE_API_BASE_URL environment variable is set in Vercel settings.',
      });
      return false;
    }
  };

  useEffect(() => {
    checkOAuthStatus();
  }, []);

  const handleWakeUpServer = async () => {
    setWakingServer(true);
    setWakeSuccess(false);

    const RENDER_LOGIN_URL = 'https://task-ai-74al.onrender.com/login';

    try {
      fetch(RENDER_LOGIN_URL, { mode: 'no-cors' }).catch(() => {});
    } catch {
      // Ignore fetch errors
    }

    window.open(RENDER_LOGIN_URL, '_blank', 'noopener,noreferrer');

    let attempts = 0;
    const interval = setInterval(async () => {
      attempts += 1;
      const isAwake = await checkOAuthStatus();
      if (isAwake || attempts >= 10) {
        clearInterval(interval);
        setWakingServer(false);
        if (isAwake) {
          setWakeSuccess(true);
          setTimeout(() => setWakeSuccess(false), 5000);
        }
      }
    }, 3000);
  };

  const validate = () => {
    const next = {};
    if (!form.email.trim()) next.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) next.email = 'Invalid email';
    if (!form.password) next.password = 'Password is required';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError('');
    if (!validate()) return;

    setLoading(true);
    try {
      await login({ email: form.email.trim(), password: form.password });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setApiError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = getGoogleOAuthUrl();
  };

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Sign in to manage your AI-powered tasks"
    >
      {sessionExpired && (
        <Alert type="info" className="mb-4">
          Your session expired. Please sign in again.
        </Alert>
      )}
      {apiError && (
        <Alert type="error" className="mb-4" onClose={() => setApiError('')}>
          {apiError}
        </Alert>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          placeholder=""
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          error={errors.email}
        />
        <Input
          id="password"
          label="Password"
          type="password"
          autoComplete="current-password"
          placeholder="••••••••"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          error={errors.password}
        />
        <Button type="submit" className="w-full" loading={loading}>
          Sign in
        </Button>
      </form>

      <div className="relative my-6">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-slate-200" />
        </div>
        <div className="relative flex justify-center text-xs uppercase">
          <span className="bg-slate-50 px-2 text-slate-500">Or continue with</span>
        </div>
      </div>

      {!googleOAuth.enabled && googleOAuth.message && (
        <Alert type="info" className="mb-4">
          {googleOAuth.message}
        </Alert>
      )}

      <Button
        type="button"
        variant="secondary"
        className="w-full"
        onClick={handleGoogleLogin}
        disabled={!googleOAuth.enabled}
      >
        <svg className="h-5 w-5" viewBox="0 0 24 24" aria-hidden="true">
          <path
            fill="#4285F4"
            d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
          />
          <path
            fill="#34A853"
            d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
          />
          <path
            fill="#FBBC05"
            d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
          />
          <path
            fill="#EA4335"
            d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
          />
        </svg>
        Continue with Google
      </Button>

      <div className="mt-3 flex items-center justify-end">
        <button
          type="button"
          onClick={handleWakeUpServer}
          disabled={wakingServer}
          title="Click to wake up backend if Google Login is disabled or loading"
          className="inline-flex items-center gap-1.5 rounded-md border border-slate-200 bg-slate-50 px-2.5 py-1 text-xs font-medium text-slate-600 shadow-sm transition hover:bg-slate-100 hover:text-slate-900 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-1 disabled:opacity-50"
        >
          {wakingServer ? (
            <>
              <RefreshCw className="h-3.5 w-3.5 animate-spin text-brand-600" />
              <span>Waking server...</span>
            </>
          ) : wakeSuccess ? (
            <>
              <CheckCircle2 className="h-3.5 w-3.5 text-emerald-500" />
              <span className="text-emerald-700 font-semibold">Server Ready!</span>
            </>
          ) : (
            <>
              <span className="relative flex h-2 w-2">
                <span
                  className={`absolute inline-flex h-full w-full animate-ping rounded-full ${
                    googleOAuth.enabled ? 'bg-emerald-400 opacity-75' : 'bg-amber-400 opacity-75'
                  }`}
                />
                <span
                  className={`relative inline-flex h-2 w-2 rounded-full ${
                    googleOAuth.enabled ? 'bg-emerald-500' : 'bg-amber-500'
                  }`}
                />
              </span>
              <Server className="h-3.5 w-3.5 text-slate-500" />
              <span>{googleOAuth.enabled ? 'Server Active' : 'Wake Up Server'}</span>
              <ExternalLink className="h-3 w-3 text-slate-400" />
            </>
          )}
        </button>
      </div>

      <p className="mt-6 text-center text-sm text-slate-600">
        Don&apos;t have an account?{' '}
        <Link to="/register" className="font-semibold text-brand-600 hover:text-brand-700">
          Create account
        </Link>
      </p>
    </AuthLayout>
  );
}
