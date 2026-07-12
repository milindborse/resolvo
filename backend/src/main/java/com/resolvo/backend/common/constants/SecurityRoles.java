package com.resolvo.backend.common.constants;

/**
 * Role name literals used in @PreAuthorize SpEL expressions. These must
 * stay plain compile-time String constants (Java constant folding resolves
 * them at compile time) since annotation values can't reference arbitrary
 * static fields - but centralizing them here still kills the ~15 scattered
 * "ADMIN"/"RESIDENT" string literals that existed across controllers.
 */
public final class SecurityRoles {

    private SecurityRoles() {
    }

    public static final String ADMIN = "ADMIN";
    public static final String RESIDENT = "RESIDENT";
}