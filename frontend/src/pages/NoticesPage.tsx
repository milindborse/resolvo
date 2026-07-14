import { useAuth } from '@/hooks/useAuth'
import { UserRole } from '@/types/enums'
import { AdminNoticesPage } from '@/features/notices/pages/AdminNoticesPage'
import { NoticeBoardPage } from '@/features/notices/pages/NoticeBoardPage'

export function NoticesPage() {
  const { user } = useAuth()
  return user?.role === UserRole.ADMIN ? <AdminNoticesPage /> : <NoticeBoardPage />
}
