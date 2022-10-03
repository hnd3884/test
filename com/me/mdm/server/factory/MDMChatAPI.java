package com.me.mdm.server.factory;

import org.json.JSONObject;

public interface MDMChatAPI
{
    JSONObject getBasicChatInfo(final JSONObject p0);
    
    String getUserEmail();
}
