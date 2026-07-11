package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.event.ComplaintOverdueEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Placeholder reaction to ComplaintOverdueEvent. Deliberately just logs for
 * now - wiring an actual notification channel (email to admin, push, etc.)
 * is a future extension and plugs in here without OverdueDetectionService
 * ever needing to change.
 */
@Slf4j
@Component
public class ComplaintOverdueListener {

    @EventListener
    public void onComplaintOverdue(ComplaintOverdueEvent event) {
        log.info("Complaint {} has crossed the overdue threshold - notification hook goes here", event.getComplaintId());
    }
}