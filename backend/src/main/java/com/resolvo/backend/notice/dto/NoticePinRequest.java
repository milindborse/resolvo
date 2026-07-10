package com.resolvo.backend.notice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticePinRequest {

    @NotNull(message = "pinned is required")
    private Boolean pinned;
}