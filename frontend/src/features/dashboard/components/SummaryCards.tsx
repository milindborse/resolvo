import type { LucideIcon } from 'lucide-react'
import { AlertTriangle, CheckCircle2, Clock, ListChecks, TrendingUp } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { cn } from '@/utils/cn'
import type { DashboardSummary } from '@/types/dashboard'

interface CardConfig {
  label: string
  key: keyof DashboardSummary
  icon: LucideIcon
  tone: string
}

const CARDS: CardConfig[] = [
  { label: 'Total Complaints', key: 'totalComplaints', icon: ListChecks, tone: 'text-blue-600 bg-blue-500/10' },
  { label: 'Open', key: 'openComplaints', icon: Clock, tone: 'text-amber-600 bg-amber-500/10' },
  { label: 'Resolved', key: 'resolvedComplaints', icon: CheckCircle2, tone: 'text-emerald-600 bg-emerald-500/10' },
  { label: 'High Priority', key: 'highPriorityComplaints', icon: TrendingUp, tone: 'text-rose-600 bg-rose-500/10' },
  { label: 'Overdue', key: 'overdueComplaints', icon: AlertTriangle, tone: 'text-red-600 bg-red-500/10' },
]

export function SummaryCards({ data, isLoading }: { data?: DashboardSummary; isLoading: boolean }) {
  return (
    <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
      {CARDS.map(({ label, key, icon: Icon, tone }) => (
        <Card key={key}>
          <CardContent className="flex flex-col gap-3 p-5">
            <div className={cn('flex h-9 w-9 items-center justify-center rounded-lg', tone)}>
              <Icon className="h-4.5 w-4.5" />
            </div>
            {isLoading ? (
              <Skeleton className="h-7 w-14" />
            ) : (
              <p className="text-2xl font-semibold tracking-tight">{data?.[key] ?? 0}</p>
            )}
            <p className="text-xs font-medium text-muted-foreground">{label}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
