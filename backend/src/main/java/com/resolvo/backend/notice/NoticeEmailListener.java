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
 * Reacts to NoticePublishedEvent - fired only when an IMPORTANT notice is
 * published. NoticeService has no reference to EmailService at all; this
 * listener is the sole place email is triggered from, mirroring
 * ComplaintEmailListener's role in the complaint module.
 */
@Component
@RequiredArgsConstructor
public class NoticeEmailListener {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @EventListener
    public void onNoticePublished(NoticePublishedEvent event) {
        Notice notice = noticeRepository.findById(event.getNoticeId())
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found"));

        for (User resident : userRepository.findAll()) {
            if (resident.getRole() == UserRole.RESIDENT) {
                String html = EmailTemplates.noticePosted(
                        resident.getFullName(), notice.getTitle(), notice.getBody(), true);
                emailService.sendHtml(resident.getEmail(), "Important Notice - Resolvo", html);
            }
        }
    }
}