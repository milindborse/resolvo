package com.resolvo.backend.dashboard;

import com.resolvo.backend.common.constants.ApiPaths;
import com.resolvo.backend.common.constants.SecurityRoles;
import com.resolvo.backend.common.dto.ApiResponse;
import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.dashboard.dto.CategoryCountResponse;
import com.resolvo.backend.dashboard.dto.DashboardSummaryResponse;
import com.resolvo.backend.dashboard.dto.MonthlyComplaintStatsResponse;
import com.resolvo.backend.dashboard.dto.PriorityCountResponse;
import com.resolvo.backend.dashboard.dto.RecentComplaintResponse;
import com.resolvo.backend.dashboard.dto.StatusCountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.Dashboard.BASE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('" + SecurityRoles.ADMIN + "')")
@Tag(name = "Dashboard", description = "Admin analytics: summary counts, group-bys, monthly trends, recent activity")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Legacy combined dashboard payload",
            description = "Kept for backward compatibility. Prefer /summary, /by-status, /by-category for new integrations.")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard()));
    }

    @GetMapping(ApiPaths.Dashboard.SUMMARY)
    @Operation(summary = "Headline counts",
            description = "Total, open, resolved, high-priority, and overdue complaint counts in a single call - each backed by a dedicated COUNT query, no in-memory aggregation.")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }

    @GetMapping(ApiPaths.Dashboard.BY_CATEGORY)
    @Operation(summary = "Complaints grouped by category",
            description = "One row per ComplaintCategory with its count. Not paginated - the result set is bounded by the fixed enum size.")
    public ResponseEntity<ApiResponse<List<CategoryCountResponse>>> getByCategory() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCountsByCategory()));
    }

    @GetMapping(ApiPaths.Dashboard.BY_PRIORITY)
    @Operation(summary = "Complaints grouped by priority",
            description = "One row per ComplaintPriority with its count. Not paginated - bounded by the fixed enum size.")
    public ResponseEntity<ApiResponse<List<PriorityCountResponse>>> getByPriority() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCountsByPriority()));
    }

    @GetMapping(ApiPaths.Dashboard.BY_STATUS)
    @Operation(summary = "Complaints grouped by status",
            description = "One row per ComplaintStatus with its count. Not paginated - bounded by the fixed enum size.")
    public ResponseEntity<ApiResponse<List<StatusCountResponse>>> getByStatus() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getCountsByStatus()));
    }

    @GetMapping(ApiPaths.Dashboard.MONTHLY_STATS)
    @Operation(summary = "Monthly complaint statistics",
            description = "Paginated, newest month first. Each row is totalCount (created that month) and resolvedCount (of those, currently RESOLVED). Grouping and date formatting happen entirely in the database.")
    public ResponseEntity<ApiResponse<PageResponse<MonthlyComplaintStatsResponse>>> getMonthlyStats(
            @ParameterObject @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getMonthlyStats(pageable)));
    }

    @GetMapping(ApiPaths.Dashboard.RECENT_CREATED)
    @Operation(summary = "Recently created complaints",
            description = "Paginated, newest first. Returns a lightweight RecentComplaintResponse, not the full complaint payload.")
    public ResponseEntity<ApiResponse<PageResponse<RecentComplaintResponse>>> getRecentlyCreated(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentlyCreated(pageable)));
    }

    @GetMapping(ApiPaths.Dashboard.RECENT_RESOLVED)
    @Operation(summary = "Recently resolved complaints",
            description = "Paginated, most recently resolved first (approximated via updatedAt since Complaint has no dedicated resolvedAt column).")
    public ResponseEntity<ApiResponse<PageResponse<RecentComplaintResponse>>> getRecentlyResolved(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentlyResolved(pageable)));
    }
}