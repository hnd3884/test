package com.me.mdm.onpremise.api.integrations;

import java.util.Hashtable;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;

public class SDPFacade
{
    private boolean isMSP;
    private static SDPFacade instance;
    
    private SDPFacade() {
        this.isMSP = CustomerInfoUtil.getInstance().isMSP();
    }
    
    public static SDPFacade getInstance() {
        return SDPFacade.instance;
    }
    
    public void checkPostParams(final JSONObject obj) {
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
    
    public void modifySettings(final JSONObject body) throws Exception {
        final JSONObject previousSettings = this.getSettings();
        if (body.has("server")) {
            previousSettings.put("server", (Object)String.valueOf(body.get("server")));
        }
        if (body.has("port")) {
            previousSettings.put("port", body.getInt("port"));
        }
        if (body.has("protocol")) {
            previousSettings.put("protocol", (Object)String.valueOf(body.get("protocol")));
        }
        if (body.has("authentication_key")) {
            previousSettings.put("authentication_key", (Object)String.valueOf(body.get("authentication_key")));
        }
        if (body.has("features")) {
            final JSONObject features = body.getJSONObject("features");
            final JSONObject prevFeatures = previousSettings.getJSONObject("features");
            if (!this.isMSP && features.has("helpdesk_alert") && features.has("helpdesk_alert")) {
                final JSONObject alert = features.getJSONObject("helpdesk_alert");
                final JSONObject prevAlert = prevFeatures.getJSONObject("helpdesk_alert");
                if (alert.has("log_new_apps")) {
                    prevAlert.put("log_new_apps", alert.getBoolean("log_new_apps"));
                }
                if (alert.has("log_blacklisted_apps")) {
                    prevAlert.put("log_blacklisted_apps", alert.getBoolean("log_blacklisted_apps"));
                }
            }
            final boolean share = features.has("share_asset_data") ? features.getBoolean("share_asset_data") : prevFeatures.getBoolean("share_asset_data");
            prevFeatures.put("share_asset_data", share);
            if (!this.isMSP) {
                if (features.has("asset_data_settings")) {
                    final JSONObject settings = features.getJSONObject("asset_data_settings");
                    final JSONObject prevShareSettings = prevFeatures.getJSONObject("asset_data_settings");
                    if (settings.has("device_remove_action")) {
                        prevShareSettings.put("device_remove_action", (Object)String.valueOf(settings.get("device_remove_action")));
                    }
                    if (settings.has("auto_assign")) {
                        prevShareSettings.put("auto_assign", (Object)settings.optString("auto_assign"));
                    }
                }
                else {
                    final JSONObject settings = prevFeatures.getJSONObject("asset_data_settings");
                    settings.put("device_remove_action", (Object)"none");
                    settings.put("auto_assign", (Object)"none");
                }
            }
        }
        this.checkValidity(previousSettings);
        this.saveServiceDeskPlusSettings(previousSettings);
    }
    
    public void createSettings(final JSONObject body) throws Exception {
        this.checkPostParams(body);
        if (!body.has("authentication_key")) {
            throw new APIHTTPException("COM0005", new Object[] { "authentication_key" });
        }
        if (body.has("features") && body.getJSONObject("features").length() > 0) {
            final JSONObject features = body.getJSONObject("features");
            if (!features.has("share_asset_data")) {
                features.put("share_asset_data", false);
            }
            else if (features.getBoolean("share_asset_data")) {
                if (!features.has("asset_data_settings")) {
                    final JSONObject settings = new JSONObject();
                    settings.put("device_remove_action", (Object)"none");
                    settings.put("auto_assign", (Object)"none");
                    features.put("asset_data_settings", (Object)settings);
                }
                else {
                    final JSONObject settings = features.getJSONObject("asset_data_settings");
                    if (!settings.has("device_remove_action")) {
                        settings.put("device_remove_action", (Object)"none");
                    }
                    if (!settings.has("auto_assign")) {
                        settings.put("auto_assign", (Object)"none");
                    }
                }
            }
            else {
                final JSONObject settings = new JSONObject();
                settings.put("device_remove_action", (Object)"none");
                settings.put("auto_assign", (Object)"none");
                features.put("asset_data_settings", (Object)settings);
            }
            if (!features.has("helpdesk_alert")) {
                final JSONObject obj2 = new JSONObject();
                obj2.put("log_new_apps", false);
                obj2.put("log_blacklisted_apps", false);
                features.put("helpdesk_alert", (Object)obj2);
            }
            else {
                final JSONObject obj2 = features.getJSONObject("helpdesk_alert");
                if (!obj2.has("log_new_apps")) {
                    obj2.put("log_new_apps", false);
                }
                if (!obj2.has("log_blacklisted_apps")) {
                    obj2.put("log_blacklisted_apps", false);
                }
                features.put("helpdesk_alert", (Object)obj2);
            }
            body.put("features", (Object)features);
        }
        else {
            final JSONObject features = body.has("features") ? body.getJSONObject("features") : new JSONObject();
            features.put("share_asset_data", false);
            final JSONObject settings = new JSONObject();
            settings.put("device_remove_action", (Object)"none");
            settings.put("auto_assign", (Object)"none");
            features.put("asset_data_settings", (Object)settings);
            final JSONObject alert = new JSONObject();
            alert.put("log_blacklisted_apps", false);
            alert.put("log_new_apps", false);
            features.put("helpdesk_alert", (Object)alert);
            body.put("features", (Object)features);
        }
        this.checkValidity(body);
        this.saveServiceDeskPlusSettings(body);
    }
    
    void checkValidity(final JSONObject body) throws Exception {
        if (!this.checkSDPServerCert(body)) {
            throw new APIHTTPException("INTG0001", new Object[0]);
        }
        if (!this.checkSDPConfigServerStatus(body)) {
            throw new APIHTTPException("INTG0002", new Object[0]);
        }
        if (!this.checkSDPServerKeyConfigStatus(body)) {
            throw new APIHTTPException("COM0005", new Object[] { "authentication_key" });
        }
    }
    
    public void saveServiceDeskPlusSettings(JSONObject request) throws Exception {
        final JSONObject sdpServerStatus = MDMSDPIntegrationUtil.getInstance().getIntegrationstatus();
        this.handleServerSettings(request);
        request = request.getJSONObject("features");
        final String isSDPMDMInvEnabledStr = String.valueOf(request.getBoolean("share_asset_data"));
        MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_INV_INTEGRATION", isSDPMDMInvEnabledStr);
        if (!this.isMSP) {
            final JSONObject assetDataSettings = request.getJSONObject("asset_data_settings");
            final String mdmAssetDelValueStr = String.valueOf(assetDataSettings.get("device_remove_action"));
            final String postMDMOwnerStr = String.valueOf(assetDataSettings.get("auto_assign"));
            MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_ASSET_DEL_VALUE", mdmAssetDelValueStr);
            MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_POST_OWNER", postMDMOwnerStr);
        }
        MDMSDPIntegrationUtil.getInstance().updateConsentforSdp(isSDPMDMInvEnabledStr, MDMCommonConstants.SDP_ASSET_CONSENT_ID);
        if (!this.isMSP) {
            final JSONObject helpDesk = request.getJSONObject("helpdesk_alert");
            final boolean logNewApps = helpDesk.getBoolean("log_new_apps");
            final boolean logBlistedApps = helpDesk.getBoolean("log_blacklisted_apps");
            final boolean isSDPAlertEnabled = logNewApps || logBlistedApps;
            final String isSDPAlertEnabledStr = String.valueOf(isSDPAlertEnabled);
            if (isSDPAlertEnabled != sdpServerStatus.getBoolean("SDP_MDM_HELPDESK_ALERT")) {
                MDMSDPIntegrationUtil.getInstance().updateConsentforSdp(isSDPAlertEnabledStr, MDMCommonConstants.SDP_ALERTS_CONSENT_ID);
            }
            MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_ALERT", isSDPAlertEnabledStr);
            if (isSDPAlertEnabled) {
                final String isSDPNewAppAlertEnabledStr = String.valueOf(logNewApps);
                final String isSDPBlacklistAppEnabledStr = String.valueOf(logBlistedApps);
                MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_NEW_APP_ALERT", isSDPNewAppAlertEnabledStr);
                MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT", isSDPBlacklistAppEnabledStr);
            }
            else {
                MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_NEW_APP_ALERT", "false");
                MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT", "false");
            }
        }
        MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("SDP_MDM_HELPDESK_UNMANAGED_CERTIFICATE_ALERT", "false");
    }
    
    private void handleServerSettings(final JSONObject request) throws Exception {
        final String appName = "HelpDesk";
        final Properties serverProps = new Properties();
        ((Hashtable<String, String>)serverProps).put("SERVER", String.valueOf(request.get("server")));
        ((Hashtable<String, String>)serverProps).put("PORT", String.valueOf(request.getInt("port")));
        ((Hashtable<String, String>)serverProps).put("PROTOCOL", String.valueOf(request.get("protocol")));
        ((Hashtable<String, String>)serverProps).put("IS_ENABLED", "true");
        final String authenticationKey = String.valueOf(request.get("authentication_key"));
        if (authenticationKey != null) {
            ((Hashtable<String, String>)serverProps).put("AUTHENTICATION_KEY", authenticationKey);
        }
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings(appName, serverProps);
    }
    
    public boolean checkSDPConfigServerStatus(final JSONObject request) throws Exception {
        final Properties sdpServerProp = new Properties();
        sdpServerProp.setProperty("SERVER", String.valueOf(request.get("server")));
        sdpServerProp.setProperty("PORT", String.valueOf(request.getInt("port")));
        sdpServerProp.setProperty("PROTOCOL", String.valueOf(request.get("protocol")));
        final boolean sdpServerStatus = MDMSDPIntegrationUtil.getInstance().checkSDPConfigServerStatus(sdpServerProp);
        return sdpServerStatus;
    }
    
    public boolean checkSDPServerCert(final JSONObject request) throws Exception {
        final Properties sdpServerProp = new Properties();
        sdpServerProp.setProperty("SERVER", String.valueOf(request.get("server")));
        sdpServerProp.setProperty("PORT", String.valueOf(request.getInt("port")));
        sdpServerProp.setProperty("PROTOCOL", String.valueOf(request.get("protocol")));
        final boolean sdpServerStatus = MDMSDPIntegrationUtil.getInstance().checkSDPServerCert(sdpServerProp);
        return sdpServerStatus;
    }
    
    public boolean checkSDPServerStatus() throws Exception {
        final boolean sdpServerStatus = MDMSDPIntegrationUtil.getInstance().checkSDPServerStatus();
        return sdpServerStatus;
    }
    
    public void deleteSDPSettings() throws Exception {
        MDMSDPIntegrationUtil.getInstance().deleteServerSettings("HelpDesk");
    }
    
    public boolean checkSDPServerKeyStatus() throws Exception {
        boolean sdpKeyStatus = false;
        final Properties sdpSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk");
        if (sdpSettingsProps != null && sdpSettingsProps.containsKey("AUTHENDICATION_KEY")) {
            final String appAuthKey = sdpSettingsProps.getProperty("AUTHENDICATION_KEY");
            sdpKeyStatus = MDMSDPIntegrationUtil.getInstance().checkSDPServerKeyStatus("HelpDesk", appAuthKey);
        }
        return sdpKeyStatus;
    }
    
    public boolean checkSDPServerKeyConfigStatus(final JSONObject request) throws Exception {
        boolean sdpKeyStatus = false;
        final Properties sdpServerProp = new Properties();
        sdpServerProp.setProperty("SERVER", String.valueOf(request.get("server")));
        sdpServerProp.setProperty("PORT", String.valueOf(request.getInt("port")));
        sdpServerProp.setProperty("PROTOCOL", String.valueOf(request.get("protocol")));
        sdpServerProp.setProperty("AUTHENTICATION_KEY", String.valueOf(request.get("authentication_key")));
        sdpKeyStatus = MDMSDPIntegrationUtil.getInstance().checkSDPServerKeyStatus(sdpServerProp);
        return sdpKeyStatus;
    }
    
    public void updateSDPServerAPIKey(final JSONObject request) throws SyMException, JSONException {
        final String sdpAPIKey = String.valueOf(request.get("authentication_key"));
        final Properties sdpSettingsProps = new Properties();
        sdpSettingsProps.setProperty("AUTHENTICATION_KEY", sdpAPIKey);
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings("HelpDesk", sdpSettingsProps);
    }
    
    public JSONObject getSettings() throws JSONException, SyMException, Exception {
        final Properties sdpServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk");
        if (sdpServerProp == null) {
            return null;
        }
        final JSONObject responseJSONBody = new JSONObject();
        responseJSONBody.put("server", (Object)sdpServerProp.getProperty("SERVER"));
        responseJSONBody.put("port", (Object)sdpServerProp.getProperty("PORT"));
        responseJSONBody.put("protocol", (Object)sdpServerProp.getProperty("PROTOCOL"));
        responseJSONBody.put("server_reachable", (Object)String.valueOf(this.checkSDPServerStatus()));
        responseJSONBody.put("authentication_key", (Object)String.valueOf(sdpServerProp.getProperty("AUTHENDICATION_KEY")));
        final JSONObject features = new JSONObject();
        final boolean share_asset_data = MDMSDPIntegrationUtil.getInstance().getIntegrationstatus().getBoolean("SDP_MDM_INV_INTEGRATION");
        features.put("share_asset_data", (Object)String.valueOf(share_asset_data));
        if (!this.isMSP) {
            final JSONObject assetDataSettings = new JSONObject();
            assetDataSettings.put("device_remove_action", (Object)MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_ASSET_DEL_VALUE"));
            assetDataSettings.put("auto_assign", (Object)MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_POST_OWNER"));
            features.put("asset_data_settings", (Object)assetDataSettings);
            final JSONObject logging = new JSONObject();
            logging.put("log_new_apps", (Object)MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_NEW_APP_ALERT"));
            logging.put("log_blacklisted_apps", (Object)MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_BLACKLIST_APP_ALERT"));
            features.put("helpdesk_alert", (Object)logging);
        }
        responseJSONBody.put("features", (Object)features);
        return responseJSONBody;
    }
    
    static {
        SDPFacade.instance = new SDPFacade();
    }
}
