import { LayoutDashboard, MessageSquareWarning, Megaphone, UserCircle } from 'lucide-react'
import { UserRole } from '@/types/enums'

export interface NavItem {
  label: string
  path: string
  icon: typeof LayoutDashboard
  roles: UserRole[]
}

export const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard, roles: [UserRole.ADMIN, UserRole.RESIDENT] },
  { label: 'Complaints', path: '/complaints', icon: MessageSquareWarning, roles: [UserRole.ADMIN, UserRole.RESIDENT] },
  { label: 'Notices', path: '/notices', icon: Megaphone, roles: [UserRole.ADMIN, UserRole.RESIDENT] },
  { label: 'Profile', path: '/profile', icon: UserCircle, roles: [UserRole.ADMIN, UserRole.RESIDENT] },
]
