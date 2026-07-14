# Resolvo Frontend

React 19 + TypeScript + Vite frontend for Resolvo, consuming the existing Spring Boot backend exactly as-is - no backend changes were made or required.

## Stack
React 19, TypeScript, Vite, Tailwind CSS v4, shadcn/ui-style components (hand-rolled on Radix primitives), React Router v7, Axios, TanStack Query v5, React Hook Form + Zod, Lucide React, Recharts, next-themes, Sonner (toasts).

## Folder structure
```
src/
├── api/            axiosClient.ts - interceptors, JWT attachment, 401 handling
├── assets/
├── components/
│   ├── ui/         design system: Button, Card, Input, Select, Badge, Table, Dialog...
│   ├── layout/     Sidebar, Topbar, MobileNav, UserMenu
│   └── shared/      Pagination, EmptyState, ErrorState, StatusBadge, ConfirmDialog...
├── constants/      apiPaths.ts (mirrors backend ApiPaths), queryKeys.ts, navigation.ts
├── contexts/       AuthContext, ThemeProvider
├── features/
│   ├── authentication/
│   ├── complaints/
│   ├── notices/
│   ├── dashboard/
│   └── profile/
├── hooks/          useAuth, useComplaints, useNotices, useDashboard (TanStack Query)
├── layouts/        AppLayout (sidebar+topbar), AuthLayout
├── pages/          role-aware wrapper pages (Dashboard/Complaints/Notices pick admin vs resident view)
├── routes/         AppRoutes, ProtectedRoute, RoleRoute, GuestRoute
├── services/       authService, complaintService, noticeService, dashboardService
├── types/          mirrors backend DTOs exactly (enums, complaint, notice, dashboard, auth, api)
└── utils/          cn.ts (className merge), formatters.ts (dates, title-case)
```

## Setup

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Runs at `http://localhost:5173`. Requires the backend running (default `http://localhost:8080`).

## Environment variables

| Variable | Purpose | Default |
|---|---|---|
| `VITE_API_BASE_URL` | Base URL the Axios client points at | `http://localhost:8080` |

For production (Vercel), set `VITE_API_BASE_URL` to your deployed Render backend URL.

## How auth works
- Login/register call the backend, JWT + user info are stored in `localStorage`
- Every Axios request automatically attaches `Authorization: Bearer <token>` via a request interceptor
- A 401 response anywhere clears the session and redirects to `/login` automatically via a response interceptor
- Routes are protected via `ProtectedRoute` (must be logged in) and `RoleRoute` (must have a specific role - e.g. only residents can access `/complaints/new`)

## Design system
All screens are built exclusively from the components in `components/ui` and `components/shared` - no one-off styled elements. Status/Priority/Overdue badges are shared components used identically across the dashboard, complaint list, complaint detail, and admin table.

## Manual checklist before/after running

1. **Backend must be running first** - the frontend has no mock/offline mode. Start the backend (`cd ../backend && mvn spring-boot:run`) before `npm run dev`.
2. **CORS** - the backend's `SecurityConfig` already allows all origins (`*`) for development. Before production deployment, restrict `CorsConfigurationSource` to your actual Vercel domain instead of `*`.
3. **Copy `.env.example` to `.env`** and confirm `VITE_API_BASE_URL` matches wherever your backend actually runs.
4. **Residents can now self-register** via the `/register` page (always creates a `RESIDENT` account - role selection is intentionally not exposed on the public form). **You still need to seed at least one ADMIN account manually** via `POST /api/v1/auth/register` (Swagger or curl, with `role: ADMIN`) - there is no UI path to create an admin, by design.
5. **Cloudinary** must be configured on the backend (`.env` there) for complaint photo uploads to actually work from the frontend's create-complaint form.
6. **No refresh-token flow yet** - when the JWT expires (default 24h), the user is redirected to `/login` and must log in again. This matches the backend's current auth design; a refresh-token flow is a documented future improvement on both ends.

## Deployment (Vercel)

1. Push this `frontend/` folder to GitHub (as part of the monorepo or its own repo)
2. Import into Vercel, set **Root Directory** to `frontend` if deploying from the monorepo
3. Set the environment variable `VITE_API_BASE_URL` to your Render backend's public URL
4. Build command: `npm run build` · Output directory: `dist` (Vercel detects Vite automatically)
5. After deploying, update the backend's CORS config to allow your Vercel domain specifically instead of `*`

## What's intentionally out of scope for this phase
- Admin account creation UI (still Swagger/curl-only, by design - self-registration always creates a RESIDENT)
- Push/real-time notifications (backend has no WebSocket layer yet)
- Automated tests (no test runner configured yet - candidate for a future phase)
- Code-splitting the initial bundle (currently one ~1MB JS chunk - fine for this project's size, but `React.lazy()` per route would be the next optimization at scale)
