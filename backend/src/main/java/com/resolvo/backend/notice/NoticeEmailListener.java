package com.resolvo.backend.notice;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.auth.UserRepository;
import com.resolvo.backend.common.enums.UserRole;
import com.resolvo.backend.email.EmailService;
import com.resolvo.backend.email.EmailTemplates;
import com.resolvo.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to NoticePublishedEvent - fired when any notice is published.
 * Sends emails to all residents, and records in-app notifications.
 */
@Component
@RequiredArgsConstructor
public class NoticeEmailListener {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final com.resolvo.backend.notification.NotificationService notificationService;

    @EventListener
    public void onNoticePublished(NoticePublishedEvent event) {
        Notice notice = noticeRepository.findById(event.getNoticeId())
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

        for (User resident : userRepository.findAll()) {
            if (resident.getRole() == UserRole.RESIDENT) {
                String html = EmailTemplates.noticePosted(
                        resident.getFullName(), notice.getTitle(), notice.getBody(), notice.isImportant());
                String subject = notice.isImportant() ? "Important Notice - Resolvo" : "New Notice - Resolvo";
                emailService.sendHtml(resident.getEmail(), subject, html);

                // Also send in-app notification
                String title = notice.isImportant() ? "Important Notice Posted" : "New Notice Posted";
                String message = String.format("A new notice has been posted: %s", notice.getTitle());
                notificationService.createNotification(
                        resident.getId(),
                        title,
                        message,
                        com.resolvo.backend.notification.NotificationType.NOTICE_PUBLISHED,
                        notice.getId()
                );
            }
        }
    }
}
