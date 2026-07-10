package com.resolvo.backend.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class NoticeResponse {
    private Long id;
    private String title;
    private String body;
    private boolean important;
    private boolean pinned;
    private boolean published;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}