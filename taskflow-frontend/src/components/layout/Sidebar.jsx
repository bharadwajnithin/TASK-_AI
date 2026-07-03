import { NavLink } from 'react-router-dom';
import {
  BarChart3,
  CheckSquare,
  Inbox,
  LayoutDashboard,
  Mail,
  MessageCircle,
  Sparkles,
} from 'lucide-react';

const navItems = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/tasks', label: 'Tasks', icon: CheckSquare },
  { to: '/ai-extract', label: 'AI Extract', icon: Sparkles },
  { to: '/inbox', label: 'AI Inbox', icon: Inbox, disabled: true },
  { to: '/gmail', label: 'Gmail', icon: Mail },
  { to: '/whatsapp', label: 'WhatsApp Import', icon: MessageCircle },
  { to: '/analytics', label: 'Analytics', icon: BarChart3 },
];

export default function Sidebar({ mobileOpen, onClose }) {
  return (
    <>
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-slate-900/50 lg:hidden"
          onClick={onClose}
          aria-hidden="true"
        />
      )}
      <aside
        className={`fixed inset-y-0 left-0 z-50 flex w-64 flex-col border-r border-slate-200 bg-white transition-transform lg:static lg:translate-x-0 ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-16 items-center gap-2 border-b border-slate-100 px-5">
          <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-brand-600 text-white">
            <Sparkles className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-bold text-slate-900">OMNITASK</p>
            <p className="text-xs text-slate-500">Productivity SaaS</p>
          </div>
        </div>

        <nav className="flex-1 space-y-1 p-3">
          {navItems.map(({ to, label, icon: Icon, disabled }) =>
            disabled ? (
              <span
                key={to}
                className="flex cursor-not-allowed items-center gap-3 rounded-lg px-3 py-2.5 text-sm text-slate-400"
                title="Coming in a future phase"
              >
                <Icon className="h-5 w-5" />
                {label}
                <span className="ml-auto rounded bg-slate-100 px-1.5 py-0.5 text-[10px] font-medium">
                  Soon
                </span>
              </span>
            ) : (
              <NavLink
                key={to}
                to={to}
                onClick={onClose}
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition ${
                    isActive
                      ? 'bg-brand-50 text-brand-700'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                  }`
                }
              >
                <Icon className="h-5 w-5" />
                {label}
              </NavLink>
            )
          )}
        </nav>

        <div className="border-t border-slate-100 p-4">
          <p className="text-xs text-slate-500">Phase 7 — Analytics ready</p>
        </div>
      </aside>
    </>
  );
}
