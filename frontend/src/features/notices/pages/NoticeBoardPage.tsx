import { useState } from 'react'
import { EmptyState } from '@/components/shared/EmptyState'
import { ErrorState } from '@/components/shared/ErrorState'
import { Skeleton } from '@/components/ui/skeleton'
import { Pagination } from '@/components/shared/Pagination'
import { NoticeCard } from '@/features/notices/components/NoticeCard'
import { useNoticeBoard } from '@/hooks/useNotices'

export function NoticeBoardPage() {
  const [page, setPage] = useState(0)
  const { data, isLoading, isError, refetch } = useNoticeBoard(page)

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Notice Board</h1>
        <p className="text-sm text-muted-foreground">Announcements from the society administration.</p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        {isLoading ? (
          Array.from({ length: 4 }).map((_, i) => <Skeleton key={i} className="h-32 w-full" />)
        ) : isError ? (
          <ErrorState onRetry={() => refetch()} />
        ) : !data || data.content.length === 0 ? (
          <EmptyState title="No notices yet" description="Check back later for announcements." className="sm:col-span-2" />
        ) : (
          data.content.map((notice) => <NoticeCard key={notice.id} notice={notice} />)
        )}
      </div>

      {data && <Pagination page={page} totalPages={data.totalPages} onPageChange={setPage} />}
    </div>
  )
}
