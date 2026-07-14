import { apiClient } from '@/api/axiosClient'
import { DASHBOARD_PATHS } from '@/constants/apiPaths'
import type { ApiResponse, PageResponse } from '@/types/api'
import type {
  CategoryCount,
  DashboardSummary,
  MonthlyStats,
  PriorityCount,
  RecentComplaint,
  StatusCount,
} from '@/types/dashboard'

export const dashboardService = {
  getSummary: async (): Promise<DashboardSummary> => {
    const { data } = await apiClient.get<ApiResponse<DashboardSummary>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.summary}`,
    )
    return data.data
  },

  getByCategory: async (): Promise<CategoryCount[]> => {
    const { data } = await apiClient.get<ApiResponse<CategoryCount[]>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.byCategory}`,
    )
    return data.data
  },

  getByPriority: async (): Promise<PriorityCount[]> => {
    const { data } = await apiClient.get<ApiResponse<PriorityCount[]>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.byPriority}`,
    )
    return data.data
  },

  getByStatus: async (): Promise<StatusCount[]> => {
    const { data } = await apiClient.get<ApiResponse<StatusCount[]>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.byStatus}`,
    )
    return data.data
  },

  getMonthlyStats: async (size = 6): Promise<PageResponse<MonthlyStats>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<MonthlyStats>>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.monthlyStats}`,
      { params: { size } },
    )
    return data.data
  },

  getRecentCreated: async (size = 5): Promise<PageResponse<RecentComplaint>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<RecentComplaint>>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.recentCreated}`,
      { params: { size } },
    )
    return data.data
  },

  getRecentResolved: async (size = 5): Promise<PageResponse<RecentComplaint>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<RecentComplaint>>>(
      `${DASHBOARD_PATHS.base}${DASHBOARD_PATHS.recentResolved}`,
      { params: { size } },
    )
    return data.data
  },
}
