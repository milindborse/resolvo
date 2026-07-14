package com.resolvo.backend.common.constants;

/**
 * Single source of truth for every URL path segment in the API. No
 * controller should ever hardcode a path literal - every @RequestMapping,
 * @GetMapping, etc. references a constant from here. This is what keeps a
 * path change (e.g. renaming /admin/all) a one-line edit instead of a
 * project-wide find-and-replace across controllers, tests, and Swagger docs.
 */
public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String API_BASE = "/api/v1";

    public static final class Auth {
        private Auth() {
        }

        public static final String BASE = API_BASE + "/auth";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
    }

    public static final class Complaints {
        private Complaints() {
        }

        public static final String BASE = API_BASE + "/complaints";
        public static final String MY = "/my";
        public static final String BY_ID = "/{id}";
        public static final String HISTORY = "/{id}/history";
        public static final String STATUS = "/{id}/status";
        public static final String PRIORITY = "/{id}/priority";
    }

    public static final class Notices {
        private Notices() {
        }

        public static final String BASE = API_BASE + "/notices";
        public static final String BY_ID = "/{id}";
        public static final String PUBLISH = "/{id}/publish";
        public static final String PIN = "/{id}/pin";
        public static final String ADMIN_ALL = "/admin/all";
    }

    public static final class Dashboard {
        private Dashboard() {
        }

        public static final String BASE = API_BASE + "/dashboard";
        public static final String SUMMARY = "/summary";
        public static final String BY_CATEGORY = "/by-category";
        public static final String BY_PRIORITY = "/by-priority";
        public static final String BY_STATUS = "/by-status";
        public static final String MONTHLY_STATS = "/monthly-stats";
        public static final String RECENT_CREATED = "/recent-created";
        public static final String RECENT_RESOLVED = "/recent-resolved";
    }

    public static final class Notifications {
        private Notifications() {
        }

        public static final String BASE = API_BASE + "/notifications";
        public static final String UNREAD_COUNT = "/unread-count";
        public static final String READ = "/{id}/read";
        public static final String READ_ALL = "/read-all";
    }
}