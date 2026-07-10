package com.resolvo.backend.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /** Resident-facing board: published, not soft-deleted, pinned always first. */
    Page<Notice> findByDeletedFalseAndPublishedTrueOrderByPinnedDescCreatedAtDesc(Pageable pageable);

    /** Admin-facing board: everything not soft-deleted (drafts + published), pinned first. */
    Page<Notice> findByDeletedFalseOrderByPinnedDescCreatedAtDesc(Pageable pageable);
}