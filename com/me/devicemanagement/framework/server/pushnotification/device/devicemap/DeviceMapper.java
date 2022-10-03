package com.me.devicemanagement.framework.server.pushnotification.device.devicemap;

import com.me.devicemanagement.framework.server.pushnotification.device.NotificationDevice;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.pushnotification.common.NotificationPlatform;

interface DeviceMapper<E>
{
    ArrayList<NotificationDevice> getMappedDevices(final E p0, final NotificationPlatform... p1) throws Exception;
    
    Long mapNewDevice(final E p0, final String p1, final NotificationPlatform p2, final String p3, final String p4) throws Exception;
}
