package com.me.mdm.onpremise.api.integrations;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Properties;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AssetExplorerAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public AssetExplorerAPIRequestHandler() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    private JSONObject getSettings() throws Exception {
        final Properties aeServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer");
        final JSONObject responseJSONBody = new JSONObject();
        if (aeServerProp == null) {
            return responseJSONBody;
        }
        final boolean share_asset_data = MDMSDPIntegrationUtil.getInstance().getIntegrationstatus().getBoolean("AE_MDM_INV_INTEGRATION");
        responseJSONBody.put("server", (Object)aeServerProp.getProperty("SERVER"));
        responseJSONBody.put("port", (Object)aeServerProp.getProperty("PORT"));
        responseJSONBody.put("protocol", (Object)aeServerProp.getProperty("PROTOCOL"));
        final JSONObject features = new JSONObject();
        features.put("share_asset_data", (Object)String.valueOf(share_asset_data));
        responseJSONBody.put("features", (Object)features);
        return responseJSONBody;
    }
    
    private void checkPostParams(final JSONObject obj) throws Exception {
        if (!obj.has("server")) {
            throw new APIHTTPException("COM0005", new Object[] { "server" });
        }
        if (!obj.has("port")) {
            throw new APIHTTPException("COM0005", new Object[] { "port" });
        }
        if (!obj.has("protocol")) {
            throw new APIHTTPException("COM0005", new Object[] { "protocol" });
        }
    }
    
    private void setShareAssetData(final boolean state) throws Exception {
        MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("AE_MDM_INV_INTEGRATION", String.valueOf(state));
        MDMSDPIntegrationUtil.getInstance().updateConsentforSdp(String.valueOf(state), MDMCommonConstants.AE_ASSET_CONSENT_ID);
    }
    
    private void checkCert(final Properties aeServerProp) throws APIHTTPException {
        final boolean sdpServerCertifStatus = MDMSDPIntegrationUtil.getInstance().checkAEServerCert(aeServerProp);
        if (!sdpServerCertifStatus) {
            throw new APIHTTPException("INTG0001", new Object[0]);
        }
    }
    
    private void modifySettings(final JSONObject body) throws Exception {
        final JSONObject prevSettings = this.getSettings();
        if (body.has("server")) {
            prevSettings.put("server", (Object)String.valueOf(body.get("server")));
        }
        if (body.has("port")) {
            prevSettings.put("port", (Object)String.valueOf(body.get("port")));
        }
        if (body.has("protocol")) {
            prevSettings.put("protocol", (Object)String.valueOf(body.get("protocol")));
        }
        final Properties serverProps = new Properties();
        ((Hashtable<String, String>)serverProps).put("SERVER", String.valueOf(prevSettings.get("server")));
        ((Hashtable<String, String>)serverProps).put("PORT", String.valueOf(prevSettings.getInt("port")));
        ((Hashtable<String, String>)serverProps).put("PROTOCOL", String.valueOf(prevSettings.get("protocol")).toUpperCase());
        if (String.valueOf(prevSettings.get("protocol")).equals("HTTPS")) {
            this.checkCert(serverProps);
        }
        final boolean isAEServerReachable = MDMSDPIntegrationUtil.getInstance().checkAEConfigServerStatus(serverProps);
        if (!isAEServerReachable) {
            throw new APIHTTPException("INTG0002", new Object[0]);
        }
        ((Hashtable<String, String>)serverProps).put("IS_ENABLED", "true");
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings("AssetExplorer", serverProps);
        if (body.has("features")) {
            final JSONObject features = body.getJSONObject("features");
            if (features.has("share_asset_data")) {
                this.setShareAssetData(features.getBoolean("share_asset_data"));
            }
        }
    }
    
    private void createSettings(final JSONObject body) throws Exception {
        this.checkPostParams(body);
        final String server = String.valueOf(body.get("server"));
        final int port = body.getInt("port");
        final String protocol = String.valueOf(body.get("protocol")).toUpperCase();
        final Properties serverProps = new Properties();
        ((Hashtable<String, String>)serverProps).put("SERVER", server);
        ((Hashtable<String, String>)serverProps).put("PORT", String.valueOf(port));
        ((Hashtable<String, String>)serverProps).put("PROTOCOL", protocol.toUpperCase());
        if (protocol.equals("HTTPS")) {
            this.checkCert(serverProps);
        }
        final boolean isAEServerReachable = MDMSDPIntegrationUtil.getInstance().checkAEConfigServerStatus(serverProps);
        if (!isAEServerReachable) {
            throw new APIHTTPException("INTG0002", new Object[0]);
        }
        ((Hashtable<String, String>)serverProps).put("IS_ENABLED", "true");
        if (body.has("features")) {
            final JSONObject features = body.getJSONObject("features");
            if (features.has("share_asset_data")) {
                this.setShareAssetData(features.getBoolean("share_asset_data"));
            }
        }
        else {
            this.setShareAssetData(false);
        }
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings("AssetExplorer", serverProps);
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSONBody = this.getSettings();
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
            final Properties aeServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer");
            if (aeServerProp == null) {
                this.createSettings(body);
            }
            else {
                this.modifySettings(body);
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
            MDMSDPIntegrationUtil.getInstance().deleteServerSettings("AssetExplorer");
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
