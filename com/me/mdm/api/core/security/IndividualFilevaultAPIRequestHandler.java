package com.me.mdm.api.core.security;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualFilevaultAPIRequestHandler extends ApiRequestHandler
{
    MDMFileVaultFacade facade;
    
    public IndividualFilevaultAPIRequestHandler() {
        this.facade = new MDMFileVaultFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "filevaults";
            responseDetails.put("RESPONSE", (Object)this.facade.getFileVaultDetail(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "filevaults";
            responseJSON.put("RESPONSE", (Object)this.facade.modifyFileVault(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            apiRequest.urlStartKey = "filevaults";
            this.facade.deleteFilevault(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0008", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
