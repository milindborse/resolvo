import { useEffect } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { useCreateNotice, useUpdateNotice } from '@/hooks/useNotices'
import { noticeFormSchema, type NoticeFormValues } from '@/features/notices/schemas'
import type { Notice } from '@/types/notice'

interface NoticeFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  notice?: Notice
}

export function NoticeFormDialog({ open, onOpenChange, notice }: NoticeFormDialogProps) {
  const createNotice = useCreateNotice()
  const updateNotice = useUpdateNotice()
  const isEditing = !!notice

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors },
  } = useForm<NoticeFormValues>({
    resolver: zodResolver(noticeFormSchema),
    defaultValues: { title: '', body: '', important: false, pinned: false },
  })

  useEffect(() => {
    if (open) {
      reset(
        notice
          ? { title: notice.title, body: notice.body, important: notice.important, pinned: notice.pinned }
          : { title: '', body: '', important: false, pinned: false },
      )
    }
  }, [open, notice, reset])

  const isPending = createNotice.isPending || updateNotice.isPending

  const onSubmit = async (values: NoticeFormValues) => {
    if (isEditing) {
      await updateNotice.mutateAsync({ id: notice.id, payload: values })
    } else {
      await createNotice.mutateAsync(values)
    }
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <DialogHeader>
            <DialogTitle>{isEditing ? 'Edit Notice' : 'New Notice'}</DialogTitle>
            <DialogDescription>
              {isEditing ? 'Update this notice.' : 'Created as a draft - publish it separately when ready.'}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-1.5">
            <Label htmlFor="title">Title</Label>
            <Input id="title" error={!!errors.title} {...register('title')} />
            {errors.title && <p className="text-xs text-destructive">{errors.title.message}</p>}
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="body">Body</Label>
            <Textarea id="body" rows={4} error={!!errors.body} {...register('body')} />
            {errors.body && <p className="text-xs text-destructive">{errors.body.message}</p>}
          </div>

          <div className="flex gap-6">
            <label className="flex items-center gap-2 text-sm">
              <Controller
                control={control}
                name="important"
                render={({ field }) => (
                  <input type="checkbox" checked={field.value} onChange={(e) => field.onChange(e.target.checked)} className="h-4 w-4 rounded border-input" />
                )}
              />
              Important (emails residents on publish)
            </label>
            <label className="flex items-center gap-2 text-sm">
              <Controller
                control={control}
                name="pinned"
                render={({ field }) => (
                  <input type="checkbox" checked={field.value} onChange={(e) => field.onChange(e.target.checked)} className="h-4 w-4 rounded border-input" />
                )}
              />
              Pinned
            </label>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={isPending}>
              {isEditing ? 'Save changes' : 'Create draft'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
