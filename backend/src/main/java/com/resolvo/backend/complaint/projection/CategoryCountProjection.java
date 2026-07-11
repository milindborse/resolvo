package com.resolvo.backend.complaint.projection;

import com.resolvo.backend.common.enums.ComplaintCategory;

/**
 * Interface-based projection for the category group-by aggregate query.
 * Spring Data maps this directly off the JPQL query's aliases - no
 * intermediate entity or Java-side grouping involved.
 */
public interface CategoryCountProjection {
    ComplaintCategory getCategory();
    Long getCount();
}