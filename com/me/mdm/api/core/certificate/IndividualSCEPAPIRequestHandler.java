package com.me.mdm.api.core.certificate;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.SCEPFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualSCEPAPIRequestHandler extends ApiRequestHandler
{
    SCEPFacade facade;
    
    public IndividualSCEPAPIRequestHandler() {
        this.facade = new SCEPFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "scepsettings";
            if (apiRequest.pathInfo.toLowerCase().contains("/scep/servers")) {
                apiRequest.urlStartKey = "servers";
            }
            responseDetails.put("RESPONSE", (Object)this.facade.getSCEPConfiguration(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "scepsettings";
            if (apiRequest.pathInfo.toLowerCase().contains("/scep/servers")) {
                apiRequest.urlStartKey = "servers";
            }
            responseDetails.put("RESPONSE", (Object)this.facade.modifySCEPConfiguration(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
