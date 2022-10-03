package com.me.devicemanagement.onpremise.start.startupstatus;

public interface StartupStatusNotifierApi
{
    void subscribeStartupStatusNotification();
    
    boolean isStatusNotifierRunning();
    
    void removeStatusNotifier(final String p0);
}
