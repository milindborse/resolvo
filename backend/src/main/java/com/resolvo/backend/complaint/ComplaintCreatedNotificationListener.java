package com.resolvo.backend.complaint;

import com.resolvo.backend.auth.User;
import com.resolvo.backend.auth.UserRepository;
import com.resolvo.backend.common.enums.UserRole;
import com.resolvo.backend.complaint.event.ComplaintCreatedEvent;
import com.resolvo.backend.exception.ResourceNotFoundException;
import com.resolvo.backend.notification.NotificationService;
import com.resolvo.backend.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintCreatedNotificationListener {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @EventListener
    public void onComplaintCreated(ComplaintCreatedEvent event) {
        Complaint complaint = complaintRepository.findById(event.getComplaintId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        String title = "New Complaint Raised";
        String message = String.format("Resident %s (%s) raised: %s",
                complaint.getResident().getFullName(),
                complaint.getResident().getFlatNumber(),
                complaint.getTitle());

        for (User admin : userRepository.findAll()) {
            if (admin.getRole() == UserRole.ADMIN) {
                notificationService.createNotification(
                        admin.getId(),
                        title,
                        message,
                        NotificationType.COMPLAINT_CREATED,
                        complaint.getId()
                );
            }
        }
    }
}
