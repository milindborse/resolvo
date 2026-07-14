import { Search, X } from 'lucide-react'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { ComplaintCategory, ComplaintPriority, ComplaintStatus } from '@/types/enums'
import { titleCase } from '@/utils/formatters'
import type { ComplaintSearchFilters } from '@/types/complaint'

interface ComplaintFilterPanelProps {
  filters: ComplaintSearchFilters
  onChange: (filters: ComplaintSearchFilters) => void
}

const ALL = '__all__'

export function ComplaintFilterPanel({ filters, onChange }: ComplaintFilterPanelProps) {
  const update = (patch: Partial<ComplaintSearchFilters>) => onChange({ ...filters, ...patch, page: 0 })

  const hasActiveFilters =
    filters.status || filters.priority || filters.category || filters.overdue !== undefined || filters.keyword || filters.residentName

  return (
    <div className="space-y-3 rounded-xl border border-border bg-card p-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div className="relative flex-1">
          <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search title or description..."
            className="pl-8"
            defaultValue={filters.keyword ?? ''}
            onChange={(e) => update({ keyword: e.target.value || undefined })}
          />
        </div>
        <Input
          placeholder="Resident name..."
          className="sm:w-48"
          defaultValue={filters.residentName ?? ''}
          onChange={(e) => update({ residentName: e.target.value || undefined })}
        />
      </div>

      <div className="flex flex-wrap gap-2">
        <Select
          value={filters.status ?? ALL}
          onValueChange={(v) => update({ status: v === ALL ? undefined : (v as ComplaintStatus) })}
        >
          <SelectTrigger className="w-[150px]">
            <SelectValue placeholder="Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL}>All statuses</SelectItem>
            {Object.values(ComplaintStatus).map((s) => (
              <SelectItem key={s} value={s}>
                {titleCase(s)}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filters.priority ?? ALL}
          onValueChange={(v) => update({ priority: v === ALL ? undefined : (v as ComplaintPriority) })}
        >
          <SelectTrigger className="w-[150px]">
            <SelectValue placeholder="Priority" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL}>All priorities</SelectItem>
            {Object.values(ComplaintPriority).map((p) => (
              <SelectItem key={p} value={p}>
                {titleCase(p)}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filters.category ?? ALL}
          onValueChange={(v) => update({ category: v === ALL ? undefined : (v as ComplaintCategory) })}
        >
          <SelectTrigger className="w-[160px]">
            <SelectValue placeholder="Category" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL}>All categories</SelectItem>
            {Object.values(ComplaintCategory).map((c) => (
              <SelectItem key={c} value={c}>
                {titleCase(c)}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filters.overdue === undefined ? ALL : String(filters.overdue)}
          onValueChange={(v) => update({ overdue: v === ALL ? undefined : v === 'true' })}
        >
          <SelectTrigger className="w-[150px]">
            <SelectValue placeholder="Overdue" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL}>Overdue: Any</SelectItem>
            <SelectItem value="true">Overdue only</SelectItem>
            <SelectItem value="false">Not overdue</SelectItem>
          </SelectContent>
        </Select>

        <Input
          type="date"
          className="w-[150px]"
          value={filters.fromDate ?? ''}
          onChange={(e) => update({ fromDate: e.target.value || undefined })}
        />
        <Input
          type="date"
          className="w-[150px]"
          value={filters.toDate ?? ''}
          onChange={(e) => update({ toDate: e.target.value || undefined })}
        />

        {hasActiveFilters && (
          <Button variant="ghost" size="sm" onClick={() => onChange({ page: 0, size: filters.size })}>
            <X className="h-3.5 w-3.5" /> Clear filters
          </Button>
        )}
      </div>
    </div>
  )
}
