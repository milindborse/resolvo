import type { UserRole } from './enums'

export interface AuthResponse {
  token: string
  userId: number
  fullName: string
  email: string
  role: UserRole
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  email: string
  password: string
  flatNumber: string
  phoneNumber?: string
  role: UserRole
}

export interface AuthUser {
  userId: number
  fullName: string
  email: string
  role: UserRole
}
