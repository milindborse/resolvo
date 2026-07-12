package com.resolvo.backend.complaint;

import com.resolvo.backend.common.constants.ApiPaths;
import com.resolvo.backend.common.constants.SecurityRoles;
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
import com.resolvo.backend.complaint.dto.ComplaintSummaryResponse;
import com.resolvo.backend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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

@Slf4j
@RestController
@RequestMapping(ApiPaths.Complaints.BASE)
@RequiredArgsConstructor
@Tag(name = "Complaints", description = "Complaint lifecycle, history, and advanced search/filtering")
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('" + SecurityRoles.RESIDENT + "')")
    @Operation(summary = "Raise a complaint", description = "Multipart form: title, description, category (required) and an optional image, uploaded to Cloudinary.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @ParameterObject @ModelAttribute ComplaintCreateRequest request,
            @RequestPart(required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.info("Resident id={} raising complaint: category={}, hasImage={}",
                principal.getId(), request.getCategory(), image != null && !image.isEmpty());
        ComplaintResponse response = complaintService.createComplaint(request, image, principal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint raised successfully", response));
    }

    @GetMapping(ApiPaths.Complaints.MY)
    @PreAuthorize("hasRole('" + SecurityRoles.RESIDENT + "')")
    @Operation(summary = "My complaints", description = "Residents only ever see their own complaints. Returns the lightweight summary DTO (no description/imageUrl) - use GET /{id} for full detail.")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintSummaryResponse>>> getMyComplaints(
            @AuthenticationPrincipal UserPrincipal principal,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {

        PageResponse<ComplaintSummaryResponse> response = complaintService.getMyComplaints(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('" + SecurityRoles.ADMIN + "')")
    @Operation(summary = "Advanced complaint search (admin)",
            description = "Every filter below is optional and composable. All filtering happens in the database via "
                    + "a Specification (Criteria API) - nothing is filtered in Java after the query returns. "
                    + "Sorting/pagination use standard Spring Pageable query params (page, size, sort). "
                    + "Returns the lightweight summary DTO, not the full complaint payload.")
    public ResponseEntity<ApiResponse<PageResponse<ComplaintSummaryResponse>>> getAllComplaints(
            @Parameter(description = "Exact match on complaint status") @RequestParam(required = false) ComplaintStatus status,
            @Parameter(description = "Exact match on complaint priority") @RequestParam(required = false) ComplaintPriority priority,
            @Parameter(description = "Exact match on complaint category") @RequestParam(required = false) ComplaintCategory category,
            @Parameter(description = "Inclusive lower bound on createdAt (ISO date, e.g. 2026-01-01)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Inclusive upper bound on createdAt (ISO date)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Case-insensitive partial match on the resident's full name")
            @RequestParam(required = false) String residentName,
            @Parameter(description = "Filter by the persisted overdue flag (true/false)")
            @RequestParam(required = false) Boolean overdue,
            @Parameter(description = "Case-insensitive partial match across title and description")
            @RequestParam(required = false) String keyword,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {

        PageResponse<ComplaintSummaryResponse> response = complaintService.getAllComplaints(
                status, priority, category, fromDate, toDate, residentName, overdue, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(ApiPaths.Complaints.BY_ID)
    @Operation(summary = "Get a single complaint (full detail)", description = "Owner or ADMIN only. Returns the full ComplaintResponse including description and imageUrl.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {

        ComplaintResponse response = complaintService.getComplaintById(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(ApiPaths.Complaints.HISTORY)
    @Operation(summary = "Get a complaint's audit trail", description = "Owner or ADMIN only. Append-only history rows, oldest first.")
    public ResponseEntity<ApiResponse<List<ComplaintHistoryResponse>>> getComplaintHistory(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {

        List<ComplaintHistoryResponse> response = complaintService.getComplaintHistory(id, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping(ApiPaths.Complaints.STATUS)
    @PreAuthorize("hasRole('" + SecurityRoles.ADMIN + "')")
    @Operation(summary = "Update complaint status", description = "Enforced by ComplaintStateMachine. Resolving a complaint also clears its overdue flag, if set.")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        log.info("Admin id={} updating complaint id={} status -> {}", principal.getId(), id, request.getNewStatus());
        ComplaintResponse response = complaintService.updateStatus(id, request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    @PatchMapping(ApiPaths.Complaints.PRIORITY)
    @PreAuthorize("hasRole('" + SecurityRoles.ADMIN + "')")
    @Operation(summary = "Update complaint priority")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updatePriority(
            @PathVariable Long id, @Valid @RequestBody ComplaintPriorityUpdateRequest request) {

        log.info("Updating complaint id={} priority -> {}", id, request.getPriority());
        ComplaintResponse response = complaintService.updatePriority(id, request);
        return ResponseEntity.ok(ApiResponse.success("Priority updated", response));
    }
}