package com.me.mdm.chrome.agent.commands.profiles.payloads;

import java.io.IOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.chromedevicemanagement.v1.model.AppInstallAllowed;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.chromedevicemanagement.v1.model.ChromeApp;
import com.me.mdm.chrome.agent.commands.profile.osupdate.OSUpdateProcessRequestHandler;
import com.google.chromedevicemanagement.v1.model.DevicePolicy;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.AlertContactInfo;
import com.google.chromedevicemanagement.v1.model.DeviceStatusAlertDelivery;
import com.google.chromedevicemanagement.v1.model.DeviceLogUploadSettings;
import com.google.chromedevicemanagement.v1.model.DeviceHeartbeatSettings;
import com.google.chromedevicemanagement.v1.model.AutoUpdateSettings;
import com.google.chromedevicemanagement.v1.model.AutoLaunchedAppSettings;
import java.util.List;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequest;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;

public class KioskPayloadRequestHandler extends PayloadRequestHandler
{
    public Logger logger;
    private static final String IS_SINGLE_APP_KIOSK = "IsSingleAppKiosk";
    private static final String IS_BAILOUT_ENABLED = "IsBailoutEnabled";
    private static final String PROMPT_NETWORK = "promptForNetworkWhenOffline";
    private static final String KIOSK_APPS_LIST = "KioskAppsList";
    private static final String IS_DEVICE_HEALTH_MONITORING_ENABLED = "IsDeviceHealthMonitoringEnabled";
    private static final String IS_SYSTEM_LOGS_UPLOAD_ENABLED = "IsSystemLogsUploadEnabled";
    private static final String DEVICE_STATUS_ALERT_DETAILS = "DeviceStatusAlertDetails";
    private static final String DEVICE_STATUS_ALERT_TYPE = "DeviceStatusAlertType";
    private static final String DEVICE_STATUS_ALERT_EMAILS = "DeviceSTatusAlertEmails";
    private static final String DEVICE_STATUS_ALERT_PH_NO = "DeviceSTatusAlertPhoneNo";
    private static final String ALLOW_KIOSK_APP_TO_CONTROL_OSUPDATE = "AllowKioskToControlChromeVersion";
    private static final String APP_ID = "AppId";
    private static final String APP_URL = "AppUrl";
    
    public KioskPayloadRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processInstallPayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final JSONObject payloadData = payloadReq.getPayloadData();
            final boolean isSingleAppKiosk = payloadData.getBoolean("IsSingleAppKiosk");
            final boolean isBailOutEnabled = payloadData.optBoolean("IsBailoutEnabled", true);
            final boolean promptNetwork = payloadData.optBoolean("promptForNetworkWhenOffline", false);
            final boolean isDeviceHealthMonitoringEnabled = payloadData.optBoolean("IsDeviceHealthMonitoringEnabled", false);
            final boolean isSystemLogUploadEnabled = payloadData.optBoolean("IsSystemLogsUploadEnabled", false);
            final boolean isKioskAppControlOSUpdate = payloadData.optBoolean("AllowKioskToControlChromeVersion", false);
            final JSONObject statusAlertJSON = payloadData.optJSONObject("DeviceStatusAlertDetails");
            final JSONArray kioskApps = payloadData.getJSONArray("KioskAppsList");
            final DevicePolicy devicePolicy = this.clearExistingKiosk(context);
            devicePolicy.setApplicationSettings((List)this.convertJSONArrayToList(kioskApps));
            if (isSingleAppKiosk) {
                devicePolicy.setAutoLaunchedAppSettings(new AutoLaunchedAppSettings().setAppId(String.valueOf(kioskApps.getJSONObject(0).get("AppId"))).setEnableAutoLoginBailout(Boolean.valueOf(isBailOutEnabled)).setPromptForNetworkWhenOffline(Boolean.valueOf(promptNetwork)));
                if (isKioskAppControlOSUpdate) {
                    devicePolicy.setAutoUpdateSettings(new AutoUpdateSettings().setAllowKioskAppControlChromeVersion(Boolean.TRUE).setUpdateEnabled(Boolean.FALSE));
                }
            }
            devicePolicy.setDeviceHeartbeatSettings(new DeviceHeartbeatSettings().setHeartbeatEnabled(Boolean.valueOf(isDeviceHealthMonitoringEnabled)));
            devicePolicy.setDeviceLogUploadSettings(new DeviceLogUploadSettings().setSystemLogUploadEnabled(Boolean.valueOf(isSystemLogUploadEnabled)));
            if (statusAlertJSON != null) {
                final String statusAlertMode = statusAlertJSON.optString("DeviceStatusAlertType", (String)null);
                final JSONArray statusAlertEmails = statusAlertJSON.optJSONArray("DeviceSTatusAlertEmails");
                final JSONArray statusAlertPh = statusAlertJSON.optJSONArray("DeviceSTatusAlertPhoneNo");
                devicePolicy.setDeviceStatusAlertDelivery(new DeviceStatusAlertDelivery().setDeviceOfflineAlerts((List)this.getDeviceAlertList(statusAlertMode)));
                devicePolicy.setAlertContactInfo(new AlertContactInfo().setAlertingEmails((List)this.jsonArrayToList(statusAlertEmails)).setAlertingMobilePhones((List)this.jsonArrayToList(statusAlertPh)));
            }
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    @Override
    public void processModifyPayload(final Request request, final Response response, final PayloadRequest oldPayloadReq, final PayloadRequest modifyPayloadReq, final PayloadResponse payloadResp) {
        this.processInstallPayload(request, response, oldPayloadReq, payloadResp);
    }
    
    @Override
    public void processRemovePayload(final Request request, final Response response, final PayloadRequest payloadReq, final PayloadResponse payloadResp) {
        try {
            final Context context = request.getContainer().getContext();
            final DevicePolicy devicePolicy = this.clearExistingKiosk(context);
            final String updateMask = this.getUpdateMask(devicePolicy.keySet());
            context.getCMPAService().enterprises().devices().updateDevicePolicy(context.getCMPAEnterpriseAndUDID(), devicePolicy).setUpdateMask(updateMask).execute();
            new OSUpdateProcessRequestHandler().applyOSUpdatePolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    private DevicePolicy clearExistingKiosk(final Context context) {
        DevicePolicy devicePolicy = new DevicePolicy();
        try {
            devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
            devicePolicy.setApplicationSettings((List)Arrays.asList(new ChromeApp[0]));
            devicePolicy.setAutoLaunchedAppSettings(new AutoLaunchedAppSettings());
            devicePolicy.setDeviceHeartbeatSettings(new DeviceHeartbeatSettings());
            devicePolicy.setDeviceLogUploadSettings(new DeviceLogUploadSettings());
            devicePolicy.setDeviceStatusAlertDelivery(new DeviceStatusAlertDelivery());
            devicePolicy.setAlertContactInfo(new AlertContactInfo());
        }
        catch (final Exception e) {
            Logger.getLogger(KioskPayloadRequestHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return devicePolicy;
    }
    
    private List<ChromeApp> convertJSONArrayToList(final JSONArray appsArray) throws JSONException {
        final List<ChromeApp> list = new ArrayList<ChromeApp>();
        for (int i = 0; i < appsArray.length(); ++i) {
            list.add(this.convertJSONObjectToChromeApp(appsArray.getJSONObject(i)));
        }
        return list;
    }
    
    private ChromeApp convertJSONObjectToChromeApp(final JSONObject app) throws JSONException {
        final ChromeApp chromeApp = new ChromeApp().setAppId(String.valueOf(app.get("AppId"))).setAppInstallAllowed(new AppInstallAllowed().setAppInstallAllowedType("APP_INSTALL_ALLOWED")).setPinned(Boolean.TRUE).setInstalled(Boolean.TRUE);
        if (app.has("AppUrl")) {
            chromeApp.setUrl(app.optString("AppUrl"));
        }
        return chromeApp;
    }
    
    private List<String> getDeviceAlertList(final String type) {
        final List<String> alertList = new ArrayList<String>();
        if (type.equalsIgnoreCase("Email")) {
            alertList.add("EMAIL");
        }
        else if (type.equalsIgnoreCase("SMS")) {
            alertList.add("SMS");
        }
        else if (type.equalsIgnoreCase("Both")) {
            alertList.add("SMS");
            alertList.add("EMAIL");
        }
        else {
            alertList.add("ALERT_DELIVERY_MODE_UNSPECIFIED");
        }
        return alertList;
    }
    
    public DevicePolicy getDevicePolicy(final Context context) throws IOException {
        try {
            final DevicePolicy devicePolicy = (DevicePolicy)context.getCMPAService().enterprises().devices().getDevicePolicy(context.getCMPAEnterpriseAndUDID()).execute();
            if (devicePolicy != null) {
                return devicePolicy;
            }
            return new DevicePolicy();
        }
        catch (final GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 404) {
                return new DevicePolicy();
            }
            throw ex;
        }
    }
}
