package com.resolvo.backend.complaint.dto;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ComplaintResponse {
    private Long id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private String imageUrl;
    private boolean closed;
    private boolean overdue;
    private Long residentId;
    private String residentName;
    private String flatNumber;
    private Instant createdAt;
    private Instant updatedAt;
}
