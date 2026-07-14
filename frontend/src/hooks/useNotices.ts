import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { isAxiosError } from 'axios'
import { noticeService } from '@/services/noticeService'
import { QUERY_KEYS } from '@/constants/queryKeys'
import type { NoticeCreateRequest, NoticeUpdateRequest } from '@/types/notice'

function extractErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError(error)) return error.response?.data?.message ?? fallback
  return fallback
}

export function useNoticeBoard(page: number) {
  return useQuery({ queryKey: QUERY_KEYS.notices(page), queryFn: () => noticeService.getPublished(page) })
}

export function useAdminNotices(page: number) {
  return useQuery({ queryKey: QUERY_KEYS.adminNotices(page), queryFn: () => noticeService.getAllForAdmin(page) })
}

export function useCreateNotice() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (payload: NoticeCreateRequest) => noticeService.create(payload),
    onSuccess: () => {
      toast.success('Notice draft created')
      queryClient.invalidateQueries({ queryKey: ['notices'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not create notice')),
  })
}

export function useUpdateNotice() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, payload }: { id: number; payload: NoticeUpdateRequest }) => noticeService.update(id, payload),
    onSuccess: () => {
      toast.success('Notice updated')
      queryClient.invalidateQueries({ queryKey: ['notices'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not update notice')),
  })
}

export function useDeleteNotice() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => noticeService.remove(id),
    onSuccess: () => {
      toast.success('Notice deleted')
      queryClient.invalidateQueries({ queryKey: ['notices'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not delete notice')),
  })
}

export function usePublishNotice() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (id: number) => noticeService.publish(id),
    onSuccess: () => {
      toast.success('Notice published')
      queryClient.invalidateQueries({ queryKey: ['notices'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not publish notice')),
  })
}

export function useSetNoticePinned() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, pinned }: { id: number; pinned: boolean }) => noticeService.setPinned(id, pinned),
    onSuccess: (_data, variables) => {
      toast.success(variables.pinned ? 'Notice pinned' : 'Notice unpinned')
      queryClient.invalidateQueries({ queryKey: ['notices'] })
    },
    onError: (error) => toast.error(extractErrorMessage(error, 'Could not update pin state')),
  })
}
