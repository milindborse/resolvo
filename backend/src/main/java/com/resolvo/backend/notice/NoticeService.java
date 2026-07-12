package com.resolvo.backend.notice;

import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.exception.InvalidStateTransitionException;
import com.resolvo.backend.exception.ResourceNotFoundException;
import com.resolvo.backend.exception.UnauthorizedAccessException;
import com.resolvo.backend.notice.dto.NoticeCreateRequest;
import com.resolvo.backend.notice.dto.NoticeResponse;
import com.resolvo.backend.notice.dto.NoticeUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Owns the Notice lifecycle: draft -> published, pin/unpin, soft delete.
 * Deliberately never talks to EmailService - it only ever publishes
 * NoticePublishedEvent and lets NoticeEmailListener react independently,
 * the same event-driven separation used by the complaint module.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .important(request.isImportant())
                .pinned(request.isPinned())
                .published(false)
                .deleted(false)
                .build();

        Notice saved = noticeRepository.save(notice);
        log.info("Notice draft created: id={}, important={}", saved.getId(), saved.isImportant());
        return mapper.toResponse(saved);
    }

    @Transactional
    public NoticeResponse updateNotice(Long id, NoticeUpdateRequest request) {
        Notice notice = findActiveOrThrow(id);

        if (request.getTitle() != null) {
            notice.setTitle(request.getTitle());
        }
        if (request.getBody() != null) {
            notice.setBody(request.getBody());
        }
        if (request.getImportant() != null) {
            notice.setImportant(request.getImportant());
        }
        if (request.getPinned() != null) {
            notice.setPinned(request.getPinned());
        }

        log.info("Notice id={} updated", id);
        return mapper.toResponse(noticeRepository.save(notice));
    }

    @Transactional
    public void softDeleteNotice(Long id) {
        Notice notice = findActiveOrThrow(id);
        notice.setDeleted(true);
        noticeRepository.save(notice);
        log.info("Notice id={} soft-deleted", id);
    }

    @Transactional
    public NoticeResponse publishNotice(Long id) {
        Notice notice = findActiveOrThrow(id);

        if (notice.isPublished()) {
            throw new InvalidStateTransitionException("Notice is already published");
        }

        notice.setPublished(true);
        notice.setPublishedAt(Instant.now());
        Notice saved = noticeRepository.save(notice);
        log.info("Notice id={} published (important={})", saved.getId(), saved.isImportant());

        if (saved.isImportant()) {
            eventPublisher.publishEvent(new NoticePublishedEvent(this, saved.getId()));
        }

        return mapper.toResponse(saved);
    }

    @Transactional
    public NoticeResponse setPinned(Long id, boolean pinned) {
        Notice notice = findActiveOrThrow(id);

        if (notice.isPinned() == pinned) {
            throw new InvalidStateTransitionException(
                    "Notice is already " + (pinned ? "pinned" : "unpinned"));
        }

        notice.setPinned(pinned);
        log.info("Notice id={} pinned={}", id, pinned);
        return mapper.toResponse(noticeRepository.save(notice));
    }

    /** Resident + admin board view: published only, pinned first. */
    @Transactional(readOnly = true)
    public PageResponse<NoticeResponse> getPublishedNotices(Pageable pageable) {
        Page<Notice> page = noticeRepository.findByDeletedFalseAndPublishedTrueOrderByPinnedDescCreatedAtDesc(pageable);
        return new PageResponse<>(page.map(mapper::toResponse));
    }

    /** Admin-only view: drafts + published, still pinned first. */
    @Transactional(readOnly = true)
    public PageResponse<NoticeResponse> getAllNoticesForAdmin(Pageable pageable) {
        Page<Notice> page = noticeRepository.findByDeletedFalseOrderByPinnedDescCreatedAtDesc(pageable);
        return new PageResponse<>(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public NoticeResponse getNoticeById(Long id, boolean isAdmin) {
        Notice notice = findActiveOrThrow(id);

        if (!isAdmin && !notice.isPublished()) {
            throw new UnauthorizedAccessException("This notice is not yet published");
        }

        return mapper.toResponse(notice);
    }

    private Notice findActiveOrThrow(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));

        if (notice.isDeleted()) {
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }

        return notice;
    }
}