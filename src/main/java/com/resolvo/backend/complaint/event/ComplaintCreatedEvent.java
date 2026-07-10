package com.resolvo.backend.complaint.event;

import com.resolvo.backend.auth.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published on complaint creation so the very first history row
 * (null -> OPEN) is recorded the same way every subsequent transition is:
 * through a listener, not inline in the service.
 */
@Getter
public class ComplaintCreatedEvent extends ApplicationEvent {

    private final Long complaintId;
    private final User actor;

    public ComplaintCreatedEvent(Object source, Long complaintId, User actor) {
        super(source);
        this.complaintId = complaintId;
        this.actor = actor;
    }
}
