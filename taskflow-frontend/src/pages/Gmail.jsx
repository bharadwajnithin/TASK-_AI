import { Mail, Plus, RefreshCw, Sparkles, Unplug, Wand2, X } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { extractApiError } from '../api/authApi';
import { emailApi, getGmailConnectUrl } from '../api/emailApi';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Spinner from '../components/ui/Spinner';

export default function Gmail() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [status, setStatus] = useState(null);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [syncFromEmails, setSyncFromEmails] = useState([]);
  const [newEmail, setNewEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const connected = searchParams.get('connected') === 'true';

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [statusRes, emailsRes] = await Promise.all([
        emailApi.getGmailStatus(),
        emailApi.getEmails({ page: 0, size: 20 }),
      ]);
      setStatus(statusRes.data);
      setEmails(emailsRes.data.content || []);
      const loadedSenders =
        statusRes.data.syncFromEmails?.length > 0
          ? statusRes.data.syncFromEmails
          : statusRes.data.syncFromEmail
          ? [statusRes.data.syncFromEmail]
          : [];
      setSyncFromEmails(loadedSenders);
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
    if (connected) {
      setSuccess('Gmail connected successfully!');
      setSearchParams({}, { replace: true });
    }
  }, [loadData, connected, setSearchParams]);

  const handleConnect = () => {
    window.location.href = getGmailConnectUrl();
  };

  const handleAddSender = (e) => {
    if (e) e.preventDefault();
    const trimmed = newEmail.trim().toLowerCase();
    if (!trimmed) return;

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)) {
      setError('Please enter a valid email address.');
      return;
    }

    if (syncFromEmails.includes(trimmed)) {
      setError('This email address is already added.');
      return;
    }

    if (syncFromEmails.length >= 4) {
      setError('You can add a maximum of 4 client sender email addresses.');
      return;
    }

    setError('');
    setSyncFromEmails((prev) => [...prev, trimmed]);
    setNewEmail('');
  };

  const handleRemoveSender = (emailToRemove) => {
    setSyncFromEmails((prev) => prev.filter((item) => item !== emailToRemove));
  };

  const handleSync = async () => {
    let currentSenders = [...syncFromEmails];

    // Auto-add text from newEmail field if user typed an email but didn't click Add
    if (newEmail.trim()) {
      const trimmed = newEmail.trim().toLowerCase();
      if (
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed) &&
        !currentSenders.includes(trimmed) &&
        currentSenders.length < 4
      ) {
        currentSenders.push(trimmed);
        setSyncFromEmails(currentSenders);
        setNewEmail('');
      }
    }

    if (currentSenders.length === 0) {
      setError('Add at least one client email address to sync emails from.');
      return;
    }

    setSyncing(true);
    setError('');
    setSuccess('');
    try {
      const { data } = await emailApi.syncEmails(currentSenders, 20);
      setSuccess(data.message || 'Emails synced');
      await loadData();
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setSyncing(false);
    }
  };

  const handleProcess = async (emailId) => {
    setProcessingId(emailId);
    setError('');
    try {
      await emailApi.processEmail(emailId, true);
      setSuccess('Tasks extracted from email and saved.');
      await loadData();
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setProcessingId(null);
    }
  };

  const handleDisconnect = async () => {
    if (!window.confirm('Disconnect Gmail from Task AI?')) return;
    try {
      await emailApi.disconnectGmail();
      setSuccess('Gmail disconnected.');
      await loadData();
    } catch (err) {
      setError(extractApiError(err));
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <Spinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <div className="flex items-center gap-2">
            <Mail className="h-7 w-7 text-brand-600" />
            <h1 className="text-2xl font-bold text-slate-900">Gmail Integration</h1>
          </div>
          <p className="mt-1 text-slate-500">
            Import client emails and generate tasks with AI
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          {!status?.connected ? (
            <Button onClick={handleConnect}>
              <Mail className="h-4 w-4" />
              Connect Gmail
            </Button>
          ) : (
            <>
              <Button
                onClick={handleSync}
                loading={syncing}
                disabled={syncFromEmails.length === 0 && !newEmail.trim()}
              >
                <RefreshCw className="h-4 w-4" />
                Sync from senders ({syncFromEmails.length})
              </Button>
              <Button variant="secondary" onClick={handleDisconnect}>
                <Unplug className="h-4 w-4" />
                Disconnect
              </Button>
            </>
          )}
        </div>
      </div>

      {error && <Alert type="error" onClose={() => setError('')}>{error}</Alert>}
      {success && <Alert type="success" onClose={() => setSuccess('')}>{success}</Alert>}

      <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-card">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm font-medium text-slate-500">Connection status</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">
              {status?.connected ? 'Connected' : 'Not connected'}
            </p>
            {status?.accountEmail && (
              <p className="text-sm text-slate-500">{status.accountEmail}</p>
            )}
          </div>
          {status?.connected && (
            <div className="text-right">
              <p className="text-2xl font-bold text-brand-600">{status.unprocessedCount}</p>
              <p className="text-xs text-slate-500">Unprocessed emails</p>
            </div>
          )}
        </div>
        {!status?.connected && (
          <p className="mt-3 text-sm text-slate-600">
            Use the same Google email as your Task AI account. Enable Google OAuth in backend
            with <code className="rounded bg-slate-100 px-1">GOOGLE_OAUTH_ENABLED=true</code>.
          </p>
        )}
        {status?.connected && (
          <div className="mt-5 border-t border-slate-100 pt-5 space-y-4">
            <div className="flex items-center justify-between">
              <label className="block text-sm font-medium text-slate-700">
                Sync emails from (client sender addresses - up to 4)
              </label>
              <span className="text-xs font-medium text-slate-500">
                {syncFromEmails.length}/4 senders added
              </span>
            </div>

            {/* List of active sender chips */}
            {syncFromEmails.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {syncFromEmails.map((email) => (
                  <span
                    key={email}
                    className="inline-flex items-center gap-1.5 rounded-full border border-brand-200 bg-brand-50 px-3 py-1 text-xs font-semibold text-brand-700 shadow-xs"
                  >
                    <Mail className="h-3.5 w-3.5 text-brand-500" />
                    {email}
                    <button
                      type="button"
                      onClick={() => handleRemoveSender(email)}
                      className="ml-0.5 rounded-full p-0.5 text-brand-600 transition-colors hover:bg-brand-100 hover:text-brand-800"
                      title="Remove sender"
                    >
                      <X className="h-3.5 w-3.5" />
                    </button>
                  </span>
                ))}
              </div>
            )}

            {/* Input to add a new sender */}
            {syncFromEmails.length < 4 ? (
              <form onSubmit={handleAddSender} className="flex gap-2">
                <div className="flex-1">
                  <Input
                    id="new-client-sender-email"
                    type="email"
                    value={newEmail}
                    onChange={(e) => setNewEmail(e.target.value)}
                    placeholder="client@company.com"
                  />
                </div>
                <Button type="submit" variant="secondary" disabled={!newEmail.trim()}>
                  <Plus className="h-4 w-4" />
                  Add Sender
                </Button>
              </form>
            ) : (
              <p className="rounded-lg border border-amber-200 bg-amber-50 p-2.5 text-xs font-medium text-amber-600">
                Maximum of 4 client senders configured. Remove one above to add a new sender address.
              </p>
            )}

            <p className="text-xs text-slate-500">
              Only inbox emails from these client sender addresses will be imported when you sync.
            </p>
          </div>
        )}
      </div>

      <div className="rounded-xl border border-slate-200 bg-white shadow-card">
        <div className="border-b border-slate-100 px-6 py-4">
          <h2 className="font-semibold text-slate-900">Imported emails</h2>
        </div>

        {emails.length === 0 ? (
          <div className="py-12 text-center">
            <Sparkles className="mx-auto h-8 w-8 text-slate-300" />
            <p className="mt-2 text-sm text-slate-500">
              {status?.connected
                ? 'Add client email addresses above, then click Sync from senders.'
                : 'Connect Gmail to import emails.'}
            </p>
          </div>
        ) : (
          <ul className="divide-y divide-slate-100">
            {emails.map((email) => (
              <li
                key={email.id}
                className="flex flex-col gap-3 px-6 py-4 sm:flex-row sm:items-center sm:justify-between"
              >
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium text-slate-900">
                    {email.subject || '(No subject)'}
                  </p>
                  <p className="truncate text-sm text-slate-500">{email.sender}</p>
                  <p className="mt-1 line-clamp-2 text-sm text-slate-600">{email.body}</p>
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  {email.processed ? (
                    <span className="rounded-full bg-emerald-100 px-2.5 py-0.5 text-xs font-medium text-emerald-800">
                      Processed
                    </span>
                  ) : (
                    <Button
                      variant="secondary"
                      className="!py-2 !text-xs"
                      loading={processingId === email.id}
                      onClick={() => handleProcess(email.id)}
                    >
                      <Wand2 className="h-4 w-4" />
                      Generate tasks
                    </Button>
                  )}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
