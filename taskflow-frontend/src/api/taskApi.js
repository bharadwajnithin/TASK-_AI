import api from './axios';

export const taskApi = {
  getTasks: (params) => api.get('/api/tasks', { params }),
  getTask: (id) => api.get(`/api/tasks/${id}`),
  getStats: () => api.get('/api/tasks/stats'),
  getAnalytics: () => api.get('/api/analytics'),
  createTask: (data) => api.post('/api/tasks', data),
  updateTask: (id, data) => api.put(`/api/tasks/${id}`, data),
  deleteTask: (id) => api.delete(`/api/tasks/${id}`),
};

export const TASK_STATUSES = ['PENDING', 'IN_PROGRESS', 'COMPLETED'];
export const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH'];
export const SOURCE_TYPES = ['MANUAL', 'EMAIL', 'WHATSAPP', 'SLACK'];

export const statusLabel = {
  PENDING: 'Pending',
  IN_PROGRESS: 'In Progress',
  COMPLETED: 'Completed',
};

export const priorityLabel = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
};

export const priorityColor = {
  LOW: 'bg-slate-100 text-slate-700',
  MEDIUM: 'bg-amber-100 text-amber-800',
  HIGH: 'bg-red-100 text-red-800',
};

export const statusColor = {
  PENDING: 'bg-blue-100 text-blue-800',
  IN_PROGRESS: 'bg-violet-100 text-violet-800',
  COMPLETED: 'bg-emerald-100 text-emerald-800',
};
