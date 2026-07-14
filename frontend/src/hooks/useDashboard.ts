import { useQuery } from '@tanstack/react-query'
import { dashboardService } from '@/services/dashboardService'
import { QUERY_KEYS } from '@/constants/queryKeys'

export function useDashboardSummary() {
  return useQuery({ queryKey: QUERY_KEYS.dashboardSummary, queryFn: dashboardService.getSummary })
}

export function useDashboardByCategory() {
  return useQuery({ queryKey: QUERY_KEYS.dashboardByCategory, queryFn: dashboardService.getByCategory })
}

export function useDashboardByPriority() {
  return useQuery({ queryKey: QUERY_KEYS.dashboardByPriority, queryFn: dashboardService.getByPriority })
}

export function useDashboardByStatus() {
  return useQuery({ queryKey: QUERY_KEYS.dashboardByStatus, queryFn: dashboardService.getByStatus })
}

export function useDashboardMonthlyStats() {
  return useQuery({
    queryKey: QUERY_KEYS.dashboardMonthly,
    queryFn: () => dashboardService.getMonthlyStats(6),
  })
}

export function useDashboardRecentCreated() {
  return useQuery({
    queryKey: QUERY_KEYS.dashboardRecentCreated,
    queryFn: () => dashboardService.getRecentCreated(5),
  })
}

export function useDashboardRecentResolved() {
  return useQuery({
    queryKey: QUERY_KEYS.dashboardRecentResolved,
    queryFn: () => dashboardService.getRecentResolved(5),
  })
}
