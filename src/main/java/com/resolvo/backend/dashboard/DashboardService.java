package com.resolvo.backend.dashboard;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.complaint.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ComplaintRepository complaintRepository;

    @Value("${resolvo.complaint.overdue-threshold-days}")
    private int overdueThresholdDays;

    public DashboardResponse getDashboard() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ComplaintStatus status : ComplaintStatus.values()) {
            byStatus.put(status.name(), complaintRepository.countByStatus(status));
        }

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (ComplaintCategory category : ComplaintCategory.values()) {
            byCategory.put(category.name(), complaintRepository.countByCategory(category));
        }

        Instant threshold = Instant.now().minus(overdueThresholdDays, ChronoUnit.DAYS);
        long overdueCount = complaintRepository.countOverdue(threshold);

        return DashboardResponse.builder()
                .countsByStatus(byStatus)
                .countsByCategory(byCategory)
                .overdueCount(overdueCount)
                .build();
    }
}
