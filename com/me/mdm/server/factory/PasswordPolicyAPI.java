package com.me.mdm.server.factory;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public interface PasswordPolicyAPI
{
    JSONObject getPasswordPolicyDetails(final JSONObject p0) throws APIHTTPException;
    
    JSONObject addPasswordPolicy(final JSONObject p0) throws APIHTTPException;
    
    void removePasswordPolicy(final JSONObject p0) throws APIHTTPException;
}
