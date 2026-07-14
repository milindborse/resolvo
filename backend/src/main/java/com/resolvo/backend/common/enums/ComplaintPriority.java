package com.resolvo.backend.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ComplaintPriority {
    LOW(7),
    MEDIUM(5),
    HIGH(2);

    /** Number of days after which a complaint of this priority becomes overdue. */
    private final int overdueDays;
}
