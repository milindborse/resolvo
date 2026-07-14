export type NotificationType = 'COMPLAINT_STATUS_CHANGED' | 'COMPLAINT_OVERDUE' | 'NOTICE_PUBLISHED' | 'COMPLAINT_CREATED'

export interface Notification {
  id: number
  userId: number
  title: string
  message: string
  type: NotificationType
  referenceId: number | null
  read: boolean
  createdAt: string
  updatedAt: string
}
