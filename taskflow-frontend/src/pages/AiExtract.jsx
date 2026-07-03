import { CheckCircle2, Save, Sparkles, Wand2 } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { extractApiError } from '../api/authApi';
import { aiApi } from '../api/aiApi';
import { priorityColor, priorityLabel } from '../api/taskApi';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Textarea from '../components/ui/Textarea';
import Spinner from '../components/ui/Spinner';

const EXAMPLE = `Please update the login page before Friday and send the deployment link to the client.

Client: Acme Corp
Project: Website Redesign`;

export default function AiExtract() {
  const [content, setContent] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleExtract = async (saveTasks = false) => {
    if (!content.trim()) {
      setError('Please paste conversation or message content.');
      return;
    }
    setError('');
    setSuccess('');
    saveTasks ? setSaving(true) : setLoading(true);

    try {
      const { data } = await aiApi.extract(content.trim(), saveTasks);
      setResult(data);
      if (saveTasks) {
        setSuccess(`${data.savedTasks?.length || 0} task(s) saved to your task list.`);
      }
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setLoading(false);
      setSaving(false);
    }
  };

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <div>
        <div className="flex items-center gap-2">
          <Sparkles className="h-7 w-7 text-brand-600" />
          <h1 className="text-2xl font-bold text-slate-900">AI Task Extraction</h1>
        </div>
        <p className="mt-1 text-slate-500">
          Paste emails, chats, or notes — AI detects tasks, deadlines, and priorities.
        </p>
      </div>

      {error && (
        <Alert type="error" onClose={() => setError('')}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert type="success" onClose={() => setSuccess('')}>
          {success}{' '}
          <Link to="/tasks" className="font-semibold underline">
            View tasks
          </Link>
        </Alert>
      )}

      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
        <Textarea
          id="content"
          label="Conversation / message content"
          rows={8}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Paste client email, WhatsApp chat, or meeting notes..."
        />
        <div className="mt-3 flex flex-wrap gap-2">
          <Button
            type="button"
            variant="ghost"
            className="text-xs"
            onClick={() => setContent(EXAMPLE)}
          >
            Load example
          </Button>
        </div>

        <div className="mt-4 flex flex-wrap gap-3">
          <Button onClick={() => handleExtract(false)} loading={loading} disabled={saving}>
            <Wand2 className="h-4 w-4" />
            Extract tasks
          </Button>
          <Button
            variant="secondary"
            onClick={() => handleExtract(true)}
            loading={saving}
            disabled={loading}
          >
            <Save className="h-4 w-4" />
            Extract & save to tasks
          </Button>
        </div>
      </div>

      {loading && (
        <div className="flex items-center justify-center gap-2 py-8 text-slate-500">
          <Spinner />
          <span>Analyzing with Gemini AI…</span>
        </div>
      )}

      {result && !loading && (
        <div className="space-y-4">
          {(result.clientName || result.projectName) && (
            <div className="rounded-xl border border-brand-100 bg-brand-50/50 p-4 text-sm">
              {result.clientName && (
                <p>
                  <span className="font-medium text-slate-700">Client:</span> {result.clientName}
                </p>
              )}
              {result.projectName && (
                <p>
                  <span className="font-medium text-slate-700">Project:</span> {result.projectName}
                </p>
              )}
            </div>
          )}

          <div className="rounded-xl border border-slate-200 bg-white shadow-card">
            <div className="border-b border-slate-100 px-6 py-4">
              <h2 className="font-semibold text-slate-900">
                Extracted tasks ({result.tasks?.length || 0})
              </h2>
            </div>

            {!result.tasks?.length ? (
              <p className="p-6 text-sm text-slate-500">No tasks detected in this content.</p>
            ) : (
              <ul className="divide-y divide-slate-100">
                {result.tasks.map((task, index) => (
                  <li key={index} className="flex gap-4 px-6 py-4">
                    <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-brand-500" />
                    <div className="min-w-0 flex-1">
                      <div className="flex flex-wrap items-center gap-2">
                        <p className="font-medium text-slate-900">{task.title}</p>
                        <span
                          className={`rounded-full px-2 py-0.5 text-xs font-medium ${priorityColor[task.priority]}`}
                        >
                          {priorityLabel[task.priority]}
                        </span>
                      </div>
                      {task.description && (
                        <p className="mt-1 text-sm text-slate-600">{task.description}</p>
                      )}
                      {(task.dueDate || task.dueDateText) && (
                        <p className="mt-1 text-xs text-slate-500">
                          Due: {task.dueDate || task.dueDateText}
                        </p>
                      )}
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
