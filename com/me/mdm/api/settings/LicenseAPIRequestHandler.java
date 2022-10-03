package com.me.mdm.api.settings;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class LicenseAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public LicenseAPIRequestHandler() {
        this.logger = Logger.getLogger(LicenseAPIRequestHandler.class.getName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)MDMApiFactoryProvider.getLicenseDetailsAPI().storeLicense(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Error while posting license", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("remoteIP", (Object)apiRequest.httpServletRequest.getRemoteAddr());
            responseJSON.put("RESPONSE", (Object)MDMApiFactoryProvider.getLicenseDetailsAPI().getLicenseDetails(requestJSON));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Error while Getting License Details", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
