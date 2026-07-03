import { Mail, RefreshCw, Sparkles, Unplug, Wand2 } from 'lucide-react';
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
  const [syncFromEmail, setSyncFromEmail] = useState('');
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
      if (statusRes.data.syncFromEmail) {
        setSyncFromEmail(statusRes.data.syncFromEmail);
      }
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

  const handleSync = async () => {
    const fromEmail = syncFromEmail.trim();
    if (!fromEmail) {
      setError('Enter the client email address to sync emails from.');
      return;
    }

    setSyncing(true);
    setError('');
    setSuccess('');
    try {
      const { data } = await emailApi.syncEmails(fromEmail, 20);
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
    if (!window.confirm('Disconnect Gmail from OMNITASK?')) return;
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
              <Button onClick={handleSync} loading={syncing} disabled={!syncFromEmail.trim()}>
                <RefreshCw className="h-4 w-4" />
                Sync from sender
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
            Use the same Google email as your OMNITASK account. Enable Google OAuth in backend
            with <code className="rounded bg-slate-100 px-1">GOOGLE_OAUTH_ENABLED=true</code>.
          </p>
        )}
        {status?.connected && (
          <div className="mt-4 border-t border-slate-100 pt-4">
            <Input
              id="sync-from-email"
              label="Sync emails from (client sender address)"
              type="email"
              value={syncFromEmail}
              onChange={(e) => setSyncFromEmail(e.target.value)}
              placeholder="client@company.com"
            />
            <p className="mt-2 text-xs text-slate-500">
              Only inbox emails from this sender will be imported when you sync.
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
                ? 'Enter a client email above, then click Sync from sender.'
                : 'Connect Gmail to import emails.'}
            </p>
          </div>
        ) : (
          <ul className="divide-y divide-slate-100">
            {emails.map((email) => (
              <li key={email.id} className="flex flex-col gap-3 px-6 py-4 sm:flex-row sm:items-center sm:justify-between">
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium text-slate-900">{email.subject || '(No subject)'}</p>
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
