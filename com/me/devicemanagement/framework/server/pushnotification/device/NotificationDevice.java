package com.me.devicemanagement.framework.server.pushnotification.device;

import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;

public class NotificationDevice
{
    private final Long deviceID;
    private final String deviceToken;
    private final NotificationPlatform platform;
    
    public NotificationDevice(final Long deviceID, final NotificationPlatform platform, final String deviceToken) {
        this.deviceID = deviceID;
        this.platform = platform;
        this.deviceToken = deviceToken;
    }
    
    public Long getDeviceID() {
        return this.deviceID;
    }
    
    public NotificationPlatform getPlatform() {
        return this.platform;
    }
    
    public String getDeviceToken() {
        return this.deviceToken;
    }
}
