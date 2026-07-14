import { apiClient } from '@/api/axiosClient'
import { NOTIFICATION_PATHS } from '@/constants/apiPaths'
import type { ApiResponse, PageResponse } from '@/types/api'
import type { Notification } from '@/types/notification'

export const notificationService = {
  getMyNotifications: async (page: number, size = 15): Promise<PageResponse<Notification>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<Notification>>>(
      NOTIFICATION_PATHS.base,
      { params: { page, size } },
    )
    return data.data
  },

  getUnreadCount: async (): Promise<number> => {
    const { data } = await apiClient.get<ApiResponse<number>>(
      `${NOTIFICATION_PATHS.base}${NOTIFICATION_PATHS.unreadCount}`,
    )
    return data.data
  },

  markAsRead: async (id: number): Promise<void> => {
    await apiClient.patch<ApiResponse<void>>(
      `${NOTIFICATION_PATHS.base}${NOTIFICATION_PATHS.read(id)}`,
    )
  },

  markAllAsRead: async (): Promise<void> => {
    await apiClient.patch<ApiResponse<void>>(
      `${NOTIFICATION_PATHS.base}${NOTIFICATION_PATHS.readAll}`,
    )
  },
}
