package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.event.ComplaintCreatedEvent;
import com.resolvo.backend.complaint.event.ComplaintStatusChangedEvent;
import com.resolvo.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sole writer of ComplaintHistory rows. Runs first (Order 1) so the audit
 * trail is guaranteed to be persisted even if the email listener fails.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintHistoryListener {

    private final ComplaintRepository complaintRepository;
    private final ComplaintHistoryRepository historyRepository;

    @Order(1)
    @EventListener
    @Transactional
    public void onComplaintCreated(ComplaintCreatedEvent event) {
        Complaint complaint = complaintRepository.findById(event.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(complaint)
                .previousStatus(null)
                .newStatus(complaint.getStatus())
                .actor(event.getActor())
                .remarks("Complaint raised")
                .build();

        historyRepository.save(history);
        log.debug("History row recorded for complaint id={}: initial status {}", complaint.getId(), complaint.getStatus());
    }

    @Order(1)
    @EventListener
    @Transactional
    public void onStatusChanged(ComplaintStatusChangedEvent event) {
        Complaint complaint = complaintRepository.findById(event.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        ComplaintHistory history = ComplaintHistory.builder()
                .complaint(complaint)
                .previousStatus(event.getPreviousStatus())
                .newStatus(event.getNewStatus())
                .actor(event.getActor())
                .remarks(event.getRemarks())
                .build();

        historyRepository.save(history);
        log.debug("History row recorded for complaint id={}: {} -> {}", complaint.getId(),
                event.getPreviousStatus(), event.getNewStatus());
    }
}