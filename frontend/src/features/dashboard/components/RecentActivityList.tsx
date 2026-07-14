import { Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { EmptyState } from '@/components/shared/EmptyState'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { timeAgo } from '@/utils/formatters'
import type { RecentComplaint } from '@/types/dashboard'

export function RecentActivityList({
  title,
  data,
  isLoading,
}: {
  title: string
  data?: RecentComplaint[]
  isLoading: boolean
}) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">{title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-1">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-12 w-full" />)
        ) : !data || data.length === 0 ? (
          <EmptyState title="Nothing here yet" className="py-10" />
        ) : (
          data.map((item) => (
            <Link
              key={item.id}
              to={`/complaints/${item.id}`}
              className="flex items-center justify-between gap-3 rounded-md px-2 py-2.5 text-sm transition-colors hover:bg-accent"
            >
              <div className="min-w-0">
                <p className="truncate font-medium">{item.title}</p>
                <p className="truncate text-xs text-muted-foreground">
                  {item.residentName} · {item.flatNumber} · {timeAgo(item.updatedAt)}
                </p>
              </div>
              <StatusBadge status={item.status} />
            </Link>
          ))
        )}
      </CardContent>
    </Card>
  )
}
