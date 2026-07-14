import type { ComplaintCategory, ComplaintPriority, ComplaintStatus } from './enums'

export interface ComplaintSummary {
  id: number
  title: string
  category: ComplaintCategory
  status: ComplaintStatus
  priority: ComplaintPriority
  overdue: boolean
  closed: boolean
  residentName: string
  flatNumber: string
  createdAt: string
  updatedAt: string
}

export interface ComplaintDetail {
  id: number
  title: string
  description: string
  category: ComplaintCategory
  status: ComplaintStatus
  priority: ComplaintPriority
  imageUrl: string | null
  closed: boolean
  overdue: boolean
  residentId: number
  residentName: string
  flatNumber: string
  createdAt: string
  updatedAt: string
}

export interface ComplaintHistoryEntry {
  id: number
  previousStatus: ComplaintStatus | null
  newStatus: ComplaintStatus
  actorName: string
  remarks: string | null
  changedAt: string
}

export interface ComplaintCreateRequest {
  title: string
  description: string
  category: ComplaintCategory
  image?: File | null
}

export interface ComplaintStatusUpdateRequest {
  newStatus: ComplaintStatus
  remarks?: string
}

export interface ComplaintPriorityUpdateRequest {
  priority: ComplaintPriority
}

export interface ComplaintSearchFilters {
  status?: ComplaintStatus
  priority?: ComplaintPriority
  category?: ComplaintCategory
  fromDate?: string
  toDate?: string
  residentName?: string
  overdue?: boolean
  keyword?: string
  page?: number
  size?: number
  sort?: string
}
