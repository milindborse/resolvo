package com.resolvo.backend.complaint.projection;

/**
 * Backs the native monthly-stats query. monthLabel is pre-formatted as
 * 'YYYY-MM' by the database (to_char/date_trunc) so no date math happens
 * on the Java side at all.
 */
public interface MonthlyStatsProjection {
    String getMonthLabel();
    Long getTotalCount();
    Long getResolvedCount();
}