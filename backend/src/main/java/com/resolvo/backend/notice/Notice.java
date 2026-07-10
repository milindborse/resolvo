package com.resolvo.backend.notice;

import com.resolvo.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Notice board entry with a small lifecycle of its own:
 * draft (published=false) -> published (published=true, publishedAt set).
 * Soft-deleted notices are never physically removed - `deleted` just hides
 * them from every query, the same audit-friendly pattern used elsewhere
 * in this codebase (e.g. ComplaintHistory is append-only).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notices", indexes = {
        @Index(name = "idx_notice_important", columnList = "important"),
        @Index(name = "idx_notice_pinned", columnList = "pinned"),
        @Index(name = "idx_notice_published", columnList = "published"),
        @Index(name = "idx_notice_deleted", columnList = "deleted")
})
public class Notice extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 3000)
    private String body;

    /** Marks a notice as important - drives the email-on-publish event, not visual pinning. */
    @Builder.Default
    @Column(nullable = false)
    private boolean important = false;

    /** Admin-controlled: pinned notices always sort above normal ones on the board. */
    @Builder.Default
    @Column(nullable = false)
    private boolean pinned = false;

    /** Drafts are only visible to admins; residents only ever see published notices. */
    @Builder.Default
    @Column(nullable = false)
    private boolean published = false;

    private Instant publishedAt;

    /** Soft-delete flag. Deleted notices are excluded from every repository query. */
    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;
}