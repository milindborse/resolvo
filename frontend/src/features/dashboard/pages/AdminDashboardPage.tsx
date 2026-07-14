import { SummaryCards } from '@/features/dashboard/components/SummaryCards'
import { CategoryDistributionChart } from '@/features/dashboard/components/CategoryDistributionChart'
import { MonthlyTrendChart } from '@/features/dashboard/components/MonthlyTrendChart'
import { RecentActivityList } from '@/features/dashboard/components/RecentActivityList'
import {
  useDashboardByCategory,
  useDashboardMonthlyStats,
  useDashboardRecentCreated,
  useDashboardRecentResolved,
  useDashboardSummary,
} from '@/hooks/useDashboard'

export function AdminDashboardPage() {
  const summary = useDashboardSummary()
  const byCategory = useDashboardByCategory()
  const monthly = useDashboardMonthlyStats()
  const recentCreated = useDashboardRecentCreated()
  const recentResolved = useDashboardRecentResolved()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">Admin Dashboard</h1>
        <p className="text-sm text-muted-foreground">Society-wide complaint analytics at a glance.</p>
      </div>

      <SummaryCards data={summary.data} isLoading={summary.isLoading} />

      <div className="grid gap-4 lg:grid-cols-2">
        <MonthlyTrendChart data={monthly.data?.content} isLoading={monthly.isLoading} />
        <CategoryDistributionChart data={byCategory.data} isLoading={byCategory.isLoading} />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <RecentActivityList
          title="Recently Created"
          data={recentCreated.data?.content}
          isLoading={recentCreated.isLoading}
        />
        <RecentActivityList
          title="Recently Resolved"
          data={recentResolved.data?.content}
          isLoading={recentResolved.isLoading}
        />
      </div>
    </div>
  )
}
