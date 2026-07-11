package com.resolvo.backend.dashboard.dto;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Deliberately lighter than complaint.dto.ComplaintResponse - the dashboard
 * only needs enough fields to render a recent-activity list, not the full
 * complaint payload (description, image, etc).
 */
@Getter
@Builder
@AllArgsConstructor
public class RecentComplaintResponse {
    private Long id;
    private String title;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private String residentName;
    private String flatNumber;
    private Instant createdAt;
    private Instant updatedAt;
}