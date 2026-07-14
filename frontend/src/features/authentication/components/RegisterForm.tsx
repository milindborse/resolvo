import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'
import { Eye, EyeOff, UserPlus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useAuth } from '@/hooks/useAuth'
import { registerSchema, type RegisterFormValues } from '@/features/authentication/schemas'
import { UserRole } from '@/types/enums'

export function RegisterForm() {
  const { register: registerUser } = useAuth()
  const navigate = useNavigate()
  const [showPassword, setShowPassword] = useState(false)
  const [serverError, setServerError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({ resolver: zodResolver(registerSchema) })

  const onSubmit = async (values: RegisterFormValues) => {
    setServerError(null)
    try {
      // Always RESIDENT - self-registration as ADMIN is intentionally not exposed.
      await registerUser({
        fullName: values.fullName,
        email: values.email,
        password: values.password,
        flatNumber: values.flatNumber,
        phoneNumber: values.phoneNumber || undefined,
        role: UserRole.RESIDENT,
      })
      // AuthContext.register() already persists the session (token + user),
      // so the user is logged in immediately - land them straight on their
      // role-aware dashboard, exactly like a fresh login would.
      navigate('/dashboard', { replace: true })
    } catch (error) {
      if (isAxiosError(error)) {
        setServerError(error.response?.data?.message ?? 'Could not create account')
      } else {
        setServerError('Something went wrong. Please try again.')
      }
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 rounded-xl border border-border bg-card p-6 shadow-sm">
      <div className="space-y-1.5">
        <Label htmlFor="fullName">Full name</Label>
        <Input id="fullName" placeholder="Jane Doe" error={!!errors.fullName} {...register('fullName')} />
        {errors.fullName && <p className="text-xs text-destructive">{errors.fullName.message}</p>}
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="email">Email</Label>
        <Input id="email" type="email" placeholder="you@example.com" error={!!errors.email} {...register('email')} />
        {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div className="space-y-1.5">
          <Label htmlFor="flatNumber">Flat number</Label>
          <Input id="flatNumber" placeholder="A-101" error={!!errors.flatNumber} {...register('flatNumber')} />
          {errors.flatNumber && <p className="text-xs text-destructive">{errors.flatNumber.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label htmlFor="phoneNumber">Phone (optional)</Label>
          <Input id="phoneNumber" placeholder="9876543210" error={!!errors.phoneNumber} {...register('phoneNumber')} />
          {errors.phoneNumber && <p className="text-xs text-destructive">{errors.phoneNumber.message}</p>}
        </div>
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="password">Password</Label>
        <div className="relative">
          <Input
            id="password"
            type={showPassword ? 'text' : 'password'}
            placeholder="At least 8 characters"
            error={!!errors.password}
            className="pr-10"
            {...register('password')}
          />
          <button
            type="button"
            onClick={() => setShowPassword((v) => !v)}
            className="absolute right-2 top-1/2 -translate-y-1/2 text-muted-foreground"
            tabIndex={-1}
          >
            {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
          </button>
        </div>
        {errors.password && <p className="text-xs text-destructive">{errors.password.message}</p>}
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="confirmPassword">Confirm password</Label>
        <Input
          id="confirmPassword"
          type={showPassword ? 'text' : 'password'}
          placeholder="Re-enter your password"
          error={!!errors.confirmPassword}
          {...register('confirmPassword')}
        />
        {errors.confirmPassword && <p className="text-xs text-destructive">{errors.confirmPassword.message}</p>}
      </div>

      {serverError && (
        <p className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">{serverError}</p>
      )}

      <Button type="submit" className="w-full" loading={isSubmitting}>
        <UserPlus className="h-4 w-4" /> Create account
      </Button>
    </form>
  )
}
