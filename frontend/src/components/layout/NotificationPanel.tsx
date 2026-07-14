import * as DropdownMenu from '@radix-ui/react-dropdown-menu'
import { useNavigate } from 'react-router-dom'
import { Bell, Check, Info } from 'lucide-react'
import { useUnreadCount, useNotifications, useMarkRead, useMarkAllRead } from '@/hooks/useNotifications'
import { cn } from '@/utils/cn'
import { formatDateTime } from '@/utils/formatters'
import { useState } from 'react'

export function NotificationPanel() {
  const navigate = useNavigate()
  const { data: unreadCount = 0 } = useUnreadCount()
  const [page] = useState(0)
  const { data: notificationsData } = useNotifications(page)
  const markRead = useMarkRead()
  const markAllRead = useMarkAllRead()

  const notifications = notificationsData?.content ?? []

  const handleNotificationClick = async (id: number, type: string, referenceId: number | null) => {
    await markRead.mutateAsync(id)
    if (referenceId) {
      if (type.startsWith('COMPLAINT_')) {
        navigate(`/complaints/${referenceId}`)
      } else if (type === 'NOTICE_PUBLISHED') {
        navigate('/notices')
      }
    }
  }

  return (
    <DropdownMenu.Root>
      <DropdownMenu.Trigger asChild>
        <button
          type="button"
          aria-label="Notifications"
          className="relative flex h-9 w-9 items-center justify-center rounded-md text-muted-foreground transition-colors hover:bg-accent hover:text-accent-foreground"
        >
          <Bell className="h-4 w-4" />
          {unreadCount > 0 && (
            <span className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[9px] font-bold text-destructive-foreground">
              {unreadCount}
            </span>
          )}
        </button>
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal>
        <DropdownMenu.Content
          align="end"
          sideOffset={8}
          className="z-50 w-80 max-w-[calc(100vw-32px)] rounded-lg border border-border bg-popover text-popover-foreground shadow-md outline-none"
        >
          <div className="flex items-center justify-between border-b border-border px-4 py-2.5">
            <h2 className="text-sm font-semibold">Notifications</h2>
            {unreadCount > 0 && (
              <button
                type="button"
                onClick={() => markAllRead.mutate()}
                className="flex items-center gap-1 text-xs font-medium text-primary hover:underline"
              >
                <Check className="h-3.5 w-3.5" /> Mark all read
              </button>
            )}
          </div>

          <div className="max-h-[300px] overflow-y-auto">
            {notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center gap-2 p-8 text-center">
                <Info className="h-8 w-8 text-muted-foreground/50" />
                <p className="text-sm text-muted-foreground">All caught up!</p>
              </div>
            ) : (
              <div className="divide-y divide-border">
                {notifications.map((n) => (
                  <button
                    key={n.id}
                    type="button"
                    onClick={() => handleNotificationClick(n.id, n.type, n.referenceId)}
                    className={cn(
                      'flex w-full flex-col gap-1 px-4 py-3 text-left transition-colors hover:bg-accent/40',
                      !n.read && 'bg-primary/[0.02] font-medium',
                    )}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <span className={cn('text-xs', !n.read ? 'text-primary' : 'text-muted-foreground')}>
                        {n.title}
                      </span>
                      {!n.read && <span className="mt-1 h-1.5 w-1.5 shrink-0 rounded-full bg-primary" />}
                    </div>
                    <p className="text-xs text-muted-foreground line-clamp-2">{n.message}</p>
                    <span className="text-[10px] text-muted-foreground/60">{formatDateTime(n.createdAt)}</span>
                  </button>
                ))}
              </div>
            )}
          </div>
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
    </DropdownMenu.Root>
  )
}
