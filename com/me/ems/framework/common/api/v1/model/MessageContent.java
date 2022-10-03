package com.me.ems.framework.common.api.v1.model;

import java.util.Map;

public class MessageContent
{
    private Map messageContent;
    private Boolean enableUserClose;
    private String msgContentID;
    private String msgGroupID;
    private String msgType;
    
    public Map getMessageContent() {
        return this.messageContent;
    }
    
    public void setMessageContent(final Map messageContent) {
        this.messageContent = messageContent;
    }
    
    public Boolean getEnableUserClose() {
        return this.enableUserClose;
    }
    
    public void setEnableUserClose(final Boolean enableUserClose) {
        this.enableUserClose = enableUserClose;
    }
    
    public String getMsgGroupID() {
        return this.msgGroupID;
    }
    
    public void setMsgGroupID(final String msgGroupID) {
        this.msgGroupID = msgGroupID;
    }
    
    public String getMsgContentID() {
        return this.msgContentID;
    }
    
    public void setMsgContentID(final String msgContentID) {
        this.msgContentID = msgContentID;
    }
    
    public String getMsgType() {
        return this.msgType;
    }
    
    public void setMsgType(final String msgType) {
        this.msgType = msgType;
    }
}
