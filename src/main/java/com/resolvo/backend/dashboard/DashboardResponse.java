package com.resolvo.backend.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class DashboardResponse {
    private Map<String, Long> countsByStatus;
    private Map<String, Long> countsByCategory;
    private long overdueCount;
}
