import { NavLink } from 'react-router-dom'
import { ShieldCheck } from 'lucide-react'
import { NAV_ITEMS } from '@/constants/navigation'
import { useAuth } from '@/hooks/useAuth'
import { cn } from '@/utils/cn'

export function Sidebar() {
  const { user } = useAuth()
  if (!user) return null
  const items = NAV_ITEMS.filter((item) => item.roles.includes(user.role))

  return (
    <aside className="hidden w-60 shrink-0 flex-col border-r border-sidebar-border bg-sidebar md:flex">
      <div className="flex h-14 items-center gap-2 border-b border-sidebar-border px-5">
        <ShieldCheck className="h-5 w-5 text-primary" />
        <span className="font-semibold tracking-tight text-sidebar-foreground">Resolvo</span>
      </div>
      <nav className="flex-1 space-y-1 px-3 py-4">
        {items.map(({ label, path, icon: Icon }) => (
          <NavLink
            key={path}
            to={path}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-sidebar-foreground/70 transition-colors hover:bg-accent hover:text-accent-foreground',
                isActive && 'bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground',
              )
            }
          >
            <Icon className="h-4 w-4" />
            {label}
          </NavLink>
        ))}
      </nav>
      <div className="border-t border-sidebar-border p-4 text-xs text-muted-foreground">
        Resolvo &copy; {new Date().getFullYear()}
      </div>
    </aside>
  )
}
