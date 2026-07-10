package com.resolvo.backend.email;

import com.resolvo.backend.common.enums.ComplaintStatus;

public final class EmailTemplates {

    private EmailTemplates() {
    }

    public static String complaintStatusChanged(String residentName, String complaintTitle,
                                                  ComplaintStatus previousStatus, ComplaintStatus newStatus,
                                                  String remarks) {
        String remarksBlock = (remarks == null || remarks.isBlank())
                ? ""
                : "<p><strong>Note from admin:</strong> " + remarks + "</p>";

        return "<div style=\"font-family:Arial,sans-serif;line-height:1.5\">"
                + "<h2>Complaint Update - Resolvo</h2>"
                + "<p>Hi " + residentName + ",</p>"
                + "<p>Your complaint \"" + complaintTitle + "\" has moved from "
                + "<strong>" + (previousStatus == null ? "NEW" : previousStatus) + "</strong> to "
                + "<strong>" + newStatus + "</strong>.</p>"
                + remarksBlock
                + "<p>You can view the full history in your Resolvo dashboard.</p>"
                + "<p>- Resolvo Society Management</p>"
                + "</div>";
    }

    public static String noticePosted(String residentName, String noticeTitle, String noticeBody, boolean important) {
        return "<div style=\"font-family:Arial,sans-serif;line-height:1.5\">"
                + "<h2>" + (important ? "Important Notice" : "New Notice") + " - Resolvo</h2>"
                + "<p>Hi " + residentName + ",</p>"
                + "<h3>" + noticeTitle + "</h3>"
                + "<p>" + noticeBody + "</p>"
                + "<p>- Resolvo Society Management</p>"
                + "</div>";
    }
}
