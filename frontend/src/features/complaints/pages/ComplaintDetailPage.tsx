import { useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { ArrowLeft, MapPin, Pencil, RefreshCcw, User } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorState } from '@/components/shared/ErrorState'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { PriorityBadge } from '@/components/shared/PriorityBadge'
import { OverdueBadge } from '@/components/shared/OverdueBadge'
import { ComplaintTimeline } from '@/features/complaints/components/ComplaintTimeline'
import { UpdateStatusDialog } from '@/features/complaints/components/UpdateStatusDialog'
import { UpdatePriorityDialog } from '@/features/complaints/components/UpdatePriorityDialog'
import { useComplaintDetail, useComplaintHistory } from '@/hooks/useComplaints'
import { useAuth } from '@/hooks/useAuth'
import { UserRole } from '@/types/enums'
import { formatDateTime, titleCase } from '@/utils/formatters'

export function ComplaintDetailPage() {
  const { id } = useParams<{ id: string }>()
  const complaintId = Number(id)
  const { user } = useAuth()
  const isAdmin = user?.role === UserRole.ADMIN

  const detailQuery = useComplaintDetail(complaintId)
  const historyQuery = useComplaintHistory(complaintId)

  const [statusDialogOpen, setStatusDialogOpen] = useState(false)
  const [priorityDialogOpen, setPriorityDialogOpen] = useState(false)

  if (detailQuery.isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-40" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (detailQuery.isError || !detailQuery.data) {
    return <ErrorState onRetry={() => detailQuery.refetch()} title="Could not load this complaint" />
  }

  const complaint = detailQuery.data

  return (
    <div className="space-y-5">
      <Link to="/complaints" className="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground">
        <ArrowLeft className="h-3.5 w-3.5" /> Back to complaints
      </Link>

      <div className="grid gap-5 lg:grid-cols-3">
        <div className="space-y-5 lg:col-span-2">
          <Card>
            <CardHeader>
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <div className="flex flex-wrap items-center gap-2">
                    <CardTitle className="text-xl">{complaint.title}</CardTitle>
                    <OverdueBadge overdue={complaint.overdue} />
                  </div>
                  <p className="mt-1 flex items-center gap-1 text-xs text-muted-foreground">
                    <MapPin className="h-3 w-3" /> {complaint.flatNumber} &middot;{' '}
                    <User className="h-3 w-3" /> {complaint.residentName}
                  </p>
                </div>
                <div className="flex gap-2">
                  {isAdmin && <PriorityBadge priority={complaint.priority} />}
                  {isAdmin && complaint.suggestedPriority && (
                    <span className="inline-flex items-center gap-1 rounded-md border border-dashed border-primary/40 px-2 py-0.5 text-xs text-primary">
                      AI Suggested: {complaint.suggestedPriority}
                    </span>
                  )}
                  <StatusBadge status={complaint.status} />
                </div>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <p className="mb-1 text-xs font-medium uppercase text-muted-foreground">Category</p>
                <p className="text-sm">{titleCase(complaint.category)}</p>
              </div>
              <div>
                <p className="mb-1 text-xs font-medium uppercase text-muted-foreground">Description</p>
                <p className="whitespace-pre-wrap text-sm">{complaint.description}</p>
              </div>
              {complaint.imageUrl && (
                <div>
                  <p className="mb-1 text-xs font-medium uppercase text-muted-foreground">Photo</p>
                  <img
                    src={complaint.imageUrl}
                    alt="Complaint attachment"
                    className="max-h-80 rounded-lg border border-border object-cover"
                  />
                </div>
              )}
              <p className="text-xs text-muted-foreground">
                Raised {formatDateTime(complaint.createdAt)} · Last updated {formatDateTime(complaint.updatedAt)}
              </p>

              {isAdmin && (
                <div className="flex flex-wrap gap-2 border-t border-border pt-4">
                  <Button size="sm" variant="outline" onClick={() => setStatusDialogOpen(true)}>
                    <RefreshCcw className="h-3.5 w-3.5" /> Update Status
                  </Button>
                  <Button size="sm" variant="outline" onClick={() => setPriorityDialogOpen(true)}>
                    <Pencil className="h-3.5 w-3.5" /> Update Priority
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">History</CardTitle>
          </CardHeader>
          <CardContent>
            <ComplaintTimeline history={historyQuery.data} isLoading={historyQuery.isLoading} />
          </CardContent>
        </Card>
      </div>

      {isAdmin && (
        <>
          <UpdateStatusDialog
            complaintId={complaint.id}
            currentStatus={complaint.status}
            open={statusDialogOpen}
            onOpenChange={setStatusDialogOpen}
          />
          <UpdatePriorityDialog
            complaintId={complaint.id}
            currentPriority={complaint.priority}
            suggestedPriority={complaint.suggestedPriority}
            open={priorityDialogOpen}
            onOpenChange={setPriorityDialogOpen}
          />
        </>
      )}
    </div>
  )
}
