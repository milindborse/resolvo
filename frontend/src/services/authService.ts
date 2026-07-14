import { apiClient } from '@/api/axiosClient'
import { AUTH_PATHS } from '@/constants/apiPaths'
import type { ApiResponse } from '@/types/api'
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth'

export const authService = {
  login: async (payload: LoginRequest): Promise<AuthResponse> => {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      `${AUTH_PATHS.base}${AUTH_PATHS.login}`,
      payload,
    )
    return data.data
  },

  register: async (payload: RegisterRequest): Promise<AuthResponse> => {
    const { data } = await apiClient.post<ApiResponse<AuthResponse>>(
      `${AUTH_PATHS.base}${AUTH_PATHS.register}`,
      payload,
    )
    return data.data
  },
}
