import { z } from 'zod'
import { ComplaintCategory, ComplaintPriority, ComplaintStatus } from '@/types/enums'

export const complaintCreateSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(150),
  description: z.string().min(10, 'Description must be at least 10 characters').max(2000),
  category: z.nativeEnum(ComplaintCategory, { message: 'Select a category' }),
  image: z
    .instanceof(File)
    .optional()
    .nullable()
    .refine((file) => !file || file.size <= 5 * 1024 * 1024, 'Image must be under 5MB'),
})
export type ComplaintCreateFormValues = z.infer<typeof complaintCreateSchema>

export const statusUpdateSchema = z.object({
  newStatus: z.nativeEnum(ComplaintStatus),
  remarks: z.string().max(1000).optional(),
})
export type StatusUpdateFormValues = z.infer<typeof statusUpdateSchema>

export const priorityUpdateSchema = z.object({
  priority: z.nativeEnum(ComplaintPriority),
})
export type PriorityUpdateFormValues = z.infer<typeof priorityUpdateSchema>
