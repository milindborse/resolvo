import { useLocation } from 'react-router-dom'
import { ChevronRight } from 'lucide-react'
import { ThemeToggle } from '@/components/shared/ThemeToggle'
import { UserMenu } from '@/components/layout/UserMenu'
import { NotificationPanel } from '@/components/layout/NotificationPanel'
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
        <NotificationPanel />
        <ThemeToggle />
        <UserMenu />
      </div>
    </header>
  )
}
