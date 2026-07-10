package com.resolvo.backend.auth.dto;

import com.resolvo.backend.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String flatNumber;
    private UserRole role;
}
