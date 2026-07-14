import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/hooks/useAuth'
import { FullPageSpinner } from '@/components/ui/spinner'

/** Redirects already-authenticated users away from /login instead of showing it again. */
export function GuestRoute() {
  const { isAuthenticated, isInitializing } = useAuth()
  if (isInitializing) return <FullPageSpinner />
  if (isAuthenticated) return <Navigate to="/dashboard" replace />
  return <Outlet />
}
