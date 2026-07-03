import { AlertTriangle, BarChart3, CheckCircle2, Clock, ListTodo, TrendingUp } from 'lucide-react';
import { useEffect, useState } from 'react';
import { extractApiError } from '../api/authApi';
import { priorityLabel, statusLabel, taskApi } from '../api/taskApi';
import Spinner from '../components/ui/Spinner';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
} from 'recharts';

const COLORS = {
  PENDING: '#3b82f6',
  IN_PROGRESS: '#8b5cf6',
  COMPLETED: '#10b981',
  LOW: '#64748b',
  MEDIUM: '#f59e0b',
  HIGH: '#ef4444',
  MANUAL: '#3b82f6',
  EMAIL: '#10b981',
  WHATSAPP: '#25d366',
  SLACK: '#4a154b',
};

export default function Analytics() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    taskApi
      .getAnalytics()
      .then(({ data }) => setAnalytics(data))
      .catch((err) => setError(extractApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  const statusData = analytics
    ? Object.entries(analytics.tasksByStatus).map(([name, value]) => ({
        name: statusLabel[name] || name,
        value,
      }))
    : [];

  const priorityData = analytics
    ? Object.entries(analytics.tasksByPriority).map(([name, value]) => ({
        name: priorityLabel[name] || name,
        value,
      }))
    : [];

  const sourceData = analytics
    ? Object.entries(analytics.tasksBySource).map(([name, value]) => ({
        name,
        value,
      }))
    : [];

  const trendData = analytics
    ? Object.entries(analytics.tasksCreatedLast7Days).map(([date, created]) => ({
        date,
        created,
        completed: analytics.tasksCompletedLast7Days[date] || 0,
      }))
    : [];

  const statCards = analytics
    ? [
        { label: 'Total Tasks', value: analytics.totalTasks, icon: ListTodo, color: 'bg-brand-50 text-brand-600' },
        { label: 'Overdue', value: analytics.overdueTasks, icon: AlertTriangle, color: 'bg-red-50 text-red-600' },
        { label: 'On Time', value: analytics.onTimeTasks, icon: CheckCircle2, color: 'bg-emerald-50 text-emerald-600' },
        { label: 'Completion Rate', value: `${analytics.completionRate}%`, icon: TrendingUp, color: 'bg-violet-50 text-violet-600' },
      ]
    : [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Analytics Dashboard</h1>
        <p className="mt-1 text-slate-500">Track your task performance and trends</p>
      </div>

      {error && (
        <p className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>
      )}

      {loading ? (
        <div className="flex justify-center py-12">
          <Spinner size="lg" />
        </div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {statCards.map(({ label, value, icon: Icon, color }) => (
              <div
                key={label}
                className="rounded-xl border border-slate-200 bg-white p-5 shadow-card"
              >
                <div className="flex items-center justify-between">
                  <p className="text-sm font-medium text-slate-500">{label}</p>
                  <div className={`rounded-lg p-2 ${color}`}>
                    <Icon className="h-5 w-5" />
                  </div>
                </div>
                <p className="mt-3 text-3xl font-bold text-slate-900">{value}</p>
              </div>
            ))}
          </div>

          <div className="grid gap-6 lg:grid-cols-2">
            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
              <h2 className="mb-4 font-semibold text-slate-900">Tasks by Status</h2>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={statusData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {statusData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[entry.name.toUpperCase()] || COLORS[entry.name]} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
              <h2 className="mb-4 font-semibold text-slate-900">Tasks by Priority</h2>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={priorityData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#8884d8">
                    {priorityData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[entry.name.toUpperCase()]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
              <h2 className="mb-4 font-semibold text-slate-900">Tasks by Source</h2>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={sourceData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="value" fill="#8884d8">
                    {sourceData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[entry.name] || '#8884d8'} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
              <h2 className="mb-4 font-semibold text-slate-900">7-Day Trend</h2>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={trendData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="created" stroke="#3b82f6" name="Created" />
                  <Line type="monotone" dataKey="completed" stroke="#10b981" name="Completed" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
