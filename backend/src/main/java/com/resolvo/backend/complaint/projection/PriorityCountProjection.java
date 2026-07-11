package com.resolvo.backend.complaint.projection;

import com.resolvo.backend.common.enums.ComplaintPriority;

public interface PriorityCountProjection {
    ComplaintPriority getPriority();
    Long getCount();
}