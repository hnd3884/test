package com.me.mdm.server.factory;

import org.json.JSONObject;

public interface AssistAuthTokenHandlerAPI
{
    boolean isAssistIntegrated(final Long p0);
    
    JSONObject generateSession(final Long p0);
    
    String getAssistSessionUrl(final Long p0);
}
