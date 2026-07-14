import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { isAxiosError } from 'axios'
import { complaintService } from '@/services/complaintService'
import { QUERY_KEYS } from '@/constants/queryKeys'
import type {
  ComplaintCreateRequest,
  ComplaintPriorityUpdateRequest,
  ComplaintSearchFilters,
  ComplaintStatusUpdateRequest,
} from '@/types/complaint'

export function useMyComplaints(page: number) {
  return useQuery({
    queryKey: QUERY_KEYS.myComplaints(page),
    queryFn: () => complaintService.getMyComplaints(page),
  })
}

export function useAdminComplaints(filters: ComplaintSearchFilters) {
  return useQuery({
    queryKey: QUERY_KEYS.adminComplaints(filters),
    queryFn: () => complaintService.searchComplaints(filters),
  })
}

export function useComplaintDetail(id: number | undefined) {
  return useQuery({
    queryKey: QUERY_KEYS.complaintDetail(id ?? 0),
    queryFn: () => complaintService.getById(id as number),
    enabled: !!id,
  })
}

export function useComplaintHistory(id: number | undefined) {
  return useQuery({
    queryKey: QUERY_KEYS.complaintHistory(id ?? 0),
    queryFn: () => complaintService.getHistory(id as number),
    enabled: !!id,
  })
}

function extractErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) {
    return error.response?.data?.message ?? fallback
  }
  return fallback
}

export function useCreateComplaint() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: ComplaintCreateRequest) => complaintService.create(payload),
    onSuccess: () => {
      toast.success('Complaint raised successfully')
      queryClient.invalidateQueries({ queryKey: ['complaints'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not raise complaint')),
  })
}

export function useUpdateComplaintStatus() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: ComplaintStatusUpdateRequest }) =>
      complaintService.updateStatus(id, payload),
    onSuccess: (_data, variables) => {
      toast.success('Status updated')
      queryClient.invalidateQueries({ queryKey: ['complaints'] })
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.complaintHistory(variables.id) })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not update status')),
  })
}

export function useUpdateComplaintPriority() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: ComplaintPriorityUpdateRequest }) =>
      complaintService.updatePriority(id, payload),
    onSuccess: () => {
      toast.success('Priority updated')
      queryClient.invalidateQueries({ queryKey: ['complaints'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not update priority')),
  })
}
