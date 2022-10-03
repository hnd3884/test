package com.me.devicemanagement.framework.server.pushnotification.notification;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.pushnotification.message.NotificationInfo;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import java.io.Serializable;

public class Notification implements Serializable
{
    Long userID;
    ArrayList<NotificationDevice> devices;
    NotificationPlatform[] platforms;
    NotificationPriority priority;
    NotificationInfo info;
    boolean isForUser;
    private boolean isAddedToTable;
    long time;
    
    Notification() {
        this.devices = new ArrayList<NotificationDevice>();
        this.platforms = new NotificationPlatform[] { NotificationPlatform.ANDROID, NotificationPlatform.IOS, NotificationPlatform.WEB };
        this.priority = NotificationPriority.HIGH;
        this.isForUser = false;
        this.isAddedToTable = false;
    }
    
    void addToTable() throws Exception {
        if (!this.isAddedToTable) {
            new NotificationTableHandler().addToTable(this);
            this.isAddedToTable = true;
        }
    }
    
    public ArrayList<String> getAndroidDeviceTokens() {
        return this.getDeviceTokens(NotificationPlatform.ANDROID);
    }
    
    public ArrayList<String> getIOSDeviceTokens() {
        return this.getDeviceTokens(NotificationPlatform.IOS);
    }
    
    public ArrayList<NotificationDevice> getAndroidDevices() {
        return this.getDevices(NotificationPlatform.ANDROID);
    }
    
    public ArrayList<NotificationDevice> getIOSDevices() {
        return this.getDevices(NotificationPlatform.IOS);
    }
    
    private ArrayList<NotificationDevice> getDevices(final NotificationPlatform platform) {
        final ArrayList<NotificationDevice> devices = new ArrayList<NotificationDevice>();
        for (final NotificationDevice device : this.getDevices()) {
            if (device.getPlatform() == platform) {
                devices.add(device);
            }
        }
        return devices;
    }
    
    private ArrayList<String> getDeviceTokens(final NotificationPlatform platform) {
        final ArrayList<String> deviceTokens = new ArrayList<String>();
        for (final NotificationDevice device : this.getDevices()) {
            if (device.getPlatform() == platform) {
                deviceTokens.add(device.getDeviceToken());
            }
        }
        return deviceTokens;
    }
    
    public NotificationPlatform[] getPlatforms() {
        return this.platforms;
    }
    
    public NotificationPriority getPriority() {
        return this.priority;
    }
    
    public NotificationInfo getInfo() {
        return this.info;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public ArrayList<NotificationDevice> getDevices() {
        return this.devices;
    }
}
