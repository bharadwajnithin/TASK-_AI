import api from './axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

export const emailApi = {
  getEmails: (params) => api.get('/api/emails', { params }),
  syncEmails: (fromEmails, maxResults = 20) => {
    const list = Array.isArray(fromEmails) ? fromEmails : [fromEmails];
    const params = new URLSearchParams();
    list.forEach((email) => params.append('fromEmails', email));
    params.append('maxResults', maxResults.toString());
    return api.post(`/api/emails/sync?${params.toString()}`);
  },
  processEmail: (emailId, saveTasks = true) =>
    api.post('/api/emails/process', { emailId, saveTasks }),
  getGmailStatus: () => api.get('/api/gmail/status'),
  disconnectGmail: () => api.delete('/api/gmail/disconnect'),
};    

export const getGmailConnectUrl = () =>
  `${API_BASE || window.location.origin.replace(':5173', ':8080')}/oauth2/authorization/google-gmail`;
