import { Badge } from '@/components/ui/badge'
import { ComplaintStatus } from '@/types/enums'
import { titleCase } from '@/utils/formatters'

const STATUS_VARIANT: Record<ComplaintStatus, 'warning' | 'info' | 'success'> = {
  [ComplaintStatus.OPEN]: 'warning',
  [ComplaintStatus.IN_PROGRESS]: 'info',
  [ComplaintStatus.RESOLVED]: 'success',
}

export function StatusBadge({ status }: { status: ComplaintStatus }) {
  return <Badge variant={STATUS_VARIANT[status]}>{titleCase(status)}</Badge>
}
