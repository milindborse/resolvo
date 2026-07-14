import { AlertCircle, CheckCircle2, Clock, ListChecks } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { ComplaintStatus } from '@/types/enums'
import type { ComplaintSummary } from '@/types/complaint'

export function ResidentSummaryCards({ complaints, isLoading }: { complaints: ComplaintSummary[]; isLoading: boolean }) {
  const total = complaints.length
  const open = complaints.filter((c) => c.status === ComplaintStatus.OPEN).length
  const inProgress = complaints.filter((c) => c.status === ComplaintStatus.IN_PROGRESS).length
  const resolved = complaints.filter((c) => c.status === ComplaintStatus.RESOLVED).length

  const cards = [
    { label: 'Total Raised', value: total, icon: ListChecks, tone: 'text-blue-600 bg-blue-500/10' },
    { label: 'Open', value: open, icon: AlertCircle, tone: 'text-amber-600 bg-amber-500/10' },
    { label: 'In Progress', value: inProgress, icon: Clock, tone: 'text-indigo-600 bg-indigo-500/10' },
    { label: 'Resolved', value: resolved, icon: CheckCircle2, tone: 'text-emerald-600 bg-emerald-500/10' },
  ]

  return (
    <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
      {cards.map(({ label, value, icon: Icon, tone }) => (
        <Card key={label}>
          <CardContent className="flex flex-col gap-3 p-5">
            <div className={`flex h-9 w-9 items-center justify-center rounded-lg ${tone}`}>
              <Icon className="h-4.5 w-4.5" />
            </div>
            {isLoading ? <Skeleton className="h-7 w-10" /> : <p className="text-2xl font-semibold">{value}</p>}
            <p className="text-xs font-medium text-muted-foreground">{label}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
