package com.me.devicemanagement.framework.server.pushnotification.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import com.me.devicemanagement.framework.server.pushnotification.device.devicemap.UserDeviceMapper;
import com.me.devicemanagement.framework.server.pushnotification.message.NotificationInfo;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPriority;

public class NotificationBuilder
{
    private Notification notification;
    
    private NotificationBuilder() {
        this.notification = new Notification();
    }
    
    public NotificationBuilder withPriority(final NotificationPriority priority) {
        if (priority == null) {
            return this;
        }
        this.notification.priority = priority;
        return this;
    }
    
    public NotificationBuilder withPlatforms(final NotificationPlatform... platforms) {
        if (platforms == null || platforms.length == 0) {
            if (this.notification.isForUser) {
                this.notification.platforms = NotificationPlatform.values();
            }
            else {
                this.notification.platforms = new NotificationPlatform[] { NotificationPlatform.ANDROID, NotificationPlatform.IOS };
            }
            return this;
        }
        this.notification.platforms = platforms;
        if (!this.notification.isForUser) {
            this.notification.platforms = NotificationPlatform.getMobileOnlyPlatforms(this.notification.platforms);
        }
        return this;
    }
    
    public NotificationBuilder withInfo(final NotificationInfo info) {
        this.notification.info = info;
        return this;
    }
    
    public NotificationBuilder onTime(final long timestamp) {
        this.notification.time = timestamp;
        return this;
    }
    
    public Notification build() throws Exception {
        if (this.notification.time == 0L) {
            this.notification.time = System.currentTimeMillis();
        }
        if (this.notification.info == null) {
            this.notification.info = new NotificationInfo.Builder().build();
        }
        if (this.notification.isForUser) {
            this.notification.devices = new UserDeviceMapper().getMappedDevices(this.notification.userID, this.notification.platforms);
        }
        else {
            this.removeDevicesBasedOnPlatform();
        }
        this.notification.addToTable();
        return this.notification;
    }
    
    private void removeDevicesBasedOnPlatform() {
        final Iterator iterator = this.notification.devices.iterator();
        while (iterator.hasNext()) {
            final NotificationDevice device = iterator.next();
            if (!Arrays.asList(this.notification.platforms).contains(device.getPlatform())) {
                iterator.remove();
            }
        }
    }
    
    public static class forDevice
    {
        public static NotificationBuilder withDevice(final NotificationDevice device) {
            final NotificationBuilder builder = new NotificationBuilder(null);
            final ArrayList<NotificationDevice> devices = new ArrayList<NotificationDevice>();
            devices.add(device);
            builder.notification.devices = devices;
            return builder;
        }
        
        public static NotificationBuilder withDevices(final ArrayList<NotificationDevice> devices) {
            final NotificationBuilder builder = new NotificationBuilder(null);
            builder.notification.devices = devices;
            return builder;
        }
    }
    
    public static class forUser
    {
        public static NotificationBuilder withID(final Long userID) throws Exception {
            final NotificationBuilder builder = new NotificationBuilder(null);
            builder.notification.isForUser = true;
            builder.notification.userID = userID;
            return builder;
        }
    }
}
