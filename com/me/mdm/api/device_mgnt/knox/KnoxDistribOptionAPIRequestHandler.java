package com.me.mdm.api.device_mgnt.knox;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class KnoxDistribOptionAPIRequestHandler extends ApiRequestHandler
{
    private KnoxFacade knox;
    private Logger logger;
    
    public KnoxDistribOptionAPIRequestHandler() {
        this.knox = KnoxFacade.getInstance();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject apiRequestJSON = apiRequest.toJSONObject();
            if (!this.knox.isConfigured(APIUtil.getCustomerID(apiRequestJSON))) {
                throw new APIHTTPException("COM0026", new Object[0]);
            }
            final JSONObject distribDetails = this.knox.getDistribDetails(apiRequestJSON);
            if (distribDetails != null) {
                responseJSON.put("status", 200);
                responseJSON.put("RESPONSE", (Object)distribDetails);
            }
            else {
                responseJSON.put("status", 204);
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
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiRequestJSON = apiRequest.toJSONObject();
            if (!apiRequestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            if (!this.knox.isConfigured(APIUtil.getCustomerID(apiRequestJSON))) {
                throw new APIHTTPException("COM0026", new Object[0]);
            }
            final JSONObject requestJSON = apiRequestJSON.getJSONObject("msg_body");
            if (requestJSON.has("distribution")) {
                if (String.valueOf(requestJSON.get("distribution")).equals("auto")) {
                    this.knox.setAutoDistribDetails(apiRequestJSON);
                }
                else {
                    this.knox.setManualDistribDetails(apiRequestJSON);
                }
                final JSONObject responseJSON = new JSONObject();
                responseJSON.put("status", 201);
                return responseJSON;
            }
            throw new APIHTTPException("COM0005", new Object[] { "distribution" });
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing doPost", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
