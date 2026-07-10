package com.resolvo.backend.notice;

import com.resolvo.backend.notice.dto.NoticeResponse;
import org.springframework.stereotype.Component;

@Component
public class NoticeMapper {

    public NoticeResponse toResponse(Notice n) {
        return NoticeResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .important(n.isImportant())
                .pinned(n.isPinned())
                .published(n.isPublished())
                .publishedAt(n.getPublishedAt())
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }
}