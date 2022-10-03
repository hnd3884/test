package com.me.mdm.server.factory;

import com.me.ems.framework.common.factory.UnifiedAuthenticationService;
import org.json.JSONObject;
import java.util.HashMap;

public interface SecureKeyProviderAPI
{
    HashMap<Integer, HashMap<String, String>> getWindowsWakeUpCredentials();
    
    String getSecret(final String p0) throws Exception;
    
    JSONObject signCSR(final JSONObject p0) throws Exception;
    
    JSONObject enrollESA(final JSONObject p0) throws Exception;
    
    JSONObject unenrollESA(final JSONObject p0) throws Exception;
    
    JSONObject processAFWRegistrationRequest(final JSONObject p0) throws Exception;
    
    JSONObject getFCMAgentNotificationSecret() throws Exception;
    
    default JSONObject getSecureKeyForOP(final JSONObject data) throws Exception {
        throw new UnsupportedOperationException("Method implementation not found");
    }
    
    UnifiedAuthenticationService getDefaultUnifiedAuthHandler();
}
