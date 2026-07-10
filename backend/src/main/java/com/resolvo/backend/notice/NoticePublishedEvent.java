package com.resolvo.backend.notice;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published only when an IMPORTANT notice is published (not on create,
 * not on edit). NoticeService never talks to EmailService directly -
 * NoticeEmailListener reacts to this event, same decoupling pattern as
 * ComplaintStatusChangedEvent / ComplaintEmailListener.
 */
@Getter
public class NoticePublishedEvent extends ApplicationEvent {
    private final Long noticeId;

    public NoticePublishedEvent(Object source, Long noticeId) {
        super(source);
        this.noticeId = noticeId;
    }
}