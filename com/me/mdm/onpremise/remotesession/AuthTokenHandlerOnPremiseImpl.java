package com.me.mdm.onpremise.remotesession;

import org.json.JSONException;
import java.util.HashMap;
import com.me.mdm.server.remotesession.AssistAPIManager;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMOnPremisesAPIFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.AssistAuthTokenHandlerAPI;

public class AuthTokenHandlerOnPremiseImpl implements AssistAuthTokenHandlerAPI
{
    protected Logger logger;
    
    public AuthTokenHandlerOnPremiseImpl() {
        this.logger = Logger.getLogger("MDMRemoteControlLogger");
    }
    
    public boolean isAssistIntegrated(final Long customerId) {
        return new AssistAuthTokenHandler().isAssistIntegrated(customerId);
    }
    
    public JSONObject generateSession(final Long customerId) {
        try {
            final JSONObject assistAccDetails = new AssistAuthTokenHandler().getAssistAccountDetails(customerId);
            final HashMap headerparams = MDMOnPremisesAPIFactoryProvider.getAssistApiHandlerImpl(assistAccDetails.getInt("TOKEN_PARAM")).getAssistHeaderMap(assistAccDetails);
            if (headerparams.containsKey("Authorization")) {
                if (headerparams.get("Authorization").toString().startsWith("Zoho-oauthtoken")) {
                    this.logger.log(Level.INFO, "Successfully obtained auth key");
                }
                else {
                    this.logger.log(Level.SEVERE, "Failed to get auth token");
                }
            }
            else {
                this.logger.log(Level.SEVERE, "Failed to get auth token");
            }
            final AssistAPIManager assistAPIManager = new AssistAPIManager();
            final HashMap<String, String> queryParams = new HashMap<String, String>();
            final JSONObject assistProperties = new JSONObject();
            assistProperties.put("customerId", (Object)customerId);
            return assistAPIManager.generateSession(headerparams, (HashMap)queryParams, assistProperties);
        }
        catch (final JSONException ex) {
            Logger.getLogger(AuthTokenHandlerOnPremiseImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return null;
        }
    }
    
    public String getAssistSessionUrl(final Long cusID) {
        final JSONObject assistDetails = new AssistAuthTokenHandler().getAssistAccountDetails(cusID);
        String domain = null;
        String sessionKeyUrl = null;
        try {
            domain = assistDetails.optString("CUSTOMER_COUNTRY_CODE", "--");
            sessionKeyUrl = MDMOnPremisesAPIFactoryProvider.getAssistApiHandlerImpl(assistDetails.getInt("TOKEN_PARAM")).getAssistSessionUrl(domain);
        }
        catch (final Exception e) {
            Logger.getLogger(AuthTokenHandlerOnPremiseImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return sessionKeyUrl;
    }
}
