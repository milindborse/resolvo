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
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useUpdateComplaintPriority } from '@/hooks/useComplaints'
import { priorityUpdateSchema, type PriorityUpdateFormValues } from '@/features/complaints/schemas'
import { ComplaintPriority } from '@/types/enums'
import { titleCase } from '@/utils/formatters'

interface UpdatePriorityDialogProps {
  complaintId: number
  currentPriority: ComplaintPriority
  suggestedPriority?: ComplaintPriority | null
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function UpdatePriorityDialog({ complaintId, currentPriority, suggestedPriority, open, onOpenChange }: UpdatePriorityDialogProps) {
  const updatePriority = useUpdateComplaintPriority()

  const { control, handleSubmit } = useForm<PriorityUpdateFormValues>({
    resolver: zodResolver(priorityUpdateSchema),
    defaultValues: { priority: currentPriority },
  })

  const onSubmit = async (values: PriorityUpdateFormValues) => {
    await updatePriority.mutateAsync({ id: complaintId, payload: values })
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <DialogHeader>
            <DialogTitle>Update Priority</DialogTitle>
            <DialogDescription>
              Set how urgently this complaint should be handled.
              {suggestedPriority && (
                <span className="block mt-1 text-primary">
                  AI Suggested Priority: <strong>{titleCase(suggestedPriority)}</strong>
                </span>
              )}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-1.5">
            <Label>Priority</Label>
            <Controller
              control={control}
              name="priority"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {Object.values(ComplaintPriority).map((p) => (
                      <SelectItem key={p} value={p}>
                        {titleCase(p)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={updatePriority.isPending}>
              Update
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
