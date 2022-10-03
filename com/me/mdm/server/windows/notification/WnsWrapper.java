package com.me.mdm.server.windows.notification;

import ar.com.fernandospr.wns.model.WnsNotificationResponse;
import java.util.HashMap;
import java.util.List;

public interface WnsWrapper
{
    void initialize();
    
    void reinitialize();
    
    HashMap wakeUpWindowsDevices(final List p0);
    
    WnsNotificationResponse wakeUpWindowsDevice(final String p0);
}
