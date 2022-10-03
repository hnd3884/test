package com.turo.pushy.apns;

import java.util.Date;
import java.util.UUID;

public interface PushNotificationResponse<T extends ApnsPushNotification>
{
    T getPushNotification();
    
    boolean isAccepted();
    
    UUID getApnsId();
    
    String getRejectionReason();
    
    Date getTokenInvalidationTimestamp();
}
