package com.resolvo.backend.notice;

import com.resolvo.backend.common.dto.ApiResponse;
import com.resolvo.backend.common.dto.PageResponse;
import com.resolvo.backend.notice.dto.NoticeCreateRequest;
import com.resolvo.backend.notice.dto.NoticeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "Society notice board")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NoticeResponse>> createNotice(@Valid @RequestBody NoticeCreateRequest request) {
        NoticeResponse response = noticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Notice posted", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NoticeResponse>>> getNotices(
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<NoticeResponse> response = noticeService.getNotices(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
