package com.resolvo.backend.notice;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoticeCreatedEvent extends ApplicationEvent {
    private final Long noticeId;

    public NoticeCreatedEvent(Object source, Long noticeId) {
        super(source);
        this.noticeId = noticeId;
    }
}
