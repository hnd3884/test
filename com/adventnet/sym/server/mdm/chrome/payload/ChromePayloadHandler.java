package com.adventnet.sym.server.mdm.chrome.payload;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.command.smscommand.SmsDbHandler;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.server.settings.location.LocationSettingsRequestHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import com.me.mdm.server.privacy.PrivacyDeviceMessageHandler;
import com.adventnet.persistence.Row;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidConfigurationPayload;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.chrome.payload.transform.DO2ChromePayloadHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Level;
import org.json.JSONException;
import java.util.logging.Logger;

public class ChromePayloadHandler
{
    private static ChromePayloadHandler pHandler;
    private Logger logger;
    public Logger mdmLogger;
    
    public ChromePayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static ChromePayloadHandler getInstance() {
        if (ChromePayloadHandler.pHandler == null) {
            ChromePayloadHandler.pHandler = new ChromePayloadHandler();
        }
        return ChromePayloadHandler.pHandler;
    }
    
    public ChromeCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final ChromeCommandPayload commandPayload = new ChromeCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    public ChromeCommandPayload createAssetScanCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("AssetScan");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "AssetScan");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "AssetScan", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createAssetScanContainerCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("AssetScanContainer");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "AssetScanContainer");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "AssetScanContainer", commandPayload.toString() });
        return commandPayload;
    }
    
    public String generateInstallAppProfile(final Properties configProp, final String profileFileName) {
        return this.generateAppProfile(configProp, profileFileName, "InstallApplication");
    }
    
    private String generateAppProfile(final Properties prop, final String appProfileFileName, final String command) {
        String commandUUID = null;
        try {
            final Long appId = ((Hashtable<K, Long>)prop).get("APP_ID");
            final Long collectionID = ((Hashtable<K, Long>)prop).get("collectionId");
            final ChromeCommandPayload commandPayload = this.createApplicationCommand(collectionID, appId, command);
            commandUUID = commandPayload.getCommandUUID();
            commandPayload.setScope(-1);
            final String property = commandPayload.getPayloadJSON().toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(appProfileFileName, property.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", appProfileFileName);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting generateRemoveAppProfile ", ex);
        }
        return commandUUID;
    }
    
    public ChromeCommandPayload createApplicationCommand(final Long collectionID, final Long appId, final String command) throws JSONException {
        ChromeCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload(command);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            final JSONObject appJSON = AppsUtil.getInstance().getAppJSONObj(appId, collectionID, command);
            commandPayload.setRequestData(appJSON);
            this.logger.log(Level.INFO, "{0} Command has been created successfully", command);
            this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { command, commandPayload.toString() });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createRemoveApplicationCommand....", ex);
        }
        return commandPayload;
    }
    
    public String generateRemoveAppProfile(final Properties configProp, final String profileFileName) {
        return this.generateAppProfile(configProp, profileFileName, "RemoveApplication");
    }
    
    public String generateProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final ChromeCommandPayload commandPayload = this.generateInstallProfilePayload(collectionID);
            commandUUID = commandPayload.getCommandUUID();
            final String property = commandPayload.getPayloadJSON().toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, property.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFilePath);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting generateProfile ", ex);
        }
        return commandUUID;
    }
    
    public ChromeCommandPayload generateInstallProfilePayload(final Long collectionID) throws Exception {
        final ChromeCommandPayload commandPayload = this.createChromeCommandPayLoad("InstallProfile");
        final String payloadData = this.getChromeProfilePayloadData(collectionID);
        this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
        commandPayload.setRequestData(new JSONObject(payloadData));
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    private ChromeCommandPayload createChromeCommandPayLoad(final String requestType) throws JSONException {
        final ChromeCommandPayload commandPayload = new ChromeCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    private String getChromeProfilePayloadData(final Long collectionID) {
        String toXMLPropertyList = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDataItemDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2ChromePayloadHandler handler = new DO2ChromePayloadHandler();
            final AndroidConfigurationPayload cfgPayload = handler.createPayload(dataObject, configDataItemDOList);
            toXMLPropertyList = cfgPayload.getPayloadJSON().toString();
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception ", (Throwable)ex);
        }
        return toXMLPropertyList;
    }
    
    public String generateRemoveProfile(final String profileName, final Long collectionID, final String profileFileName) {
        String commandUUID = null;
        try {
            final ChromeCommandPayload commandPayload = this.generateRemoveProfilePayload(profileName, collectionID);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception  ", exp);
        }
        return commandUUID;
    }
    
    public ChromeCommandPayload generateRemoveProfilePayload(final String payloadIdentifier, final Long collectionID) throws Exception {
        final ChromeCommandPayload commandPayload = this.createRemoveProfileCommand(payloadIdentifier, collectionID);
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public String createScheduleOSUpdateCommandJSON(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final ChromeCommandPayload commandPayload = this.createScheduleOSUpdateCommand(collectionID);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "ChromeOsUpdatePolicy;Collection=" + collectionID.toString(), false);
            commandUUID = commandPayload.getCommandUUID();
            final String toJSON = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toJSON.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception   ", e);
        }
        return commandUUID;
    }
    
    public String createRemoveOSUpdateCommandJSON(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final ChromeCommandPayload commandPayload = this.createRemoveOsUpdateCommand("RemoveOsUpdatePolicy;Collection=" + collectionID.toString(), collectionID);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveChromeOsUpdatePolicy;Collection=" + collectionID.toString(), false);
            commandUUID = commandPayload.getCommandUUID();
            final String toJSON = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toJSON.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception   ", e);
        }
        return commandUUID;
    }
    
    public ChromeCommandPayload createScheduleOSUpdateCommand(final Long collectionID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ChromeOsUpdatePolicy");
        try {
            final DataObject dataObject = OSUpdatePolicyHandler.getInstance().getOSUpdatePolicy(collectionID);
            final Row osUpdatePolicyRow = dataObject.getRow("OSUpdatePolicy");
            final Row osUpdatePolicySettingsRow = dataObject.getRow("DeploymentPolicySettings");
            final Integer policyType = (Integer)osUpdatePolicyRow.get("POLICY_TYPE");
            final Integer noOfDays = (Integer)osUpdatePolicyRow.get("DEFER_DAYS");
            final JSONObject policyJSON = new JSONObject();
            policyJSON.put("AutoUpdatesEnabled", policyType == 2);
            final JSONObject policyDataJSON = new JSONObject();
            if (osUpdatePolicySettingsRow != null) {
                if (policyType == 2) {
                    policyDataJSON.put("RandomScatterDuration", (Object)noOfDays);
                    policyDataJSON.put("MaxUpdatableTargetPrefix", (Object)osUpdatePolicySettingsRow.get("MAX_TARGET_PREFIX"));
                }
                policyDataJSON.put("AutoRebbotAfterUpdate", (Object)osUpdatePolicySettingsRow.get("REBOOT_AFTER_UPDATE"));
            }
            policyJSON.put("PolicyData", (Object)policyDataJSON);
            policyJSON.put("PolicyType", (Object)policyType);
            final JSONObject requestData = new JSONObject();
            requestData.put("Policy", (Object)policyJSON);
            commandPayload.setRequestData(policyJSON);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception  ", e);
        }
        return commandPayload;
    }
    
    public ChromeCommandPayload createRemoveOsUpdateCommand(final String profileName, final Long collectionID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RemoveChromeOsUpdatePolicy");
        final JSONObject idenJSON = new JSONObject();
        idenJSON.put("PayloadIdentifier", (Object)profileName);
        commandPayload.setRequestData(idenJSON);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveOsUpdatePolicy");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveChromeOsUpdatePolicy", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createPrivacySettingsCommand(final Long customerId) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("SyncPrivacySettings");
        final JSONObject privacySettings = PrivacyDeviceMessageHandler.getInstance().getChromePrivacyData(customerId);
        commandPayload.setRequestData(privacySettings);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "SyncPrivacySettings");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SyncPrivacySettings", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createDeviceLockCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("DeviceLock");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceLock");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceLock", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createDeviceInfoCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("DeviceInfo");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createDeviceRingCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RemoteAlarm");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceRing");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceRing", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createClearPasscodeCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ClearPasscode");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ClearPasscode", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createPersonalAppsInfoCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("PersonalAppsInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "PersonalAppsInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createResetPasscodeCommand(final Long resourceID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ResetPasscode");
        final JSONObject resetCommandData = new ResetPasscodeHandler().getResetPasscodeRequestData(resourceID);
        commandPayload.setRequestData(resetCommandData);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ClearPasscode", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createCorporateWipeCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("CorporateWipe");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "CorporateWipe", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createEraseDeviceCommand(final Long resourceID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("EraseDevice");
        final JSONObject wipeOptionData = new RemoteWipeHandler().getWipeOptionData(resourceID);
        commandPayload.setRequestData(wipeOptionData);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "EraseDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "EraseDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createRemoveDeviceCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RemoveDevice");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "RemoveDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createLocationDeviceCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("GetLocation");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "GetLocation");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "GetLocation", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createLocationConfigurationCommand(final DeviceDetails device) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("LocationConfiguration");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "LocationConfiguration", commandPayload.toString() });
        final JSONObject syncReqData = LocationSettingsDataHandler.getInstance().getLocationConfigCommandData(device);
        commandPayload.setRequestData(syncReqData);
        return commandPayload;
    }
    
    public ChromeCommandPayload createLocationSettingCommand(final DeviceDetails device) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("LocationSettings");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "LocationSettings", commandPayload.toString() });
        final JSONObject syncReqData = LocationSettingsRequestHandler.getInstance().getAndroidLocationSettingPayloadJSON(device);
        commandPayload.setRequestData(syncReqData);
        return commandPayload;
    }
    
    public ChromeCommandPayload createInstallProfileCommand(final String payloadData) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "InstallProfile");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "InstallProfile", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createRemoveProfileCommand(final String profileName, final Long collectionID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        final JSONObject idenJSON = new JSONObject();
        idenJSON.put("PayloadIdentifier", (Object)profileName);
        commandPayload.setRequestData(idenJSON);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveProfile");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveProfile", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createSyncAppCatalogCommand() throws JSONException {
        ChromeCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload("SyncAppCatalog");
            commandPayload.setCommandUUID("SyncAppCatalog", false);
            this.logger.log(Level.INFO, "{0} Command has been created successfully", "SyncAppCatalog");
            this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SyncAppCatalog", commandPayload.toString() });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createSyncAppCatalogCommand....", ex);
        }
        return commandPayload;
    }
    
    public ChromeCommandPayload createAgentUpgradeCommand(final DeviceRequest request, final int agentType) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("AgentUpgrade");
        final JSONObject upgradeJSON = MDMAgentUpdateHandler.getInstance().getAgentUpgradeRequestData(agentType, request.customerID, MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.requestMap.get("ServletPath")));
        commandPayload.setRequestData(upgradeJSON);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "AgentUpgrade");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "AgentUpgrade", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createServerUpgradedCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ServerUpgraded");
        return commandPayload;
    }
    
    public ChromeCommandPayload createLanguageLicenseCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("LanguagePackUpdate");
        final boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        final JSONObject langConfigData = new JSONObject();
        langConfigData.put("IsLanguagePackEnabled", isLangPackEnabled);
        commandPayload.setRequestData(langConfigData);
        return commandPayload;
    }
    
    public ChromeCommandPayload createAgentMigrateCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("AgentMigrate");
        final JSONObject agentMigrateCmd = new JSONObject();
        agentMigrateCmd.put("AgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2));
        agentMigrateCmd.put("NewAgentURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 5));
        agentMigrateCmd.put("NewAgentVersionCode", ((Hashtable<K, Object>)MDMUtil.getMDMServerInfo()).get("ANDROID_AGENT_VERSION_CODE"));
        agentMigrateCmd.put("ShowNotificationInKiosk", (Object)MDMUtil.getSyMParameter("ShowNotificationInKiosk"));
        commandPayload.setRequestData(agentMigrateCmd);
        return commandPayload;
    }
    
    public ChromeCommandPayload createUnManageOldAgentCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RemoveOldAgent");
        return commandPayload;
    }
    
    public ChromeCommandPayload createUpdateUserInfoCommand(final Long resourceId, final String udid) throws Exception {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("UpdateUserInfo");
        final HashMap map = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(udid);
        final JSONObject userInfo = new JSONObject();
        userInfo.put("UserName", map.get("NAME"));
        userInfo.put("EmailAddress", map.get("EMAIL_ADDRESS"));
        final JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("DeviceName", (Object)ManagedDeviceHandler.getInstance().getDeviceName(resourceId));
        final JSONObject reqData = new JSONObject();
        reqData.put("UserInfo", (Object)userInfo);
        reqData.put("DeviceInfo", (Object)deviceInfo);
        commandPayload.setRequestData(reqData);
        return commandPayload;
    }
    
    public ChromeCommandPayload createUploadAgentLogCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("UploadAgentLogs");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "UploadAgentLogs");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "UploadAgentLogs", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createGCMReregisterCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ReregisterNotificationToken");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ReregisterNotificationToken", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createSystemAppScanCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("PreloadedAppsInfo");
        commandPayload.setScope(0);
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "PreloadedAppsInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createSystemAppContainerScanCommand() throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("PreloadedContainerAppsInfo");
        commandPayload.setScope(1);
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "PreloadedContainerAppsInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createManagedAppConfigCommand(final Long resourceId) throws JSONException, Exception {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("ManagedApplicationConfiguration");
        final JSONObject configJSON = new JSONObject();
        configJSON.put("ApplicationConfigurations", (Object)new AppConfigDataPolicyHandler().getAppConfigurationForResource(resourceId));
        commandPayload.setRequestData(configJSON);
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ManagedApplicationConfiguration", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createDeviceApprovalCommand(final Long resourceId) throws JSONException, Exception {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("DeviceApproval");
        final JSONObject configJSON = new JSONObject();
        configJSON.put("DeviceApprovalStatus", new StoreAccountManagementHandler().getDeviceAccountState(resourceId));
        commandPayload.setRequestData(configJSON);
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceApproval", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createSmsPublicKeyCommand(final Long customerId) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("SavePublicKey");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "SavePublicKey");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SavePublicKey", commandPayload.toString() });
        final SmsDbHandler databaseHandler = new SmsDbHandler();
        final JSONObject publicKey = new JSONObject();
        publicKey.put("PublicKey", (Object)databaseHandler.getPublicKey(customerId));
        commandPayload.setRequestData(publicKey);
        return commandPayload;
    }
    
    static {
        ChromePayloadHandler.pHandler = null;
    }
}
