export interface Notice {
  id: number
  title: string
  body: string
  important: boolean
  pinned: boolean
  published: boolean
  publishedAt: string | null
  createdAt: string
  updatedAt: string
}

export interface NoticeCreateRequest {
  title: string
  body: string
  important: boolean
  pinned: boolean
}

export interface NoticeUpdateRequest {
  title?: string
  body?: string
  important?: boolean
  pinned?: boolean
}
