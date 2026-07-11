package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.event.ComplaintOverdueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Owns the overdue-detection business logic, on purpose separate from
 * ComplaintService - OverdueComplaintScheduler depends only on this class,
 * never on ComplaintService, so the two responsibilities (complaint CRUD/
 * lifecycle vs. background overdue scanning) can evolve independently.
 *
 * Loads candidate complaints as entities (rather than a single bulk UPDATE)
 * specifically so it can publish one ComplaintOverdueEvent per complaint
 * that just crossed the threshold - a deliberate trade-off of per-row
 * events over raw UPDATE throughput, reasonable at society-scale data volumes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueDetectionService {

    private final ComplaintRepository complaintRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${resolvo.complaint.overdue-threshold-days}")
    private int overdueThresholdDays;

    @Transactional
    public void detectAndMarkOverdueComplaints() {
        Instant threshold = Instant.now().minus(overdueThresholdDays, ChronoUnit.DAYS);

        List<Complaint> newlyOverdue = complaintRepository.findByClosedFalseAndOverdueFalseAndCreatedAtBefore(threshold);

        if (newlyOverdue.isEmpty()) {
            return;
        }

        for (Complaint complaint : newlyOverdue) {
            complaint.setOverdue(true);
        }
        complaintRepository.saveAll(newlyOverdue);

        log.info("Overdue scan: marked {} complaint(s) as overdue", newlyOverdue.size());

        for (Complaint complaint : newlyOverdue) {
            eventPublisher.publishEvent(new ComplaintOverdueEvent(this, complaint.getId()));
        }
    }
}