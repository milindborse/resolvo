export const UserRole = {
  ADMIN: 'ADMIN',
  RESIDENT: 'RESIDENT',
} as const
export type UserRole = (typeof UserRole)[keyof typeof UserRole]

export const ComplaintStatus = {
  OPEN: 'OPEN',
  IN_PROGRESS: 'IN_PROGRESS',
  RESOLVED: 'RESOLVED',
} as const
export type ComplaintStatus = (typeof ComplaintStatus)[keyof typeof ComplaintStatus]

export const ComplaintPriority = {
  LOW: 'LOW',
  MEDIUM: 'MEDIUM',
  HIGH: 'HIGH',
} as const
export type ComplaintPriority = (typeof ComplaintPriority)[keyof typeof ComplaintPriority]

export const ComplaintCategory = {
  PLUMBING: 'PLUMBING',
  ELECTRICAL: 'ELECTRICAL',
  CLEANING: 'CLEANING',
  SECURITY: 'SECURITY',
  PARKING: 'PARKING',
  LIFT: 'LIFT',
  NOISE: 'NOISE',
  OTHER: 'OTHER',
} as const
export type ComplaintCategory = (typeof ComplaintCategory)[keyof typeof ComplaintCategory]
