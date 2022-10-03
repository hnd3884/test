package com.me.mdm.api.reports.integ;

import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import org.json.JSONObject;

public interface MDMAnalyticRequestHandler
{
    JSONObject getSettings() throws JSONException;
    
    void addOrUpdateSettings(final JSONObject p0) throws SyMException;
    
    void deleteSettings() throws SyMException;
}
