package com.me.mdm.api.core.dataprotection;

import com.me.mdm.server.payload.PayloadException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.core.dataprotection.windows.WindowsDataPolicyHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class PolicyAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new WindowsDataPolicyHandler().createPolicy(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new WindowsDataPolicyHandler().getPolicy(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final PayloadException ex) {
            throw new APIHTTPException(ex.getPayloadErrorCode(), (Object[])null);
        }
        catch (final APIHTTPException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
