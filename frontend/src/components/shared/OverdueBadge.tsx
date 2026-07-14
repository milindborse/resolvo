import { AlertCircle } from 'lucide-react'
import { Badge } from '@/components/ui/badge'

export function OverdueBadge({ overdue }: { overdue: boolean }) {
  if (!overdue) return null
  return (
    <Badge variant="destructive">
      <AlertCircle className="h-3 w-3" /> Overdue
    </Badge>
  )
}
