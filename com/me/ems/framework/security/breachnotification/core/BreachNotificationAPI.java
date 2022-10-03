package com.me.ems.framework.security.breachnotification.core;

import java.util.Properties;

public interface BreachNotificationAPI
{
    String getUploadURL();
    
    String getFormURL();
    
    String getBuildVersion();
    
    boolean routineUserNotification(final Properties p0);
    
    boolean schedulerUpdate();
}
