package com.resolvo.backend.complaint.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published once per complaint the moment OverdueDetectionService flags it.
 * No listener sends a notification yet - ComplaintOverdueListener currently
 * just logs - but any future notification channel (email/SMS/push) plugs in
 * here without touching the detection logic itself.
 */
@Getter
public class ComplaintOverdueEvent extends ApplicationEvent {
    private final Long complaintId;

    public ComplaintOverdueEvent(Object source, Long complaintId) {
        super(source);
        this.complaintId = complaintId;
    }
}