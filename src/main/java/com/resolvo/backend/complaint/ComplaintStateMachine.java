package com.resolvo.backend.complaint;

import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Central authority for valid complaint status transitions. Keeping this in
 * one place (rather than scattering if/else across the service) means the
 * lifecycle rules are trivially unit-testable and easy to defend in a
 * design conversation: OPEN -> IN_PROGRESS -> RESOLVED, no skipping,
 * no re-opening once RESOLVED.
 */
@Component
public class ComplaintStateMachine {

    private static final Map<ComplaintStatus, Set<ComplaintStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ComplaintStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ComplaintStatus.OPEN, EnumSet.of(ComplaintStatus.IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.IN_PROGRESS, EnumSet.of(ComplaintStatus.RESOLVED, ComplaintStatus.OPEN));
        ALLOWED_TRANSITIONS.put(ComplaintStatus.RESOLVED, EnumSet.noneOf(ComplaintStatus.class));
    }

    public void validateTransition(ComplaintStatus current, ComplaintStatus target) {
        if (current == target) {
            throw new InvalidStateTransitionException(
                    "Complaint is already in status " + current);
        }

        Set<ComplaintStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(ComplaintStatus.class));

        if (!allowed.contains(target)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition complaint from " + current + " to " + target
                            + ". Allowed next states: " + allowed);
        }
    }
}
