package com.resolvo.backend.complaint.dto;

import com.resolvo.backend.common.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private ComplaintStatus newStatus;

    @Size(max = 1000)
    private String remarks;
}
