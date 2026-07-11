package com.resolvo.backend.dashboard;

import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.complaint.ComplaintRepository;
import com.resolvo.backend.dashboard.dto.CategoryCountResponse;
import com.resolvo.backend.dashboard.dto.DashboardSummaryResponse;
import com.resolvo.backend.dashboard.dto.MonthlyComplaintStatsResponse;
import com.resolvo.backend.dashboard.dto.PriorityCountResponse;
import com.resolvo.backend.dashboard.dto.RecentComplaintResponse;
import com.resolvo.backend.dashboard.dto.StatusCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ComplaintRepository complaintRepository;
    private final DashboardMapper mapper;

    @Value("${resolvo.complaint.overdue-threshold-days}")
    private int overdueThresholdDays;

    // ---- Existing method, untouched - kept for backward compatibility ----
    public DashboardResponse getDashboard() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            byStatus.put(status.name(), complaintRepository.countByStatus(status));
        }

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (ComplaintCategory category : ComplaintCategory.values()) {
            byCategory.put(category.name(), complaintRepository.countByCategory(category));
        }

        Instant threshold = Instant.now().minus(overdueThresholdDays, ChronoUnit.DAYS);
        long overdueCount = complaintRepository.countOverdue(threshold);

        return DashboardResponse.builder()
                .countsByStatus(byStatus)
                .countsByCategory(byCategory)
                .overdueCount(overdueCount)
                .build();
    }

    // ---- Dashboard Analytics additions below ----

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        Instant threshold = Instant.now().minus(overdueThresholdDays, ChronoUnit.DAYS);

        return DashboardSummaryResponse.builder()
                .totalComplaints(complaintRepository.count())
                .openComplaints(complaintRepository.countByStatus(ComplaintStatus.OPEN))
                .resolvedComplaints(complaintRepository.countByStatus(ComplaintStatus.RESOLVED))
                .highPriorityComplaints(complaintRepository.countByPriority(ComplaintPriority.HIGH))
                .overdueComplaints(complaintRepository.countOverdue(threshold))
                .build();
    }

    /**
     * Not paginated on purpose: bounded by the fixed ComplaintCategory enum
     * size (8 values today), so pagination would add ceremony without value.
     */
    @Transactional(readOnly = true)
    public List<CategoryCountResponse> getCountsByCategory() {
        return complaintRepository.countGroupedByCategory().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Not paginated - bounded by the fixed ComplaintPriority enum size (3 values). */
    @Transactional(readOnly = true)
    public List<PriorityCountResponse> getCountsByPriority() {
        return complaintRepository.countGroupedByPriority().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Not paginated - bounded by the fixed ComplaintStatus enum size (3 values). */
    @Transactional(readOnly = true)
    public List<StatusCountResponse> getCountsByStatus() {
        return complaintRepository.countGroupedByStatus().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<MonthlyComplaintStatsResponse> getMonthlyStats(Pageable pageable) {
        Page<MonthlyComplaintStatsResponse> page = complaintRepository.findMonthlyStats(pageable)
                .map(mapper::toResponse);
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecentComplaintResponse> getRecentlyCreated(Pageable pageable) {
        Page<RecentComplaintResponse> page = complaintRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(mapper::toRecentResponse);
        return new PageResponse<>(page);
    }

    /**
     * "Resolved recently" is approximated via updatedAt on RESOLVED complaints,
     * since Complaint has no dedicated resolvedAt column. ComplaintHistory
     * holds the authoritative transition timestamp if that precision is
     * ever needed - flagged in the README as a known simplification.
     */
    @Transactional(readOnly = true)
    public PageResponse<RecentComplaintResponse> getRecentlyResolved(Pageable pageable) {
        Page<RecentComplaintResponse> page = complaintRepository
                .findByStatusOrderByUpdatedAtDesc(ComplaintStatus.RESOLVED, pageable)
                .map(mapper::toRecentResponse);
        return new PageResponse<>(page);
    }
}