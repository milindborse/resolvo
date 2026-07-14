package com.resolvo.backend.complaint;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.auth.UserRepository;
import com.resolvo.backend.common.enums.UserRole;
import com.resolvo.backend.complaint.event.ComplaintOverdueEvent;
import com.resolvo.backend.exception.ResourceNotFoundException;
import com.resolvo.backend.notification.NotificationService;
import com.resolvo.backend.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reaction to ComplaintOverdueEvent.
 * Sends an in-app notification to all admins when a complaint is overdue.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintOverdueListener {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @EventListener
    public void onComplaintOverdue(ComplaintOverdueEvent event) {
        log.info("Complaint {} has crossed the overdue threshold - sending notification to admins", event.getComplaintId());
        Complaint complaint = complaintRepository.findById(event.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        String title = "Complaint Overdue Alert";
        String message = String.format("Complaint \"%s\" raised by %s is now overdue.",
                complaint.getTitle(), complaint.getResident().getFullName());

        for (User admin : userRepository.findAll()) {
            if (admin.getRole() == UserRole.ADMIN) {
                notificationService.createNotification(
                        admin.getId(),
                        title,
                        message,
                        NotificationType.COMPLAINT_OVERDUE,
                        complaint.getId()
                );
            }
        }
    }
}