import { ComplaintCreateForm } from '@/features/complaints/components/ComplaintCreateForm'

export function ComplaintCreatePage() {
  return (
    <div className="mx-auto max-w-2xl space-y-4">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Raise a Complaint</h1>
        <p className="text-sm text-muted-foreground">Fill in the details below - you can track its status afterwards.</p>
      </div>
      <ComplaintCreateForm />
    </div>
  )
}
