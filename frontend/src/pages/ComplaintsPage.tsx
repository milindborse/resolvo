import { useAuth } from '@/hooks/useAuth'
import { UserRole } from '@/types/enums'
import { AdminComplaintsPage } from '@/features/complaints/pages/AdminComplaintsPage'
import { MyComplaintsPage } from '@/features/complaints/pages/MyComplaintsPage'

export function ComplaintsPage() {
  const { user } = useAuth()
  return user?.role === UserRole.ADMIN ? <AdminComplaintsPage /> : <MyComplaintsPage />
}
