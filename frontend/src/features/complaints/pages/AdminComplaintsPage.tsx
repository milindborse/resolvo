import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { TableSkeleton } from '@/components/shared/TableSkeleton'
import { EmptyState } from '@/components/shared/EmptyState'
import { ErrorState } from '@/components/shared/ErrorState'
import { Pagination } from '@/components/shared/Pagination'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { PriorityBadge } from '@/components/shared/PriorityBadge'
import { OverdueBadge } from '@/components/shared/OverdueBadge'
import { ComplaintFilterPanel } from '@/features/complaints/components/ComplaintFilterPanel'
import { useAdminComplaints } from '@/hooks/useComplaints'
import { formatDate, titleCase } from '@/utils/formatters'
import type { ComplaintSearchFilters } from '@/types/complaint'

export function AdminComplaintsPage() {
  const navigate = useNavigate()
  const [filters, setFilters] = useState<ComplaintSearchFilters>({ page: 0, size: 10, sort: 'createdAt,desc' })
  const { data, isLoading, isError, refetch } = useAdminComplaints(filters)

  return (
    <div className="space-y-4">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Manage Complaints</h1>
        <p className="text-sm text-muted-foreground">Search, filter, and update complaints across the society.</p>
      </div>

      <ComplaintFilterPanel filters={filters} onChange={setFilters} />

      {isLoading ? (
        <TableSkeleton rows={6} cols={6} />
      ) : isError ? (
        <ErrorState onRetry={() => refetch()} />
      ) : !data || data.content.length === 0 ? (
        <EmptyState title="No complaints match these filters" description="Try adjusting or clearing your filters." />
      ) : (
        <>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Title</TableHead>
                <TableHead>Resident</TableHead>
                <TableHead>Category</TableHead>
                <TableHead>Priority</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Created</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.content.map((c) => (
                <TableRow key={c.id} className="cursor-pointer" onClick={() => navigate(`/complaints/${c.id}`)}>
                  <TableCell className="max-w-[220px] truncate font-medium">
                    <div className="flex items-center gap-2">
                      {c.title}
                      <OverdueBadge overdue={c.overdue} />
                    </div>
                  </TableCell>
                  <TableCell>
                    {c.residentName} <span className="text-muted-foreground">· {c.flatNumber}</span>
                  </TableCell>
                  <TableCell>{titleCase(c.category)}</TableCell>
                  <TableCell>
                    <PriorityBadge priority={c.priority} />
                  </TableCell>
                  <TableCell>
                    <StatusBadge status={c.status} />
                  </TableCell>
                  <TableCell className="text-muted-foreground">{formatDate(c.createdAt)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <Pagination
            page={filters.page ?? 0}
            totalPages={data.totalPages}
            onPageChange={(page) => setFilters((f) => ({ ...f, page }))}
          />
        </>
      )}
    </div>
  )
}
