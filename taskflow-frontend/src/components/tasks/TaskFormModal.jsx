import { useEffect, useState } from 'react';
import { extractApiError } from '../../api/authApi';
import {
  PRIORITIES,
  SOURCE_TYPES,
  TASK_STATUSES,
  priorityLabel,
  statusLabel,
} from '../../api/taskApi';
import Button from '../ui/Button';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Textarea from '../ui/Textarea';

const emptyForm = {
  title: '',
  description: '',
  priority: 'MEDIUM',
  status: 'PENDING',
  dueDate: '',
  sourceType: 'MANUAL',
  sourceContent: '',
  clientName: '',
  projectName: '',
};

export default function TaskFormModal({ open, onClose, onSubmit, task, loading }) {
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (task) {
      setForm({
        title: task.title || '',
        description: task.description || '',
        priority: task.priority || 'MEDIUM',
        status: task.status || 'PENDING',
        dueDate: task.dueDate || '',
        sourceType: task.sourceType || 'MANUAL',
        sourceContent: task.sourceContent || '',
        clientName: task.clientName || '',
        projectName: task.projectName || '',
      });
    } else {
      setForm(emptyForm);
    }
    setErrors({});
  }, [task, open]);

  const validate = () => {
    const next = {};
    if (!form.title.trim()) next.title = 'Title is required';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const payload = {
      title: form.title.trim(),
      description: form.description.trim() || null,
      priority: form.priority,
      status: form.status,
      dueDate: form.dueDate || null,
      sourceType: form.sourceType,
      sourceContent: form.sourceContent.trim() || null,
      clientName: form.clientName.trim() || null,
      projectName: form.projectName.trim() || null,
    };

    try {
      await onSubmit(payload);
      onClose();
    } catch (err) {
      setErrors({ form: err.message || extractApiError(err) });
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {errors.form && (
        <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">{errors.form}</p>
      )}

      <Input
        id="title"
        label="Title *"
        value={form.title}
        onChange={(e) => setForm({ ...form, title: e.target.value })}
        error={errors.title}
        placeholder="Task title"
      />

      <Textarea
        id="description"
        label="Description"
        rows={3}
        value={form.description}
        onChange={(e) => setForm({ ...form, description: e.target.value })}
        placeholder="Task details..."
      />

      <div className="grid gap-4 sm:grid-cols-2">
        <Select
          id="priority"
          label="Priority"
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
        >
          {PRIORITIES.map((p) => (
            <option key={p} value={p}>
              {priorityLabel[p]}
            </option>
          ))}
        </Select>

        <Select
          id="status"
          label="Status"
          value={form.status}
          onChange={(e) => setForm({ ...form, status: e.target.value })}
        >
          {TASK_STATUSES.map((s) => (
            <option key={s} value={s}>
              {statusLabel[s]}
            </option>
          ))}
        </Select>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <Input
          id="dueDate"
          label="Due date"
          type="date"
          value={form.dueDate}
          onChange={(e) => setForm({ ...form, dueDate: e.target.value })}
        />
        <Select
          id="sourceType"
          label="Source"
          value={form.sourceType}
          onChange={(e) => setForm({ ...form, sourceType: e.target.value })}
        >
          {SOURCE_TYPES.map((s) => (
            <option key={s} value={s}>
              {s.charAt(0) + s.slice(1).toLowerCase()}
            </option>
          ))}
        </Select>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <Input
          id="clientName"
          label="Client name"
          value={form.clientName}
          onChange={(e) => setForm({ ...form, clientName: e.target.value })}
          placeholder="Client"
        />
        <Input
          id="projectName"
          label="Project name"
          value={form.projectName}
          onChange={(e) => setForm({ ...form, projectName: e.target.value })}
          placeholder="Project"
        />
      </div>

      <Textarea
        id="sourceContent"
        label="Source content"
        rows={2}
        value={form.sourceContent}
        onChange={(e) => setForm({ ...form, sourceContent: e.target.value })}
        placeholder="Original message or email snippet"
      />

      <div className="flex justify-end gap-3 border-t border-slate-100 pt-4">
        <Button type="button" variant="secondary" onClick={onClose}>
          Cancel
        </Button>
        <Button type="submit" loading={loading}>
          {task ? 'Save changes' : 'Create task'}
        </Button>
      </div>
    </form>
  );
}
