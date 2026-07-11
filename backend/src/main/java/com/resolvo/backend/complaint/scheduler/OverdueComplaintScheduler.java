package com.resolvo.backend.complaint.scheduler;

import com.resolvo.backend.complaint.OverdueDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Pure trigger - depends only on OverdueDetectionService, never on
 * ComplaintService, keeping the scheduled job fully isolated from the
 * complaint CRUD/lifecycle code path. Interval is configurable via
 * application.yml (resolvo.complaint.overdue-scan-interval-ms), not hardcoded.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueComplaintScheduler {

    private final OverdueDetectionService overdueDetectionService;

    @Scheduled(fixedRateString = "${resolvo.complaint.overdue-scan-interval-ms}")
    public void scanForOverdueComplaints() {
        log.debug("Running scheduled overdue complaint scan");
        overdueDetectionService.detectAndMarkOverdueComplaints();
    }
}