import api from './axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

export const emailApi = {
  getEmails: (params) => api.get('/api/emails', { params }),
  syncEmails: (fromEmail, maxResults = 20) =>
    api.post(`/api/emails/sync?fromEmail=${encodeURIComponent(fromEmail)}&maxResults=${maxResults}`),
  processEmail: (emailId, saveTasks = true) =>
    api.post('/api/emails/process', { emailId, saveTasks }),
  getGmailStatus: () => api.get('/api/gmail/status'),
  disconnectGmail: () => api.delete('/api/gmail/disconnect'),
};

export const getGmailConnectUrl = () =>
  `${API_BASE || window.location.origin.replace(':5173', ':8080')}/oauth2/authorization/google-gmail`;
