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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Complaint> {

    /**
     * @EntityGraph fetches `resident` in the same query instead of one lazy
     * load per row - without this, listing N complaints costs N+1 queries
     * (1 for the page, N for each row's resident) purely from ComplaintMapper
     * touching resident.getFullName()/getFlatNumber(). Applied to every
     * list-returning method below for the same reason.
     */
    @EntityGraph(attributePaths = "resident")
    Page<Complaint> findByResidentId(Long residentId, Pageable pageable);

    @Query("select count(c) from Complaint c where c.status = :status")
    long countByStatus(@Param("status") ComplaintStatus status);

    @Query("select count(c) from Complaint c where c.category = :category")
    long countByCategory(@Param("category") ComplaintCategory category);

    @Query("select count(c) from Complaint c where c.closed = false and c.createdAt < :threshold")
    long countOverdue(@Param("threshold") Instant threshold);

    // ---- Dashboard Analytics additions below (all aggregation, no Java-side grouping) ----

    long countByPriority(ComplaintPriority priority);

    @EntityGraph(attributePaths = "resident")
    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "resident")
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

    // ---- Overdue Detection additions below ----

    /**
     * Candidates for the scheduled overdue scan: still open, not yet flagged,
     * older than the configured threshold. OverdueDetectionService loads
     * these as entities (rather than a bulk UPDATE) specifically so it can
     * publish one ComplaintOverdueEvent per complaint that just crossed the
     * line - a documented trade-off of per-row events over raw UPDATE throughput.
     * No resident fetch needed here - the scheduler never touches resident.
     */
    List<Complaint> findByClosedFalseAndOverdueFalseAndCreatedAtBefore(Instant threshold);

    /** Dashboard-facing count: reads the persisted flag directly, no threshold recomputation. */
    long countByOverdueTrueAndClosedFalse();

    /** All unclosed, non-overdue complaints - used by dynamic per-priority overdue detection. */
    List<Complaint> findByClosedFalseAndOverdueFalse();
}