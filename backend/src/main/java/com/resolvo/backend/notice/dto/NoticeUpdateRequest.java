package com.resolvo.backend.notice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * All fields optional/nullable - only non-null fields are applied by
 * NoticeService.updateNotice, so admins can PATCH-style edit just the
 * pieces they want to change via a single PUT endpoint.
 */
@Getter
@Setter
public class NoticeUpdateRequest {

    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @Size(max = 3000, message = "Body must be at most 3000 characters")
    private String body;

    private Boolean important;

    private Boolean pinned;
}