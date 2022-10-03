package com.me.devicemanagement.framework.server.mailmanager;

import org.json.JSONObject;

public interface MailCallBackHandler
{
    void handleSuccessfulCompletion(final JSONObject p0);
    
    void handleMailSendingFailure(final JSONObject p0);
}
