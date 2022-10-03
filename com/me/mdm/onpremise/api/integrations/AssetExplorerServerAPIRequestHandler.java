package com.me.mdm.onpremise.api.integrations;

import java.util.Hashtable;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AssetExplorerServerAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public AssetExplorerServerAPIRequestHandler() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    static boolean isAEServerReachable() throws SyMException {
        final Properties aeServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer");
        final boolean reachable = MDMSDPIntegrationUtil.getInstance().checkAEConfigServerStatus(aeServerProp);
        ((Hashtable<String, Boolean>)aeServerProp).put("IS_ENABLED", reachable);
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings("AssetExplorer", aeServerProp);
        return reachable;
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            if (MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer") == null) {
                throw new APIHTTPException("INTG0005", new Object[0]);
            }
            final JSONObject responseJSONBody = new JSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSONBody.put("server_reachable", isAEServerReachable());
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
            if (!body.has("server")) {
                throw new APIHTTPException("COM0005", new Object[] { "server" });
            }
            if (!body.has("port")) {
                throw new APIHTTPException("COM0005", new Object[] { "port" });
            }
            if (!body.has("protocol")) {
                throw new APIHTTPException("COM0005", new Object[] { "protocol" });
            }
            final Properties aeServerProp = new Properties();
            ((Hashtable<String, String>)aeServerProp).put("SERVER", String.valueOf(body.get("server")));
            ((Hashtable<String, String>)aeServerProp).put("PORT", String.valueOf(body.get("port")));
            ((Hashtable<String, String>)aeServerProp).put("PROTOCOL", String.valueOf(body.get("protocol")).toUpperCase());
            final boolean reachable = MDMSDPIntegrationUtil.getInstance().checkAEConfigServerStatus(aeServerProp);
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
