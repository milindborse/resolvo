package com.resolvo.backend.complaint;

import com.resolvo.backend.common.dto.ApiResponse;
import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.common.enums.ComplaintCategory;
import com.resolvo.backend.common.enums.ComplaintPriority;
import com.resolvo.backend.common.enums.ComplaintStatus;
import com.resolvo.backend.complaint.dto.ComplaintCreateRequest;
import com.resolvo.backend.complaint.dto.ComplaintHistoryResponse;
import com.resolvo.backend.complaint.dto.ComplaintPriorityUpdateRequest;
import com.resolvo.backend.complaint.dto.ComplaintResponse;
import com.resolvo.backend.complaint.dto.ComplaintStatusUpdateRequest;
import com.resolvo.backend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Complaint lifecycle, history, and filtering")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @ParameterObject @ModelAttribute ComplaintCreateRequest request,
            @RequestPart(required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal principal) {

        ComplaintResponse response = complaintService.createComplaint(request, image, principal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint raised successfully", response));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10) Pageable pageable) {

        PageResponse<ComplaintResponse> response = complaintService.getMyComplaints(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintResponse>>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) ComplaintPriority priority,
            @RequestParam(required = false) ComplaintCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20) Pageable pageable) {

        PageResponse<ComplaintResponse> response = complaintService.getAllComplaints(
                status, priority, category, fromDate, toDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {

        ComplaintResponse response = complaintService.getComplaintById(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<ComplaintHistoryResponse>>> getComplaintHistory(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {

        List<ComplaintHistoryResponse> response = complaintService.getComplaintHistory(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ComplaintResponse response = complaintService.updateStatus(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updatePriority(
            @PathVariable Long id, @Valid @RequestBody ComplaintPriorityUpdateRequest request) {

        ComplaintResponse response = complaintService.updatePriority(id, request);
        return ResponseEntity.ok(ApiResponse.success("Priority updated", response));
    }
}
