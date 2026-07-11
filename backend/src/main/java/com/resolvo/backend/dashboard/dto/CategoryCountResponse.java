package com.resolvo.backend.dashboard.dto;

import com.resolvo.backend.common.enums.ComplaintCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryCountResponse {
    private ComplaintCategory category;
    private long count;
}