package com.me.mdm.api.core.users;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.user.TwoFactorAuthenticationFacade;
import com.me.mdm.api.ApiRequestHandler;

public class TwoFactorAuthenticationAPIRequestHandler extends ApiRequestHandler
{
    private TwoFactorAuthenticationFacade twoFactorAuthenticationFacade;
    
    public TwoFactorAuthenticationAPIRequestHandler() {
        this.twoFactorAuthenticationFacade = new TwoFactorAuthenticationFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.twoFactorAuthenticationFacade.getTFADetails(requestJSON));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            final JSONObject responseData = this.twoFactorAuthenticationFacade.addTFA(requestJSON);
            if (responseData.optBoolean("state_change")) {
                responseJSON.put("status", 202);
                responseJSON.remove("state_change");
                responseJSON.put("RESPONSE", (Object)responseData);
            }
            else {
                responseJSON.put("status", 204);
            }
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            if (this.twoFactorAuthenticationFacade.removeTFA(requestJSON)) {
                responseJSON.put("status", 202);
                final JSONObject responseBodyJSON = new JSONObject();
                responseBodyJSON.put("is_tfa_enabled", false);
                requestJSON.put("RESPONSE", (Object)responseBodyJSON);
            }
            else {
                responseJSON.put("status", 204);
            }
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Exception    ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
