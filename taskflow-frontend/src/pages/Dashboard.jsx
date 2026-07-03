import { AlertTriangle, CheckCircle2, Clock, ListTodo, Sparkles } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { extractApiError } from '../api/authApi';
import { priorityLabel, statusLabel, taskApi } from '../api/taskApi';
import { useAuth } from '../context/AuthContext';
import Spinner from '../components/ui/Spinner';

export default function Dashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    taskApi
      .getStats()
      .then(({ data }) => setStats(data))
      .catch((err) => setError(extractApiError(err)))
      .finally(() => setLoading(false));
  }, []);

  const statCards = stats
    ? [
        { label: 'Total Tasks', value: stats.totalTasks, icon: ListTodo, color: 'bg-brand-50 text-brand-600' },
        { label: 'Pending', value: stats.pendingTasks, icon: Clock, color: 'bg-amber-50 text-amber-600' },
        { label: 'Completed', value: stats.completedTasks, icon: CheckCircle2, color: 'bg-emerald-50 text-emerald-600' },
        { label: 'Overdue', value: stats.overdueTasks, icon: AlertTriangle, color: 'bg-red-50 text-red-600' },
      ]
    : [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">
          Welcome back, {user?.fullName?.split(' ')[0] || 'there'}!
        </h1>
        <p className="mt-1 text-slate-500">Overview of your tasks and recent activity</p>
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

          <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
            <div className="flex items-center justify-between">
              <h2 className="font-semibold text-slate-900">Recent activity</h2>
              <Link to="/tasks" className="text-sm font-medium text-brand-600 hover:text-brand-700">
                View all tasks
              </Link>
            </div>

            {stats?.recentTasks?.length ? (
              <ul className="mt-4 divide-y divide-slate-100">
                {stats.recentTasks.map((task) => (
                  <li key={task.id} className="flex items-center justify-between py-3">
                    <div>
                      <p className="font-medium text-slate-900">{task.title}</p>
                      <p className="text-xs text-slate-500">
                        {statusLabel[task.status]} · {priorityLabel[task.priority]}
                      </p>
                    </div>
                    {task.overdue && (
                      <span className="rounded-full bg-red-100 px-2 py-0.5 text-xs font-medium text-red-700">
                        Overdue
                      </span>
                    )}
                  </li>
                ))}
              </ul>
            ) : (
              <div className="py-8 text-center">
                <Sparkles className="mx-auto h-8 w-8 text-brand-400" />
                <p className="mt-2 text-sm text-slate-500">No tasks yet.</p>
                <Link
                  to="/tasks"
                  className="mt-3 inline-block text-sm font-semibold text-brand-600 hover:text-brand-700"
                >
                  Create your first task
                </Link>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
