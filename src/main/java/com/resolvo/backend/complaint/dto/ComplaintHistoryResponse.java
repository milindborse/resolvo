package com.resolvo.backend.complaint.dto;

import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ComplaintHistoryResponse {
    private Long id;
    private ComplaintStatus previousStatus;
    private ComplaintStatus newStatus;
    private String actorName;
    private String remarks;
    private Instant changedAt;
}
