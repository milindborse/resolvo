import { useLocation } from 'react-router-dom'
import { Bell, ChevronRight } from 'lucide-react'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { UserMenu } from '@/components/layout/UserMenu'
import { NAV_ITEMS } from '@/constants/navigation'

function useBreadcrumb() {
  const { pathname } = useLocation()
  const match = NAV_ITEMS.find((item) => pathname.startsWith(item.path))
  return match?.label ?? 'Overview'
}

export function Topbar() {
  const crumb = useBreadcrumb()

  return (
    <header className="sticky top-0 z-30 flex h-14 items-center justify-between border-b border-border bg-background/80 px-4 backdrop-blur md:px-6">
      <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
        <span>Resolvo</span>
        <ChevronRight className="h-3.5 w-3.5" />
        <span className="font-medium text-foreground">{crumb}</span>
      </div>
      <div className="flex items-center gap-1">
        <button
          type="button"
          aria-label="Notifications"
          className="relative flex h-9 w-9 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
        >
          <Bell className="h-4 w-4" />
        </button>
        <ThemeToggle />
        <UserMenu />
      </div>
    </header>
  )
}
