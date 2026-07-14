package com.resolvo.backend.complaint;

import com.resolvo.backend.complaint.dto.ComplaintHistoryResponse;
import com.resolvo.backend.complaint.dto.ComplaintResponse;
import com.resolvo.backend.complaint.dto.ComplaintSummaryResponse;
import org.springframework.stereotype.Component;

/**
 * Kept as a plain component (rather than MapStruct-generated) since it's
 * small and reads clearly as-is. "overdue" is read directly off the
 * persisted Complaint.overdue flag - OverdueDetectionService is the single
 * writer of that flag, so this mapper is purely a read-through, not a
 * second place where overdue logic could drift out of sync.
 */
@Component
public class ComplaintMapper {

    public ComplaintResponse toResponse(Complaint c) {
        return ComplaintResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .status(c.getStatus())
                .priority(c.getPriority())
                .suggestedPriority(c.getSuggestedPriority())
                .imageUrl(c.getImageUrl())
                .closed(c.isClosed())
                .overdue(c.isOverdue())
                .residentId(c.getResident().getId())
                .residentName(c.getResident().getFullName())
                .flatNumber(c.getResident().getFlatNumber())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    /** Lightweight projection for list/search endpoints - no description/imageUrl. */
    public ComplaintSummaryResponse toSummaryResponse(Complaint c) {
        return ComplaintSummaryResponse.builder()
                .id(c.getId())
                .title(c.getTitle())
                .category(c.getCategory())
                .status(c.getStatus())
                .priority(c.getPriority())
                .suggestedPriority(c.getSuggestedPriority())
                .overdue(c.isOverdue())
                .closed(c.isClosed())
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