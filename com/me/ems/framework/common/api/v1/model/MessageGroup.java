package com.me.ems.framework.common.api.v1.model;

import java.util.ArrayList;
import java.util.List;

public class MessageGroup
{
    private Long messageCount;
    private String messageType;
    private List<MessageContent> messageLinks;
    private String messageTypeLabel;
    
    public String getMessageType() {
        return this.messageType;
    }
    
    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }
    
    public List<MessageContent> getMessageLinks() {
        return this.messageLinks;
    }
    
    public void setMessageLinks(final List<MessageContent> messageLinks) {
        this.messageLinks = messageLinks;
    }
    
    public void addMessageContent(final MessageContent dcMessageContent) {
        if (this.messageLinks == null) {
            this.messageLinks = new ArrayList<MessageContent>();
        }
        this.messageLinks.add(dcMessageContent);
    }
    
    public void incrementMessageCount() {
        if (this.messageCount == null) {
            this.messageCount = 0L;
        }
        final Long messageCount = this.messageCount;
        ++this.messageCount;
    }
    
    public Long getMessageCount() {
        return this.messageCount;
    }
    
    public void setMessageCount(final Long messageCount) {
        this.messageCount = messageCount;
    }
    
    public String getMessageTypeLabel() {
        return this.messageTypeLabel;
    }
    
    public void setMessageTypeLabel(final String messageTypeLabel) {
        this.messageTypeLabel = messageTypeLabel;
    }
}
