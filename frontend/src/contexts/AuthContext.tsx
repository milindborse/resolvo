import { createContext, useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import { authService } from '@/services/authService'
import { clearStoredToken, setStoredToken } from '@/api/axiosClient'
import type { AuthUser, LoginRequest, RegisterRequest } from '@/types/auth'

const USER_STORAGE_KEY = 'resolvo_user'

interface AuthContextValue {
  user: AuthUser | null
  isAuthenticated: boolean
  isInitializing: boolean
  login: (payload: LoginRequest) => Promise<void>
  register: (payload: RegisterRequest) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isInitializing, setIsInitializing] = useState(true)

  useEffect(() => {
    const stored = localStorage.getItem(USER_STORAGE_KEY)
    if (stored) {
      try {
        setUser(JSON.parse(stored))
      } catch {
        localStorage.removeItem(USER_STORAGE_KEY)
      }
    }
    setIsInitializing(false)
  }, [])

  const persistSession = useCallback((authUser: AuthUser, token: string) => {
    setStoredToken(token)
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(authUser))
    setUser(authUser)
  }, [])

  const login = useCallback(
    async (payload: LoginRequest) => {
      const response = await authService.login(payload)
      persistSession(
        { userId: response.userId, fullName: response.fullName, email: response.email, role: response.role },
        response.token,
      )
      toast.success(`Welcome back, ${response.fullName.split(' ')[0]}`)
    },
    [persistSession],
  )

  const register = useCallback(
    async (payload: RegisterRequest) => {
      const response = await authService.register(payload)
      persistSession(
        { userId: response.userId, fullName: response.fullName, email: response.email, role: response.role },
        response.token,
      )
      toast.success('Account created successfully')
    },
    [persistSession],
  )

  const logout = useCallback(() => {
    clearStoredToken()
    localStorage.removeItem(USER_STORAGE_KEY)
    setUser(null)
    toast.success('Logged out')
  }, [])

  const value = useMemo(
    () => ({ user, isAuthenticated: !!user, isInitializing, login, register, logout }),
    [user, isInitializing, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
