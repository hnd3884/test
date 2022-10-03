package com.me.devicemanagement.framework.server.api;

import org.json.JSONObject;

public interface EvaluatorAPI
{
    void addOrIncrementClickCountForTrialUsers(final String p0, final String p1);
    
    void addOrIncrementClickCountForAll(final String p0, final String p1);
    
    void addOrIncrementOnDemandActionsCount(final String p0, final String p1);
    
    JSONObject getJSONFromFileForModule(final String p0);
    
    void addUserProperty(final String p0, final String p1, final Object p2);
}
