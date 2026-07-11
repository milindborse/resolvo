package com.resolvo.backend.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalComplaints;
    private long openComplaints;
    private long resolvedComplaints;
    private long highPriorityComplaints;
    private long overdueComplaints;
}