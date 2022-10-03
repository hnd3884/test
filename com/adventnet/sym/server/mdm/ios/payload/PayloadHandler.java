package com.adventnet.sym.server.mdm.ios.payload;

import java.util.ArrayList;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2SharedDeviceSettings;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import java.security.cert.X509Certificate;
import com.me.mdm.server.command.mac.querygenerator.filevault.MacFilevaultPersonalRecoveyKeyRotateGenerator;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.apps.handler.AppAutoDeploymentHandler;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.dd.plist.NSString;
import com.dd.plist.NSSet;
import com.adventnet.sym.server.mdm.ios.payload.transform.PayloadIdentifierConstants;
import java.util.Set;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.security.MacDeviceUserUnlockHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.command.mac.DeviceRestartOptions;
import com.me.mdm.api.command.mac.DeviceRestartOptionsAPI;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.dd.plist.NSData;
import java.util.HashMap;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import com.dd.plist.Base64;
import com.adventnet.sym.server.mdm.command.LockScreenMessageUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2PayloadHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.util.MDMSecurityLogger;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.dd.plist.NSDictionary;
import com.me.mdm.webclient.formbean.MDMAppleCustomProfileFormBean;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.me.mdm.server.profiles.AppleCustomProfileHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import java.util.logging.Logger;

public class PayloadHandler
{
    private static PayloadHandler pHandler;
    private Logger logger;
    public Logger mdmLogger;
    public static final List<Integer> PROFILEREMOVALCONFIGS;
    
    public PayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static PayloadHandler getInstance() {
        if (PayloadHandler.pHandler == null) {
            PayloadHandler.pHandler = new PayloadHandler();
        }
        return PayloadHandler.pHandler;
    }
    
    public String generateProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateIOSInstallCommandPayload(collectionID);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, toXMLPropertyList.getBytes());
                this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFilePath);
            }
            else {
                this.logger.log(Level.WARNING, "IOSCommandPayload has not been created.");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in generate profile", ex);
        }
        return commandUUID;
    }
    
    public String generateCustomCommand(final Long collectionId, final String profileFilePath) {
        String commandUUID = null;
        try {
            this.logger.log(Level.INFO, "Going to publish custom command for collectionid:{0}", new Object[] { collectionId });
            final DataObject dataObject = new AppleCustomProfileHandler().getCustomCommandDO(collectionId);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row customProfile = dataObject.getRow("CustomProfileDetails");
                String customProfilePath = (String)customProfile.get("CUSTOM_PROFILE_PATH");
                customProfilePath = customProfilePath.replace("/", File.separator);
                final String profileRepoPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String filePath = profileRepoPath + File.separator + customProfilePath;
                final NSDictionary rootDict = new MDMAppleCustomProfileFormBean().getDictionaryFromStream(filePath);
                final NSDictionary commandDict = (NSDictionary)rootDict.get((Object)"Command");
                final String requestType = commandDict.get((Object)"RequestType").toString();
                final IOSCommandPayload commandPayload = this.createCommandPayload(requestType);
                for (final String key : commandDict.keySet()) {
                    if (!key.equalsIgnoreCase("RequestType")) {
                        commandPayload.getCommandDict().put(key, commandDict.get((Object)key));
                    }
                }
                commandPayload.setCommandUUID("InstallProfile;Collection=" + collectionId.toString(), false);
                final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, toXMLPropertyList.getBytes());
                commandUUID = commandPayload.getCommandUUID();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in generate custom command", ex);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateIOSInstallCommandPayload(final Long collectionID) {
        IOSCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload("InstallProfile");
            final String payloadData = this.getProfilePayloadData(collectionID);
            MDMSecurityLogger.info(this.logger, "PayloadHandler", "generateProfile", "Profile payloadData for collection ID -- " + collectionID + " \n\n {0}", payloadData);
            commandPayload.setPayload(payloadData.getBytes());
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in generate profile", ex);
        }
        return commandPayload;
    }
    
    public String generateAccountConfigProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final MacAccountConfigPayload commandPayload = this.generateMacAccountConfigPayload(collectionID);
            commandUUID = commandPayload.getCommandUUID();
            final String payloadData = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, payloadData.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFilePath);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in creating Account configuration payload", e);
        }
        return commandUUID;
    }
    
    public MacAccountConfigPayload generateMacAccountConfigPayload(final Long collectionID) {
        MacAccountConfigPayload commandPayload = null;
        try {
            commandPayload = (MacAccountConfigPayload)this.getAppProfilePayloadData(collectionID);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in creating Account configuration payload", e);
        }
        return commandPayload;
    }
    
    public String generateInstallAppProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final AppsPayload commandPayload = this.generateInstallAppPayload(collectionID);
            commandUUID = commandPayload.getCommandUUID();
            final String payloadData = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, payloadData.getBytes());
            final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(profileFilePath);
            ApiFactoryProvider.getCacheAccessAPI().removeCache(cacheName, 2);
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFilePath);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return commandUUID;
    }
    
    public AppsPayload generateInstallAppPayload(final Long collectionID) {
        AppsPayload commandPayload = null;
        try {
            commandPayload = (AppsPayload)this.getAppProfilePayloadData(collectionID);
            final int agentType = AppsAutoDeployment.getInstance().getAgentIDFromCollectionID(collectionID);
            if (agentType == -1) {
                commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            }
            else {
                commandPayload.setCommandUUID("InstallAgentID=" + String.valueOf(agentType) + ";Collection=" + collectionID.toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in creating Account configuration payload", e);
        }
        return commandPayload;
    }
    
    public String generateProfile(final ConfigurationPayload payload, final String profileFilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
            final String payloadDictXml = payload.getPayloadDict().toXMLPropertyList();
            commandPayload.setPayload(payloadDictXml.getBytes());
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, toXMLPropertyList.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFilePath);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return commandUUID;
    }
    
    public String createManagedSettingCommandXML(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateManagedSettingCommandXML(collectionID);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                toXMLPropertyList = toXMLPropertyList.replaceAll("%profileId%", collectionID.toString());
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating managed settings", e);
        }
        return commandUUID;
    }
    
    public String createFileVaultPersonalRecoveryKeyRotateXML(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateManagedSettingCommandXML(collectionID);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                toXMLPropertyList = toXMLPropertyList.replaceAll("%profileId%", collectionID.toString());
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating managed settings", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateManagedSettingCommandXML(final Long collectionID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        final String commandUUID = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configIDList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2PayloadHandler payloadHander = new DO2PayloadHandler();
            final NSArray configArray = payloadHander.createManagedSettingsArrayItem(configIDList, dataObject);
            if (configArray.count() > 0) {
                commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
                commandPayload.setCommandUUID(commandPayload.commandUUID = "InstallManagedSettings;Collection=" + collectionID.toString(), false);
                return commandPayload;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating managed settings", e);
        }
        return null;
    }
    
    public String createScheduleOSUpdateCommandXML(final Long collectionID, final String profilePath) {
        String commandUUID = null;
        try {
            final DataObject dataObject = new OSUpdatePolicyHandler().getOSUpdatePolicy(collectionID);
            final IOSCommandPayload commandPayload = this.createScheduleOSUpdateCommand(collectionID);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "ScheduleOSUpdate;Collection=" + collectionID.toString(), false);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception   ", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload createScheduleOSUpdateCommand(final Long collectionID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("ScheduleOSUpdate");
        try {
            final DataObject dataObject = new OSUpdatePolicyHandler().getOSUpdatePolicy(collectionID);
            final String installAction = "Default";
            final NSArray updatesArray = new NSArray(1);
            final NSDictionary updateDict = new NSDictionary();
            updateDict.put("ProductKey", (Object)"%os_prodkey%");
            updateDict.put("InstallAction", (Object)installAction);
            updatesArray.setValue(0, (Object)updateDict);
            commandPayload.getCommandDict().put("Updates", (NSObject)updatesArray);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception  ", e);
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createCommandPayload(final String requestType) {
        final IOSCommandPayload commandPayload = new IOSCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    public String createRedemptionCodeCommand(final Long resourceID, final String appIdentifier, final String commandUUID) {
        String command = null;
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
        final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(appIdentifier, 1, customerID);
        final String strRedemptionCode = new AppleAppLicenseMgmtHandler().getRedemptionCodeForResource(resourceID, appGroupID);
        final IOSCommandPayload commandPayload = this.createCommandPayload("ApplyRedemptionCode");
        commandPayload.setCommandUUID(commandUUID, false);
        commandPayload.getCommandDict().put("Identifier", (Object)appIdentifier);
        if (strRedemptionCode != null) {
            commandPayload.getCommandDict().put("RedemptionCode", (Object)strRedemptionCode);
        }
        else {
            commandPayload.getCommandDict().put("RedemptionCode", (Object)"VPP_DUMMY_CODE");
        }
        command = commandPayload.toString();
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ApplyRedemptionCode");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ApplyRedemptionCode", command });
        return command;
    }
    
    public IOSCommandPayload createDeviceLockCommand(final Long resourceID, final DeviceCommand deviceCommand) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("DeviceLock");
        String lockMessage = "";
        String phoneNumber = "";
        String unlockpin = "";
        if (MDMStringUtils.isEmpty(deviceCommand.commandStr)) {
            final HashMap hsLockScreenMessage = LockScreenMessageUtil.getInstance().getLockScreenMessage(resourceID);
            if (hsLockScreenMessage != null) {
                lockMessage = String.valueOf(hsLockScreenMessage.get("LOCK_MESSAGE"));
                phoneNumber = String.valueOf(hsLockScreenMessage.get("PHONE_NUMBER"));
                unlockpin = ((hsLockScreenMessage.get("UNLOCK_PIN") == null) ? null : hsLockScreenMessage.get("UNLOCK_PIN").toString());
            }
            else {
                try {
                    final byte[] decodedData = Base64.decode(deviceCommand.commandStr);
                    final JSONObject tempJSON = new JSONObject(new String(decodedData));
                    lockMessage = String.valueOf(tempJSON.get("lock_message"));
                    phoneNumber = String.valueOf(tempJSON.get("phone_number"));
                    unlockpin = String.valueOf(tempJSON.get("unlock_pin"));
                }
                catch (final JSONException | IOException e) {
                    this.logger.log(Level.SEVERE, "exception in createDeviceLockCommand() ", e);
                }
            }
            if (!MDMStringUtils.isEmpty(lockMessage)) {
                commandPayload.getCommandDict().put("Message", (Object)lockMessage);
            }
            if (!MDMStringUtils.isEmpty(phoneNumber)) {
                commandPayload.getCommandDict().put("PhoneNumber", (Object)phoneNumber);
            }
            if (!MDMStringUtils.isEmpty(unlockpin)) {
                commandPayload.getCommandDict().put("PIN", (Object)unlockpin);
            }
        }
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceLock");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceLock", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createClearPasscodeCommand(final String unlockToken) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("ClearPasscode");
        try {
            final NSData nsdata = new NSData(unlockToken);
            commandPayload.getCommandDict().put("UnlockToken", (NSObject)nsdata);
            this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ClearPasscode");
            this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ClearPasscode", commandPayload.toString() });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in createClearPasscodeCommand", e);
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createEraseDeviceCommand(final Long resourceId, final DeviceCommand deviceCommand) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("EraseDevice");
        String wipeLockPin = "";
        if (MDMStringUtils.isEmpty(deviceCommand.commandStr)) {
            final JSONObject remoteWipeObject = new RemoteWipeHandler().getWipeOptionData(resourceId);
            wipeLockPin = remoteWipeObject.optString("WIPE_LOCK_PIN");
        }
        else {
            try {
                final byte[] decodedData = Base64.decode(deviceCommand.commandStr);
                final JSONObject tempJSON = new JSONObject(new String(decodedData));
                wipeLockPin = String.valueOf(tempJSON.get("wipe_lock_pin"));
            }
            catch (final JSONException | IOException e) {
                this.logger.log(Level.SEVERE, "exception in createEraseDeviceCommand() ", e);
            }
        }
        if (!MDMStringUtils.isEmpty(wipeLockPin)) {
            commandPayload.getCommandDict().put("PIN", (Object)wipeLockPin);
        }
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "EraseDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "EraseDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createShutDownDeviceCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("ShutDownDevice");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ShutDownDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ShutDownDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createRestartDeviceCommand(final Long resourceId) throws DataAccessException {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RestartDevice");
        final DeviceRestartOptions deviceRestartOptions = DeviceRestartOptionsAPI.getRestartOptions(resourceId);
        if (deviceRestartOptions != null) {
            this.logger.log(Level.INFO, "The Restart option selected for resource: {0} is {1}", new Object[] { resourceId, deviceRestartOptions.isNotifyUser });
            commandPayload.getCommandDict().put("NotifyUser", (Object)deviceRestartOptions.isNotifyUser);
        }
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "RestartDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RestartDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createUnlockDeviceAccountCommand(final Long resourceID) throws Exception {
        final String userName = new MacDeviceUserUnlockHandler().getUserNameForResourceID(resourceID);
        final IOSCommandPayload commandPayload = this.createCommandPayload("UnlockUserAccount");
        commandPayload.getCommandDict().put("UserName", (Object)userName);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "UnlockUserAccount");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "UnlockUserAccount", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createPlayLostModeSoundDeviceCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("PlayLostModeSound");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "PlayLostModeSound");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "PlayLostModeSound", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createInstallProfileCommand(final String payloadData) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        commandPayload.setPayload(payloadData.getBytes());
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "InstallProfile");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "InstallProfile", commandPayload.toString() });
        return commandPayload;
    }
    
    public String generateRemoveProfile(final String profileName, final Long collectionID, final String profileFileName) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateIOSRemoveCommonPayload(profileName, collectionID);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
                this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in generate remove profile", exp);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateIOSRemoveCommonPayload(final String profileName, final Long collectionID) {
        IOSCommandPayload commandPayload = null;
        try {
            final List<Integer> configIds = MDMConfigUtil.getConfigIds(collectionID);
            if (ProfileUtil.containsConfigIDs(PayloadHandler.PROFILEREMOVALCONFIGS, configIds)) {
                commandPayload = this.createCommandPayload("InstallProfile");
                final String payloadData = this.getRemoveProfilePayloadData(collectionID);
                commandPayload.setPayload(payloadData.getBytes());
                final String commandUUID = "RemoveProfile;Collection=" + collectionID.toString();
                commandPayload.setCommandUUID(commandUUID, false);
            }
            else {
                commandPayload = this.createRemoveProfileCommand(profileName, collectionID);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in generate remove profile", exp);
        }
        return commandPayload;
    }
    
    public String generateRemoveApplication(final String appIdentifier, final Long collectionID, final String profileFileName) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createRemoveApplicationCommand(appIdentifier, collectionID);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in generate remove application", exp);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload createRemoveProfileCommand(final String profileName, final Long collectionID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        commandPayload.getCommandDict().put("Identifier", (Object)profileName);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveProfile");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveProfile", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createRemoveApplicationCommand(final String appIdentifier, final Long collectionID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveApplication");
        commandPayload.getCommandDict().put("Identifier", (Object)appIdentifier);
        commandPayload.setCommandUUID("Collection=" + collectionID.toString());
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveApplication");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveApplication", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createSecurityInfoCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SecurityInfo");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "SecurityInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SecurityInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    private NSArray getInternallyManagedAgentList() {
        final Set<String> internallyManagedApps = new HashSet<String>() {
            {
                this.add("dcagentservice");
                this.add("com.manageengine.mdm.mac");
            }
        };
        final NSArray nsArray = new NSArray(internallyManagedApps.size());
        final Iterator<String> iterator = internallyManagedApps.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            final String identifier = iterator.next();
            nsArray.setValue(index++, (Object)identifier);
        }
        return nsArray;
    }
    
    public IOSCommandPayload createInstallAppListCommand(final Boolean isManagedAppOnly) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstalledApplicationList");
        if (isManagedAppOnly) {
            commandPayload.getCommandDict().put("ManagedAppsOnly", (Object)Boolean.TRUE);
        }
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "InstalledApplicationList");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "InstalledApplicationList", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload fetchAgentDetailsCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstalledApplicationList");
        commandPayload.setCommandUUID("FetchAppleAgentDetails");
        commandPayload.getCommandDict().put("Identifiers", (NSObject)this.getInternallyManagedAgentList());
        this.logger.log(Level.INFO, "FetchAppleAgentDetails Command has been created successfully");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "FetchAppleAgentDetails", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createManagedAppListCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("ManagedApplicationList");
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "ManagedApplicationList");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ManagedApplicationList", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createCertificateListCommand(final boolean managedOnly) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("CertificateList");
        commandPayload.getCommandDict().put("ManagedOnly", (Object)managedOnly);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "CertificateList");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "CertificateList", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createRestrictionsCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Restrictions");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "Restrictions");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "Restrictions", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createRemoveDeviceCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        commandPayload.getCommandDict().put("Identifier", (Object)PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_IDENTIFIER);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "RemoveDevice");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createDeviceInformationCommand(final Boolean fetchDeviceName, final Boolean fetchPhonenum, final Boolean fetchMacAddr, final Boolean isEnrollmentCommand) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("DeviceInformation");
        final NSSet queriesData = new NSSet();
        if (fetchDeviceName) {
            queriesData.addObject((NSObject)new NSString("DeviceName"));
        }
        queriesData.addObject((NSObject)new NSString("OSVersion"));
        queriesData.addObject((NSObject)new NSString("BuildVersion"));
        queriesData.addObject((NSObject)new NSString("ModelName"));
        queriesData.addObject((NSObject)new NSString("Model"));
        queriesData.addObject((NSObject)new NSString("ProductName"));
        queriesData.addObject((NSObject)new NSString("SerialNumber"));
        queriesData.addObject((NSObject)new NSString("DeviceCapacity"));
        queriesData.addObject((NSObject)new NSString("AvailableDeviceCapacity"));
        queriesData.addObject((NSObject)new NSString("BatteryLevel"));
        queriesData.addObject((NSObject)new NSString("CellularTechnology"));
        queriesData.addObject((NSObject)new NSString("IMEI"));
        queriesData.addObject((NSObject)new NSString("MEID"));
        queriesData.addObject((NSObject)new NSString("ModemFirmwareVersion"));
        queriesData.addObject((NSObject)new NSString("ICCID"));
        if (fetchMacAddr) {
            queriesData.addObject((NSObject)new NSString("BluetoothMAC"));
            queriesData.addObject((NSObject)new NSString("WiFiMAC"));
            queriesData.addObject((NSObject)new NSString("EthernetMAC"));
            queriesData.addObject((NSObject)new NSString("EthernetMACs"));
        }
        queriesData.addObject((NSObject)new NSString("CurrentCarrierNetwork"));
        queriesData.addObject((NSObject)new NSString("SIMCarrierNetwork"));
        queriesData.addObject((NSObject)new NSString("SubscriberCarrier-Network"));
        queriesData.addObject((NSObject)new NSString("CarrierSettingsVersion"));
        if (fetchPhonenum) {
            queriesData.addObject((NSObject)new NSString("PhoneNumber"));
        }
        queriesData.addObject((NSObject)new NSString("VoiceRoamingEnabled"));
        queriesData.addObject((NSObject)new NSString("DataRoamingEnabled"));
        queriesData.addObject((NSObject)new NSString("IsRoaming"));
        queriesData.addObject((NSObject)new NSString("SubscriberMCC"));
        queriesData.addObject((NSObject)new NSString("SubscriberMNC"));
        queriesData.addObject((NSObject)new NSString("CurrentMCC"));
        queriesData.addObject((NSObject)new NSString("CurrentMNC"));
        queriesData.addObject((NSObject)new NSString("UDID"));
        queriesData.addObject((NSObject)new NSString("IsSupervised"));
        queriesData.addObject((NSObject)new NSString("AccessibilitySettings"));
        queriesData.addObject((NSObject)new NSString("IsMultiUser"));
        queriesData.addObject((NSObject)new NSString("ActiveManagedUsers"));
        queriesData.addObject((NSObject)new NSString("AutoSetupAdminAccounts"));
        queriesData.addObject((NSObject)new NSString("EstimatedResidentUsers"));
        queriesData.addObject((NSObject)new NSString("MaximumResidentUsers"));
        queriesData.addObject((NSObject)new NSString("ResidentUsers"));
        queriesData.addObject((NSObject)new NSString("QuotaSize"));
        queriesData.addObject((NSObject)new NSString("IsDeviceLocatorServiceEnabled"));
        queriesData.addObject((NSObject)new NSString("IsActivationLockEnabled"));
        queriesData.addObject((NSObject)new NSString("IsDoNotDisturbInEffect"));
        queriesData.addObject((NSObject)new NSString("iTunesStoreAccountIsActive"));
        queriesData.addObject((NSObject)new NSString("EASDeviceIdentifier"));
        queriesData.addObject((NSObject)new NSString("PersonalHotspotEnabled"));
        queriesData.addObject((NSObject)new NSString("LastCloudBackupDate"));
        queriesData.addObject((NSObject)new NSString("IsCloudBackupEnabled"));
        queriesData.addObject((NSObject)new NSString("IsMDMLostModeEnabled"));
        queriesData.addObject((NSObject)new NSString("AwaitingConfiguration"));
        queriesData.addObject((NSObject)new NSString("MDMOptions"));
        queriesData.addObject((NSObject)new NSString("IsAppleSilicon"));
        queriesData.addObject((NSObject)new NSString("SupportsiOSAppInstalls"));
        queriesData.addObject((NSObject)new NSString("IsActivationLockSupported"));
        if (!isEnrollmentCommand) {
            if (fetchPhonenum) {
                queriesData.addObject((NSObject)new NSString("ServiceSubscriptions"));
            }
            queriesData.addObject((NSObject)new NSString("Languages"));
            queriesData.addObject((NSObject)new NSString("Locales"));
            queriesData.addObject((NSObject)new NSString("DeviceID"));
            queriesData.addObject((NSObject)new NSString("OrganizationInfo"));
            queriesData.addObject((NSObject)new NSString("iTunesStoreAccountHash"));
            queriesData.addObject((NSObject)new NSString("SIMMCC"));
            queriesData.addObject((NSObject)new NSString("SIMMNC"));
            queriesData.addObject((NSObject)new NSString("OSUpdateSettings"));
            queriesData.addObject((NSObject)new NSString("LocalHostName"));
            queriesData.addObject((NSObject)new NSString("HostName"));
            queriesData.addObject((NSObject)new NSString("CatalogURL"));
            queriesData.addObject((NSObject)new NSString("IsDefaultCatalog"));
            queriesData.addObject((NSObject)new NSString("PreviousScanDate"));
            queriesData.addObject((NSObject)new NSString("PreviousScanResult"));
            queriesData.addObject((NSObject)new NSString("PerformPeriodicCheck"));
            queriesData.addObject((NSObject)new NSString("AutomaticCheckEnabled"));
            queriesData.addObject((NSObject)new NSString("BackgroundDownloadEnabled"));
            queriesData.addObject((NSObject)new NSString("AutomaticAppInstallationEnabled"));
            queriesData.addObject((NSObject)new NSString("AutomaticOSInstallationEnabled"));
            queriesData.addObject((NSObject)new NSString("AutomaticSecurityUpdatesEnabled"));
            queriesData.addObject((NSObject)new NSString("IsMultiUser"));
            queriesData.addObject((NSObject)new NSString("MaximumResidentUsers"));
            queriesData.addObject((NSObject)new NSString("PushToken"));
            queriesData.addObject((NSObject)new NSString("DiagnosticSubmissionEnabled"));
            queriesData.addObject((NSObject)new NSString("AppAnalyticsEnabled"));
            queriesData.addObject((NSObject)new NSString("IsNetworkTethered"));
        }
        commandPayload.getCommandDict().put("Queries", (NSObject)queriesData);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceInformation");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceInformation", commandPayload.toString() });
        return commandPayload;
    }
    
    private String getProfilePayloadData(final Long collectionID) {
        String toXMLPropertyList = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDataItemDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2PayloadHandler handler = new DO2PayloadHandler();
            final ConfigurationPayload cfgPayload = handler.createPayload(dataObject, configDataItemDOList);
            toXMLPropertyList = cfgPayload.getPayloadDict().toXMLPropertyList();
            toXMLPropertyList = toXMLPropertyList.replaceAll("%profileId%", collectionID.toString());
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception in profile payload data", (Throwable)ex);
        }
        return toXMLPropertyList;
    }
    
    private String getRemoveProfilePayloadData(final Long collectionId) {
        String toXMLPropertyList = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionId);
            final List configDOList = MDMConfigUtil.getConfigurations(collectionId);
            final DO2PayloadHandler handler = new DO2PayloadHandler();
            final ConfigurationPayload cfgPayload = handler.createRemovePayload(dataObject, configDOList);
            toXMLPropertyList = cfgPayload.getPayloadDict().toXMLPropertyList();
        }
        catch (final SyMException e) {
            this.logger.log(Level.SEVERE, "Exception in remove profile data", (Throwable)e);
        }
        return toXMLPropertyList;
    }
    
    private IOSPayload getAppProfilePayloadData(final Long collectionID) {
        IOSPayload appPayload = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2PayloadHandler handler = new DO2PayloadHandler();
            appPayload = handler.createAppPayload(dataObject, configDOList);
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppProfilePayloadData", (Throwable)ex);
        }
        return appPayload;
    }
    
    public String readProfileFromFile(final String profilePath) {
        final StringBuilder toXMLPropertyList = new StringBuilder();
        try {
            final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(profilePath);
            if (is != null) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    toXMLPropertyList.append(line + "\n");
                }
                is.close();
            }
            else {
                this.logger.log(Level.INFO, "Input stream is NULL while read profile from file");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in read profile from file", exp);
        }
        return toXMLPropertyList.toString();
    }
    
    public IOSCommandPayload createInviteToProgramCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InviteToProgram");
        try {
            commandPayload.getCommandDict().put("ProgramID", (Object)"com.apple.cloudvpp");
            commandPayload.getCommandDict().put("InvitationURL", (Object)"%invitationUrl%");
            this.logger.log(Level.INFO, "{0} Command has been created successfully", "InviteToProgram");
            this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "InviteToProgram", commandPayload.toString() });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createApplicationConfigurationCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        try {
            final NSDictionary commandDict = new NSDictionary();
            commandDict.put("Item", (Object)"ApplicationConfiguration");
            commandDict.put("Identifier", (Object)"com.manageengine.mdm.iosagent");
            commandDict.put("Configuration", (NSObject)MDMiOSEntrollmentUtil.getMDMDefaultAppConfiguration());
            final NSArray configArray = new NSArray(1);
            configArray.setValue(0, (Object)commandDict);
            commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
            commandPayload.setCommandUUID("ApplicationConfiguration", Boolean.FALSE);
            this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ApplicationConfiguration");
            this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ApplicationConfiguration", commandPayload.toString() });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createDeviceNameCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        final NSDictionary commandDict = new NSDictionary();
        commandDict.put("Item", (Object)"DeviceName");
        commandDict.put("DeviceName", (Object)"%device_name%");
        final NSArray configArray = new NSArray(1);
        configArray.setValue(0, (Object)commandDict);
        commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
        commandPayload.setCommandUUID("DeviceName", Boolean.FALSE);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "DeviceName");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DeviceName", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createGetLocationCommand() {
        return this.createMDMManagedAppFeedbackCommand("GetLocation");
    }
    
    public IOSCommandPayload createMDMManagedAppFeedbackCommand(final String commandUUID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        commandPayload.setRequestType("ManagedApplicationFeedback");
        final NSArray configArray = new NSArray(1);
        configArray.setValue(0, (Object)new NSString("com.manageengine.mdm.iosagent"));
        commandPayload.getCommandDict().put("Identifiers", (NSObject)configArray);
        commandPayload.getCommandDict().put("DeleteFeedback", (Object)Boolean.FALSE);
        commandPayload.setCommandUUID(commandUUID, Boolean.FALSE);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ManagedApplicationFeedback");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ApplicationConfiguration", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createAvailableOSUpdatesCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("AvailableOSUpdates");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "AvailableOSUpdates");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "AvailableOSUpdates", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createOSUpdateStatusCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("OSUpdateStatus");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "OSUpdateStatus");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "OSUpdateStatus", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createEnableLostModeCommand(final Long resourceID, final DeviceCommand deviceCommand) {
        final JSONObject json = new JSONObject();
        if (MDMStringUtils.isEmpty(deviceCommand.commandStr)) {
            final HashMap hsLockScreenMessage = LockScreenMessageUtil.getInstance().getLockScreenMessage(resourceID);
            try {
                if (hsLockScreenMessage != null) {
                    final Object msg = hsLockScreenMessage.get("LOCK_MESSAGE");
                    final Object phone = hsLockScreenMessage.get("PHONE_NUMBER");
                    if (msg != null) {
                        json.put("LostModeMessage", msg);
                    }
                    if (phone != null) {
                        json.put("LostModePhone", phone);
                    }
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while createEnableLostModeCommand() ", e);
            }
        }
        else {
            try {
                final byte[] decodedData = Base64.decode(deviceCommand.commandStr);
                final JSONObject tempJSON = new JSONObject(new String(decodedData));
                json.put("LostModeMessage", (Object)String.valueOf(tempJSON.get("lock_message")));
                json.put("LostModePhone", (Object)String.valueOf(tempJSON.get("phone_number")));
            }
            catch (final JSONException | IOException e2) {
                this.logger.log(Level.SEVERE, "Exception while createEnableLostModeCommand() ", e2);
            }
        }
        return this.createEnableLostModeCommand(json);
    }
    
    public IOSCommandPayload createEnableLostModeCommand(final JSONObject commandDetailsJson) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("EnableLostMode");
        String message = commandDetailsJson.optString("LostModeMessage", (String)null);
        final String phone = commandDetailsJson.optString("LostModePhone", (String)null);
        final String footnote = commandDetailsJson.optString("LostModeFootnote", (String)null);
        if (phone == null && message == null) {
            message = "This phone is lost";
        }
        if (phone != null) {
            commandPayload.getCommandDict().put("PhoneNumber", (Object)phone);
        }
        if (message != null) {
            commandPayload.getCommandDict().put("Message", (Object)message);
        }
        if (footnote != null) {
            commandPayload.getCommandDict().put("Footnote", (Object)footnote);
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createDisableLostModeCommand() {
        return this.createCommandPayload("DisableLostMode");
    }
    
    public IOSCommandPayload createLostModeDeviceLocationCommand() {
        return this.createCommandPayload("DeviceLocation");
    }
    
    public JSONArray generateAppProfile(final Long collectionID, final String mdmProfileRelativeDirPath, final String mdmProfileDir, final Boolean hasAppConfiguration) throws Exception {
        final JSONArray jsonArray = new JSONArray();
        final int agentID = AppsAutoDeployment.getInstance().getAgentIDFromCollectionID(collectionID);
        final Boolean isNativeAgent = agentID != -1;
        final String profileFileName = mdmProfileDir + File.separator + "install_profile.xml";
        final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.xml";
        final String commandUUID = this.generateInstallAppProfile(collectionID, profileFileName);
        final JSONObject installJSON = new JSONObject();
        installJSON.put("commandUUID", (Object)commandUUID);
        installJSON.put("commandType", (Object)"InstallApplication");
        installJSON.put("commandFilePath", (Object)(mdmProfileRelativeDirPath + File.separator + "install_profile.xml"));
        installJSON.put("dynamicVariable", (Object)Boolean.TRUE);
        jsonArray.put((Object)installJSON);
        final Long appid = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
        final Long configDataItemId = (Long)DBUtil.getValueFromDB("InstallAppPolicy", "APP_ID", (Object)appid, "CONFIG_DATA_ITEM_ID");
        final int supportedDevices = MDMUtil.getInstance().getSupportedDevice(appid);
        final HashMap appDetails = MDMUtil.getInstance().getAppDetails(appid);
        String appIdentifier = appDetails.get("IDENTIFIER");
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
            appIdentifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(appIdentifier);
        }
        final String removeCommandUUID = this.generateRemoveApplication(appIdentifier, collectionID, removeProfileFileName);
        final JSONObject removeJSON = new JSONObject();
        removeJSON.put("commandUUID", (Object)removeCommandUUID);
        if (supportedDevices == 16 && isNativeAgent) {
            removeJSON.put("commandType", (Object)"RemoveProfile");
        }
        else {
            removeJSON.put("commandType", (Object)"RemoveApplication");
        }
        removeJSON.put("commandFilePath", (Object)(mdmProfileRelativeDirPath + File.separator + "remove_profile.xml"));
        removeJSON.put("dynamicVariable", (Object)Boolean.FALSE);
        jsonArray.put((Object)removeJSON);
        if (hasAppConfiguration) {
            final String appConfigFileName = mdmProfileDir + File.separator + "app_configuration.xml";
            final String appConfigUUID = this.createAppConfigCommand(collectionID, appIdentifier, appid, configDataItemId, appConfigFileName);
            final JSONObject configJSON = new JSONObject();
            configJSON.put("commandUUID", (Object)appConfigUUID);
            configJSON.put("commandType", (Object)"ApplicationConfiguration");
            configJSON.put("commandFilePath", (Object)(mdmProfileRelativeDirPath + File.separator + "app_configuration.xml"));
            configJSON.put("dynamicVariable", (Object)Boolean.TRUE);
            if (isNativeAgent) {
                configJSON.put("priority", (Object)"100");
            }
            jsonArray.put((Object)configJSON);
        }
        return jsonArray;
    }
    
    public String createAppConfigCommand(final Long collectionID, final String appIdentifier, final Long appId, final Long configDataItemId, final String profileFileName) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateAppConfigCommand(collectionID, appIdentifier, configDataItemId, appId);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "ApplicationConfiguration");
                this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "ApplicationConfiguration", commandPayload.toString() });
                final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
                final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(profileFileName);
                ApiFactoryProvider.getCacheAccessAPI().removeCache(cacheName, 2);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in createAppConfigCommand", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateAppConfigCommand(final Long collectionID, final String appIdentifier, final Long configDataItemId, final Long appId) {
        IOSCommandPayload commandPayload = null;
        try {
            String commandUUID = null;
            final int supportedDevice = MDMUtil.getInstance().getSupportedDevice(appId);
            commandPayload = this.createCommandPayload("Settings");
            final NSDictionary commandDict = new NSDictionary();
            commandUUID = "ApplicationConfiguration;Collection=" + collectionID.toString();
            if (supportedDevice == 16) {
                final int agentID = AppsAutoDeployment.getAgentIDFromIdentifier(appIdentifier);
                final Boolean isNativeAgent = agentID != -1;
                if (isNativeAgent) {
                    commandPayload = this.createCommandPayload("InstallProfile");
                    final AppAutoDeploymentHandler handler = AppsAutoDeployment.getInstance().getAgentHandler(agentID);
                    final JSONObject agentData = handler.getAgentAppData(-1L);
                    final String configuration = String.valueOf(agentData.get("AGENT_CONFIGURATION"));
                    commandPayload.setPayload(configuration.getBytes());
                    commandPayload.setCommandUUID(commandUUID, Boolean.FALSE);
                }
            }
            else {
                commandDict.put("Item", (Object)"ApplicationConfiguration");
                commandDict.put("Identifier", (Object)appIdentifier);
                final NSDictionary configurationDict = new AppConfigDataHandler().getIosAppConfig(configDataItemId);
                commandDict.put("Configuration", (NSObject)configurationDict);
                final NSArray configArray = new NSArray(1);
                configArray.setValue(0, (Object)commandDict);
                commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
                commandPayload.setCommandUUID(commandUUID, Boolean.FALSE);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return commandPayload;
    }
    
    public String createInstallIosMultiAppConfigCommand(final Long collectionID, final String profileFileName) throws Exception {
        final IOSCommandPayload commandPayload = this.generateInstallIosMultiAppConfigCommand(collectionID);
        final String commandUUID = commandPayload.getCommandUUID();
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "InstallApplicationConfiguration");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "InstallApplicationConfiguration", commandPayload.toString() });
        final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
        ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
        return commandUUID;
    }
    
    public IOSCommandPayload generateInstallIosMultiAppConfigCommand(final Long collectionId) throws Exception {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        Integer index = 0;
        final String commandUUID = "InstallApplicationConfiguration;Collection=" + collectionId.toString();
        final DataObject dataObject = AppConfigPolicyDBHandler.getInstance().getAppConfigProfileDetails(collectionId);
        if (!dataObject.isEmpty()) {
            final NSArray configArray = new NSArray(dataObject.size("ManagedAppConfigurationPolicy"));
            final Iterator<Row> iterator = dataObject.getRows("ManagedAppConfigurationPolicy");
            while (iterator.hasNext()) {
                final Row policyRow = iterator.next();
                final Row appDetailsRow = dataObject.getRow("MdAppDetails", new Criteria(new Column("MdAppDetails", "APP_ID"), policyRow.get("APP_ID"), 0));
                final Row appConfigDataRow = dataObject.getRow("ManagedAppConfigurationData", new Criteria(new Column("AppConfigPolicy", "CONFIG_DATA_ITEM_ID"), policyRow.get("CONFIG_DATA_ITEM_ID"), 0), new Join("AppConfigPolicy", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 2));
                final String appIdentifier = (String)appDetailsRow.get("IDENTIFIER");
                String appConfigFilePath = (String)appConfigDataRow.get("APP_CONFIG_PATH");
                appConfigFilePath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath() + File.separator + appConfigFilePath;
                final String configData = new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(appConfigFilePath));
                final NSDictionary commandDict = new NSDictionary();
                commandDict.put("Item", (Object)"ApplicationConfiguration");
                commandDict.put("Identifier", (Object)appIdentifier);
                final NSDictionary configurationDict = new AppConfigDataHandler().parseJsonToIosAppConfig(new JSONArray(configData));
                commandDict.put("Configuration", (NSObject)configurationDict);
                final NSArray nsArray = configArray;
                final Integer n = index;
                ++index;
                nsArray.setValue((int)n, (Object)commandDict);
            }
            commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
            commandPayload.setCommandUUID(commandUUID, Boolean.FALSE);
        }
        return commandPayload;
    }
    
    public String createRemoveIosMultiAppConfigCommand(final Long collectionID, final String profileFileName) throws Exception {
        final IOSCommandPayload commandPayload = this.generateRemoveIosMultiAppConfigCommand(collectionID);
        final String commandUUID = commandPayload.getCommandUUID();
        this.logger.log(Level.INFO, "{0} Command has been created successfully", "RemoveApplicationConfiguration");
        this.logger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RemoveApplicationConfiguration", commandPayload.toString() });
        final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
        ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, toXMLPropertyList.getBytes());
        return commandUUID;
    }
    
    public IOSCommandPayload generateRemoveIosMultiAppConfigCommand(final Long collectionID) throws Exception {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        Integer index = 0;
        final String commandUUID = "RemoveApplicationConfiguration;Collection=" + collectionID.toString();
        final DataObject dataObject = AppConfigPolicyDBHandler.getInstance().getAppConfigProfileDetails(collectionID);
        if (!dataObject.isEmpty()) {
            final NSArray configArray = new NSArray(dataObject.size("ManagedAppConfigurationPolicy"));
            final Iterator<Row> iterator = dataObject.getRows("MdAppDetails");
            while (iterator.hasNext()) {
                final Row appDetailsRow = iterator.next();
                final String appIdentifier = (String)appDetailsRow.get("IDENTIFIER");
                final NSDictionary commandDict = new NSDictionary();
                commandDict.put("Item", (Object)"ApplicationConfiguration");
                commandDict.put("Identifier", (Object)appIdentifier);
                final NSArray nsArray = configArray;
                final Integer n = index;
                ++index;
                nsArray.setValue((int)n, (Object)commandDict);
            }
            commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
            commandPayload.setCommandUUID(commandUUID, Boolean.FALSE);
        }
        return commandPayload;
    }
    
    public String createManagedAppListFromIdentifier(final Long collectionID, final List bundleIdentifier, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createManagedAppListCommand();
            final NSArray bundlearray = new NSArray(1);
            for (int i = 0; i < bundleIdentifier.size(); ++i) {
                bundlearray.setValue(i, (Object)bundleIdentifier.get(i));
            }
            commandPayload.getCommandDict().put("Identifiers", (NSObject)bundlearray);
            commandPayload.setCommandUUID("Collection=" + collectionID.toString());
            commandUUID = commandPayload.getCommandUUID();
            this.logger.log(Level.SEVERE, "{0}For BundleID Command has been created successfully", "ManagedApplicationList");
            final String toXMLProppertyList = commandPayload.getPayloadDict().toXMLPropertyList();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLProppertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Error while creating ManagedApplicationList for BundleIdentifier");
        }
        return commandUUID;
    }
    
    public String generateCustomKioskInstallProfile(final Long collectionID, final String profileFilePath, final Long customerId) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateCustomKioskPayload(collectionID, customerId);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, toXMLPropertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generate custom kiosk profile", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateCustomKioskPayload(final Long collectionID, final Long customerId) {
        IOSCommandPayload commandPayload = null;
        try {
            commandPayload = this.createCommandPayload("InstallProfile");
            final DO2PayloadHandler payload = new DO2PayloadHandler();
            final ConfigurationPayload configuration = payload.createCustomKioskPayload(collectionID, "com.mdm.kiosk_install_profile", customerId);
            final String payloadData = configuration.getPayloadDict().toXMLPropertyList();
            commandPayload.setPayload(payloadData.getBytes());
            commandPayload.setCommandUUID(commandPayload.commandUUID = "KioskDefaultRestriction;Collection=" + collectionID.toString(), false);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generate custom kiosk profile", e);
        }
        return commandPayload;
    }
    
    public String generateCustomKioskRemoveProfile(final Long collectionId, final String profileFilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateCustomKioskRemoveProfile(collectionId);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.getPayloadDict().toXMLPropertyList();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, toXMLPropertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in remove custom kiosk profile", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateCustomKioskRemoveProfile(final Long collectionId) {
        IOSCommandPayload commandPayload = null;
        try {
            commandPayload = this.createRemoveProfileCommand("com.mdm.kiosk_install_profile", collectionId);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveKioskDefaultRestriction;Collection=" + collectionId.toString(), false);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in remove custom kiosk profile", e);
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createDefaultMDMKiosk() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        final DO2PayloadHandler payload = new DO2PayloadHandler();
        final ConfigurationPayload configuration = payload.createDefaultKioskPayload();
        final String payloadData = configuration.getPayloadDict().toXMLPropertyList();
        commandPayload.setPayload(payloadData.getBytes());
        return commandPayload;
    }
    
    public IOSCommandPayload createRemoveDefaultMDMKiosk() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        commandPayload.getCommandDict().put("Identifier", (Object)"com.mdm.kiosk_default_mdm_app");
        return commandPayload;
    }
    
    public String getDefaultAppCatalogProfile(final String webclipsUrl, final Long customerID, final String commandUUID) {
        String strQuery = null;
        try {
            String iconFileName = MDMUtil.getAppCatalogWebClipsImagePath();
            String webclipLabel = "App Catalog";
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("RebrandIOSAppCatalog")) {
                this.logger.log(Level.INFO, "Feature Param enabled to rebrand iOS App catalog with Android ");
                final MDMAgentSettingsHandler settingsHandler = new MDMAgentSettingsHandler();
                final JSONObject rebrandingSettings = settingsHandler.getAgentRebrandingSetting(customerID);
                if (rebrandingSettings != null && rebrandingSettings.has("MDM_APP_ICON_FILE_NAME") && rebrandingSettings.has("REBRANDING_PATH")) {
                    final String fileLocation = settingsHandler.getRebrandingImageFolderPath(customerID) + File.separator + rebrandingSettings.getString("MDM_APP_ICON_FILE_NAME");
                    if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileLocation)) {
                        iconFileName = fileLocation;
                    }
                }
                if (rebrandingSettings != null && rebrandingSettings.has("MDM_APP_NAME") && !MDMStringUtils.isEmpty(rebrandingSettings.getString("MDM_APP_NAME"))) {
                    webclipLabel = rebrandingSettings.getString("MDM_APP_NAME");
                }
                this.logger.log(Level.INFO, "Rebranded Path:{0}", iconFileName);
                this.logger.log(Level.INFO, "Rebranded Webclip Label:{0}", webclipLabel);
            }
            final String collectionName = "DCAppCatalog";
            final String payloadIdentifier = "com.mdm." + collectionName;
            final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", payloadIdentifier, collectionName);
            final WebClipsPayload webclipsPayload = new WebClipsPayload(1, "MDM", "com.mdm.mobiledevice.webclips", "WebClips Profile Configuration");
            webclipsPayload.setLabel(webclipLabel);
            webclipsPayload.setURL(webclipsUrl);
            webclipsPayload.setIcon(iconFileName);
            webclipsPayload.setIsRemovable(false);
            webclipsPayload.setIsPrecomposed(true);
            webclipsPayload.setIgnoreManifestScope(true);
            webclipsPayload.setIsFullScreen(true);
            final NSArray nsarray = new NSArray(1);
            nsarray.setValue(0, (Object)webclipsPayload.getPayloadDict());
            cfgPayload.setPayloadRemovalDisallowed(2);
            cfgPayload.setPayloadContent(nsarray);
            final String payloadDictXml = cfgPayload.getPayloadDict().toXMLPropertyList();
            final IOSCommandPayload commandPayload = getInstance().createCommandPayload("InstallProfile");
            commandPayload.setCommandUUID(commandPayload.commandUUID = commandUUID, false);
            commandPayload.setPayload(payloadDictXml.getBytes());
            strQuery = commandPayload.getPayloadDict().toXMLPropertyList();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getDefaultAppCatalogProfile", exp);
        }
        return strQuery;
    }
    
    public String getDefaultAppCatalogRemoveProfile(final String commandUUID) {
        String strQuery = null;
        try {
            final String collectionName = "DCAppCatalog";
            final String payloadIdentifier = "com.mdm." + collectionName;
            final IOSCommandPayload commandPayload = getInstance().createCommandPayload("RemoveProfile");
            commandPayload.commandUUID = "DefaultRemoveAppCatalogWebClips";
            commandPayload.setCommandUUID(commandUUID, false);
            commandPayload.getCommandDict().put("Identifier", (Object)payloadIdentifier);
            strQuery = commandPayload.getPayloadDict().toXMLPropertyList();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getDefaultAppCatalogRemoveProfile ", exp);
        }
        return strQuery;
    }
    
    public String createLockScreenCommand(final Long resourceId, final String commandUUID, final String strUDID) {
        String strQuery = null;
        try {
            final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
            final Long collectionID = Long.parseLong(MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID));
            final JSONObject params = new JSONObject();
            params.put("ResourceId", (Object)resourceId);
            params.put("strUDID", (Object)strUDID);
            final List configIDList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2PayloadHandler payloadHandler = new DO2PayloadHandler();
            final NSArray configArray = payloadHandler.createLockScreenSettingArrayItem(configIDList, params);
            commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "LockScreenMessages;Collection=" + collectionID.toString(), false);
            strQuery = commandPayload.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while Creating lockscreen messages", ex);
        }
        return strQuery;
    }
    
    public String createSharedConfigurationCommand(final String commandUUID, final JSONObject configJSON) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        final ManagedSettingItem payload = new ManagedSettingItem("SharedDeviceConfiguration");
        if (configJSON != null && !configJSON.keySet().isEmpty()) {
            if (configJSON.optInt("QUOTA_SIZE", -1) != -1) {
                payload.setQuotaSize(configJSON.getInt("QUOTA_SIZE"));
            }
            if (configJSON.optInt("NO_RESIDENT_USERS", -1) != -1) {
                payload.setResidentUsers(configJSON.getInt("NO_RESIDENT_USERS"));
            }
        }
        final NSArray configArray = new NSArray(1);
        configArray.setValue(0, (Object)payload.getPayloadDict());
        commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
        commandPayload.setCommandUUID(commandUUID, false);
        return commandPayload.toString();
    }
    
    public String createOSUpdateRestrictionXML(final Long collectionId, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createOSUpdateRestrictionCommand(collectionId);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "RestrictOSUpdates;Collection=" + collectionId.toString(), false);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating os update restriction xml", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload createOSUpdateRestrictionCommand(final Long collectionId) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        try {
            final DataObject dataObject = new OSUpdatePolicyHandler().getOSUpdatePolicy(collectionId);
            final ConfigurationPayload configuration = new DO2PayloadHandler().createRestrictOSUpdatePayload(dataObject);
            final String payloadData = configuration.getPayloadDict().toXMLPropertyList();
            commandPayload.setPayload(payloadData.getBytes());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in createOSUpdateRestrictionCommand", ex);
        }
        return commandPayload;
    }
    
    public void createFilevaultPersonalRecoveryKeyCommandXML(final Long collectionId, final String profilePath) {
        try {
            final Long certificateID = MDMFilevaultUtils.getFilevaultPersonalRecoveryCertificateID(collectionId);
            if (certificateID != null) {
                final IOSCommandPayload commandPayload = this.createFilevaultPersonalRecoveryKeyCommand(certificateID);
                final String toXMLPropertyList = commandPayload.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
                this.logger.log(Level.INFO, "Successfully written Path[{0}] with content :{1}", new Object[] { profilePath, toXMLPropertyList });
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in createFilevaultPersonalRecoveryKeyCommandXML", e);
        }
    }
    
    private IOSCommandPayload createFilevaultPersonalRecoveryKeyCommand(final Long certificateID) throws Exception {
        final IOSCommandPayload commandPayload = new PayloadHandler().createCommandPayload("RotateFileVaultKey");
        commandPayload.setCommandUUID("MacFileVaultPersonalKeyRotate", false);
        commandPayload.getCommandDict().put("KeyType", (Object)"personal");
        final X509Certificate certificate = MacFilevaultPersonalRecoveyKeyRotateGenerator.getPersonalRecoveryKeyCertificate(certificateID);
        commandPayload.getCommandDict().put("ReplyEncryptionCertificate", (Object)certificate.getEncoded());
        final NSDictionary unlockDict = new NSDictionary();
        unlockDict.put("Password", (Object)"%filevault_personal_recovery_key%");
        commandPayload.getCommandDict().put("FileVaultUnlock", (NSObject)unlockDict);
        return commandPayload;
    }
    
    public IOSCommandPayload createRemoveOSUpdateRestrictionCommand(final Long collectionId) {
        final IOSCommandPayload commandPayload = this.createRemoveProfileCommand("com.mdm.osupdate.restriction", collectionId);
        commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveRestrictOSUpdates;Collection=" + collectionId.toString(), false);
        return commandPayload;
    }
    
    public IOSCommandPayload createRemoveUserInstalledProfileCommand(final String commandUUID, final String payloadIdentifier) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        commandPayload.getCommandDict().put("Identifier", (Object)payloadIdentifier);
        commandPayload.setCommandUUID(commandUUID, false);
        return commandPayload;
    }
    
    public IOSCommandPayload createSingletonRestrictCommand(final String requestType, final JSONObject restrictionObject) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        commandPayload.setCommandUUID(requestType, false);
        final ConfigurationPayload configuration = new DO2PayloadHandler().createSingletonRestrictPayload(restrictionObject);
        final String payloadData = configuration.getPayloadDict().toXMLPropertyList();
        commandPayload.setPayload(payloadData.getBytes());
        return commandPayload;
    }
    
    public String generatePasscodeDisableRestriction(final Long collectionId, final String profilePath, final String profileIdentifier) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generatePasscodeDisableRestriction(collectionId, profileIdentifier);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generating passcode disable restriction", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generatePasscodeDisableRestriction(final Long collectionId, final String profileIdentifier) {
        IOSCommandPayload commandPayload = null;
        try {
            commandPayload = this.createPasscodeDisableRestrictionCommand(profileIdentifier);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "DisablePasscode;Collection=" + collectionId.toString(), false);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generating passcode disable restriction", e);
        }
        return commandPayload;
    }
    
    public IOSCommandPayload createPasscodeDisableRestrictionCommand(final String profileIdentifier) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        final ConfigurationPayload configurationPayload = new DO2PayloadHandler().createPasscodeRestrictionPayload(profileIdentifier, "DisablePasscode", true);
        final String payloadData = configurationPayload.getPayloadDict().toXMLPropertyList();
        commandPayload.setPayload(payloadData.getBytes());
        return commandPayload;
    }
    
    public String generateRemovePasscodeDisableRestriction(final Long collectionId, final String profilePath, final String profileIdentifier) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.generateRemovePasscodeDisableRestriction(collectionId, profileIdentifier);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generating remove passcode disable restriction", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload generateRemovePasscodeDisableRestriction(final Long collectionId, final String profileIdentifier) {
        IOSCommandPayload commandPayload = null;
        try {
            commandPayload = this.createRemoveProfileCommand(profileIdentifier, collectionId);
            commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveDisablePasscode;Collection=" + collectionId.toString(), false);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generating remove passcode disable restriction", e);
        }
        return commandPayload;
    }
    
    public String generateRestrictPasscode(final Long collectionId, final String profilePath) {
        String commandUUID = null;
        try {
            commandUUID = "RestrictPasscode;Collection=" + collectionId.toString();
            final IOSCommandPayload commandPayload = this.getRestrictPasscodeCommand(commandUUID, true);
            commandUUID = commandPayload.getCommandUUID();
            final String toXMLPropertyList = commandPayload.toString();
            ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in restriction passcode generation", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload getRestrictPasscodeCommand(final String commandUUID, final boolean passcodeRestricted) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("InstallProfile");
        final ConfigurationPayload configurationPayload = new DO2PayloadHandler().createPasscodeRestrictionPayload("com.mdm.passcode_restriction_install_profile", "RestrictPasscode", passcodeRestricted);
        final String payloadData = configurationPayload.getPayloadDict().toXMLPropertyList();
        commandPayload.setPayload(payloadData.getBytes());
        commandPayload.setCommandUUID(commandPayload.commandUUID = commandUUID, false);
        return commandPayload;
    }
    
    public IOSCommandPayload getRemoveRestrictPasscodeCommand(final String commandUUID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("RemoveProfile");
        commandPayload.getCommandDict().put("Identifier", (Object)"com.mdm.passcode_restriction_install_profile");
        commandPayload.setCommandUUID(commandPayload.commandUUID = commandUUID, false);
        return commandPayload;
    }
    
    public IOSCommandPayload createFileVaultSecurityInfoCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SecurityInfo");
        commandPayload.setCommandUUID("FileVaultUserLoginSecurityInfo");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "FileVaultUserLoginSecurityInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "FileVaultUserLoginSecurityInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public void addKioskAppConfigurationCommand(final NSDictionary commandDict, final Long resourceId) {
        final JSONObject kioskObject = new IOSKioskProfileDataHandler().getLatestSingleWebAppConfiguration(resourceId);
        if (kioskObject.length() > 0) {
            final NSDictionary configurationDict = (NSDictionary)commandDict.get((Object)"Configuration");
            final NSDictionary kioskDict = new NSDictionary();
            final boolean iosWebAppStatusBar = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("iOSWebKioskStatusBar");
            kioskDict.put("singleAppKioskMode", (Object)true);
            kioskDict.put("isKioskStatusBarRestricted", (Object)iosWebAppStatusBar);
            kioskDict.put("KioskCollectionID", (Object)String.valueOf(kioskObject.getLong("COLLECTION_ID")));
            kioskDict.put("idleRefreshTimeout", (Object)String.valueOf(kioskObject.get("IDLE_REFRESH_TIMEOUT")));
            final NSArray webURLArray = new NSArray(1);
            kioskDict.put("kioskWebURLInfo", (NSObject)webURLArray);
            final NSDictionary webClipDict = new NSDictionary();
            webClipDict.put("name", (Object)kioskObject.getString("WEBCLIP_LABEL"));
            webClipDict.put("weburl", (Object)kioskObject.getString("WEBCLIP_URL"));
            webURLArray.setValue(0, (Object)webClipDict);
            configurationDict.put("KioskPolicy", (NSObject)kioskDict);
        }
    }
    
    public IOSCommandPayload createAppleUserListCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("UserList");
        commandPayload.setCommandUUID("UserList", false);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "UserList", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createAppleLogoutUserCommand(final String commandUUID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("LogOutUser");
        commandPayload.setCommandUUID(commandUUID, false);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "LogOutUser", commandPayload.toString() });
        return commandPayload;
    }
    
    public String generateSharedDeviceRestrictions(final Long collectionId, final List configIdList, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createSharedDeviceRestriction(collectionId, configIdList);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in generateSharedDeviceRestrictions", e);
        }
        return commandUUID;
    }
    
    public String generateRemoveSharedDeviceRestrictions(final Long collectionId, final String profilePath) {
        String commandUUID = null;
        try {
            final IOSCommandPayload commandPayload = this.createRemoveSharedDeviceRestriction(collectionId);
            if (commandPayload != null) {
                commandUUID = commandPayload.getCommandUUID();
                final String toXMLPropertyList = commandPayload.toString();
                ApiFactoryProvider.getFileAccessAPI().writeFile(profilePath, toXMLPropertyList.getBytes());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "generateRemoveSharedDeviceRestrictions", e);
        }
        return commandUUID;
    }
    
    public IOSCommandPayload createSharedDeviceRestriction(final Long collectionId, final List configIDList) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        try {
            final DO2PayloadHandler payloadHander = new DO2PayloadHandler();
            final NSArray configArray = payloadHander.createSharedDeviceRestrictionItem(configIDList);
            if (configArray.count() > 0) {
                commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
                commandPayload.setCommandUUID(commandPayload.commandUUID = "SharedDeviceRestrictions;Collection=" + collectionId.toString(), false);
                return commandPayload;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating managed settings", e);
        }
        return null;
    }
    
    public IOSCommandPayload createRemoveSharedDeviceRestriction(final Long collectionId) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("Settings");
        final List<NSDictionary> removalSharedList = new DO2SharedDeviceSettings().getRemovalSharedRestriction();
        final NSArray configArray = new NSArray(removalSharedList.size());
        for (int k = 0; k < removalSharedList.size(); ++k) {
            configArray.setValue(k, (Object)removalSharedList.get(k));
        }
        commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
        commandPayload.setCommandUUID(commandPayload.commandUUID = "RemoveSharedDeviceRestrictions;Collection=" + collectionId.toString(), false);
        return commandPayload;
    }
    
    static {
        PayloadHandler.pHandler = null;
        PROFILEREMOVALCONFIGS = new ArrayList<Integer>() {};
    }
}
