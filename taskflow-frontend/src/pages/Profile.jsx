import { Shield, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user } = useAuth();

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Profile</h1>
        <p className="mt-1 text-slate-500">Your account information</p>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white p-6 shadow-card">
        <div className="flex items-center gap-4">
          {user?.profilePictureUrl ? (
            <img
              src={user.profilePictureUrl}
              alt=""
              className="h-16 w-16 rounded-full object-cover"
            />
          ) : (
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-brand-100 text-brand-700">
              <User className="h-8 w-8" />
            </div>
          )}
          <div>
            <h2 className="text-xl font-semibold text-slate-900">{user?.fullName}</h2>
            <p className="text-slate-500">{user?.email}</p>
          </div>
        </div>

        <dl className="mt-6 space-y-4 border-t border-slate-100 pt-6">
          <div className="flex justify-between">
            <dt className="text-sm text-slate-500">Role</dt>
            <dd className="flex items-center gap-1 text-sm font-medium text-slate-900">
              <Shield className="h-4 w-4 text-brand-500" />
              {user?.role}
            </dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-sm text-slate-500">Sign-in method</dt>
            <dd className="text-sm font-medium text-slate-900">
              {user?.oauthUser ? 'Google OAuth' : 'Email & password'}
            </dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-sm text-slate-500">Member since</dt>
            <dd className="text-sm font-medium text-slate-900">
              {user?.createdAt
                ? new Date(user.createdAt).toLocaleDateString()
                : '—'}
            </dd>
          </div>
        </dl>
      </div>
    </div>
  );
}
