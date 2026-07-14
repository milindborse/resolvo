import { format, formatDistanceToNow } from 'date-fns'

export function formatDate(value: string | null | undefined, pattern = 'dd MMM yyyy'): string {
  if (!value) return '-'
  try {
    return format(new Date(value), pattern)
  } catch {
    return '-'
  }
}

export function formatDateTime(value: string | null | undefined): string {
  return formatDate(value, 'dd MMM yyyy, h:mm a')
}

export function timeAgo(value: string | null | undefined): string {
  if (!value) return '-'
  try {
    return formatDistanceToNow(new Date(value), { addSuffix: true })
  } catch {
    return '-'
  }
}

export function titleCase(value: string | null | undefined): string {
  if (!value) return ''
  return value
    .toLowerCase()
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}
