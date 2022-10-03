package com.adventnet.sym.server.mdm.iosnativeapp.payload;

import com.me.mdm.server.acp.MDMAppCatalogHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.acp.WindowsAppCatalogHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.server.remotesession.RemoteSessionMessageHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.settings.location.LocationSettingsRequestHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.DeviceDetails;
import org.json.JSONException;
import java.util.logging.Logger;

public class IOSNativeAppPayloadHandler
{
    private static IOSNativeAppPayloadHandler pHandler;
    private Logger logger;
    
    public IOSNativeAppPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static IOSNativeAppPayloadHandler getInstance() {
        if (IOSNativeAppPayloadHandler.pHandler == null) {
            IOSNativeAppPayloadHandler.pHandler = new IOSNativeAppPayloadHandler();
        }
        return IOSNativeAppPayloadHandler.pHandler;
    }
    
    private IOSNativeAppCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = new IOSNativeAppCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    private IOSNativeAppCommandPayload createWindowsCommandPayload(final String requestType) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload(requestType);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createLocationSettingsCommand(final DeviceDetails device, final String strUDID) throws Exception {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("SyncAgentSettings");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "SyncAgentSettings");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SyncAgentSettings", commandPayload.toString() });
        final JSONObject syncReqData = LocationSettingsRequestHandler.getInstance().getiOSLocationSettingPayloadJSON(device);
        syncReqData.put("FCMAgentSettings", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getFCMAgentNotificationSecret());
        commandPayload.setRequestData(syncReqData);
        commandPayload.setCommandUUID(strUDID);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createBatteryConfigurationCommand(final DeviceDetails deviceDetails) throws Exception {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("BATTERY_CONFIGURATION");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "BATTERY_CONFIGURATION");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "BATTERY_CONFIGURATION", commandPayload.toString() });
        final JSONObject batteryConfigJson = MDMBatterySettingsDBHandler.getInstance().getBatteryConfigurationForDevice(deviceDetails);
        commandPayload.setRequestData(batteryConfigJson);
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createSyncPrivacySettingsCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("SyncPrivacySettings");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "SyncAgentSettings");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SyncPrivacySettings", commandPayload.toString() });
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createLocationConfigCommand(final DeviceDetails device, final String strUDID) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("LocationConfiguration");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "LocationConfiguration");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "LocationConfiguration", commandPayload.toString() });
        final JSONObject syncReqData = LocationSettingsDataHandler.getInstance().getLocationConfigCommandData(device);
        commandPayload.setRequestData(syncReqData);
        commandPayload.setCommandUUID(strUDID);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createRemoteSessionCommand(final DeviceDetails device, final String strUDID) throws JSONException, Exception {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("RemoteSession");
        this.logger.log(Level.INFO, "iOS {0} Command has been created successfully", "RemoteSession");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoteSession", commandPayload.toString() });
        final JSONObject requestData = new RemoteSessionMessageHandler().getRemoteSessionInfo(device.resourceId, device.customerId);
        commandPayload.setRequestData(requestData);
        commandPayload.setCommandUUID(strUDID);
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public DeviceMessage createGetRemoteSessionInfoMsg(final DeviceRequest request) throws JSONException {
        DeviceMessage deviceMsg = null;
        deviceMsg = new DeviceMessage();
        deviceMsg.messageResponse = new RemoteSessionMessageHandler().getRemoteSessionInfo(request.resourceID, request.customerID);
        deviceMsg.status = "Acknowledged";
        deviceMsg.messageType = "GetRemoteSessionInfo";
        this.logger.log(Level.INFO, "iOS {0} message has been created successfully", "GetRemoteSessionInfo");
        this.logger.log(Level.FINE, "{0} Message : {1}", new Object[] { "RemoteSession", deviceMsg.toString() });
        return deviceMsg;
    }
    
    public IOSNativeAppCommandPayload createAgentUpgradeCommand(final DeviceRequest request) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("AgentUpgrade");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "AgentUpgrade");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "AgentUpgrade", commandPayload.toString() });
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(request.deviceUDID);
        final int agentType = ManagedDeviceHandler.getInstance().getAgentType(resourceId);
        final JSONObject upgradeJSON = MDMAgentUpdateHandler.getInstance().getAgentUpgradeRequestData(agentType, request.customerID, MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.requestMap.get("ServletPath")));
        commandPayload.setRequestData(upgradeJSON);
        commandPayload.setCommandUUID(request.deviceUDID);
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createLanguageLicenseCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("LanguagePackUpdate");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "LanguagePackUpdate");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "LanguagePackUpdate", commandPayload.toString() });
        final boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        final JSONObject langConfigData = new JSONObject();
        langConfigData.put("IsLanguagePackEnabled", isLangPackEnabled);
        commandPayload.setRequestData(langConfigData);
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createNotificationCredentialCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createWindowsCommandPayload("AppNotificationCredential");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "AppNotificationCredential");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createLocationCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createWindowsCommandPayload("GetLocation");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "GetLocation");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createSyncAppCatalogCommand(final Long resourceId) throws JSONException, SyMException {
        final IOSNativeAppCommandPayload commandPayload = this.createWindowsCommandPayload("SyncAppCatalog");
        final JSONObject appListJSON = new WindowsAppCatalogHandler().getSyncAppCatalogCommandJSON(resourceId);
        commandPayload.setRequestData(appListJSON);
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createCorporateWipeCommand(final Long resourceId) throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createWindowsCommandPayload("CorporateWipe");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createAppCatalogStatusSummaryCommand(final Long resourceId) throws JSONException, SyMException {
        final IOSNativeAppCommandPayload commandPayload = this.createWindowsCommandPayload("AppCatalogSummary");
        final JSONObject installedAppSummaryJson = new MDMAppCatalogHandler().getAppCatalogSummaryCommandJSON(resourceId);
        commandPayload.setRequestData(installedAppSummaryJson);
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createTermsSyncCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("TermsOfUse");
        this.logger.log(Level.INFO, "iOS {0} Command has been created successfully", "TermsOfUse");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "TermsOfUse", commandPayload.toString() });
        commandPayload.setCommandVersion("2.0");
        return commandPayload;
    }
    
    public IOSNativeAppCommandPayload createReRegisterFCMTokenCommand() throws JSONException {
        final IOSNativeAppCommandPayload commandPayload = this.createCommandPayload("ReregisterNotificationToken");
        this.logger.log(Level.INFO, "iOS {0} Command has been created successfully", "ReregisterNotificationToken");
        return commandPayload;
    }
    
    static {
        IOSNativeAppPayloadHandler.pHandler = null;
    }
}
