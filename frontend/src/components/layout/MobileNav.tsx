import { NavLink } from 'react-router-dom'
import { NAV_ITEMS } from '@/constants/navigation'
import { useAuth } from '@/hooks/useAuth'
import { cn } from '@/utils/cn'

export function MobileNav() {
  const { user } = useAuth()
  if (!user) return null
  const items = NAV_ITEMS.filter((item) => item.roles.includes(user.role))

  return (
    <nav className="fixed inset-x-0 bottom-0 z-40 flex border-t border-border bg-card md:hidden">
      {items.map(({ label, path, icon: Icon }) => (
        <NavLink
          key={path}
          to={path}
          className={({ isActive }) =>
            cn(
              'flex flex-1 flex-col items-center gap-0.5 py-2 text-[11px] font-medium text-muted-foreground',
              isActive && 'text-primary',
            )
          }
        >
          <Icon className="h-5 w-5" />
          {label}
        </NavLink>
      ))}
    </nav>
  )
}
