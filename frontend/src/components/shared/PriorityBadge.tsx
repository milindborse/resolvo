import { Badge } from '@/components/ui/badge'
import { ComplaintPriority } from '@/types/enums'
import { titleCase } from '@/utils/formatters'

const PRIORITY_VARIANT: Record<ComplaintPriority, 'secondary' | 'warning' | 'destructive'> = {
  [ComplaintPriority.LOW]: 'secondary',
  [ComplaintPriority.MEDIUM]: 'warning',
  [ComplaintPriority.HIGH]: 'destructive',
}

export function PriorityBadge({ priority }: { priority: ComplaintPriority }) {
  return <Badge variant={PRIORITY_VARIANT[priority]}>{titleCase(priority)}</Badge>
}
