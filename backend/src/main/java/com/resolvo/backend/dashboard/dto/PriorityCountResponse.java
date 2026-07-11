package com.resolvo.backend.dashboard.dto;

import com.resolvo.backend.common.enums.ComplaintPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PriorityCountResponse {
    private ComplaintPriority priority;
    private long count;
}