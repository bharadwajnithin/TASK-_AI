import { Link } from 'react-router-dom';
import { Sparkles } from 'lucide-react';

export default function AuthLayout({ children, title, subtitle }) {
  return (
    <div className="flex min-h-screen">
      <div className="auth-gradient hidden w-1/2 flex-col justify-between p-12 text-white lg:flex">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/10 backdrop-blur">
            <Sparkles className="h-6 w-6" />
          </div>
          <span className="text-xl font-bold">OMNITASK</span>
        </div>
        <div>
          <h1 className="text-4xl font-bold leading-tight">
            Turn client chats into structured tasks
          </h1>
          <p className="mt-4 max-w-md text-lg text-indigo-200">
            Import emails and WhatsApp conversations. AI extracts deadlines, priorities,
            and actionable tasks automatically.
          </p>
        </div>
        <p className="text-sm text-indigo-300">© 2026 OMNITASK</p>
      </div>

      <div className="flex w-full flex-col justify-center px-6 py-12 lg:w-1/2 lg:px-16">
        <div className="mb-8 lg:hidden">
          <Link to="/" className="flex items-center gap-2 text-brand-600">
            <Sparkles className="h-6 w-6" />
            <span className="font-bold">OMNITASK</span>
          </Link>
        </div>
        <div className="mx-auto w-full max-w-md">
          <h2 className="text-2xl font-bold text-slate-900">{title}</h2>
          {subtitle && <p className="mt-2 text-slate-500">{subtitle}</p>}
          <div className="mt-8">{children}</div>
        </div>
      </div>
    </div>
  );
}
