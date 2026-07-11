package com.resolvo.backend.dashboard;

import com.resolvo.backend.complaint.Complaint;
import com.resolvo.backend.complaint.projection.CategoryCountProjection;
import com.resolvo.backend.complaint.projection.MonthlyStatsProjection;
import com.resolvo.backend.complaint.projection.PriorityCountProjection;
import com.resolvo.backend.complaint.projection.StatusCountProjection;
import com.resolvo.backend.dashboard.dto.CategoryCountResponse;
import com.resolvo.backend.dashboard.dto.MonthlyComplaintStatsResponse;
import com.resolvo.backend.dashboard.dto.PriorityCountResponse;
import com.resolvo.backend.dashboard.dto.RecentComplaintResponse;
import com.resolvo.backend.dashboard.dto.StatusCountResponse;
import org.springframework.stereotype.Component;

@Component
public class DashboardMapper {

    public CategoryCountResponse toResponse(CategoryCountProjection p) {
        return CategoryCountResponse.builder()
                .category(p.getCategory())
                .count(p.getCount())
                .build();
    }

    public PriorityCountResponse toResponse(PriorityCountProjection p) {
        return PriorityCountResponse.builder()
                .priority(p.getPriority())
                .count(p.getCount())
                .build();
    }

    public StatusCountResponse toResponse(StatusCountProjection p) {
        return StatusCountResponse.builder()
                .status(p.getStatus())
                .count(p.getCount())
                .build();
    }

    public MonthlyComplaintStatsResponse toResponse(MonthlyStatsProjection p) {
        return MonthlyComplaintStatsResponse.builder()
                .monthLabel(p.getMonthLabel())
                .totalCount(p.getTotalCount())
                .resolvedCount(p.getResolvedCount())
                .build();
    }

    public RecentComplaintResponse toRecentResponse(Complaint c) {
        return RecentComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .category(c.getCategory())
                .status(c.getStatus())
                .priority(c.getPriority())
                .residentName(c.getResident().getFullName())
                .flatNumber(c.getResident().getFlatNumber())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}