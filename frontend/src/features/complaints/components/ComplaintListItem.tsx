import { Link } from 'react-router-dom'
import { MapPin } from 'lucide-react'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { OverdueBadge } from '@/components/shared/OverdueBadge'
import { timeAgo, titleCase } from '@/utils/formatters'
import type { ComplaintSummary } from '@/types/complaint'

export function ComplaintListItem({ complaint }: { complaint: ComplaintSummary }) {
  return (
    <Link
      to={`/complaints/${complaint.id}`}
      className="flex flex-col gap-2 rounded-lg border border-border p-4 transition-colors hover:border-primary/40 hover:bg-accent/40 sm:flex-row sm:items-center sm:justify-between"
    >
      <div className="min-w-0 space-y-1">
        <div className="flex flex-wrap items-center gap-2">
          <p className="truncate font-medium">{complaint.title}</p>
          <OverdueBadge overdue={complaint.overdue} />
        </div>
        <p className="flex items-center gap-1 text-xs text-muted-foreground">
          <MapPin className="h-3 w-3" /> {complaint.flatNumber} · {titleCase(complaint.category)} · {timeAgo(complaint.createdAt)}
        </p>
      </div>
      <div className="flex shrink-0 items-center gap-2">
        <StatusBadge status={complaint.status} />
      </div>
    </Link>
  )
}
