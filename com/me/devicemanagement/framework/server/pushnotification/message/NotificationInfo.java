package com.me.devicemanagement.framework.server.pushnotification.message;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationType;

public class NotificationInfo
{
    String title;
    String message;
    String icon;
    String sound;
    String collapseID;
    NotificationType type;
    JSONObject payload;
    
    public String getTitle() {
        return this.title;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public String getSound() {
        return this.sound;
    }
    
    public String getCollapseID() {
        return this.collapseID;
    }
    
    public JSONObject getPayload() {
        return this.payload;
    }
    
    private NotificationInfo() {
        this.type = NotificationType.DEFAULT;
        this.payload = new JSONObject();
    }
    
    public void addInfo(final Long notificationID) throws Exception {
        new NotificationInfoTableHandler().addToTable(notificationID, this);
    }
    
    public static class Builder
    {
        private NotificationInfo info;
        
        public Builder() {
            this.info = new NotificationInfo(null);
        }
        
        public Builder withTitle(final String title) {
            this.info.title = title;
            return this;
        }
        
        public Builder withMessage(final String message) {
            this.info.message = message;
            return this;
        }
        
        public Builder withType(final NotificationType type) {
            this.info.type = type;
            return this;
        }
        
        public Builder withIcon(final String icon) {
            this.info.icon = icon;
            return this;
        }
        
        public Builder withSound(final String sound) {
            this.info.sound = sound;
            return this;
        }
        
        public Builder withCollapseID(final String collapseID) {
            this.info.collapseID = collapseID;
            return this;
        }
        
        public Builder withCustomPayload(final JSONObject payload) {
            this.info.payload = payload;
            return this;
        }
        
        public NotificationInfo build() {
            return this.info;
        }
    }
}
