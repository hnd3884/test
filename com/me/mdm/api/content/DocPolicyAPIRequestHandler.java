package com.me.mdm.api.content;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.doc.policy.DocPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DocPolicyAPIRequestHandler extends ApiRequestHandler
{
    private DocPolicyFacade policyFacade;
    
    public DocPolicyAPIRequestHandler() {
        this.policyFacade = new DocPolicyFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final JSONObject responseJSON = new JSONObject();
        final JSONArray policyArray = this.policyFacade.getDocPolicy(requestJSON);
        final JSONObject res = new JSONObject();
        res.put("policy", (Object)policyArray);
        responseJSON.put("RESPONSE", (Object)res);
        responseJSON.put("status", 200);
        return responseJSON;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject requestJSON = apiRequest.toJSONObject();
        final JSONObject responseJSON = new JSONObject();
        final JSONObject docPolicy = this.policyFacade.addDocPolicy(requestJSON);
        responseJSON.put("RESPONSE", (Object)docPolicy);
        responseJSON.put("status", 200);
        return responseJSON;
    }
}
