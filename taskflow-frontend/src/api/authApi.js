import api from './axios';

export const authApi = {
  register: (data) => api.post('/api/auth/register', data),
  login: (data) => api.post('/api/auth/login', data),
  getMe: () => api.get('/api/users/me'),
  getGoogleOAuthInfo: () => api.get('/api/auth/google'),
};

export const getGoogleOAuthUrl = () =>
  import.meta.env.VITE_GOOGLE_OAUTH_URL || '/oauth2/authorization/google';

export const extractApiError = (error) => {
  const data = error.response?.data;
  if (data?.validationErrors) {
    return Object.values(data.validationErrors).join('. ');
  }
  return data?.message || error.message || 'Something went wrong';
};
