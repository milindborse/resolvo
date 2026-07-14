import { Routes, Route, Navigate } from 'react-router-dom'
import { AppLayout } from '@/layouts/AppLayout'
import { AuthLayout } from '@/layouts/AuthLayout'
import { ProtectedRoute } from '@/routes/ProtectedRoute'
import { GuestRoute } from '@/routes/GuestRoute'
import { LoginPage } from '@/features/authentication/pages/LoginPage'
import { RegisterPage } from '@/features/authentication/pages/RegisterPage'
import { DashboardPage } from '@/pages/DashboardPage'
import { ComplaintsPage } from '@/pages/ComplaintsPage'
import { NoticesPage } from '@/pages/NoticesPage'
import { NotFoundPage } from '@/pages/NotFoundPage'
import { ComplaintCreatePage } from '@/features/complaints/pages/ComplaintCreatePage'
import { ComplaintDetailPage } from '@/features/complaints/pages/ComplaintDetailPage'
import { ProfilePage } from '@/features/profile/pages/ProfilePage'
import { RoleRoute } from '@/routes/RoleRoute'
import { UserRole } from '@/types/enums'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />

      <Route element={<GuestRoute />}>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/complaints" element={<ComplaintsPage />} />
          <Route path="/complaints/:id" element={<ComplaintDetailPage />} />
          <Route path="/notices" element={<NoticesPage />} />
          <Route path="/profile" element={<ProfilePage />} />

          <Route element={<RoleRoute allowedRoles={[UserRole.RESIDENT]} />}>
            <Route path="/complaints/new" element={<ComplaintCreatePage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
