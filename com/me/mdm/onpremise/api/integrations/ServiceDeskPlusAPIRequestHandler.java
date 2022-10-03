package com.me.mdm.onpremise.api.integrations;

import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ServiceDeskPlusAPIRequestHandler extends ApiRequestHandler
{
    SDPFacade sdp2;
    Logger logger;
    
    public ServiceDeskPlusAPIRequestHandler() {
        this.sdp2 = SDPFacade.getInstance();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSONBody = this.sdp2.getSettings();
            if (responseJSONBody == null) {
                throw new APIHTTPException("INTG0005", new Object[0]);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)responseJSONBody);
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
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            if (!apiRequest.toJSONObject().has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject body = (JSONObject)apiRequest.toJSONObject().get("msg_body");
            if (this.sdp2.getSettings() == null) {
                this.sdp2.createSettings(body);
            }
            else {
                this.sdp2.modifySettings(body);
            }
            return this.doGet(apiRequest);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while executing doPost", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.sdp2.deleteSDPSettings();
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
