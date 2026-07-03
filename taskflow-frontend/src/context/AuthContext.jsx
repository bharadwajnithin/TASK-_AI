import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { authApi, extractApiError } from '../api/authApi';
import { storage } from '../utils/storage';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => storage.getUser());
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const isAuthenticated = useMemo(
    () => storage.isSessionValid() && !!user,
    [user]
  );

  const applyAuthResponse = useCallback((data) => {
    storage.saveSession({
      token: data.token,
      user: data.user,
      expiresIn: data.expiresIn,
    });
    setUser(data.user);
    setError(null);
    return data.user;
  }, []);

  const refreshUser = useCallback(async () => {
    if (!storage.isSessionValid()) {
      setUser(null);
      setLoading(false);
      return null;
    }
    try {
      const { data } = await authApi.getMe();
      storage.setUser(data);
      setUser(data);
      return data;
    } catch {
      storage.clearSession();
      setUser(null);
      return null;
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshUser();
  }, [refreshUser]);

  const register = async (payload) => {
    setError(null);
    try {
      const { data } = await authApi.register(payload);
      return applyAuthResponse(data);
    } catch (err) {
      const msg = extractApiError(err);
      setError(msg);
      throw new Error(msg);
    }
  };

  const login = async (payload) => {
    setError(null);
    try {
      const { data } = await authApi.login(payload);
      return applyAuthResponse(data);
    } catch (err) {
      const msg = extractApiError(err);
      setError(msg);
      throw new Error(msg);
    }
  };

  const loginWithToken = useCallback((token, expiresIn, userData) => {
    storage.saveSession({ token, user: userData, expiresIn: Number(expiresIn) });
    setUser(userData);
    setError(null);
  }, []);

  const logout = useCallback(() => {
    storage.clearSession();
    setUser(null);
    setError(null);
  }, []);

  const value = useMemo(
    () => ({
      user,
      loading,
      error,
      isAuthenticated,
      register,
      login,
      loginWithToken,
      logout,
      refreshUser,
      setError,
    }),
    [user, loading, error, isAuthenticated, loginWithToken, logout, refreshUser]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}
