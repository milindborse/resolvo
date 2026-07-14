import { Link } from 'react-router-dom'
import { RegisterForm } from '@/features/authentication/components/RegisterForm'

export function RegisterPage() {
  return (
    <div className="space-y-4">
      <RegisterForm />
      <p className="text-center text-sm text-muted-foreground">
        Already have an account?{' '}
        <Link to="/login" className="font-medium text-primary hover:underline">
          Sign in
        </Link>
      </p>
    </div>
  )
}
