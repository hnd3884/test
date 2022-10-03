package com.me.devicemanagement.onpremise.server.admin;

import org.json.simple.JSONObject;

public interface CustomColumnListener
{
    void customColumnAdded(final JSONObject p0);
    
    void customColumnModified(final JSONObject p0);
    
    void customColumnDeleted(final JSONObject p0);
}
