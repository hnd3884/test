package com.me.mdm.onpremise.api.integrations;

import java.util.Properties;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ServiceDeskPlusServerAPIRequestHandler extends ApiRequestHandler
{
    SDPFacade sdp2;
    Logger logger;
    
    public ServiceDeskPlusServerAPIRequestHandler() {
        this.sdp2 = SDPFacade.getInstance();
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSONBody = new JSONObject();
            final JSONObject responseJSON = new JSONObject();
            final Properties sdpServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk");
            if (sdpServerProp == null) {
                throw new APIHTTPException("INTG0005", new Object[0]);
            }
            responseJSONBody.put("server_reachable", this.sdp2.checkSDPServerStatus());
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
            final JSONObject responseJSONBody = new JSONObject();
            final JSONObject responseJSON = new JSONObject();
            final JSONObject body = (JSONObject)apiRequest.toJSONObject().get("msg_body");
            this.sdp2.checkPostParams(body);
            final boolean reachable = this.sdp2.checkSDPConfigServerStatus(body);
            responseJSONBody.put("server_reachable", reachable);
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)responseJSONBody);
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
}
