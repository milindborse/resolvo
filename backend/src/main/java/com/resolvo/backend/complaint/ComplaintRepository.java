package com.resolvo.backend.complaint;

import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Complaint> {

    Page<Complaint> findByResidentId(Long residentId, Pageable pageable);

    @Query("select count(c) from Complaint c where c.status = :status")
    long countByStatus(@Param("status") ComplaintStatus status);

    @Query("select count(c) from Complaint c where c.category = :category")
    long countByCategory(@Param("category") ComplaintCategory category);

    @Query("select count(c) from Complaint c where c.closed = false and c.createdAt < :threshold")
    long countOverdue(@Param("threshold") Instant threshold);
}
