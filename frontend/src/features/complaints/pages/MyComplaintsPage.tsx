import { useState } from 'react'
import { Link } from 'react-router-dom'
import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { EmptyState } from '@/components/shared/EmptyState'
import { ErrorState } from '@/components/shared/ErrorState'
import { Skeleton } from '@/components/ui/skeleton'
import { Pagination } from '@/components/shared/Pagination'
import { ComplaintListItem } from '@/features/complaints/components/ComplaintListItem'
import { useMyComplaints } from '@/hooks/useComplaints'

export function MyComplaintsPage() {
  const [page, setPage] = useState(0)
  const { data, isLoading, isError, refetch } = useMyComplaints(page)

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">My Complaints</h1>
          <p className="text-sm text-muted-foreground">Track every complaint you've raised.</p>
        </div>
        <Button asChild>
          <Link to="/complaints/new">
            <Plus className="h-4 w-4" /> Raise a Complaint
          </Link>
        </Button>
      </div>

      <div className="space-y-2">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-20 w-full" />)
        ) : isError ? (
          <ErrorState onRetry={() => refetch()} />
        ) : !data || data.content.length === 0 ? (
          <EmptyState
            title="No complaints yet"
            description="Raise your first complaint to get started."
            action={
              <Button asChild size="sm">
                <Link to="/complaints/new">Raise a Complaint</Link>
              </Button>
            }
          />
        ) : (
          data.content.map((c) => <ComplaintListItem key={c.id} complaint={c} />)
        )}
      </div>

      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}
    </div>
  )
}
