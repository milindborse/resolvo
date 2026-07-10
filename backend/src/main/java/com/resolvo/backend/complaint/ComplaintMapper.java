package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.dto.ComplaintHistoryResponse;
import com.resolvo.backend.complaint.dto.ComplaintResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Kept as a plain component (rather than MapStruct-generated) so the
 * "overdue" derived field can be computed inline against the configured
 * threshold without extra plumbing.
 */
@Component
public class ComplaintMapper {

    @Value("${resolvo.complaint.overdue-threshold-days}")
    private int overdueThresholdDays;

    public ComplaintResponse toResponse(Complaint c) {
        boolean overdue = !c.isClosed()
                && c.getCreatedAt().isBefore(Instant.now().minus(overdueThresholdDays, ChronoUnit.DAYS));

        return ComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .status(c.getStatus())
                .priority(c.getPriority())
                .imageUrl(c.getImageUrl())
                .closed(c.isClosed())
                .overdue(overdue)
                .residentId(c.getResident().getId())
                .residentName(c.getResident().getFullName())
                .flatNumber(c.getResident().getFlatNumber())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    public ComplaintHistoryResponse toHistoryResponse(ComplaintHistory h) {
        return ComplaintHistoryResponse.builder()
                .id(h.getId())
                .previousStatus(h.getPreviousStatus())
                .newStatus(h.getNewStatus())
                .actorName(h.getActor().getFullName())
                .remarks(h.getRemarks())
                .changedAt(h.getCreatedAt())
                .build();
    }
}
