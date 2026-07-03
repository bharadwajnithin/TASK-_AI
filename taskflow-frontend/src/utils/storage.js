const TOKEN_KEY = 'taskflow_token';
const USER_KEY = 'taskflow_user';
const EXPIRES_KEY = 'taskflow_expires_at';

export const storage = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  setToken: (token) => localStorage.setItem(TOKEN_KEY, token),
  removeToken: () => localStorage.removeItem(TOKEN_KEY),

  getUser: () => {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },
  setUser: (user) => localStorage.setItem(USER_KEY, JSON.stringify(user)),
  removeUser: () => localStorage.removeItem(USER_KEY),

  getExpiresAt: () => {
    const v = localStorage.getItem(EXPIRES_KEY);
    return v ? Number(v) : null;
  },
  setExpiresAt: (ms) => localStorage.setItem(EXPIRES_KEY, String(ms)),
  removeExpiresAt: () => localStorage.removeItem(EXPIRES_KEY),

  saveSession: ({ token, user, expiresIn }) => {
    storage.setToken(token);
    storage.setUser(user);
    storage.setExpiresAt(Date.now() + expiresIn);
  },

  clearSession: () => {
    storage.removeToken();
    storage.removeUser();
    storage.removeExpiresAt();
  },

  isSessionValid: () => {
    const token = storage.getToken();
    const expiresAt = storage.getExpiresAt();
    if (!token) return false;
    if (expiresAt && Date.now() > expiresAt) {
      storage.clearSession();
      return false;
    }
    return true;
  },
};
