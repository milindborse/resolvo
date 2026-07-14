/**
 * Mirrors backend com.resolvo.backend.common.constants.ApiPaths exactly.
 * Keeping this in sync manually is intentional - the frontend has no
 * runtime dependency on the backend's Java source, so this is the
 * single source of truth for every path used by axios services below.
 */
export const API_BASE = '/api/v1'

export const AUTH_PATHS = {
  base: `${API_BASE}/auth`,
  register: '/register',
  login: '/login',
}

export const COMPLAINT_PATHS = {
  base: `${API_BASE}/complaints`,
  my: '/my',
  byId: (id: number | string) => `/${id}`,
  history: (id: number | string) => `/${id}/history`,
  status: (id: number | string) => `/${id}/status`,
  priority: (id: number | string) => `/${id}/priority`,
}

export const NOTICE_PATHS = {
  base: `${API_BASE}/notices`,
  byId: (id: number | string) => `/${id}`,
  publish: (id: number | string) => `/${id}/publish`,
  pin: (id: number | string) => `/${id}/pin`,
  adminAll: '/admin/all',
}

export const DASHBOARD_PATHS = {
  base: `${API_BASE}/dashboard`,
  summary: '/summary',
  byCategory: '/by-category',
  byPriority: '/by-priority',
  byStatus: '/by-status',
  monthlyStats: '/monthly-stats',
  recentCreated: '/recent-created',
  recentResolved: '/recent-resolved',
}

export const NOTIFICATION_PATHS = {
  base: `${API_BASE}/notifications`,
  unreadCount: '/unread-count',
  read: (id: number | string) => `/${id}/read`,
  readAll: '/read-all',
}

