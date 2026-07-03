import { MessageCircle, Save, Trash2, Upload, Wand2 } from 'lucide-react';
import { useCallback, useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { extractApiError } from '../api/authApi';
import { chatApi } from '../api/chatApi';
import Alert from '../components/ui/Alert';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import Spinner from '../components/ui/Spinner';
import Textarea from '../components/ui/Textarea';

const EXAMPLE = `[6/4/26, 10:30:15 AM] Client: Please update the login page before Friday
[6/4/26, 10:31:00 AM] You: Sure, will send deployment link after that
[6/4/26, 10:32:00 AM] Client: Also fix the mobile menu on homepage`;

export default function WhatsApp() {
  const fileInputRef = useRef(null);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [chats, setChats] = useState([]);
  const [unprocessedCount, setUnprocessedCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [importing, setImporting] = useState(false);
  const [saving, setSaving] = useState(false);
  const [processingId, setProcessingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadChats = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const { data } = await chatApi.getChats({ page: 0, size: 20 });
      setChats(data.content || []);
      setUnprocessedCount(data.unprocessedCount || 0);
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadChats();
  }, [loadChats]);

  const handleFileSelect = (event) => {
    const file = event.target.files?.[0];
    if (!file) return;

    if (!file.name.endsWith('.txt')) {
      setError('Please upload a WhatsApp .txt export file.');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      setContent(String(e.target?.result || ''));
      if (!title) {
        setTitle(file.name.replace(/\.txt$/i, ''));
      }
      setError('');
    };
    reader.onerror = () => setError('Failed to read file.');
    reader.readAsText(file);
    event.target.value = '';
  };

  const handleImport = async (saveTasks = false) => {
    if (!content.trim()) {
      setError('Paste chat content or upload a .txt export file.');
      return;
    }

    setError('');
    setSuccess('');
    saveTasks ? setSaving(true) : setImporting(true);

    try {
      const { data } = await chatApi.importChat(content.trim(), title.trim() || undefined, saveTasks);
      setSuccess(
        saveTasks
          ? `Imported ${data.chat?.messageCount || 0} messages and saved ${data.extraction?.savedTasks?.length || 0} task(s).`
          : data.message || 'Chat imported successfully.'
      );
      setContent('');
      setTitle('');
      await loadChats();
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setImporting(false);
      setSaving(false);
    }
  };

  const handleProcess = async (chatId) => {
    setProcessingId(chatId);
    setError('');
    try {
      const { data } = await chatApi.processChat(chatId, true);
      setSuccess(`${data.extraction?.savedTasks?.length || 0} task(s) created from chat.`);
      await loadChats();
    } catch (err) {
      setError(extractApiError(err));
    } finally {
      setProcessingId(null);
    }
  };

  const handleDelete = async (chatId) => {
    if (!window.confirm('Delete this chat import?')) return;
    try {
      await chatApi.deleteChat(chatId);
      setSuccess('Chat import deleted.');
      await loadChats();
    } catch (err) {
      setError(extractApiError(err));
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <div className="flex items-center gap-2">
          <MessageCircle className="h-7 w-7 text-brand-600" />
          <h1 className="text-2xl font-bold text-slate-900">WhatsApp Import</h1>
        </div>
        <p className="mt-1 text-slate-500">
          Upload or paste a WhatsApp chat export — AI extracts actionable tasks
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
        <h2 className="font-semibold text-slate-900">Import chat</h2>
        <p className="mt-1 text-sm text-slate-500">
          In WhatsApp: Chat → ⋮ → More → Export chat → Without media (.txt)
        </p>

        <div className="mt-4 space-y-4">
          <Input
            id="chat-title"
            label="Chat title (optional)"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. Acme Corp client chat"
          />

          <Textarea
            id="chat-content"
            label="Chat export content"
            rows={8}
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Paste WhatsApp .txt export here..."
          />

          <div className="flex flex-wrap gap-2">
            <input
              ref={fileInputRef}
              type="file"
              accept=".txt"
              className="hidden"
              onChange={handleFileSelect}
            />
            <Button type="button" variant="secondary" onClick={() => fileInputRef.current?.click()}>
              <Upload className="h-4 w-4" />
              Upload .txt file
            </Button>
            <Button type="button" variant="ghost" className="text-xs" onClick={() => setContent(EXAMPLE)}>
              Load example
            </Button>
          </div>

          <div className="flex flex-wrap gap-3">
            <Button onClick={() => handleImport(false)} loading={importing} disabled={saving}>
              <Upload className="h-4 w-4" />
              Import chat
            </Button>
            <Button variant="secondary" onClick={() => handleImport(true)} loading={saving} disabled={importing}>
              <Save className="h-4 w-4" />
              Import &amp; save tasks
            </Button>
          </div>
        </div>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white shadow-card">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <h2 className="font-semibold text-slate-900">Imported chats</h2>
            {unprocessedCount > 0 && (
              <p className="text-sm text-slate-500">{unprocessedCount} unprocessed</p>
            )}
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-12">
            <Spinner size="lg" />
          </div>
        ) : chats.length === 0 ? (
          <div className="py-12 text-center">
            <MessageCircle className="mx-auto h-8 w-8 text-slate-300" />
            <p className="mt-2 text-sm text-slate-500">No chats imported yet.</p>
          </div>
        ) : (
          <ul className="divide-y divide-slate-100">
            {chats.map((chat) => (
              <li
                key={chat.id}
                className="flex flex-col gap-3 px-6 py-4 sm:flex-row sm:items-center sm:justify-between"
              >
                <div className="min-w-0 flex-1">
                  <p className="truncate font-medium text-slate-900">{chat.title}</p>
                  <p className="text-sm text-slate-500">
                    {chat.messageCount} messages
                    {chat.participants?.length > 0 && ` · ${chat.participants.length} participants`}
                  </p>
                  {chat.preview && (
                    <p className="mt-1 line-clamp-2 text-sm text-slate-600">{chat.preview}</p>
                  )}
                </div>
                <div className="flex shrink-0 items-center gap-2">
                  {chat.processed ? (
                    <span className="rounded-full bg-emerald-100 px-2.5 py-0.5 text-xs font-medium text-emerald-800">
                      Processed
                    </span>
                  ) : (
                    <Button
                      variant="secondary"
                      className="!py-2 !text-xs"
                      loading={processingId === chat.id}
                      onClick={() => handleProcess(chat.id)}
                    >
                      <Wand2 className="h-4 w-4" />
                      Generate tasks
                    </Button>
                  )}
                  <Button
                    variant="ghost"
                    className="!px-2 !py-2 text-slate-400 hover:text-red-600"
                    onClick={() => handleDelete(chat.id)}
                    aria-label="Delete chat"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
