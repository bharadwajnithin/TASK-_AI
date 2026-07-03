import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authApi, extractApiError } from '../api/authApi';
import Spinner from '../components/ui/Spinner';
import Alert from '../components/ui/Alert';
import { useAuth } from '../context/AuthContext';
import { storage } from '../utils/storage';

export default function OAuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { loginWithToken } = useAuth();
  const [error, setError] = useState('');

  useEffect(() => {
    const token = searchParams.get('token');
    const expiresIn = searchParams.get('expiresIn');

    if (!token) {
      setError('OAuth login failed. No token received.');
      return;
    }

    const completeLogin = async () => {
      try {
        storage.setToken(token);
        storage.setExpiresAt(Date.now() + Number(expiresIn || 86400000));

        const { data: user } = await authApi.getMe();
        loginWithToken(token, expiresIn || 86400000, user);
        navigate('/dashboard', { replace: true });
      } catch (err) {
        storage.clearSession();
        setError(extractApiError(err));
      }
    };

    completeLogin();
  }, [searchParams, navigate, loginWithToken]);

  if (error) {
    return (
      <div className="flex min-h-screen flex-col items-center justify-center gap-4 p-6">
        <Alert type="error">{error}</Alert>
        <a href="/login" className="text-sm font-semibold text-brand-600 hover:underline">
          Back to login
        </a>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3">
      <Spinner size="lg" />
      <p className="text-sm text-slate-600">Completing Google sign-in…</p>
    </div>
  );
}
