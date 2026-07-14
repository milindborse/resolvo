package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.event.ComplaintOverdueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the overdue-detection business logic, on purpose separate from
 * ComplaintService - OverdueComplaintScheduler depends only on this class,
 * never on ComplaintService, so the two responsibilities (complaint CRUD/
 * lifecycle vs. background overdue scanning) can evolve independently.
 *
 * Now uses per-priority thresholds: HIGH=2d, MEDIUM=5d, LOW=7d, rather
 * than a single flat threshold for every complaint.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverdueDetectionService {

    private final ComplaintRepository complaintRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void detectAndMarkOverdueComplaints() {
        List<Complaint> candidates = complaintRepository.findByClosedFalseAndOverdueFalse();

        List<Complaint> newlyOverdue = new ArrayList<>();
        Instant now = Instant.now();

        for (Complaint complaint : candidates) {
            int thresholdDays = complaint.getPriority().getOverdueDays();
            Instant deadline = complaint.getCreatedAt().plus(thresholdDays, ChronoUnit.DAYS);

            if (now.isAfter(deadline)) {
                complaint.setOverdue(true);
                newlyOverdue.add(complaint);
            }
        }

        if (newlyOverdue.isEmpty()) {
            return;
        }

        complaintRepository.saveAll(newlyOverdue);
        log.info("Overdue scan: marked {} complaint(s) as overdue (priority-based thresholds)", newlyOverdue.size());

        for (Complaint complaint : newlyOverdue) {
            eventPublisher.publishEvent(new ComplaintOverdueEvent(this, complaint.getId()));
        }
    }
}