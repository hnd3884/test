package com.me.mdm.api.device_mgnt.knox;

import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class KnoxAPIRequestHandler extends ApiRequestHandler
{
    private KnoxFacade knox;
    private Logger logger;
    
    public KnoxAPIRequestHandler() {
        this.knox = KnoxFacade.getInstance();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject apiRequestJSON = apiRequest.toJSONObject();
        final Long customerId = APIUtil.getCustomerID(apiRequestJSON);
        try {
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            if (!this.knox.isConfigured(customerId)) {
                throw new APIHTTPException("COM0026", new Object[0]);
            }
            if (this.knox.isLicenseExpired(customerId)) {
                throw new APIHTTPException("KN002", new Object[0]);
            }
            if (MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
                throw new APIHTTPException("COM00020", new Object[0]);
            }
            this.knox.createKnox(apiRequestJSON);
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
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject apiRequestJSON = apiRequest.toJSONObject();
        final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
        try {
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            if (!this.knox.isConfigured(customerId)) {
                throw new APIHTTPException("COM0026", new Object[0]);
            }
            this.knox.removeKnox(apiRequestJSON);
            final JSONObject responseJSON = new JSONObject();
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
