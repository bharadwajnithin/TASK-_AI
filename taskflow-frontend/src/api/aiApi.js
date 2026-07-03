import api from './axios';

export const aiApi = {
  extract: (content, saveTasks = false) =>
    api.post('/api/ai/extract', { content, saveTasks }),
};
