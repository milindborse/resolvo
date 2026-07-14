import { apiClient } from '@/api/axiosClient'
import { COMPLAINT_PATHS } from '@/constants/apiPaths'
import type { ApiResponse, PageResponse } from '@/types/api'
import type {
  ComplaintCreateRequest,
  ComplaintDetail,
  ComplaintHistoryEntry,
  ComplaintPriorityUpdateRequest,
  ComplaintSearchFilters,
  ComplaintStatusUpdateRequest,
  ComplaintSummary,
} from '@/types/complaint'

function buildSearchParams(filters: ComplaintSearchFilters): URLSearchParams {
  const params = new URLSearchParams()
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      params.append(key, String(value))
    }
  })
  return params
}

export const complaintService = {
  create: async (payload: ComplaintCreateRequest): Promise<ComplaintDetail> => {
    const form = new FormData()
    form.append('title', payload.title)
    form.append('description', payload.description)
    form.append('category', payload.category)
    if (payload.image) {
      form.append('image', payload.image)
    }
    const { data } = await apiClient.post<ApiResponse<ComplaintDetail>>(
      COMPLAINT_PATHS.base,
      form,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    )
    return data.data
  },

  getMyComplaints: async (page: number, size = 10): Promise<PageResponse<ComplaintSummary>> => {
    const { data } = await apiClient.get<ApiResponse<PageResponse<ComplaintSummary>>>(
      `${COMPLAINT_PATHS.base}${COMPLAINT_PATHS.my}`,
      { params: { page, size, sort: 'createdAt,desc' } },
    )
    return data.data
  },

  searchComplaints: async (filters: ComplaintSearchFilters): Promise<PageResponse<ComplaintSummary>> => {
    const params = buildSearchParams(filters)
    const { data } = await apiClient.get<ApiResponse<PageResponse<ComplaintSummary>>>(
      `${COMPLAINT_PATHS.base}?${params.toString()}`,
    )
    return data.data
  },

  getById: async (id: number): Promise<ComplaintDetail> => {
    const { data } = await apiClient.get<ApiResponse<ComplaintDetail>>(
      `${COMPLAINT_PATHS.base}${COMPLAINT_PATHS.byId(id)}`,
    )
    return data.data
  },

  getHistory: async (id: number): Promise<ComplaintHistoryEntry[]> => {
    const { data } = await apiClient.get<ApiResponse<ComplaintHistoryEntry[]>>(
      `${COMPLAINT_PATHS.base}${COMPLAINT_PATHS.history(id)}`,
    )
    return data.data
  },

  updateStatus: async (id: number, payload: ComplaintStatusUpdateRequest): Promise<ComplaintDetail> => {
    const { data } = await apiClient.patch<ApiResponse<ComplaintDetail>>(
      `${COMPLAINT_PATHS.base}${COMPLAINT_PATHS.status(id)}`,
      payload,
    )
    return data.data
  },

  updatePriority: async (id: number, payload: ComplaintPriorityUpdateRequest): Promise<ComplaintDetail> => {
    const { data } = await apiClient.patch<ApiResponse<ComplaintDetail>>(
      `${COMPLAINT_PATHS.base}${COMPLAINT_PATHS.priority(id)}`,
      payload,
    )
    return data.data
  },
}
