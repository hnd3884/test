package com.me.mdm.api.device_mgnt.knox;

import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class KnoxLicenseAPIRequestHandler extends ApiRequestHandler
{
    private KnoxFacade knox;
    private Logger logger;
    
    public KnoxLicenseAPIRequestHandler() {
        this.knox = KnoxFacade.getInstance();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiRequestJSON = apiRequest.toJSONObject();
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            this.knox.setLicenseDetails(apiRequestJSON);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 201);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing doPost", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject licenseDetails = this.knox.getLicenseDetails(apiRequest.toJSONObject());
            if (licenseDetails != null) {
                responseJSON.put("status", 200);
                responseJSON.put("RESPONSE", (Object)licenseDetails);
            }
            else {
                responseJSON.put("status", 404);
            }
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing doGet", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            this.knox.removeKnoxLicense(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing doDelete", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
