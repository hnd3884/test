package com.zoho.security.agent.notification;

import org.json.JSONObject;
import com.zoho.security.agent.Components;

public interface NotificationReceiver
{
    boolean receive(final Components.COMPONENT p0, final Components.COMPONENT_NAME p1, final JSONObject p2);
    
    Object getRecentDataOnChange(final JSONObject p0, final Components.COMPONENT_NAME p1);
    
    boolean isChangePushEnabled(final Components.COMPONENT_NAME p0);
}
