package com.resolvo.backend.complaint;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.common.BaseEntity;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Core Complaint entity. Status transitions are never applied directly here -
 * ComplaintService enforces the state machine and publishes a domain event,
 * which is what actually drives history recording + email notification.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "complaints", indexes = {
        @Index(name = "idx_complaint_status", columnList = "status"),
        @Index(name = "idx_complaint_priority", columnList = "priority"),
        @Index(name = "idx_complaint_category", columnList = "category"),
        @Index(name = "idx_complaint_created_at", columnList = "createdAt")
})
public class Complaint extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ComplaintPriority priority = ComplaintPriority.LOW;

    @Column(length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    /**
     * True once status has ever reached RESOLVED. Enforces "closed after resolved" per spec.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean closed = false;
}
