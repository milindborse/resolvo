import { apiClient } from '@/api/axiosClient'
import { NOTICE_PATHS } from '@/constants/apiPaths'
import type { ApiResponse, PageResponse } from '@/types/api'
import type { Notice, NoticeCreateRequest, NoticeUpdateRequest } from '@/types/notice'

export const noticeService = {
  getPublished: async (page: number, size = 10): Promise<PageResponse<Notice>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<Notice>>>(NOTICE_PATHS.base, {
      params: { page, size },
    })
    return data.data
  },

  getAllForAdmin: async (page: number, size = 10): Promise<PageResponse<Notice>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<Notice>>>(
      `${NOTICE_PATHS.base}${NOTICE_PATHS.adminAll}`,
      { params: { page, size } },
    )
    return data.data
  },

  getById: async (id: number): Promise<Notice> => {
    const { data } = await apiClient.get<ApiResponse<Notice>>(`${NOTICE_PATHS.base}${NOTICE_PATHS.byId(id)}`)
    return data.data
  },

  create: async (payload: NoticeCreateRequest): Promise<Notice> => {
    const { data } = await apiClient.post<ApiResponse<Notice>>(NOTICE_PATHS.base, payload)
    return data.data
  },

  update: async (id: number, payload: NoticeUpdateRequest): Promise<Notice> => {
    const { data } = await apiClient.put<ApiResponse<Notice>>(
      `${NOTICE_PATHS.base}${NOTICE_PATHS.byId(id)}`,
      payload,
    )
    return data.data
  },

  remove: async (id: number): Promise<void> => {
    await apiClient.delete(`${NOTICE_PATHS.base}${NOTICE_PATHS.byId(id)}`)
  },

  publish: async (id: number): Promise<Notice> => {
    const { data } = await apiClient.patch<ApiResponse<Notice>>(
      `${NOTICE_PATHS.base}${NOTICE_PATHS.publish(id)}`,
    )
    return data.data
  },

  setPinned: async (id: number, pinned: boolean): Promise<Notice> => {
    const { data } = await apiClient.patch<ApiResponse<Notice>>(
      `${NOTICE_PATHS.base}${NOTICE_PATHS.pin(id)}`,
      { pinned },
    )
    return data.data
  },
}
