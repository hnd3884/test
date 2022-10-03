package com.adventnet.sym.server.mdm.android.payload;

import java.util.Hashtable;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.agent.handlers.android.servletmigration.AndroidServletMigrationUtil;
import com.me.mdm.server.agent.DiscoveryServiceHandler;
import com.adventnet.persistence.Row;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.ServerCertificateFetchingUtil;
import com.adventnet.sym.server.mdm.command.smscommand.SmsDbHandler;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.me.mdm.server.apps.permission.config.PermissionConfigDataPolicyHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.blacklist.android.AndroidBlacklistProcessor;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.android.payload.transform.DO2AndroidPayloadHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import java.util.Properties;
import com.me.mdm.server.settings.DownloadSettingsHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.mdm.server.apps.android.afw.AFWAccountRegistrationHandler;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import com.me.mdm.server.apps.config.AppConfigDataPolicyHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.settings.location.LocationSettingsRequestHandler;
import com.me.mdm.server.security.passcode.AndroidRecoveryPasscodeHandler;
import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.io.IOException;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.dd.plist.Base64;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import java.util.logging.Level;
import com.me.mdm.server.android.message.ResetPasscodeTokenUpdator;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import java.net.URL;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Logger;

public class AndroidPayloadHandler
{
    private static AndroidPayloadHandler pHandler;
    private Logger logger;
    public Logger mdmLogger;
    
    public AndroidPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static AndroidPayloadHandler getInstance() {
        if (AndroidPayloadHandler.pHandler == null) {
            AndroidPayloadHandler.pHandler = new AndroidPayloadHandler();
        }
        return AndroidPayloadHandler.pHandler;
    }
    
    public AndroidCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final AndroidCommandPayload commandPayload = new AndroidCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeviceScanCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AndroidInvScan");
        return commandPayload;
    }
    
    public AndroidCommandPayload createContainerScanCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AndroidInvScanContainer");
        return commandPayload;
    }
    
    public AndroidCommandPayload createAssetScanCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AssetScan");
        return commandPayload;
    }
    
    public AndroidCommandPayload createAssetScanContainerCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AssetScanContainer");
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeviceLockCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DeviceLock");
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoteSessionCommand(final Long resourceID, final Long cusID) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoteSession");
        final JSONObject sessionrequestData = new JSONObject();
        final String sessionKey = new RemoteSessionManager().getSessionKey(resourceID);
        final URL assistUrl = new URL(MDMApiFactoryProvider.getAssistAuthTokenHandler().getAssistSessionUrl(cusID));
        sessionrequestData.put("SessionKey", (Object)sessionKey);
        sessionrequestData.put("SessionAppServerUrl", (Object)(assistUrl.getProtocol() + "://" + assistUrl.getAuthority()));
        commandPayload.setRequestData(sessionrequestData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createPauseKioskCommand(final Long resourceID) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("KioskCommand");
        final Long delay = new KioskPauseResumeManager().getResumeDelay(resourceID);
        final JSONObject commandData = new JSONObject();
        if (delay != null) {
            commandData.put("ReEnterTime", (Object)delay);
        }
        commandData.put("ExitKiosk", true);
        commandPayload.setRequestData(commandData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createResumeKioskCommand(final Long resourceID) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("KioskCommand");
        final JSONObject commandData = new JSONObject();
        commandData.put("ExitKiosk", false);
        return commandPayload;
    }
    
    public AndroidCommandPayload createTermsSyncCommand(final Long resourceID) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("TermsOfUse");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSyncDocumentsCommand(final Long resourceID) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SyncDocuments");
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeviceInfoCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DeviceInfo");
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeviceRingCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoteAlarm");
        return commandPayload;
    }
    
    public AndroidCommandPayload createClearPasscodeCommand(final Long resourceId) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ClearPasscode");
        final JSONObject clearCommandData = new JSONObject();
        try {
            clearCommandData.put("ResetPasscodeToken", (Object)new ResetPasscodeTokenUpdator().getResetPasscodeToken(resourceId));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while getResetPasscodeToken. res ID : " + n);
        }
        commandPayload.setRequestData(clearCommandData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createPersonalAppsInfoCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("PersonalAppsInfo");
        return commandPayload;
    }
    
    public AndroidCommandPayload createResetPasscodeCommand(final Long resourceID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ResetPasscode");
        final JSONObject resetCommandData = new ResetPasscodeHandler().getResetPasscodeRequestData(resourceID);
        commandPayload.setRequestData(resetCommandData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createCorporateWipeCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("CorporateWipe");
        return commandPayload;
    }
    
    public AndroidCommandPayload createEraseDeviceCommand(final Long resourceID, final DeviceCommand deviceCommand) throws JSONException, IOException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("EraseDevice");
        final int status = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resourceID);
        JSONObject wipeOptionData = new JSONObject();
        if (MDMStringUtils.isEmpty(deviceCommand.commandStr)) {
            wipeOptionData = new RemoteWipeHandler().getWipeOptionData(resourceID);
        }
        else {
            final byte[] decodedData = Base64.decode(deviceCommand.commandStr);
            final JSONObject tempJSON = new JSONObject(new String(decodedData));
            wipeOptionData.put("WipeSDCard", tempJSON.getBoolean("wipe_sd_card"));
            wipeOptionData.put("RetainMDM", tempJSON.getBoolean("wipe_but_retain_mdm"));
            wipeOptionData.put("WIPE_LOCK_PIN", (Object)String.valueOf(tempJSON.get("wipe_lock_pin")));
        }
        if (status == 9 || status == 11 || status == 10) {
            wipeOptionData.put("Deprovision", (Object)Boolean.TRUE);
        }
        final JSONObject wipeReason = ManagedDeviceHandler.getInstance().getCommandReasonJson(resourceID, DeviceCommandRepository.getInstance().getCommandID(deviceCommand.commandType));
        if (wipeReason.has("WipeReason")) {
            wipeOptionData.put("ReasonForWipe", (Object)wipeReason);
        }
        commandPayload.setRequestData(wipeOptionData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoveDeviceCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoveDevice");
        return commandPayload;
    }
    
    public AndroidCommandPayload createLocationDeviceCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("GetLocation");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSyncPrivacySettingsCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SyncPrivacySettings");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSyncAgentSettingCommand(final DeviceDetails device) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SyncAgentSettings");
        final JSONObject syncReqData = this.getSyncAgentSettingJSON(device);
        commandPayload.setRequestData(syncReqData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createDownloadSettingsForAgentCommand(final DeviceDetails device) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SyncDownloadSettings");
        final JSONObject downloadSettingsData = this.getDownloadSettingsForAgent(device);
        commandPayload.setRequestData(downloadSettingsData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createLocationConfigurationCommand(final DeviceDetails device) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("LocationConfiguration");
        final JSONObject syncReqData = LocationSettingsDataHandler.getInstance().getLocationConfigCommandData(device);
        syncReqData.put("LocationTrackingMethod", LocationSettingsDataHandler.getInstance().getLocationConfigurationMethod());
        commandPayload.setRequestData(syncReqData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createBatteryConfigurationCommand(final DeviceDetails device) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("BATTERY_CONFIGURATION");
        final JSONObject batteryConfigJson = MDMBatterySettingsDBHandler.getInstance().getBatteryConfigurationForDevice(device);
        commandPayload.setRequestData(batteryConfigJson);
        return commandPayload;
    }
    
    public AndroidCommandPayload createAndroidPasscodeRecoveryCommand(final DeviceDetails device) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AndroidPasscodeRecoveryCommand");
        final JSONObject androidPasscodeRecoveryDetailsJson = AndroidRecoveryPasscodeHandler.getInstance().getAndroidPasscodeRecoveryDetails(device.resourceId);
        commandPayload.setRequestData(androidPasscodeRecoveryDetailsJson);
        return commandPayload;
    }
    
    public AndroidCommandPayload createLocationSettingCommand(final DeviceDetails device) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("LocationSettings");
        final JSONObject syncReqData = LocationSettingsRequestHandler.getInstance().getAndroidLocationSettingPayloadJSON(device);
        commandPayload.setRequestData(syncReqData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createInstallProfileCommand(final String payloadData) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoveProfileCommand(final String profileName, final Long collectionID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        final JSONObject idenJSON = new JSONObject();
        idenJSON.put("PayloadIdentifier", (Object)profileName);
        commandPayload.setRequestData(idenJSON);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoveProfileCommand(final String profileName, final Long collectionID, final String requestType) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload(requestType);
        final JSONObject idenJSON = new JSONObject();
        idenJSON.put("PayloadIdentifier", (Object)profileName);
        commandPayload.setRequestData(idenJSON);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        return commandPayload;
    }
    
    public AndroidCommandPayload createApplicationCommand(final Long collectionID, final Long appId, final String command) throws JSONException {
        AndroidCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload(command);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            final JSONObject appJSON = AppsUtil.getInstance().getAppJSONObj(appId, collectionID, command);
            commandPayload.setRequestData(appJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createRemoveApplicationCommand....", ex);
        }
        return commandPayload;
    }
    
    private AndroidCommandPayload createAppConfigProfileCommand(final Long collectionID, final String command) {
        AndroidCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload(command);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            final JSONObject appConfigJSON = new JSONObject();
            appConfigJSON.put("ApplicationConfigurations", (Object)AppConfigDataPolicyHandler.getInstance(2).getAppConfigurationsForCollectionID(collectionID, command));
            commandPayload.setRequestData(appConfigJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in createAppConfigProfileCommand....", ex);
        }
        return commandPayload;
    }
    
    private AndroidCommandPayload createScheduleConfigProfileCommand(final Long collectionID, final String command) {
        AndroidCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload(command);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            final JSONObject scheduleConfig = new JSONObject();
            scheduleConfig.put("ScheduleConfigurations", (Object)ScheduledActionsUtils.getScheduleDetailsAsJSON(collectionID));
            scheduleConfig.put("PayloadType", (Object)"ScheduleAction");
            final long profileID = ScheduledActionsUtils.getProfileIDForCollection(collectionID);
            final String payloadIdentifier = ScheduledActionsUtils.getProfilePayloadIdentifierForProfile(profileID);
            scheduleConfig.put("payloadIdentifier", (Object)payloadIdentifier);
            commandPayload.setRequestData(scheduleConfig);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in createAppConfigProfileCommand....", ex);
        }
        return commandPayload;
    }
    
    public AndroidCommandPayload createSyncAppCatalogCommand() throws JSONException {
        AndroidCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload("SyncAppCatalog");
            commandPayload.setCommandUUID("SyncAppCatalog", false);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createSyncAppCatalogCommand....", ex);
        }
        return commandPayload;
    }
    
    public AndroidCommandPayload createAddAFWAccountCommand(final Long customerId, final String deviceUDID) throws JSONException {
        AndroidCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload("AddAFWAccount");
            commandPayload.setCommandUUID("AddAFWAccount", false);
            String token = "";
            final JSONObject accountJSON = new JSONObject();
            token = new AFWAccountRegistrationHandler().generateAFWAccountToken(deviceUDID, customerId);
            accountJSON.put("Token", (Object)token);
            commandPayload.setRequestData(accountJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createAddAFWAccountCommand....", ex);
        }
        return commandPayload;
    }
    
    private JSONObject getSyncAgentSettingJSON(final DeviceDetails device) {
        final JSONObject requestData = new JSONObject();
        final String kioskRevokePasswordAlgorithm = "SHA-256";
        try {
            final MDMAgentSettingsHandler settingHandler = new MDMAgentSettingsHandler();
            final JSONObject androidAgentSettingData = settingHandler.getAndroidAgentSetting(device.customerId);
            final JSONObject agentRebrandingData = settingHandler.getAgentRebrandingSetting(device.customerId);
            final JSONObject agentComplianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(device.customerId);
            final JSONObject dadmin = new JSONObject();
            dadmin.put("ActionOnDeactivation ", (Object)new Integer(0));
            dadmin.put("WarningMessage", androidAgentSettingData.get("DEACTIVATION_MESSAGE"));
            dadmin.put("DisableDeviceAdmin", androidAgentSettingData.get("ALLOW_ADMIN_DISABLE"));
            dadmin.put("RecoveryPassword", androidAgentSettingData.get("RECOVERY_PASSWORD_ENCRYPTED"));
            dadmin.put("ChecksumEnabled", androidAgentSettingData.get("VALIDATE_CHECKSUM"));
            dadmin.put("ShortSupportMessage", androidAgentSettingData.get("SHORT_SUPPORT_MESSAGE"));
            dadmin.put("LongSupportMessage", androidAgentSettingData.get("LONG_SUPPORT_MESSAGE"));
            final JSONObject profile = new JSONObject();
            profile.put("GraceTimeToConfigurePolicy", androidAgentSettingData.get("GRACE_TIME"));
            profile.put("TimeToRemindUser", androidAgentSettingData.get("USER_REM_TIME"));
            profile.put("NoOfTimeToRemindUser", androidAgentSettingData.get("USER_REM_COUNT"));
            final JSONObject appSettings = new JSONObject();
            appSettings.put("HideServerDetails", androidAgentSettingData.get("HIDE_SERVER_DETAILS"));
            appSettings.put("HideMDMServerDetails", androidAgentSettingData.get("HIDE_SERVER_INFO"));
            appSettings.put("HideMDMAgent", androidAgentSettingData.get("HIDE_MDM_APP"));
            final JSONObject rebrandingSetting = new JSONObject();
            final String appName = agentRebrandingData.optString("MDM_APP_NAME");
            final String path = agentRebrandingData.optString("REBRANDING_PATH");
            final String iconFileName = agentRebrandingData.optString("MDM_APP_ICON_FILE_NAME");
            final String splashImgFileName = agentRebrandingData.optString("MDM_APP_SPLASH_IMAGE_FILE_NAME");
            if (appName != null && !appName.equals("ME MDM App") && !appName.equals("")) {
                rebrandingSetting.put("CustomAppName", (Object)appName);
            }
            final HashMap hm = new HashMap();
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", false);
            if (iconFileName != null && !iconFileName.equals("")) {
                hm.put("path", path + "/" + iconFileName);
                final String customAppPath = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                rebrandingSetting.put("CustomAppIcon", (Object)customAppPath);
            }
            if (splashImgFileName != null && !splashImgFileName.equals("")) {
                hm.put("path", path + "/" + splashImgFileName);
                final String customSplashImage = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                rebrandingSetting.put("CustomSplashImage", (Object)customSplashImage);
            }
            final JSONObject wakeUpConfig = MDMAgentSettingsHandler.getInstance().getAndroidPushNotificationConfig(device.customerId);
            wakeUpConfig.put("ResourceID", device.resourceId);
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(device.customerId);
            final JSONObject locationJsonObj = new JSONObject();
            final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
            int locationServices = -1;
            if (trackingStatus == 2) {
                locationServices = 1;
            }
            else if (new LostModeDataHandler().isTrackingNeededForLostMode(device.resourceId)) {
                locationServices = 1;
            }
            else {
                final boolean isLocationTrackingDeviceEnable = (locationServices = (LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(device.resourceId) ? 1 : 0)) != 0;
            }
            locationJsonObj.put("LocationServices", locationServices);
            locationJsonObj.put("ContactInterval", locationSettingsJSON.get("LOCATION_INTERVAL"));
            locationJsonObj.put("LocationRadius", locationSettingsJSON.get("LOCATION_RADIUS"));
            locationJsonObj.put("IsLocationHistoryEnabled", locationSettingsJSON.get("LOCATION_HISTORY_STATUS"));
            locationJsonObj.put("LocationTrackingSetting", LocationSettingsDataHandler.getInstance().getTrackingSettingAndroid(device, trackingStatus));
            final JSONObject contentMgmtObj = new JSONObject();
            final Boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
            if (isProfessional) {
                contentMgmtObj.put("isContentRepoEnabled", DocMgmtDataHandler.getInstance().isContentMgmtForAndroidEnabled(device.udid));
            }
            final JSONObject announcementConfig = new JSONObject();
            announcementConfig.put("IsAnnouncementEnabled", true);
            final JSONObject complianceConfig = new JSONObject();
            complianceConfig.put("CorporateWipeOnRootedDevice", agentComplianceRules.get("CORPORATE_WIPE_ROOTED_DEVICES"));
            complianceConfig.put("WipeIntegrityFailedDevices", agentComplianceRules.get("WIPE_INTEGRITY_FAILED_DEVICES"));
            complianceConfig.put("WipeCTSFailedDevices", agentComplianceRules.get("WIPE_CTS_FAILED_DEVICES"));
            requestData.put("DeviceAdminSettings", (Object)dadmin);
            requestData.put("ProfileSettings", (Object)profile);
            requestData.put("LocationSettings", (Object)locationJsonObj);
            requestData.put("AppSettings", (Object)appSettings);
            requestData.put("AgentRebrandingSettings", (Object)rebrandingSetting);
            requestData.put("WakeUpSettings", (Object)wakeUpConfig);
            requestData.put("ContentMgmtSettings", (Object)contentMgmtObj);
            requestData.put("ComplianceRules", (Object)complianceConfig);
            requestData.put("KioskPasswordAlgoType", (Object)kioskRevokePasswordAlgorithm);
            requestData.put("AnnouncementSettings", (Object)announcementConfig);
        }
        catch (final Exception ex) {
            this.mdmLogger.log(Level.SEVERE, "Exception while getting sync agent settings ", ex);
        }
        return requestData;
    }
    
    public JSONObject getDownloadSettingsForAgent(final DeviceDetails deviceDetails) throws Exception {
        final JSONObject downloadAgentSettings = new JSONObject();
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DisableRetrySchedulerInAgent")) {
            final JSONObject downloadAgentSettingsData = DownloadSettingsHandler.getInstance().getDownloadSettingsForAgent(deviceDetails.customerId);
            downloadAgentSettings.put("MinRetryDelay", downloadAgentSettingsData.get("MIN_RETRY_DELAY"));
            downloadAgentSettings.put("MaxRetryDelay", downloadAgentSettingsData.get("MAX_RETRY_DELAY"));
            downloadAgentSettings.put("MaxRetryCount", downloadAgentSettingsData.get("MAX_RETRY_COUNT"));
            downloadAgentSettings.put("RestrictedDomainsFromRetry", downloadAgentSettingsData.get("EXCLUDED_DOMAIN"));
            downloadAgentSettings.put("DelayRandomness", downloadAgentSettingsData.get("DELAY_RANDOM"));
            downloadAgentSettings.put("CustomRetryDelay", downloadAgentSettingsData.get("CUSTOM_RETRY_DELAY"));
            downloadAgentSettings.put("ShouldRetryDownload", (Object)Boolean.TRUE);
        }
        else {
            downloadAgentSettings.put("ShouldRetryDownload", (Object)Boolean.FALSE);
        }
        return downloadAgentSettings;
    }
    
    public String generateProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final AndroidCommandPayload commandPayload = this.generateInstallProfilePayload(collectionID);
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
    
    public AndroidCommandPayload generateInstallProfilePayload(final Long collectionID) throws Exception {
        final AndroidCommandPayload commandPayload = this.createAndroidCommandPayLoad("InstallProfile");
        final String payloadData = this.getAndroidProfilePayloadData(collectionID);
        commandPayload.setRequestData(new JSONObject(payloadData));
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public String generateProfile(final Long collectionID, final String profileFilePath, final String requestType) {
        String commandUUID = null;
        try {
            final AndroidCommandPayload commandPayload = this.generateProfilePayload(collectionID, requestType);
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
    
    public AndroidCommandPayload generateProfilePayload(final Long collectionID, final String requestType) throws Exception {
        final AndroidCommandPayload commandPayload = this.createAndroidCommandPayLoad(requestType);
        final String payloadData = this.getAndroidProfilePayloadData(collectionID);
        commandPayload.setRequestData(new JSONObject(payloadData));
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    private String generateAppProfile(final Properties prop, final String appProfileFileName, final String command) {
        String commandUUID = null;
        try {
            final Long appId = ((Hashtable<K, Long>)prop).get("APP_ID");
            final Long collectionID = ((Hashtable<K, Long>)prop).get("collectionId");
            final AndroidCommandPayload commandPayload = this.createApplicationCommand(collectionID, appId, command);
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
    
    private String generateAppConfigProfile(final Properties collectionProps, final String appProfileFileName, final String command) {
        String commandUUID = null;
        try {
            final Long collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            final AndroidCommandPayload commandPayload = this.generateAppConfigPayload(collectionID, command);
            commandUUID = commandPayload.getCommandUUID();
            final String property = commandPayload.getPayloadJSON().toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(appProfileFileName, property.getBytes());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while generating generate app config profile", ex);
        }
        return commandUUID;
    }
    
    private String generateScheduleConfigProfile(final Properties collectionProps, final String appProfileFileName, final String command) {
        String commandUUID = null;
        try {
            final Long collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            final AndroidCommandPayload commandPayload = this.generateScheduleConfigPayload(collectionID, command);
            commandUUID = commandPayload.getCommandUUID();
            final String property = commandPayload.getPayloadJSON().toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(appProfileFileName, property.getBytes());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while generating generate app config profile", ex);
        }
        return commandUUID;
    }
    
    public AndroidCommandPayload generateAppConfigPayload(final Long collectionID, final String command) throws Exception {
        final AndroidCommandPayload commandPayload = this.createAppConfigProfileCommand(collectionID, command);
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public AndroidCommandPayload generateScheduleConfigPayload(final Long collectionID, final String command) throws Exception {
        final AndroidCommandPayload commandPayload = this.createScheduleConfigProfileCommand(collectionID, command);
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public String generateInstallAppProfile(final Properties configProp, final String profileFileName) {
        return this.generateAppProfile(configProp, profileFileName, "InstallApplication");
    }
    
    public String generateRemoveAppProfile(final Properties configProp, final String profileFileName) {
        return this.generateAppProfile(configProp, profileFileName, "RemoveApplication");
    }
    
    public String generateAppConfigurationInstallProfile(final Properties collectionProps, final String profileFileName) {
        return this.generateAppConfigProfile(collectionProps, profileFileName, "InstallApplicationConfiguration");
    }
    
    public String generateAppConfigurationRemoveProfile(final Properties collectionProps, final String profileFileName) {
        return this.generateAppConfigProfile(collectionProps, profileFileName, "RemoveApplicationConfiguration");
    }
    
    public String generateScheduleConfigurationInstallProfile(final Properties collectionProps, final String profileFileName) {
        return this.generateScheduleConfigProfile(collectionProps, profileFileName, "InstallScheduleConfiguration");
    }
    
    public String generateScheduleConfigurationRemoveProfile(final Properties collectionProps, final String profileFileName) {
        return this.generateScheduleConfigProfile(collectionProps, profileFileName, "RemoveScheduleConfiguration");
    }
    
    public DeviceMessage createSyncAppCatalogMessage(final HashMap<String, String> hmap) throws JSONException {
        DeviceMessage deviceMsg = null;
        final int filterValue = -1;
        final String searchValue = null;
        Long deviceLastSyncTime = -1L;
        try {
            final String strUDID = hmap.get("UDID");
            final String messageType = hmap.get("MessageType");
            final String lastSyncTimeStr = hmap.get("LastSyncTime");
            if (lastSyncTimeStr != null) {
                deviceLastSyncTime = Long.valueOf(lastSyncTimeStr);
            }
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final Long appLastUpdatedTime = AppsUtil.getInstance().getAppCatalogSyncTime(resourceId);
            deviceMsg = new DeviceMessage();
            if (deviceLastSyncTime < appLastUpdatedTime || appLastUpdatedTime == -1L) {
                final JSONArray installedAppsJSONArr = AppsUtil.getInstance().getInstalledAppJSONArrayForResource(resourceId, filterValue, searchValue);
                final JSONObject installAppsJson = new JSONObject();
                installAppsJson.put("LastSyncTime", (Object)appLastUpdatedTime);
                installAppsJson.put("ManagedApps", (Object)installedAppsJSONArr);
                deviceMsg.setMessageResponseJSON(installAppsJson);
                deviceMsg.setMessageType(messageType);
                deviceMsg.setMessageStatus("Acknowledged");
            }
            else {
                this.logger.log(Level.INFO, "No change in app catalog for device {0} since {1}", new Object[] { resourceId, deviceLastSyncTime });
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in createSyncAppCatalogMessage....", ex);
        }
        return deviceMsg;
    }
    
    private AndroidCommandPayload createAndroidCommandPayLoad(final String requestType) throws JSONException {
        final AndroidCommandPayload commandPayload = new AndroidCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    private String getAndroidProfilePayloadData(final Long collectionID) {
        String toXMLPropertyList = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDataItemDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2AndroidPayloadHandler handler = new DO2AndroidPayloadHandler();
            final AndroidConfigurationPayload cfgPayload = handler.createPayload(dataObject, configDataItemDOList);
            toXMLPropertyList = cfgPayload.getPayloadJSON().toString();
            toXMLPropertyList = toXMLPropertyList.replaceAll("%profileId%", collectionID.toString());
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception ", (Throwable)ex);
        }
        return toXMLPropertyList;
    }
    
    public String generateRemoveProfile(final String profileName, final Long collectionID, final String profileFileName) {
        String commandUUID = null;
        try {
            final AndroidCommandPayload commandPayload = this.createRemoveProfileCommand(profileName, collectionID);
            commandUUID = commandPayload.getCommandUUID();
            commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
            final String toXMLPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception  ", exp);
        }
        return commandUUID;
    }
    
    public AndroidCommandPayload generateRemoveProfilePayload(final String payloadIdentifier, final Long collectionID) throws Exception {
        final AndroidCommandPayload commandPayload = this.createRemoveProfileCommand(payloadIdentifier, collectionID);
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public String generateRemoveProfile(final String profileName, final Long collectionID, final String profileFileName, final String requestType) {
        String commandUUID = null;
        try {
            final AndroidCommandPayload commandPayload = this.generateRemoveProfileProfilePayload(profileName, collectionID, requestType);
            commandUUID = commandPayload.getCommandUUID();
            final String toJSONPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toJSONPropertyList.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception  ", exp);
        }
        return commandUUID;
    }
    
    public AndroidCommandPayload generateRemoveProfileProfilePayload(final String profileName, final Long collectionID, final String requestType) throws Exception {
        final AndroidCommandPayload commandPayload = this.createRemoveProfileCommand(profileName, collectionID, requestType);
        commandPayload.setScope(DeviceCommandRepository.getInstance().getProfileScopeForCollection(collectionID));
        return commandPayload;
    }
    
    public AndroidCommandPayload createAgentUpgradeCommand(final DeviceRequest request, final int agentType) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AgentUpgrade");
        final JSONObject upgradeJSON = MDMAgentUpdateHandler.getInstance().getAgentUpgradeRequestData(agentType, request.customerID, MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.requestMap.get("ServletPath")));
        commandPayload.setRequestData(upgradeJSON);
        return commandPayload;
    }
    
    public AndroidCommandPayload createBlacklistWhitelistCommand(final DeviceDetails deviceDetails, final String command, final int scope) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload(command);
        final HashMap params = new HashMap();
        params.put("scope", scope);
        params.put("RESOURCE_ID", deviceDetails.resourceId);
        final JSONObject blacklistWhitelistData = (JSONObject)new AndroidBlacklistProcessor().processBlackListRequest(params);
        commandPayload.setRequestData(blacklistWhitelistData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createServerUpgradedCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ServerUpgraded");
        return commandPayload;
    }
    
    public AndroidCommandPayload createLanguageLicenseCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("LanguagePackUpdate");
        final boolean isLangPackEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        final JSONObject langConfigData = new JSONObject();
        langConfigData.put("IsLanguagePackEnabled", isLangPackEnabled);
        commandPayload.setRequestData(langConfigData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createAgentMigrateCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("AgentMigrate");
        final JSONObject agentMigrateCmd = new JSONObject();
        agentMigrateCmd.put("AgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2));
        agentMigrateCmd.put("NewAgentURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 5));
        agentMigrateCmd.put("NewAgentVersionCode", ((Hashtable<K, Object>)MDMUtil.getMDMServerInfo()).get("ANDROID_AGENT_VERSION_CODE"));
        agentMigrateCmd.put("ShowNotificationInKiosk", (Object)MDMUtil.getSyMParameter("ShowNotificationInKiosk"));
        commandPayload.setRequestData(agentMigrateCmd);
        return commandPayload;
    }
    
    public AndroidCommandPayload createUnManageOldAgentCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoveOldAgent");
        return commandPayload;
    }
    
    public AndroidCommandPayload createUpdateUserInfoCommand(final Long resourceId, final String udid) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("UpdateUserInfo");
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
    
    public AndroidCommandPayload createUploadAgentLogCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("UploadAgentLogs");
        return commandPayload;
    }
    
    public AndroidCommandPayload createGCMReregisterCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ReregisterNotificationToken");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSystemAppScanCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("PreloadedAppsInfo");
        commandPayload.setScope(0);
        return commandPayload;
    }
    
    public AndroidCommandPayload createSystemAppContainerScanCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("PreloadedContainerAppsInfo");
        commandPayload.setScope(1);
        return commandPayload;
    }
    
    public AndroidCommandPayload createManagedAppConfigCommand(final Long resourceId) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ManagedApplicationConfiguration");
        final JSONObject configJSON = new JSONObject();
        configJSON.put("ApplicationConfigurations", (Object)new AppConfigDataPolicyHandler().getAppConfigurationForResource(resourceId));
        commandPayload.setRequestData(configJSON);
        return commandPayload;
    }
    
    public AndroidCommandPayload createAppPermissionPolicyCommand(final Long resourceId) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ManagedAppPermissionPolicy");
        final JSONObject configJSON = new JSONObject();
        configJSON.put("AppPermissionPolicies", (Object)new PermissionConfigDataPolicyHandler().getAppPermissionConfigForResource(resourceId));
        commandPayload.setRequestData(configJSON);
        return commandPayload;
    }
    
    public AndroidCommandPayload createDeviceApprovalCommand(final Long resourceId) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DeviceApproval");
        final JSONObject configJSON = new JSONObject();
        configJSON.put("DeviceApprovalStatus", new StoreAccountManagementHandler().getDeviceAccountState(resourceId));
        commandPayload.setRequestData(configJSON);
        return commandPayload;
    }
    
    public AndroidCommandPayload createEnableLostModeCommand(final Long resourceID, final DeviceCommand deviceCommand) throws JSONException, Exception {
        AndroidCommandPayload commandPayload = null;
        JSONObject enableLostModeData = new JSONObject();
        if (MDMStringUtils.isEmpty(deviceCommand.commandStr)) {
            commandPayload = this.createCommandPayload("EnableLostMode");
            enableLostModeData = new LostModeDataHandler().getAndroidEnableLostModePayloadData(resourceID);
        }
        else {
            final byte[] decodedByteArray = Base64.decode(deviceCommand.commandStr);
            final JSONObject commandData = new JSONObject(new String(decodedByteArray));
            enableLostModeData.put("LostModeMessage", (Object)commandData.optString("lock_message"));
            enableLostModeData.put("LostModePhone", (Object)commandData.optString("phone_number"));
            commandPayload = new AndroidCommandPayload();
            commandPayload.commandUUID = deviceCommand.commandUUID;
            commandPayload.setRequestType("EnableLostMode");
        }
        commandPayload.setRequestData(enableLostModeData);
        return commandPayload;
    }
    
    public AndroidCommandPayload createDisableLostModeCommand(final Long resourceID) throws JSONException, Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DisableLostMode");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSmsPublicKeyCommand(final Long customerId) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SavePublicKey");
        final SmsDbHandler databaseHandler = new SmsDbHandler();
        final JSONObject publicKey = new JSONObject();
        publicKey.put("PublicKey", (Object)databaseHandler.getPublicKey(customerId));
        commandPayload.setRequestData(publicKey);
        return commandPayload;
    }
    
    public AndroidCommandPayload createServerCertificateJSON(final Long customerId) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("CertificateRequest");
        JSONObject responseObject = new JSONObject();
        responseObject = ServerCertificateFetchingUtil.getInstance().fetchCertificateJSON();
        commandPayload.setRequestData(responseObject);
        return commandPayload;
    }
    
    public AndroidCommandPayload createRestartDeviceCommand(final Long resourceID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RebootDevice");
        final JSONObject restartInfo = this.getRestartInfo(resourceID);
        commandPayload.setRequestData(restartInfo);
        return commandPayload;
    }
    
    public JSONObject getRestartInfo(final Long resourceID) throws JSONException {
        try {
            final JSONObject restartInfo = new JSONObject();
            restartInfo.put("PostRebootMessage", (Object)MDMI18N.getMsg("mdm.inv.reboot.message", new Object[0]));
            restartInfo.put("NotifyAndReboot", false);
            this.mdmLogger.log(Level.FINE, "Reboot command generated:{0}", restartInfo);
            return restartInfo;
        }
        catch (final Exception ex) {
            this.mdmLogger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String createScheduleOSUpdateCommandJSON(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final AndroidCommandPayload commandPayload = this.createScheduleOSUpdateCommand(collectionID);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "OsUpdatePolicy;Collection=" + collectionID.toString(), false);
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
            final AndroidCommandPayload commandPayload = this.createRemoveOsUpdateCommand("RemoveOsUpdatePolicy;Collection=" + collectionID.toString(), collectionID);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveOsUpdatePolicy;Collection=" + collectionID.toString(), false);
            commandUUID = commandPayload.getCommandUUID();
            final String toJSON = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toJSON.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception   ", e);
        }
        return commandUUID;
    }
    
    public AndroidCommandPayload createScheduleOSUpdateCommand(final Long collectionID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("OsUpdatePolicy");
        try {
            final DataObject dataObject = OSUpdatePolicyHandler.getInstance().getOSUpdatePolicy(collectionID);
            final Row osUpdatePolicyRow = dataObject.getRow("OSUpdatePolicy");
            final Integer policyType = (Integer)osUpdatePolicyRow.get("POLICY_TYPE");
            final Integer noOfDays = (Integer)osUpdatePolicyRow.get("DEFER_DAYS");
            final JSONObject policyJSON = new JSONObject();
            policyJSON.put("PolicyType", (Object)policyType);
            final JSONObject policyDataJSON = new JSONObject();
            if (policyType == 3) {
                policyDataJSON.put("NoOfDaysToDefer", (Object)noOfDays);
            }
            final JSONObject policyWindowDataJSON = new JSONObject();
            final Row osUpdatePolicyWindowRow = dataObject.getRow("DeploymentWindowTemplate");
            if (osUpdatePolicyWindowRow != null) {
                final Integer startMinute = (Integer)osUpdatePolicyWindowRow.get("WINDOW_START_TIME");
                final Integer endMinute = (Integer)osUpdatePolicyWindowRow.get("WINDOW_END_TIME");
                final String weekOfMonth = (String)osUpdatePolicyWindowRow.get("WINDOW_WEEK_OF_MONTH");
                final String dayOfWeek = (String)osUpdatePolicyWindowRow.get("WINDOW_DAY_OF_WEEK");
                policyWindowDataJSON.put("StartTime", (Object)startMinute);
                policyWindowDataJSON.put("EndTime", (Object)endMinute);
                policyWindowDataJSON.put("WeekOfMonth", (Object)weekOfMonth);
                policyWindowDataJSON.put("DayOfWeek", (Object)dayOfWeek);
            }
            final JSONObject policyNotifyDataJSON = new JSONObject();
            final Row osUpdatePolicyNotifyRow = dataObject.getRow("DeploymentNotifTemplate");
            if (osUpdatePolicyNotifyRow != null) {
                final String title = (String)osUpdatePolicyNotifyRow.get("NOTIFY_TITLE");
                final String message = (String)osUpdatePolicyNotifyRow.get("NOTIFY_MESSAGE");
                final Boolean allowToSkip = (Boolean)osUpdatePolicyNotifyRow.get("ALLOW_USERS_TO_SKIP");
                final Integer maxSkips = (Integer)osUpdatePolicyNotifyRow.get("MAX_SKIPS_ALLOWED");
                policyNotifyDataJSON.put("Title", (Object)title);
                policyNotifyDataJSON.put("Message", (Object)message);
                policyNotifyDataJSON.put("AllowToSkip", (Object)allowToSkip);
                policyNotifyDataJSON.put("MaxSkips", (Object)maxSkips);
            }
            final JSONObject policyFilesJSON = new JSONObject();
            final Row policyFilesRow = dataObject.getRow("DeploymentPolicyFiles");
            if (policyFilesRow != null) {
                final String downloadLoction = (String)policyFilesRow.get("DOWNLOAD_FILE_LOCATION");
                final Long docID = (Long)policyFilesRow.get("DOC_ID");
                policyFilesJSON.put("DownloadLocation", (Object)downloadLoction);
                policyFilesJSON.put("DocID", (Object)docID);
                final String checksum = DocMgmtDataHandler.getInstance().getDocSHA256FileHash(docID);
                policyFilesJSON.put("SHA256Checksum", (Object)checksum);
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("OsUpdateAsFileDeployer")) {
                    policyFilesJSON.put("RestartRequired", (Object)Boolean.FALSE);
                }
            }
            final JSONObject policySettingsJSON = new JSONObject();
            final Row policySettingsRow = dataObject.getRow("DeploymentPolicySettings");
            if (policySettingsRow != null) {
                final Boolean downloadOverWiFi = (Boolean)policySettingsRow.get("DOWNLOAD_OVER_WIFI");
                final Boolean downloadInDeploymentWindow = (Boolean)policySettingsRow.get("DOWNLOAD_IN_DEP_WINDOW");
                policySettingsJSON.put("DownloadOverWiFi", (Object)downloadOverWiFi);
                policySettingsJSON.put("DownloadOnlyInDeploymentWindow", (Object)downloadInDeploymentWindow);
            }
            policyDataJSON.put("PolicyWindow", (Object)policyWindowDataJSON);
            policyDataJSON.put("PolicyNotification", (Object)policyNotifyDataJSON);
            policyDataJSON.put("PolicyFiles", (Object)policyFilesJSON);
            policyDataJSON.put("PolicySettings", (Object)policySettingsJSON);
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
    
    public AndroidCommandPayload createRemoveOsUpdateCommand(final String profileName, final Long collectionID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoveOsUpdatePolicy");
        final JSONObject idenJSON = new JSONObject();
        idenJSON.put("PayloadIdentifier", (Object)profileName);
        commandPayload.setRequestData(idenJSON);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        return commandPayload;
    }
    
    public AndroidCommandPayload createSecurityInfoCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SecurityInfo");
        return commandPayload;
    }
    
    public AndroidCommandPayload createCapabilitiesInfoCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("CapabilitiesInfo");
        return commandPayload;
    }
    
    public AndroidCommandPayload createRemoteDebugCommand(final Long resourceID) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("RemoteDebug");
        return commandPayload;
    }
    
    public AndroidCommandPayload createSyncAnnouncementCommand(final Long resourceID) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("SyncAnnouncement");
        return commandPayload;
    }
    
    public AndroidCommandPayload createUrlMigrationCommand(final Long resourceId) throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("MigrateUrl");
        final JSONObject requestJson = new JSONObject();
        final JSONObject responseJson = new JSONObject();
        requestJson.put("DevicePlatform", (Object)"android");
        requestJson.put("AgentType", (Object)"AndroidAgent");
        responseJson.put("Url", (Object)DiscoveryServiceHandler.getInstance().getLatestAgentCommDetails(requestJson));
        final JSONObject deviceDetails = ManagedDeviceHandler.getInstance().getEnrollmentDetailsForDevice(ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId));
        final JSONObject apiJSON = new JSONObject();
        apiJSON.put("ENROLLMENT_REQUEST_ID", deviceDetails.get("ENROLLMENT_REQUEST_ID"));
        apiJSON.put("decodeToken", (Object)Boolean.TRUE);
        final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiJSON);
        responseJson.put("Services", (Object)key.toClientJSON());
        commandPayload.setRequestData(responseJson);
        new AndroidServletMigrationUtil().migrationInitated(resourceId, 1);
        return commandPayload;
    }
    
    public AndroidCommandPayload createGSuiteAccountDetectCommand() throws JSONException {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("DetectUserGSuiteAccount");
        return commandPayload;
    }
    
    static {
        AndroidPayloadHandler.pHandler = null;
    }
}
