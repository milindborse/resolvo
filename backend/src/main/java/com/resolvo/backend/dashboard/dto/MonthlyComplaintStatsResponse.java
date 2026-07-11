package com.resolvo.backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MonthlyComplaintStatsResponse {
    /** Format: "YYYY-MM" */
    private String monthLabel;
    private long totalCount;
    private long resolvedCount;
}