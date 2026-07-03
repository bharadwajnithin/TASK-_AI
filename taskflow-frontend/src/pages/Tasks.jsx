import {
  Calendar,
  Eye,
  Pencil,
  Plus,
  Search,
  Trash2,
} from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { extractApiError } from '../api/authApi';
import {
  PRIORITIES,
  TASK_STATUSES,
  priorityColor,
  priorityLabel,
  statusColor,
  statusLabel,
  taskApi,
} from '../api/taskApi';
import TaskFormModal from '../components/tasks/TaskFormModal';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Modal from '../components/ui/Modal';
import Select from '../components/ui/Select';
import Spinner from '../components/ui/Spinner';

export default function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const fetchTasks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const params = { page, size: 10, sortBy: 'updatedAt', sortDir: 'desc' };
      if (search.trim()) params.search = search.trim();
      if (statusFilter) params.status = statusFilter;
      if (priorityFilter) params.priority = priorityFilter;

      const { data } = await taskApi.getTasks(params);
      setTasks(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setLoading(false);
    }
  }, [page, search, statusFilter, priorityFilter]);

  useEffect(() => {
    fetchTasks();
  }, [fetchTasks]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    fetchTasks();
  };

  const openCreate = () => {
    setSelectedTask(null);
    setFormOpen(true);
  };

  const openEdit = (task) => {
    setSelectedTask(task);
    setFormOpen(true);
  };

  const openDetail = async (task) => {
    try {
      const { data } = await taskApi.getTask(task.id);
      setSelectedTask(data);
      setDetailOpen(true);
    } catch (err) {
      setError(extractApiError(err));
    }
  };

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      if (selectedTask?.id) {
        await taskApi.updateTask(selectedTask.id, payload);
      } else {
        await taskApi.createTask(payload);
      }
      await fetchTasks();
    } catch (err) {
      throw new Error(extractApiError(err));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (task) => {
    if (!window.confirm(`Delete "${task.title}"?`)) return;
    try {
      await taskApi.deleteTask(task.id);
      if (detailOpen) setDetailOpen(false);
      await fetchTasks();
    } catch (err) {
      setError(extractApiError(err));
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Tasks</h1>
          <p className="mt-1 text-slate-500">
            {totalElements} task{totalElements !== 1 ? 's' : ''} total
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" />
          New task
        </Button>
      </div>

      {error && (
        <Alert type="error" onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-card">
        <form onSubmit={handleSearch} className="grid gap-3 lg:grid-cols-4">
          <div className="lg:col-span-2">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
              <Input
                id="search"
                placeholder="Search title, client, project..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="pl-9"
              />
            </div>
          </div>
          <Select
            id="statusFilter"
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All statuses</option>
            {TASK_STATUSES.map((s) => (
              <option key={s} value={s}>
                {statusLabel[s]}
              </option>
            ))}
          </Select>
          <Select
            id="priorityFilter"
            value={priorityFilter}
            onChange={(e) => {
              setPriorityFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All priorities</option>
            {PRIORITIES.map((p) => (
              <option key={p} value={p}>
                {priorityLabel[p]}
              </option>
            ))}
          </Select>
        </form>
      </div>

      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-card">
        {loading ? (
          <div className="flex justify-center py-16">
            <Spinner size="lg" />
          </div>
        ) : tasks.length === 0 ? (
          <div className="py-16 text-center">
            <p className="text-slate-500">No tasks found.</p>
            <Button className="mt-4" onClick={openCreate}>
              Create your first task
            </Button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[720px] text-left text-sm">
              <thead className="border-b border-slate-100 bg-slate-50/80 text-xs uppercase tracking-wide text-slate-500">
                <tr>
                  <th className="px-4 py-3 font-medium">Title</th>
                  <th className="px-4 py-3 font-medium">Priority</th>
                  <th className="px-4 py-3 font-medium">Status</th>
                  <th className="px-4 py-3 font-medium">Due</th>
                  <th className="px-4 py-3 font-medium">Client</th>
                  <th className="px-4 py-3 font-medium text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {tasks.map((task) => (
                  <tr key={task.id} className="hover:bg-slate-50/50">
                    <td className="px-4 py-3">
                      <p className="font-medium text-slate-900">{task.title}</p>
                      {task.projectName && (
                        <p className="text-xs text-slate-500">{task.projectName}</p>
                      )}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${priorityColor[task.priority]}`}
                      >
                        {priorityLabel[task.priority]}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${statusColor[task.status]}`}
                      >
                        {statusLabel[task.status]}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      {task.dueDate ? (
                        <span
                          className={`inline-flex items-center gap-1 ${
                            task.overdue ? 'font-medium text-red-600' : 'text-slate-600'
                          }`}
                        >
                          <Calendar className="h-3.5 w-3.5" />
                          {task.dueDate}
                        </span>
                      ) : (
                        '—'
                      )}
                    </td>
                    <td className="px-4 py-3 text-slate-600">{task.clientName || '—'}</td>
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-1">
                        <button
                          type="button"
                          onClick={() => openDetail(task)}
                          className="rounded-lg p-2 text-slate-400 hover:bg-slate-100 hover:text-brand-600"
                          title="View"
                        >
                          <Eye className="h-4 w-4" />
                        </button>
                        <button
                          type="button"
                          onClick={() => openEdit(task)}
                          className="rounded-lg p-2 text-slate-400 hover:bg-slate-100 hover:text-brand-600"
                          title="Edit"
                        >
                          <Pencil className="h-4 w-4" />
                        </button>
                        <button
                          type="button"
                          onClick={() => handleDelete(task)}
                          className="rounded-lg p-2 text-slate-400 hover:bg-red-50 hover:text-red-600"
                          title="Delete"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {totalPages > 1 && (
          <div className="flex items-center justify-between border-t border-slate-100 px-4 py-3">
            <p className="text-sm text-slate-500">
              Page {page + 1} of {totalPages}
            </p>
            <div className="flex gap-2">
              <Button
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                Previous
              </Button>
              <Button
                variant="secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          </div>
        )}
      </div>

      <Modal
        open={formOpen}
        onClose={() => setFormOpen(false)}
        title={selectedTask ? 'Edit task' : 'Create task'}
        wide
      >
        <TaskFormModal
          open={formOpen}
          onClose={() => setFormOpen(false)}
          onSubmit={handleSubmit}
          task={selectedTask}
          loading={submitting}
        />
      </Modal>

      <Modal
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
        title="Task details"
        wide
      >
        {selectedTask && (
          <div className="space-y-4">
            <div>
              <h3 className="text-xl font-semibold text-slate-900">{selectedTask.title}</h3>
              <div className="mt-2 flex flex-wrap gap-2">
                <span
                  className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${priorityColor[selectedTask.priority]}`}
                >
                  {priorityLabel[selectedTask.priority]}
                </span>
                <span
                  className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${statusColor[selectedTask.status]}`}
                >
                  {statusLabel[selectedTask.status]}
                </span>
                {selectedTask.overdue && (
                  <span className="rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-800">
                    Overdue
                  </span>
                )}
              </div>
            </div>

            {selectedTask.description && (
              <div>
                <p className="text-xs font-medium uppercase text-slate-500">Description</p>
                <p className="mt-1 text-slate-700">{selectedTask.description}</p>
              </div>
            )}

            <dl className="grid gap-3 sm:grid-cols-2">
              <div>
                <dt className="text-xs font-medium uppercase text-slate-500">Due date</dt>
                <dd className="mt-0.5 text-slate-900">{selectedTask.dueDate || '—'}</dd>
              </div>
              <div>
                <dt className="text-xs font-medium uppercase text-slate-500">Source</dt>
                <dd className="mt-0.5 text-slate-900">{selectedTask.sourceType}</dd>
              </div>
              <div>
                <dt className="text-xs font-medium uppercase text-slate-500">Client</dt>
                <dd className="mt-0.5 text-slate-900">{selectedTask.clientName || '—'}</dd>
              </div>
              <div>
                <dt className="text-xs font-medium uppercase text-slate-500">Project</dt>
                <dd className="mt-0.5 text-slate-900">{selectedTask.projectName || '—'}</dd>
              </div>
            </dl>

            {selectedTask.sourceContent && (
              <div>
                <p className="text-xs font-medium uppercase text-slate-500">Source content</p>
                <p className="mt-1 rounded-lg bg-slate-50 p-3 text-sm text-slate-700">
                  {selectedTask.sourceContent}
                </p>
              </div>
            )}

            <div className="flex justify-end gap-2 border-t border-slate-100 pt-4">
              <Button variant="danger" onClick={() => handleDelete(selectedTask)}>
                Delete
              </Button>
              <Button
                onClick={() => {
                  setDetailOpen(false);
                  openEdit(selectedTask);
                }}
              >
                Edit
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}
