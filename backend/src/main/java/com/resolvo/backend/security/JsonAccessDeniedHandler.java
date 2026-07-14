package com.resolvo.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolvo.backend.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Handles URL-level role denials (e.g. a RESIDENT hitting /api/v1/dashboard/**,
 * which is gated in SecurityConfig's authorizeHttpRequests, not @PreAuthorize).
 * Those are intercepted before Spring MVC dispatch, so GlobalExceptionHandler's
 * @ExceptionHandler(AccessDeniedException.class) never sees them - this keeps
 * the response in the same ApiErrorResponse JSON shape regardless of which
 * layer rejected the request.
 */
@Component
@RequiredArgsConstructor
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws java.io.IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("You do not have permission to perform this action")
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}