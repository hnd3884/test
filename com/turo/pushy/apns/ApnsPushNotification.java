package com.turo.pushy.apns;

import java.util.UUID;
import java.util.Date;

public interface ApnsPushNotification
{
    String getToken();
    
    String getPayload();
    
    Date getExpiration();
    
    DeliveryPriority getPriority();
    
    PushType getPushType();
    
    String getTopic();
    
    String getCollapseId();
    
    UUID getApnsId();
}
