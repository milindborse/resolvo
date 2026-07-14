import { useAuth } from '@/hooks/useAuth'
import { UserRole } from '@/types/enums'
import { AdminDashboardPage } from '@/features/dashboard/pages/AdminDashboardPage'
import { ResidentDashboardPage } from '@/features/dashboard/pages/ResidentDashboardPage'

export function DashboardPage() {
  const { user } = useAuth()
  return user?.role === UserRole.ADMIN ? <AdminDashboardPage /> : <ResidentDashboardPage />
}
