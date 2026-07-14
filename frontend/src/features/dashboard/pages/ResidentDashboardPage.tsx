import { Link } from 'react-router-dom'
import { Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { EmptyState } from '@/components/shared/EmptyState'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorState } from '@/components/shared/ErrorState'
import { ResidentSummaryCards } from '@/features/dashboard/components/ResidentSummaryCards'
import { ComplaintListItem } from '@/features/complaints/components/ComplaintListItem'
import { NoticeCard } from '@/features/notices/components/NoticeCard'
import { useMyComplaints } from '@/hooks/useComplaints'
import { useNoticeBoard } from '@/hooks/useNotices'
import { useAuth } from '@/hooks/useAuth'

export function ResidentDashboardPage() {
  const { user } = useAuth()
  const complaintsQuery = useMyComplaints(0)
  const noticesQuery = useNoticeBoard(0)

  const complaints = complaintsQuery.data?.content ?? []
  const notices = noticesQuery.data?.content?.slice(0, 3) ?? []

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Welcome back, {user?.fullName.split(' ')[0]}</h1>
          <p className="text-sm text-muted-foreground">Here's what's happening with your complaints.</p>
        </div>
        <Button asChild>
          <Link to="/complaints/new">
            <Plus className="h-4 w-4" /> Raise a Complaint
          </Link>
        </Button>
      </div>

      <ResidentSummaryCards complaints={complaints} isLoading={complaintsQuery.isLoading} />

      <div className="grid gap-4 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="text-base">Recent Complaints</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {complaintsQuery.isLoading ? (
              Array.from({ length: 3 }).map((_, i) => <Skeleton key={i} className="h-16 w-full" />)
            ) : complaintsQuery.isError ? (
              <ErrorState onRetry={() => complaintsQuery.refetch()} />
            ) : complaints.length === 0 ? (
              <EmptyState
                title="No complaints yet"
                description="Raise your first complaint and track it right here."
                action={
                  <Button asChild size="sm">
                    <Link to="/complaints/new">Raise a Complaint</Link>
                  </Button>
                }
              />
            ) : (
              complaints.slice(0, 5).map((c) => <ComplaintListItem key={c.id} complaint={c} />)
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Notice Board</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {noticesQuery.isLoading ? (
              Array.from({ length: 2 }).map((_, i) => <Skeleton key={i} className="h-24 w-full" />)
            ) : notices.length === 0 ? (
              <EmptyState title="No notices yet" className="py-8" />
            ) : (
              notices.map((notice) => <NoticeCard key={notice.id} notice={notice} />)
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
