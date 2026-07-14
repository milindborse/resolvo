import { useRef, useState } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { ImagePlus, Send, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useCreateComplaint } from '@/hooks/useComplaints'
import { complaintCreateSchema, type ComplaintCreateFormValues } from '@/features/complaints/schemas'
import { ComplaintCategory } from '@/types/enums'
import { titleCase } from '@/utils/formatters'

export function ComplaintCreateForm() {
  const navigate = useNavigate()
  const createComplaint = useCreateComplaint()
  const [preview, setPreview] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const {
    register,
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors },
  } = useForm<ComplaintCreateFormValues>({
    resolver: zodResolver(complaintCreateSchema),
    // category must have a real (non-undefined) default from the very first
    // render - Radix Select's `value` prop treats undefined as "uncontrolled",
    // so leaving it out here causes the exact "changing from uncontrolled to
    // controlled" warning the moment the user actually picks a category.
    defaultValues: { title: '', description: '', category: ComplaintCategory.PLUMBING, image: null },
  })

  const image = watch('image')

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] ?? null
    setValue('image', file, { shouldValidate: true })
    setPreview(file ? URL.createObjectURL(file) : null)
  }

  const clearImage = () => {
    setValue('image', null)
    setPreview(null)
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  const onSubmit = async (values: ComplaintCreateFormValues) => {
    const result = await createComplaint.mutateAsync(values)
    navigate(`/complaints/${result.id}`)
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 rounded-xl border border-border bg-card p-6 shadow-sm">
      <div className="space-y-1.5">
        <Label htmlFor="title">Title</Label>
        <Input id="title" placeholder="e.g. Water leakage near parking" error={!!errors.title} {...register('title')} />
        {errors.title && <p className="text-xs text-destructive">{errors.title.message}</p>}
      </div>

      <div className="space-y-1.5">
        <Label>Category</Label>
        <Controller
          control={control}
          name="category"
          render={({ field }) => (
            <Select value={field.value} onValueChange={field.onChange}>
              <SelectTrigger className={errors.category ? 'border-destructive' : ''}>
                <SelectValue placeholder="Select a category" />
              </SelectTrigger>
              <SelectContent>
                {Object.values(ComplaintCategory).map((cat) => (
                  <SelectItem key={cat} value={cat}>
                    {titleCase(cat)}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
        {errors.category && <p className="text-xs text-destructive">{errors.category.message}</p>}
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="description">Description</Label>
        <Textarea
          id="description"
          rows={5}
          placeholder="Describe the issue in detail..."
          error={!!errors.description}
          {...register('description')}
        />
        {errors.description && <p className="text-xs text-destructive">{errors.description.message}</p>}
      </div>

      <div className="space-y-1.5">
        <Label>Photo (optional)</Label>
        {preview ? (
          <div className="relative w-fit">
            <img src={preview} alt="Preview" className="h-32 w-32 rounded-lg border border-border object-cover" />
            <button
              type="button"
              onClick={clearImage}
              className="absolute -right-2 -top-2 flex h-6 w-6 items-center justify-center rounded-full bg-destructive text-destructive-foreground shadow"
            >
              <X className="h-3.5 w-3.5" />
            </button>
          </div>
        ) : (
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            className="flex h-32 w-32 flex-col items-center justify-center gap-1 rounded-lg border border-dashed border-border text-muted-foreground transition-colors hover:border-primary/40 hover:text-primary"
          >
            <ImagePlus className="h-5 w-5" />
            <span className="text-xs">Add photo</span>
          </button>
        )}
        <input ref={fileInputRef} type="file" accept="image/*" className="hidden" onChange={handleFileChange} />
        {errors.image && <p className="text-xs text-destructive">{errors.image.message as string}</p>}
        {image && <p className="text-xs text-muted-foreground">{image.name}</p>}
      </div>

      <Button type="submit" loading={createComplaint.isPending} className="w-full sm:w-auto">
        <Send className="h-4 w-4" /> Submit Complaint
      </Button>
    </form>
  )
}