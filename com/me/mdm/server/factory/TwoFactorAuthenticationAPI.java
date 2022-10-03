package com.me.mdm.server.factory;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public interface TwoFactorAuthenticationAPI
{
    JSONObject getTFADetails(final JSONObject p0) throws APIHTTPException;
    
    JSONObject addTFA(final JSONObject p0) throws APIHTTPException;
    
    boolean removeTFA(final JSONObject p0) throws APIHTTPException;
}
