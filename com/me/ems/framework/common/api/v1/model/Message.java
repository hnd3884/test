package com.me.ems.framework.common.api.v1.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message
{
    private Long pageNumber;
    private List<MessageGroup> messages;
    private Boolean considerCount;
    private Integer messageCount;
    
    public List<MessageGroup> getMessages() {
        return this.messages;
    }
    
    public void setMessages(final List<MessageGroup> messages) {
        this.messages = messages;
    }
    
    public Boolean getConsiderCount() {
        return this.considerCount;
    }
    
    public void setConsiderCount(final Boolean considerCount) {
        this.considerCount = considerCount;
    }
    
    public Long getPageNumber() {
        return this.pageNumber;
    }
    
    public void setPageNumber(final Long pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public void addMessageGroup(final MessageGroup dcMessageGroup) {
        if (this.messages == null) {
            this.messages = new ArrayList<MessageGroup>();
        }
        this.messages.add(dcMessageGroup);
    }
    
    public Integer getMessageCount() {
        return this.messageCount;
    }
    
    public void setMessageCount(final Integer messageCount) {
        this.messageCount = messageCount;
    }
}
