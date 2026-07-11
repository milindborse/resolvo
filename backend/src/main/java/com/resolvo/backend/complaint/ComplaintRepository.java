package com.resolvo.backend.complaint;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.complaint.projection.CategoryCountProjection;
import com.resolvo.backend.complaint.projection.MonthlyStatsProjection;
import com.resolvo.backend.complaint.projection.PriorityCountProjection;
import com.resolvo.backend.complaint.projection.StatusCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Complaint> {

    Page<Complaint> findByResidentId(Long residentId, Pageable pageable);

    @Query("select count(c) from Complaint c where c.status = :status")
    long countByStatus(@Param("status") ComplaintStatus status);

    @Query("select count(c) from Complaint c where c.category = :category")
    long countByCategory(@Param("category") ComplaintCategory category);

    @Query("select count(c) from Complaint c where c.closed = false and c.createdAt < :threshold")
    long countOverdue(@Param("threshold") Instant threshold);

    // ---- Dashboard Analytics additions below (all aggregation, no Java-side grouping) ----

    long countByPriority(ComplaintPriority priority);

    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Complaint> findByStatusOrderByUpdatedAtDesc(ComplaintStatus status, Pageable pageable);

    @Query("select c.category as category, count(c) as count from Complaint c group by c.category")
    List<CategoryCountProjection> countGroupedByCategory();

    @Query("select c.priority as priority, count(c) as count from Complaint c group by c.priority")
    List<PriorityCountProjection> countGroupedByPriority();

    @Query("select c.status as status, count(c) as count from Complaint c group by c.status")
    List<StatusCountProjection> countGroupedByStatus();

    /**
     * Monthly totals + resolved-so-far counts, grouped and formatted entirely
     * in Postgres. resolvedCount reflects complaints created that month whose
     * CURRENT status is RESOLVED (there's no dedicated resolved_at column) -
     * if exact resolution-month tracking is needed later, ComplaintHistory
     * has the authoritative transition timestamp to join against instead.
     */
    @Query(value = "SELECT to_char(date_trunc('month', created_at), 'YYYY-MM') AS monthLabel, "
            + "COUNT(*) AS totalCount, "
            + "COUNT(CASE WHEN status = 'RESOLVED' THEN 1 END) AS resolvedCount "
            + "FROM complaints "
            + "GROUP BY monthLabel "
            + "ORDER BY monthLabel DESC",
            countQuery = "SELECT COUNT(DISTINCT to_char(date_trunc('month', created_at), 'YYYY-MM')) FROM complaints",
            nativeQuery = true)
    Page<MonthlyStatsProjection> findMonthlyStats(Pageable pageable);
}