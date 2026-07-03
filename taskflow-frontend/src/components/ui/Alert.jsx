import { AlertCircle, CheckCircle2, Info } from 'lucide-react';

const styles = {
  error: 'border-red-200 bg-red-50 text-red-800',
  success: 'border-emerald-200 bg-emerald-50 text-emerald-800',
  info: 'border-blue-200 bg-blue-50 text-blue-800',
};

const icons = {
  error: AlertCircle,
  success: CheckCircle2,
  info: Info,
};

export default function Alert({ type = 'error', children, onClose, className = '' }) {
  const Icon = icons[type];
  return (
    <div
      className={`flex items-start gap-3 rounded-lg border px-4 py-3 text-sm ${styles[type]} ${className}`}
      role="alert"
    >
      <Icon className="mt-0.5 h-4 w-4 shrink-0" />
      <div className="flex-1">{children}</div>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          className="shrink-0 opacity-60 hover:opacity-100"
          aria-label="Dismiss"
        >
          ×
        </button>
      )}
    </div>
  );
}
