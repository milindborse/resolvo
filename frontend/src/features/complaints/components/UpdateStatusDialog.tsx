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
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useUpdateComplaintStatus } from '@/hooks/useComplaints'
import { statusUpdateSchema, type StatusUpdateFormValues } from '@/features/complaints/schemas'
import { ComplaintStatus } from '@/types/enums'
import { titleCase } from '@/utils/formatters'

const NEXT_STATUS: Record<ComplaintStatus, ComplaintStatus[]> = {
  [ComplaintStatus.OPEN]: [ComplaintStatus.IN_PROGRESS],
  [ComplaintStatus.IN_PROGRESS]: [ComplaintStatus.RESOLVED, ComplaintStatus.OPEN],
  [ComplaintStatus.RESOLVED]: [],
}

interface UpdateStatusDialogProps {
  complaintId: number
  currentStatus: ComplaintStatus
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function UpdateStatusDialog({ complaintId, currentStatus, open, onOpenChange }: UpdateStatusDialogProps) {
  const updateStatus = useUpdateComplaintStatus()
  const allowedNext = NEXT_STATUS[currentStatus]

  const { control, register, handleSubmit, reset } = useForm<StatusUpdateFormValues>({
    resolver: zodResolver(statusUpdateSchema),
    defaultValues: { newStatus: allowedNext[0], remarks: '' },
  })

  const onSubmit = async (values: StatusUpdateFormValues) => {
    await updateStatus.mutateAsync({ id: complaintId, payload: values })
    reset()
    onOpenChange(false)
  }

  if (allowedNext.length === 0) return null

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <DialogHeader>
            <DialogTitle>Update Status</DialogTitle>
            <DialogDescription>Move this complaint to its next lifecycle stage.</DialogDescription>
          </DialogHeader>

          <div className="space-y-1.5">
            <Label>New Status</Label>
            <Controller
              control={control}
              name="newStatus"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {allowedNext.map((status) => (
                      <SelectItem key={status} value={status}>
                        {titleCase(status)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
          </div>

          <div className="space-y-1.5">
            <Label htmlFor="remarks">Remarks (optional)</Label>
            <Textarea id="remarks" rows={3} placeholder="Add a note for the resident..." {...register('remarks')} />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={updateStatus.isPending}>
              Update
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
