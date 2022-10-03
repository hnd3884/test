package com.me.mdm.api.core.users;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.core.PasswordPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PasswordPolicyAPIRequestHandler extends ApiRequestHandler
{
    private PasswordPolicyFacade passwordPolicyFacade;
    
    public PasswordPolicyAPIRequestHandler() {
        this.passwordPolicyFacade = new PasswordPolicyFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.passwordPolicyFacade.getPasswordPolicyDetails(requestJSON));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", ex2);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.passwordPolicyFacade.addPasswordPolicy(requestJSON));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", ex2);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            this.passwordPolicyFacade.removePasswordPolicy(requestJSON);
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Exception    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Exception    ", ex2);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
