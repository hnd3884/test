package com.me.devicemanagement.framework.server.pushnotification.message;

import org.json.JSONObject;

public class CustomPayload
{
    NotificationType notificationType;
    int moduleID;
    String nType;
    String link;
    JSONObject summary;
    
    public NotificationType getNotificationType() {
        return this.notificationType;
    }
    
    public int getModuleID() {
        return this.moduleID;
    }
    
    public String getNType() {
        return this.nType;
    }
    
    public String getLink() {
        return this.link;
    }
    
    public JSONObject getSummary() {
        return this.summary;
    }
    
    private CustomPayload() {
    }
    
    public enum NotificationType
    {
        INFORMATION, 
        WARNING, 
        ERROR, 
        CRITICAL;
    }
    
    public static class Builder
    {
        private CustomPayload info;
        
        public Builder(final NotificationType notificationType) {
            this.info = new CustomPayload(null);
            this.info.notificationType = notificationType;
        }
        
        public Builder withModuleID(final int moduleID) {
            this.info.moduleID = moduleID;
            return this;
        }
        
        public Builder withNType(final String nType) {
            this.info.nType = nType;
            return this;
        }
        
        public Builder withLink(final String link) {
            this.info.link = link;
            return this;
        }
        
        public Builder withSummary(final JSONObject summary) {
            this.info.summary = summary;
            return this;
        }
        
        public CustomPayload build() {
            return this.info;
        }
    }
}
