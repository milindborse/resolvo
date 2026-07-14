import { z } from 'zod'

export const loginSchema = z.object({
  email: z.string().min(1, 'Email is required').email('Enter a valid email'),
  password: z.string().min(1, 'Password is required'),
})
export type LoginFormValues = z.infer<typeof loginSchema>

// Mirrors backend RegisterRequest validation (auth/dto/RegisterRequest.java).
// Role is deliberately NOT part of this public-facing schema - the register
// page always submits role: RESIDENT, matching the assignment's intent that
// only admins are created out-of-band (Swagger/curl), never self-registered.
export const registerSchema = z
  .object({
    fullName: z.string().min(1, 'Full name is required').max(120),
    email: z.string().min(1, 'Email is required').email('Enter a valid email').max(150),
    password: z.string().min(8, 'Password must be at least 8 characters').max(100),
    confirmPassword: z.string().min(1, 'Please confirm your password'),
    flatNumber: z.string().min(1, 'Flat number is required').max(20),
    phoneNumber: z
      .string()
      .regex(/^$|^[0-9+()\-\s]{7,20}$/, 'Enter a valid phone number')
      .optional()
      .or(z.literal('')),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  })
export type RegisterFormValues = z.infer<typeof registerSchema>
