package com.resolvo.backend.notice;

import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.notice.dto.NoticeCreateRequest;
import com.resolvo.backend.notice.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .important(request.isImportant())
                .build();

        Notice saved = noticeRepository.save(notice);
        eventPublisher.publishEvent(new NoticeCreatedEvent(this, saved.getId()));
        return toResponse(saved);
    }

    public PageResponse<NoticeResponse> getNotices(Pageable pageable) {
        Page<Notice> page = noticeRepository.findAllByOrderByImportantDescCreatedAtDesc(pageable);
        return new PageResponse<>(page.map(this::toResponse));
    }

    private NoticeResponse toResponse(Notice n) {
        return NoticeResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .important(n.isImportant())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
