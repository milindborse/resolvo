import { useState } from 'react'
import { Pin, PinOff, Plus, Send, SquarePen, Trash2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { EmptyState } from '@/components/shared/EmptyState'
import { ErrorState } from '@/components/shared/ErrorState'
import { Pagination } from '@/components/shared/Pagination'
import { ConfirmDialog } from '@/components/shared/ConfirmDialog'
import { NoticeCard } from '@/features/notices/components/NoticeCard'
import { NoticeFormDialog } from '@/features/notices/components/NoticeFormDialog'
import {
  useAdminNotices,
  useDeleteNotice,
  usePublishNotice,
  useSetNoticePinned,
} from '@/hooks/useNotices'
import type { Notice } from '@/types/notice'

export function AdminNoticesPage() {
  const [page, setPage] = useState(0)
  const { data, isLoading, isError, refetch } = useAdminNotices(page)

  const [formOpen, setFormOpen] = useState(false)
  const [editingNotice, setEditingNotice] = useState<Notice | undefined>(undefined)
  const [deleteTarget, setDeleteTarget] = useState<Notice | null>(null)

  const publishNotice = usePublishNotice()
  const setPinned = useSetNoticePinned()
  const deleteNotice = useDeleteNotice()

  const openCreate = () => {
    setEditingNotice(undefined)
    setFormOpen(true)
  }

  const openEdit = (notice: Notice) => {
    setEditingNotice(notice)
    setFormOpen(true)
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Manage Notices</h1>
          <p className="text-sm text-muted-foreground">Create, publish, and pin announcements for residents.</p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4" /> New Notice
        </Button>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-40 w-full" />)
        ) : isError ? (
          <ErrorState onRetry={() => refetch()} />
        ) : !data || data.content.length === 0 ? (
          <EmptyState
            title="No notices yet"
            description="Create your first notice for residents."
            className="sm:col-span-2"
            action={
              <Button size="sm" onClick={openCreate}>
                New Notice
              </Button>
            }
          />
        ) : (
          data.content.map((notice) => (
            <NoticeCard
              key={notice.id}
              notice={notice}
              actions={
                <>
                  {!notice.published && (
                    <Button size="sm" variant="outline" onClick={() => publishNotice.mutate(notice.id)} loading={publishNotice.isPending}>
                      <Send className="h-3.5 w-3.5" /> Publish
                    </Button>
                  )}
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setPinned.mutate({ id: notice.id, pinned: !notice.pinned })}
                    loading={setPinned.isPending}
                  >
                    {notice.pinned ? <PinOff className="h-3.5 w-3.5" /> : <Pin className="h-3.5 w-3.5" />}
                    {notice.pinned ? 'Unpin' : 'Pin'}
                  </Button>
                  <Button size="sm" variant="outline" onClick={() => openEdit(notice)}>
                    <SquarePen className="h-3.5 w-3.5" /> Edit
                  </Button>
                  <Button size="sm" variant="outline" className="text-destructive" onClick={() => setDeleteTarget(notice)}>
                    <Trash2 className="h-3.5 w-3.5" /> Delete
                  </Button>
                </>
              }
            />
          ))
        )}
      </div>

      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}

      <NoticeFormDialog open={formOpen} onOpenChange={setFormOpen} notice={editingNotice} />

      <ConfirmDialog
        open={!!deleteTarget}
        onOpenChange={(open) => !open && setDeleteTarget(null)}
        title="Delete this notice?"
        description="This soft-deletes the notice - it will no longer be visible to anyone, but is not permanently erased."
        confirmLabel="Delete"
        destructive
        loading={deleteNotice.isPending}
        onConfirm={() => {
          if (deleteTarget) {
            deleteNotice.mutate(deleteTarget.id, { onSuccess: () => setDeleteTarget(null) })
          }
        }}
      />
    </div>
  )
}
