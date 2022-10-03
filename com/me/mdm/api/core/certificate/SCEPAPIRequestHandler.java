package com.me.mdm.api.core.certificate;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.certificate.SCEPFacade;
import com.me.mdm.api.ApiRequestHandler;

public class SCEPAPIRequestHandler extends ApiRequestHandler
{
    SCEPFacade facade;
    
    public SCEPAPIRequestHandler() {
        this.facade = new SCEPFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            if (apiRequest.pathInfo.contains("/scep/servers")) {
                apiRequest.urlStartKey = "servers";
            }
            responseDetails.put("RESPONSE", (Object)this.facade.getSCEPConfigurations(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            if (apiRequest.pathInfo.contains("/scep/servers")) {
                apiRequest.urlStartKey = "servers";
            }
            responseDetails.put("RESPONSE", (Object)this.facade.addSCEPConfiguration(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Error during scep Tempalte add ", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during scep template add ", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "servers";
            responseDetails.put("RESPONSE", (Object)this.facade.deleteSCEPTemplate(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Error during scep delete ", (Throwable)e2);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Error during scep delete ", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
