import { Pin, Megaphone } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { formatDate } from '@/utils/formatters'
import type { Notice } from '@/types/notice'

export function NoticeCard({ notice, actions }: { notice: Notice; actions?: React.ReactNode }) {
  return (
    <Card className={notice.pinned ? 'border-primary/40 bg-primary/[0.03]' : ''}>
      <CardContent className="flex flex-col gap-2 p-5">
        <div className="flex items-start justify-between gap-2">
          <div className="flex items-center gap-2">
            {notice.pinned && <Pin className="h-4 w-4 shrink-0 text-primary" />}
            <h3 className="font-semibold leading-snug">{notice.title}</h3>
          </div>
          <div className="flex shrink-0 gap-1.5">
            {notice.important && (
              <Badge variant="destructive">
                <Megaphone className="h-3 w-3" /> Important
              </Badge>
            )}
            {!notice.published && <Badge variant="secondary">Draft</Badge>}
          </div>
        </div>
        <p className="whitespace-pre-wrap text-sm text-muted-foreground">{notice.body}</p>
        <p className="text-xs text-muted-foreground">
          {notice.published ? `Published ${formatDate(notice.publishedAt)}` : `Created ${formatDate(notice.createdAt)}`}
        </p>
        {actions && <div className="mt-2 flex flex-wrap gap-2">{actions}</div>}
      </CardContent>
    </Card>
  )
}
