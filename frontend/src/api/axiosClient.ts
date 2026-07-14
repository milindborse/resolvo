import axios, { AxiosError } from 'axios'
import { toast } from 'sonner'

const TOKEN_STORAGE_KEY = 'resolvo_token'

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  timeout: 15000,
  headers: {
    Accept: 'application/json',
  },
})

// --- Request interceptor: attach JWT to every outgoing request ---
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_STORAGE_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// --- Response interceptor: unwrap ApiResponse, handle 401 globally ---
let isRedirectingToLogin = false

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ message?: string }>) => {
    if (error.code === 'ECONNABORTED') {
      toast.error('Request timed out. Please try again.')
      return Promise.reject(error)
    }

    if (!error.response) {
      toast.error('Network error - is the backend running?')
      return Promise.reject(error)
    }

    const { status, data } = error.response

    if (status === 401) {
      localStorage.removeItem(TOKEN_STORAGE_KEY)
      localStorage.removeItem('resolvo_user')
      if (!isRedirectingToLogin && window.location.pathname !== '/login') {
        isRedirectingToLogin = true
        toast.error('Session expired - please log in again')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }

    if (status === 403) {
      toast.error(data?.message ?? 'You do not have permission to do that')
    } else if (status >= 500) {
      toast.error('Something went wrong on our end. Please try again shortly.')
    }
    // 400/404/409 are left to the calling feature to handle/display inline
    // (e.g. form field errors, empty states) - not every client error needs a toast.

    return Promise.reject(error)
  },
)

export function setStoredToken(token: string) {
  localStorage.setItem(TOKEN_STORAGE_KEY, token)
}

export function clearStoredToken() {
  localStorage.removeItem(TOKEN_STORAGE_KEY)
}

export function getStoredToken(): string | null {
  return localStorage.getItem(TOKEN_STORAGE_KEY)
}
