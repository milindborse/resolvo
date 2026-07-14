import { z } from 'zod'

export const noticeFormSchema = z.object({
  title: z.string().min(3, 'Title must be at least 3 characters').max(150),
  body: z.string().min(5, 'Body must be at least 5 characters').max(3000),
  important: z.boolean(),
  pinned: z.boolean(),
})
export type NoticeFormValues = z.infer<typeof noticeFormSchema>
