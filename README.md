# Resolvo — Society Maintenance Tracker (Backend, Phase 1)

Spring Boot backend for a society maintenance complaint tracker. Residents raise
complaints with photos; admins manage them through a controlled lifecycle with
priorities, overdue detection, a notice board, and email notifications.

## Stack
Java 21, Spring Boot 3.3, Spring Security + JWT, Spring Data JPA, PostgreSQL,
Bean Validation, Spring Events, Cloudinary, springdoc-openapi (Swagger), Maven.

## Architecture

Feature-based packages, layered strictly as Controller → Service → Repository.
JPA entities never cross the controller boundary — every endpoint deals in DTOs.

```
com.resolvo.backend
├── auth          User entity, register/login, JWT issuing
├── security      JwtService, JwtAuthenticationFilter, UserPrincipal
├── complaint     Complaint + ComplaintHistory, state machine, events, listeners
├── notice        Notice board (pinned important notices)
├── dashboard     Admin analytics (counts by status/category, overdue count)
├── email         Async email sending + HTML templates
├── common        BaseEntity, ApiResponse, PageResponse, enums
├── exception     GlobalExceptionHandler, custom exceptions
└── config        SecurityConfig, SwaggerConfig, CloudinaryConfig
```

### Complaint lifecycle & history (core design)
- `ComplaintStateMachine` is the single source of truth for valid transitions:
  `OPEN → IN_PROGRESS → RESOLVED`, with `IN_PROGRESS → OPEN` allowed as a
  rollback, and nothing allowed out of `RESOLVED` (closed, per spec).
- `ComplaintService` never writes to `ComplaintHistory` or sends email directly.
  On creation it publishes `ComplaintCreatedEvent`; on every status change it
  publishes `ComplaintStatusChangedEvent`. `ComplaintHistoryListener` (Order 1)
  and `ComplaintEmailListener` (Order 2) react independently — this is the
  Spring Events decoupling the assignment asked for, with no Kafka/RabbitMQ.
- History rows are append-only: previous status, new status, actor, remarks,
  timestamp. Nothing is ever updated or deleted from `complaint_history`.

### Overdue detection
Computed on read, not stored: `ComplaintMapper` flags a complaint `overdue`
if it's still open and older than `resolvo.complaint.overdue-threshold-days`
(default 5, configurable via `OVERDUE_THRESHOLD_DAYS`). The admin dashboard
also exposes a live overdue count via the same threshold.

### Photo handling
Multipart image upload → Cloudinary → only the returned `secure_url` is
persisted in Postgres. Raw bytes never touch our database.

## Running locally

```bash
cp .env.example .env      # fill in DB, JWT, Cloudinary, SMTP values
mvn clean install
mvn spring-boot:run
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## API summary

| Method | Endpoint | Role |
|---|---|---|
| POST | /api/v1/auth/register | public |
| POST | /api/v1/auth/login | public |
| POST | /api/v1/complaints (multipart) | RESIDENT |
| GET | /api/v1/complaints/my | RESIDENT |
| GET | /api/v1/complaints (filters: status, priority, category, fromDate, toDate) | ADMIN |
| GET | /api/v1/complaints/{id} | owner or ADMIN |
| GET | /api/v1/complaints/{id}/history | owner or ADMIN |
| PATCH | /api/v1/complaints/{id}/status | ADMIN |
| PATCH | /api/v1/complaints/{id}/priority | ADMIN |
| POST | /api/v1/notices | ADMIN |
| GET | /api/v1/notices | authenticated |
| GET | /api/v1/dashboard | ADMIN |

## Database schema (high level)
- **users**: id, full_name, email (unique, indexed), password (BCrypt), phone_number, flat_number, role, enabled
- **complaints**: id, title, description, category (indexed), status (indexed), priority (indexed), image_url, resident_id (FK), closed, created_at (indexed), updated_at
- **complaint_history**: id, complaint_id (FK, indexed), previous_status, new_status, actor_id (FK), remarks, created_at
- **notices**: id, title, body, important (indexed), created_at

## Things to do manually after unzipping

1. **Create a PostgreSQL database** named `resolvo` (or update `DB_URL`).
2. **Cloudinary**: sign up free at cloudinary.com, grab cloud name/API key/secret into `.env`.
3. **SMTP**: for Gmail, enable 2FA and generate an "App Password" — regular passwords won't work with `MAIL_PASSWORD`.
4. **JWT_SECRET**: replace the placeholder with a real random 256-bit+ base64 string before any real deployment.
5. **Run `mvn clean install`** locally — this project was assembled outside a sandbox with Maven Central access, so it has **not been compiled or test-run yet**. Check the output of the first build carefully; annotation-processing issues (Lombok/MapStruct ordering) are the most likely first-run snag if your local Maven/IDE annotation processing isn't enabled.
6. Seed at least one ADMIN user manually (via `/api/v1/auth/register` with `role: ADMIN`) since there's no bootstrap admin yet.
7. Deploy to Render (backend) + provision a managed Postgres (Render/Neon/Supabase) for production; set all `.env` values as environment variables there.
8. Write the 800-word system design write-up the assignment asks for — the "Architecture" section above gives you the bones of it (complaint history model, overdue detection, photo handling, notification flow).

## What's intentionally deferred to Phase 2
- React + TypeScript frontend
- Refresh tokens / token revocation
- Rate limiting on auth endpoints
- Retry/dead-letter handling for failed emails (currently logged and dropped)
- Pagination sort-by-overdue-first on the admin complaint list (overdue is exposed as a flag; explicit sort not yet wired)
