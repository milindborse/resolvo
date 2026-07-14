import type { ComplaintCategory, ComplaintPriority, ComplaintStatus } from './enums'

export interface DashboardSummary {
  totalComplaints: number
  openComplaints: number
  resolvedComplaints: number
  highPriorityComplaints: number
  overdueComplaints: number
}

export interface CategoryCount {
  category: ComplaintCategory
  count: number
}

export interface PriorityCount {
  priority: ComplaintPriority
  count: number
}

export interface StatusCount {
  status: ComplaintStatus
  count: number
}

export interface MonthlyStats {
  monthLabel: string
  totalCount: number
  resolvedCount: number
}

export interface RecentComplaint {
  id: number
  title: string
  category: ComplaintCategory
  status: ComplaintStatus
  priority: ComplaintPriority
  residentName: string
  flatNumber: string
  createdAt: string
  updatedAt: string
}
