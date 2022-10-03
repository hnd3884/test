package com.me.devicemanagement.framework.server.pushnotification.message;

public class CustomNotificationPayload
{
    String title;
    String message;
    CustomPayload payload;
    
    public String getTitle() {
        return this.title;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public CustomPayload getPayload() {
        return this.payload;
    }
    
    private CustomNotificationPayload() {
    }
    
    public static class Builder
    {
        private CustomNotificationPayload info;
        
        public Builder() {
            this.info = new CustomNotificationPayload(null);
        }
        
        public Builder withTitle(final String title) {
            this.info.title = title;
            return this;
        }
        
        public Builder withMessage(final String message) {
            this.info.message = message;
            return this;
        }
        
        public Builder withPayload(final CustomPayload payload) {
            this.info.payload = payload;
            return this;
        }
        
        public CustomNotificationPayload build() {
            return this.info;
        }
    }
}
