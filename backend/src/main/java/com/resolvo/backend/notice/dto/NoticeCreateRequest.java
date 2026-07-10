package com.resolvo.backend.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Body is required")
    @Size(max = 3000)
    private String body;

    private boolean important;

    /** Optional: admins may pin a notice right at creation instead of a separate call. */
    private boolean pinned;
}