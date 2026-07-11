package com.resolvo.backend.complaint;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.complaint.dto.ComplaintCreateRequest;
import com.resolvo.backend.complaint.dto.ComplaintHistoryResponse;
import com.resolvo.backend.complaint.dto.ComplaintPriorityUpdateRequest;
import com.resolvo.backend.complaint.dto.ComplaintResponse;
import com.resolvo.backend.complaint.dto.ComplaintStatusUpdateRequest;
import com.resolvo.backend.complaint.event.ComplaintCreatedEvent;
import com.resolvo.backend.complaint.event.ComplaintStatusChangedEvent;
import com.resolvo.backend.exception.ResourceNotFoundException;
import com.resolvo.backend.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintHistoryRepository historyRepository;
    private final ComplaintMapper mapper;
    private final ComplaintStateMachine stateMachine;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request, MultipartFile image, User resident) {
        String imageUrl = (image == null || image.isEmpty()) ? null : cloudinaryUploadService.upload(image);

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .status(ComplaintStatus.OPEN)
                .priority(ComplaintPriority.LOW)
                .imageUrl(imageUrl)
                .resident(resident)
                .closed(false)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        eventPublisher.publishEvent(new ComplaintCreatedEvent(this, saved.getId(), resident));

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> getMyComplaints(Long residentId, Pageable pageable) {
        Page<Complaint> page = complaintRepository.findByResidentId(residentId, pageable);
        return new PageResponse<>(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public PageResponse<ComplaintResponse> getAllComplaints(ComplaintStatus status, ComplaintPriority priority,
                                                             ComplaintCategory category, LocalDate fromDate,
                                                             LocalDate toDate, Pageable pageable) {
        var spec = ComplaintSpecifications.withFilters(status, priority, category, fromDate, toDate);
        Page<Complaint> page = complaintRepository.findAll(spec, pageable);
        return new PageResponse<>(page.map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long complaintId, User requester) {
        Complaint complaint = findOrThrow(complaintId);
        assertViewable(complaint, requester);
        return mapper.toResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintHistoryResponse> getComplaintHistory(Long complaintId, User requester) {
        Complaint complaint = findOrThrow(complaintId);
        assertViewable(complaint, requester);
        return historyRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId).stream()
                .map(mapper::toHistoryResponse)
                .toList();
    }

    @Transactional
    public ComplaintResponse updateStatus(Long complaintId, ComplaintStatusUpdateRequest request, User actor) {
        Complaint complaint = findOrThrow(complaintId);

        ComplaintStatus previous = complaint.getStatus();
        stateMachine.validateTransition(previous, request.getNewStatus());

        complaint.setStatus(request.getNewStatus());
        if (request.getNewStatus() == ComplaintStatus.RESOLVED) {
            complaint.setClosed(true);
        }
        Complaint saved = complaintRepository.save(complaint);

        eventPublisher.publishEvent(new ComplaintStatusChangedEvent(
                this, saved.getId(), previous, request.getNewStatus(), actor, request.getRemarks()));

        return mapper.toResponse(saved);
    }

    @Transactional
    public ComplaintResponse updatePriority(Long complaintId, ComplaintPriorityUpdateRequest request) {
        Complaint complaint = findOrThrow(complaintId);
        complaint.setPriority(request.getPriority());
        return mapper.toResponse(complaintRepository.save(complaint));
    }

    private Complaint findOrThrow(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
    }

    private void assertViewable(Complaint complaint, User requester) {
        boolean isAdmin = requester.getRole().name().equals("ADMIN");
        boolean isOwner = complaint.getResident().getId().equals(requester.getId());
        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException("You cannot view another resident's complaint");
        }
    }
}