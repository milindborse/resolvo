import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { EmptyState } from '@/components/shared/EmptyState'
import { titleCase } from '@/utils/formatters'
import type { CategoryCount } from '@/types/dashboard'

const COLORS = ['#3b82f6', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#06b6d4', '#f97316', '#64748b']

export function CategoryDistributionChart({ data, isLoading }: { data?: CategoryCount[]; isLoading: boolean }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Category Distribution</CardTitle>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <Skeleton className="h-64 w-full" />
        ) : !data || data.length === 0 ? (
          <EmptyState title="No data yet" description="Complaints will appear here once residents start raising them." />
        ) : (
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie
                data={data}
                dataKey="count"
                nameKey="category"
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={90}
                paddingAngle={2}
              >
                {data.map((entry, index) => (
                  <Cell key={entry.category} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                formatter={(value, name) => [String(value), titleCase(String(name))]}
                contentStyle={{ borderRadius: 8, border: '1px solid hsl(var(--border))', fontSize: 12 }}
              />
            </PieChart>
          </ResponsiveContainer>
        )}
        {data && data.length > 0 && (
          <div className="mt-4 grid grid-cols-2 gap-2">
            {data.map((entry, index) => (
              <div key={entry.category} className="flex items-center gap-2 text-xs">
                <span className="h-2 w-2 shrink-0 rounded-full" style={{ background: COLORS[index % COLORS.length] }} />
                <span className="truncate text-muted-foreground">{titleCase(entry.category)}</span>
                <span className="ml-auto font-medium">{entry.count}</span>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
