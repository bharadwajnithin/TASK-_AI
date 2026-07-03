import api from './axios';

export const chatApi = {
  importChat: (content, title, saveTasks = false) =>
    api.post('/api/chats/import', { content, title, saveTasks }),
  getChats: (params) => api.get('/api/chats', { params }),
  processChat: (chatId, saveTasks = true) =>
    api.post('/api/chats/process', { chatId, saveTasks }),
  deleteChat: (id) => api.delete(`/api/chats/${id}`),
};
