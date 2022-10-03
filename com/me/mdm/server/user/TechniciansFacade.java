package com.me.mdm.server.user;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public interface TechniciansFacade
{
    void addTechnicians(final JSONObject p0) throws APIHTTPException;
    
    void removeTechnicians(final JSONObject p0) throws APIHTTPException;
    
    JSONObject getTechnicians(final JSONObject p0) throws APIHTTPException;
    
    void updateTechnicians(final JSONObject p0);
    
    int getTotalTechniciansCount(final Long p0);
    
    JSONObject getNotifyConfiguredForUserEmail(final JSONObject p0);
}
