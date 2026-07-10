package com.resolvo.backend.complaint;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Composable filter predicates for the admin complaint list endpoint.
 * Keeps ComplaintService free of a giant if/else filter-building block.
 */
public final class ComplaintSpecifications {

    private ComplaintSpecifications() {
    }

    public static Specification<Complaint> withFilters(ComplaintStatus status, ComplaintPriority priority,
                                                         ComplaintCategory category, LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicate = cb.and(predicate, cb.equal(root.get("priority"), priority));
            }
            if (category != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category"), category));
            }
            if (fromDate != null) {
                Instant from = fromDate.atStartOfDay(ZoneOffset.UTC).toInstant();
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            }
            if (toDate != null) {
                Instant to = toDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
                predicate = cb.and(predicate, cb.lessThan(root.get("createdAt"), to));
            }
            return predicate;
        };
    }
}
