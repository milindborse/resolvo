package com.resolvo.backend.complaint.projection;

import com.resolvo.backend.common.enums.ComplaintStatus;

public interface StatusCountProjection {
    ComplaintStatus getStatus();
    Long getCount();
}