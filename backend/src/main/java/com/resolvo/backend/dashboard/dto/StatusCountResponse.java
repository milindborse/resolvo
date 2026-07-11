package com.resolvo.backend.dashboard.dto;

import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StatusCountResponse {
    private ComplaintStatus status;
    private long count;
}