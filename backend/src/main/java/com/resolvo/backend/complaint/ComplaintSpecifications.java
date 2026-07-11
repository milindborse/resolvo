package com.resolvo.backend.complaint;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Composable filter predicates for the admin complaint list endpoint.
 * Keeps ComplaintService free of a giant if/else filter-building block.
 * Every filter here is pushed into the generated SQL via the Criteria API -
 * nothing is ever filtered in Java after the query returns.
 */
public final class ComplaintSpecifications {

    private ComplaintSpecifications() {
    }

    public static Specification<Complaint> withFilters(ComplaintStatus status, ComplaintPriority priority,
                                                         ComplaintCategory category, LocalDate fromDate, LocalDate toDate,
                                                         String residentName, Boolean overdue, String keyword) {
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
            if (overdue != null) {
                predicate = cb.and(predicate, cb.equal(root.get("overdue"), overdue));
            }
            if (residentName != null && !residentName.isBlank()) {
                Join<Complaint, User> residentJoin = root.join("resident");
                predicate = cb.and(predicate,
                        cb.like(cb.lower(residentJoin.get("fullName")), "%" + residentName.toLowerCase() + "%"));
            }
            if (keyword != null && !keyword.isBlank()) {
                String likeTerm = "%" + keyword.toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), likeTerm);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), likeTerm);
                predicate = cb.and(predicate, cb.or(titleMatch, descriptionMatch));
            }
            return predicate;
        };
    }
}