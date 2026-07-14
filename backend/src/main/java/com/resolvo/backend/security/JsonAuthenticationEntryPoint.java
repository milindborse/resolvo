package com.resolvo.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resolvo.backend.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Without this, Spring Security's default entry point returns a bare 403
 * with no JSON body for ANY request with a missing/invalid/expired JWT -
 * indistinguishable from a genuine role-based 403, and without the
 * ApiErrorResponse shape the rest of this API guarantees everywhere else.
 * This is what should fire for "not authenticated at all" - a real 401,
 * in the same JSON shape GlobalExceptionHandler produces for everything else,
 * so the frontend's 401 interceptor (auto-logout + redirect to /login)
 * actually triggers instead of silently misfiring as a permission error.
 */
@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws java.io.IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Authentication required - please log in again")
                .path(request.getRequestURI())
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}