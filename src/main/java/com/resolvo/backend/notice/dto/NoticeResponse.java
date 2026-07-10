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
    private Instant createdAt;
}
