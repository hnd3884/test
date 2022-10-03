package com.me.devicemanagement.framework.server.api;

import org.json.JSONObject;

public interface DCSDPRequestAPI
{
    JSONObject addHiddenRoles(final JSONObject p0);
    
    void changeSDPUser(final Long p0, final String p1, final boolean p2);
}
