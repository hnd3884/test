package com.me.mdm.api.content;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.doc.policy.DocPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDocPolicyApiRequestHandler extends ApiRequestHandler
{
    private DocPolicyFacade policyFacade;
    public static final Logger LOGGER;
    
    public IndividualDocPolicyApiRequestHandler() {
        this.policyFacade = new DocPolicyFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final JSONObject responseJSON = new JSONObject();
        final JSONObject res = this.policyFacade.updateDocPolicy(requestJSON);
        responseJSON.put("RESPONSE", (Object)res);
        responseJSON.put("status", 200);
        return responseJSON;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final JSONObject responseJSON = new JSONObject();
        final JSONObject policyJson = this.policyFacade.getDocPolicyById(requestJSON);
        responseJSON.put("RESPONSE", (Object)policyJson);
        responseJSON.put("status", 200);
        return responseJSON;
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            this.policyFacade.deletePolicy(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            IndividualDocPolicyApiRequestHandler.LOGGER.log(Level.SEVERE, "JSON Exception", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMDocLogger");
    }
}
