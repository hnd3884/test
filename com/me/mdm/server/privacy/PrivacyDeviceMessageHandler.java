package com.me.mdm.server.privacy;

import org.json.JSONException;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;

public class PrivacyDeviceMessageHandler
{
    Logger logger;
    private static final String MESSAGE = "Msg";
    private static final String CONFIG = "Config";
    private static PrivacyDeviceMessageHandler handler;
    
    public PrivacyDeviceMessageHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PrivacyDeviceMessageHandler getInstance() {
        if (PrivacyDeviceMessageHandler.handler == null) {
            PrivacyDeviceMessageHandler.handler = new PrivacyDeviceMessageHandler();
        }
        return PrivacyDeviceMessageHandler.handler;
    }
    
    public DeviceMessage processPrivacySettingsRequest(final DeviceRequest request) {
        final DeviceMessage msg = new DeviceMessage();
        try {
            final Boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            final Long resourceId = request.resourceID;
            Long enrollmentRequestID = 0L;
            Criteria cRes;
            if (resourceId == null) {
                final JSONObject requestJSON = (JSONObject)request.deviceRequestData;
                final JSONObject messageJSON = requestJSON.optJSONObject("Message");
                enrollmentRequestID = messageJSON.optLong("EnrollmentRequestID");
                cRes = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentRequestID, 0);
            }
            else {
                cRes = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            }
            int ownedBy = ManagedDeviceHandler.getInstance().getOwnedByForDevice(cRes);
            if (ownedBy == 0 && enrollmentRequestID != 0L) {
                final Row devicerow = DBUtil.getRowFromDB("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID", (Object)enrollmentRequestID);
                if (devicerow != null) {
                    ownedBy = 1;
                }
            }
            final Long customerId = request.customerID;
            final JSONObject privacySettings = new PrivacySettingsHandler().getPrivacyDetails(ownedBy, customerId);
            final HashMap<String, String> privacyMessage = PrivacyCustomMessageHandler.getInstance().getCustomMessage(customerId);
            msg.setMessageStatus("Acknowledged");
            msg.setMessageType("PrivacySettings");
            final JSONObject messageResponse = new JSONObject();
            final JSONArray privacyArray = new JSONArray();
            privacyArray.put((Object)this.getSettingsJSON("FetchDeviceName", privacyMessage.get("FetchDeviceName".toLowerCase()), (int)privacySettings.get("fetch_device_name")));
            privacyArray.put((Object)this.getSettingsJSON("FetchPhoneNumber", privacyMessage.get("FetchPhoneNumber".toLowerCase()), (int)privacySettings.get("fetch_phone_number")));
            privacyArray.put((Object)this.getSettingsJSON("FetchImsi", privacyMessage.get("FetchImsi".toLowerCase()), (int)privacySettings.get("fetch_imsi_number")));
            privacyArray.put((Object)this.getSettingsJSON("FetchAppInfo", privacyMessage.get("FetchAppInfo".toLowerCase()), (int)privacySettings.get("fetch_installed_app")));
            privacyArray.put((Object)this.getSettingsJSON("DisableWipe", null, (int)privacySettings.get("disable_wipe")));
            privacyArray.put((Object)this.getSettingsJSON("RemoteDebug", null, (int)privacySettings.get("disable_bug_report")));
            privacyArray.put((Object)this.getSettingsJSON("DeviceReportingEnabled", null, (int)privacySettings.get("device_state_report")));
            privacyArray.put((Object)this.getSettingsJSON("RecentUserReporting", null, (int)privacySettings.get("recent_users_report")));
            privacyArray.put((Object)this.getSettingsJSON("FetchLocation", privacyMessage.get("FetchLocation".toLowerCase()), (int)privacySettings.get("fetch_location")));
            privacyArray.put((Object)this.getSettingsJSON("FetchMacAddress", privacyMessage.get("FetchMacAddress".toLowerCase()), (int)privacySettings.get("fetch_mac_address")));
            privacyArray.put((Object)this.getSettingsJSON("FetchUserInstalledCerts", null, (int)privacySettings.get("fetch_user_installed_certs")));
            privacyArray.put((Object)this.getSettingsJSON("DisableClearPasscode", null, (int)privacySettings.get("disable_clear_passcode")));
            if (isProfessional) {
                privacyArray.put((Object)this.getSettingsJSON("DisableRemoteControl", null, (int)privacySettings.get("disable_remote_control")));
            }
            if (request.devicePlatform == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableFetchWifiSSSID")) {
                final int modelType = new DeviceDetails(request.resourceID).modelType;
                if (modelType == 0 || modelType == 1 || modelType == 2) {
                    privacyArray.put((Object)this.getSettingsJSON("FetchWifiSSID", privacyMessage.get("FetchWifiSSID".toLowerCase()), (int)privacySettings.get("fetch_wifi_ssid")));
                }
            }
            messageResponse.put("PrivacySettings", (Object)privacyArray);
            messageResponse.put("ViewPrivacySettings", privacySettings.optBoolean("view_privacy_settings"));
            msg.setMessageResponseJSON(messageResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while generating privacy settings message", e);
        }
        return msg;
    }
    
    private JSONObject getSettingsJSON(final String commandName, final String customMessage, final int config) throws JSONException {
        final JSONObject innerConfigJSON = new JSONObject();
        if (customMessage != null && config != 2) {
            innerConfigJSON.put("Msg", (Object)customMessage);
        }
        innerConfigJSON.put("Config", config);
        final JSONObject configJSON = new JSONObject();
        configJSON.put(commandName, (Object)innerConfigJSON);
        return configJSON;
    }
    
    public JSONObject getChromePrivacyData(final Long customerId) {
        final JSONObject privacyJSON = new JSONObject();
        try {
            final JSONObject privacySettings = new PrivacySettingsHandler().getPrivacyDetails(1, customerId);
            final Boolean deviceReporting = (int)privacySettings.get("device_state_report") != 2;
            final Boolean userReporting = (int)privacySettings.get("recent_users_report") != 2;
            privacyJSON.put("DeviceReportingEnabled", (Object)deviceReporting);
            privacyJSON.put("RecentUserReporting", (Object)userReporting);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Excpetion while generation Chrome privacy JSON", (Throwable)ex);
        }
        return privacyJSON;
    }
    
    static {
        PrivacyDeviceMessageHandler.handler = null;
    }
}
