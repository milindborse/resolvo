package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.event.ComplaintStatusChangedEvent;
import com.resolvo.backend.email.EmailService;
import com.resolvo.backend.email.EmailTemplates;
import com.resolvo.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Second listener on the same event as ComplaintHistoryListener - proof that
 * ComplaintService is fully decoupled from both history and notification
 * concerns. Runs after history (Order 2) purely for log ordering; failure
 * here can never roll back the status change or the history row since
 * EmailService itself swallows and logs delivery errors.
 */
@Component
@RequiredArgsConstructor
public class ComplaintEmailListener {

    private final ComplaintRepository complaintRepository;
    private final EmailService emailService;

    @Order(2)
    @EventListener
    public void onStatusChanged(ComplaintStatusChangedEvent event) {
        Complaint complaint = complaintRepository.findById(event.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        String html = EmailTemplates.complaintStatusChanged(
                complaint.getResident().getFullName(),
                complaint.getTitle(),
                event.getPreviousStatus(),
                event.getNewStatus(),
                event.getRemarks());

        emailService.sendHtml(
                complaint.getResident().getEmail(),
                "Your complaint status has changed - Resolvo",
                html);
    }
}
