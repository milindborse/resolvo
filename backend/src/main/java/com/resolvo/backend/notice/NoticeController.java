package com.resolvo.backend.notice;

import com.resolvo.backend.common.dto.ApiResponse;
import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.notice.dto.NoticeCreateRequest;
import com.resolvo.backend.notice.dto.NoticePinRequest;
import com.resolvo.backend.notice.dto.NoticeResponse;
import com.resolvo.backend.notice.dto.NoticeUpdateRequest;
import com.resolvo.backend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "Society notice board: draft/publish/pin lifecycle, resident-visible board")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a notice (draft)",
            description = "Creates a notice as an unpublished draft. Residents cannot see it until it is published via PATCH /{id}/publish.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Draft created"))
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(@Valid @RequestBody NoticeCreateRequest request) {
        NoticeResponse response = noticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Notice draft created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Edit a notice",
            description = "Partial update - only non-null fields in the request body are applied. Fails if the notice is soft-deleted.")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(
            @PathVariable Long id, @Valid @RequestBody NoticeUpdateRequest request) {
        NoticeResponse response = noticeService.updateNotice(id, request);
        return ResponseEntity.ok(ApiResponse.success("Notice updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a notice",
            description = "Marks the notice as deleted. It is hidden from every listing/detail endpoint but never physically removed.")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id) {
        noticeService.softDeleteNotice(id);
        return ResponseEntity.ok(ApiResponse.success("Notice deleted", null));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish a notice",
            description = "Makes the notice visible to residents. If the notice is marked important, this publishes a NoticePublishedEvent which triggers an email to every resident via NoticeEmailListener. Fails with 409 if already published.")
    public ResponseEntity<ApiResponse<NoticeResponse>> publishNotice(@PathVariable Long id) {
        NoticeResponse response = noticeService.publishNotice(id);
        return ResponseEntity.ok(ApiResponse.success("Notice published", response));
    }

    @PatchMapping("/{id}/pin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Pin or unpin a notice",
            description = "Pinned notices always sort above normal notices on the board, regardless of date. Fails with 409 if already in the requested pin state.")
    public ResponseEntity<ApiResponse<NoticeResponse>> setPinned(
            @PathVariable Long id, @Valid @RequestBody NoticePinRequest request) {
        NoticeResponse response = noticeService.setPinned(id, request.getPinned());
        String message = request.getPinned() ? "Notice pinned" : "Notice unpinned";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @GetMapping
    @Operation(summary = "Get the notice board",
            description = "Published notices only, pinned notices first, then newest first. Available to both residents and admins.")
    public ResponseEntity<ApiResponse<PageResponse<NoticeResponse>>> getNotices(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<NoticeResponse> response = noticeService.getPublishedNotices(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all notices (admin)",
            description = "Includes drafts and published notices, excludes soft-deleted ones. Pinned notices sort first.")
    public ResponseEntity<ApiResponse<PageResponse<NoticeResponse>>> getAllNoticesForAdmin(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<NoticeResponse> response = noticeService.getAllNoticesForAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single notice",
            description = "Admins can view any non-deleted notice, including drafts. Residents can only view published notices (403 otherwise).")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNoticeById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAdmin = principal.getUser().getRole().name().equals("ADMIN");
        NoticeResponse response = noticeService.getNoticeById(id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}