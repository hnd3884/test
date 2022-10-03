package com.me.mdm.core.auth;

import org.json.JSONObject;

public interface MDMAPIKeyGeneratorAPI
{
    APIKey createAPIKey(final JSONObject p0) throws Exception;
    
    APIKey generateAPIKey(final JSONObject p0);
    
    APIKey updateAPIKey(final JSONObject p0);
    
    APIKey getAPIKey(final JSONObject p0);
    
    boolean validateAPIKey(final JSONObject p0);
    
    void revokeAPIKey(final JSONObject p0);
}
