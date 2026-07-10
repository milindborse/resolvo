package com.resolvo.backend.complaint.event;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.common.enums.ComplaintStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published by ComplaintService whenever a status transition succeeds.
 * ComplaintService does NOT know or care who is listening - history
 * recording and email notification are both downstream listeners,
 * decoupled from the write path itself.
 */
@Getter
public class ComplaintStatusChangedEvent extends ApplicationEvent {

    private final Long complaintId;
    private final ComplaintStatus previousStatus;
    private final ComplaintStatus newStatus;
    private final User actor;
    private final String remarks;

    public ComplaintStatusChangedEvent(Object source, Long complaintId, ComplaintStatus previousStatus,
                                        ComplaintStatus newStatus, User actor, String remarks) {
        super(source);
        this.complaintId = complaintId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.actor = actor;
        this.remarks = remarks;
    }
}
