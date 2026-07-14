import { Check, Circle } from 'lucide-react'
import { Skeleton } from '@/components/ui/skeleton'
import { EmptyState } from '@/components/shared/EmptyState'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { formatDateTime } from '@/utils/formatters'
import type { ComplaintHistoryEntry } from '@/types/complaint'

export function ComplaintTimeline({ history, isLoading }: { history?: ComplaintHistoryEntry[]; isLoading: boolean }) {
  if (isLoading) {
    return (
      <div className="space-y-4">
        {Array.from({ length: 3 }).map((_, i) => (
          <Skeleton key={i} className="h-16 w-full" />
        ))}
      </div>
    )
  }

  if (!history || history.length === 0) {
    return <EmptyState title="No history yet" className="py-8" />
  }

  return (
    <ol className="relative space-y-6 border-l border-border pl-6">
      {history.map((entry, index) => (
        <li key={entry.id} className="relative">
          <span className="absolute -left-[31px] flex h-5 w-5 items-center justify-center rounded-full bg-primary text-primary-foreground">
            {index === history.length - 1 ? <Check className="h-3 w-3" /> : <Circle className="h-2 w-2 fill-current" />}
          </span>
          <div className="flex flex-wrap items-center gap-2">
            <StatusBadge status={entry.newStatus} />
            <span className="text-xs text-muted-foreground">by {entry.actorName}</span>
          </div>
          {entry.remarks && <p className="mt-1 text-sm text-foreground">{entry.remarks}</p>}
          <p className="mt-1 text-xs text-muted-foreground">{formatDateTime(entry.changedAt)}</p>
        </li>
      ))}
    </ol>
  )
}
