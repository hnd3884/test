package com.me.mdm.server.apps.businessstore;

import org.json.JSONArray;
import org.json.JSONObject;

public interface EnterpriseBusinessStore
{
    JSONObject getCredential(final JSONObject p0) throws Exception;
    
    JSONObject inviteUsers(final JSONObject p0) throws Exception;
    
    JSONObject getUsers(final JSONObject p0) throws Exception;
    
    JSONObject getDevices(final JSONObject p0) throws Exception;
    
    JSONObject getAppDetails(final JSONObject p0) throws Exception;
    
    JSONObject installAppsToUsers(final JSONObject p0) throws Exception;
    
    JSONObject installAppsToDevices(final JSONObject p0) throws Exception;
    
    JSONObject assignAppsToUsers(final JSONObject p0) throws Exception;
    
    JSONObject getAppsAssignedForUser(final JSONObject p0) throws Exception;
    
    JSONObject getAppsAssignedForDevice() throws Exception;
    
    JSONObject removeAppsToUsers(final JSONObject p0, final JSONArray p1) throws Exception;
    
    JSONObject removeAppsToDevices(final JSONObject p0) throws Exception;
    
    JSONObject processAppData(final JSONObject p0) throws Exception;
    
    boolean isAccountActive() throws Exception;
}
