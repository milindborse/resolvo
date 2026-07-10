package com.resolvo.backend.complaint.dto;

import com.resolvo.backend.common.enums.ComplaintPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintPriorityUpdateRequest {

    @NotNull(message = "Priority is required")
    private ComplaintPriority priority;
}
