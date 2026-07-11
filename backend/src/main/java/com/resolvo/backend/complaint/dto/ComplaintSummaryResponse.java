package com.resolvo.backend.complaint.dto;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Deliberately lighter than ComplaintResponse - list/search endpoints don't
 * need description or imageUrl in every row. ComplaintResponse (full detail)
 * is still returned by GET /complaints/{id}.
 */
@Getter
@Builder
@AllArgsConstructor
public class ComplaintSummaryResponse {
    private Long id;
    private String title;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private boolean overdue;
    private boolean closed;
    private String residentName;
    private String flatNumber;
    private Instant createdAt;
    private Instant updatedAt;
}