package com.adventnet.sym.server.mdm.command;

import com.me.mdm.server.security.mac.recoverylock.RecoveryLock;
import java.util.HashSet;
import java.util.ArrayList;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Collection;
import com.me.mdm.server.config.MDMConfigUtil;
import com.dd.plist.NSString;
import com.me.mdm.apps.handler.AppAutoDeploymentHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadSigningFactory;
import com.dd.plist.NSData;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.adep.DeviceConfiguredCommandHandler;
import com.me.mdm.server.apps.blacklist.ios.IOSBlacklistAppProcessor;
import com.me.mdm.server.profiles.ios.IOSLockScreenHandler;
import com.dd.plist.NSArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.agent.handlers.ios.IOSMigrationUtil;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.mdm.server.command.CommandStatusHandler;
import java.util.Map;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.server.agent.DiscoveryServiceHandler;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.zerotrust.ZeroTrustAPIHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import com.me.mdm.server.security.profile.ApplePayloadSecretFieldsHandler;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.dd.plist.NSDictionary;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.enrollment.ios.IOSMobileConfigHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.apache.commons.lang.StringEscapeUtils;
import com.me.mdm.server.apps.ios.vpp.VPPAppAPIRequestHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedUserHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.logging.Logger;

public class CommandQueryGenerator
{
    public Logger logger;
    Logger accesslogger;
    private static CommandQueryGenerator commandQueryGenerator;
    String separator;
    private static final List<String> PROFILE_SIGN_COMMANDTYPE;
    private static final List<String> PARSE_FROM_FILE_COMMAND_TYPE;
    private static final Set<Integer> CONFIGID_REQUIRE_MODIFICATION_IN_PAYLOAD;
    private static final HashMap<String, String> COMMANDQUERYCLASS;
    
    public CommandQueryGenerator() {
        this.logger = Logger.getLogger("MDMLogger");
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.separator = "\t";
    }
    
    public static CommandQueryGenerator getInstance() {
        if (CommandQueryGenerator.commandQueryGenerator == null) {
            CommandQueryGenerator.commandQueryGenerator = new CommandQueryGenerator();
        }
        return CommandQueryGenerator.commandQueryGenerator;
    }
    
    public String getDeviceQuery(final String strUDID, final Long resourceID, final int commandRepositoryType, final HashMap requestMap) {
        String responseData = null;
        while (responseData == null) {
            final DeviceCommand command = DeviceCommandRepository.getInstance().getDeviceCommandFromCache(strUDID, commandRepositoryType);
            if (command == null) {
                break;
            }
            responseData = this.getDeviceQuery(command, strUDID, resourceID, requestMap);
            if (responseData == null) {
                continue;
            }
            this.accesslogger.log(Level.INFO, "Device Idle Query DATA-OUT: Udid: {0} \t Cmd: {1} \t Response OK \t {2}", new Object[] { strUDID, command.commandType, MDMUtil.getCurrentTimeInMillis() });
            if (!command.commandUUID.equals("Enrollment")) {
                continue;
            }
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID("Enrollment");
            DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, strUDID, 1, 12);
            this.logger.log(Level.INFO, "Enrollment command marked as Yet to Apply for resource: {0} | Command: {1}", new Object[] { resourceID, commandId });
        }
        return responseData;
    }
    
    private String getDeviceQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        this.logger.log(Level.INFO, "getDeviceQuery() -> command: {0} UDID: {1}", new Object[] { deviceCommand.commandType, strUDID });
        String strQuery = null;
        try {
            strQuery = this.generateIOSQuery(deviceCommand, strUDID, resourceID, requestMap);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getDeviceQuery", exp);
        }
        return strQuery;
    }
    
    private String generateIOSQuery(final DeviceCommand deviceCommand, final String strUDID, Long resourceID, final HashMap requestMap) {
        final String command = deviceCommand.commandType;
        final String commandUUID = deviceCommand.commandUUID;
        int agentType = -1;
        if (command.equalsIgnoreCase("InstallApplication") || command.equalsIgnoreCase("ApplicationConfiguration")) {
            agentType = AppsAutoDeployment.getInstance().getAgentIDFromCommandUUID(commandUUID);
        }
        this.logger.log(Level.INFO, "generateQuery command: {0} commandUUID: {1} UDID: {2} resourceID: {3}", new Object[] { command, commandUUID, strUDID, resourceID });
        String strQuery = null;
        try {
            if (command.equalsIgnoreCase("Restrictions") || command.equalsIgnoreCase("RestrictionProfileStatus")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createRestrictionsCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SecurityInfo")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createSecurityInfoCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("CertificateList")) {
                final JSONObject privacyJson = new PrivacySettingsHandler().getPrivacySettingsJSON(resourceID);
                final boolean managedOnly = privacyJson.getInt("fetch_user_installed_certs") == 2;
                final IOSCommandPayload createRestrictionsCommand2 = PayloadHandler.getInstance().createCertificateListCommand(managedOnly);
                createRestrictionsCommand2.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand2.toString();
            }
            else if (command.equalsIgnoreCase("InstalledApplicationList") || command.equals("FetchAppleAgentDetails")) {
                Boolean managedAppOnly = false;
                if (resourceID != null) {
                    final HashMap privacyJson2 = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
                    final int fetchApp = Integer.parseInt(privacyJson2.get("fetch_installed_app").toString());
                    if (fetchApp == 2) {
                        managedAppOnly = true;
                    }
                }
                final IOSCommandPayload createRestrictionsCommand3 = command.equals("FetchAppleAgentDetails") ? PayloadHandler.getInstance().fetchAgentDetailsCommand() : PayloadHandler.getInstance().createInstallAppListCommand(managedAppOnly);
                createRestrictionsCommand3.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand3.toString();
            }
            else if (command.equalsIgnoreCase("ManagedApplicationList")) {
                if (deviceCommand.commandFilePath != null && !deviceCommand.commandFilePath.contains("--")) {
                    final String profileRepoParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                    final String profileFullPath = profileRepoParentDir + File.separator + deviceCommand.commandFilePath;
                    strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                }
                else {
                    final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createManagedAppListCommand();
                    createRestrictionsCommand.setCommandUUID(commandUUID, false);
                    strQuery = createRestrictionsCommand.toString();
                }
            }
            else if (command.equalsIgnoreCase("ProfileList")) {
                final IOSCommandPayload cmdPayload = PayloadHandler.getInstance().createCommandPayload("ProfileList");
                cmdPayload.setCommandUUID(commandUUID, false);
                strQuery = cmdPayload.toString();
            }
            else if (command.equalsIgnoreCase("ProvisioningProfileList")) {
                final IOSCommandPayload cmdPayload = PayloadHandler.getInstance().createCommandPayload("ProvisioningProfileList");
                cmdPayload.setCommandUUID(commandUUID, false);
                strQuery = cmdPayload.toString();
            }
            else if (command.equalsIgnoreCase("DeviceLock")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createDeviceLockCommand(resourceID, deviceCommand);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("EraseDevice")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createEraseDeviceCommand(resourceID, deviceCommand);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("PlayLostModeSound")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createPlayLostModeSoundDeviceCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.startsWith("ShutDownDevice")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createShutDownDeviceCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.startsWith("RestartDevice")) {
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createRestartDeviceCommand(resourceID);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("UnlockUserAccount")) {
                final IOSCommandPayload createUnlockDeviceCommand = PayloadHandler.getInstance().createUnlockDeviceAccountCommand(resourceID);
                createUnlockDeviceCommand.setCommandUUID(commandUUID, false);
                strQuery = createUnlockDeviceCommand.toString();
            }
            else if (command.equalsIgnoreCase("InviteToProgram")) {
                final HashMap managedUserInfo = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(strUDID);
                final Long userResId = managedUserInfo.get("MANAGED_USER_ID");
                final IOSCommandPayload createRestrictionsCommand2 = PayloadHandler.getInstance().createInviteToProgramCommand();
                createRestrictionsCommand2.setCommandUUID(commandUUID, false);
                final String[] split = commandUUID.split(";BusinessStore=");
                final Long businessStoreID = Long.parseLong(split[1]);
                final String invitationcode = VPPManagedUserHandler.getInstance().getInvitationCode(businessStoreID, userResId);
                String invitationUrl = VPPAppAPIRequestHandler.getInstance().getServiceUrl("invitationEmailUrl");
                invitationUrl = invitationUrl.replace("%inviteCode%", invitationcode);
                strQuery = createRestrictionsCommand2.toString();
                strQuery = strQuery.replace("%invitationUrl%", StringEscapeUtils.escapeXml(invitationUrl));
            }
            else if (command.equalsIgnoreCase("EnableLostMode")) {
                IOSCommandPayload cmd;
                if (ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(resourceID)) {
                    cmd = PayloadHandler.getInstance().createEnableLostModeCommand(resourceID, deviceCommand);
                }
                else {
                    cmd = PayloadHandler.getInstance().createDeviceLockCommand(resourceID, deviceCommand);
                }
                cmd.setCommandUUID(commandUUID, false);
                strQuery = cmd.toString();
            }
            else if (command.equalsIgnoreCase("DisableLostMode")) {
                final IOSCommandPayload cmd = PayloadHandler.getInstance().createDisableLostModeCommand();
                cmd.setCommandUUID(commandUUID, false);
                strQuery = cmd.toString();
            }
            else if (command.equalsIgnoreCase("LostModeDeviceLocation")) {
                IOSCommandPayload cmd;
                if (ManagedDeviceHandler.getInstance().isSupervisedAnd9_3Above(resourceID)) {
                    cmd = PayloadHandler.getInstance().createLostModeDeviceLocationCommand();
                }
                else {
                    cmd = PayloadHandler.getInstance().createGetLocationCommand();
                }
                cmd.setCommandUUID(commandUUID, false);
                strQuery = cmd.toString();
            }
            else if (CommandQueryGenerator.PARSE_FROM_FILE_COMMAND_TYPE.contains(command)) {
                if (commandUUID.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5") || commandUUID.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4")) {
                    final String servletPath = requestMap.get("ServletPath");
                    final String queryParams = requestMap.get("RequestURI");
                    final long enrollmentReqId = requestMap.get("ENROLLMENT_REQUEST_ID");
                    strQuery = IOSMobileConfigHandler.getInstance().generateUpgradeMobileConfig(enrollmentReqId, strUDID, commandUUID, servletPath, queryParams, resourceID);
                }
                else {
                    final String profileRepoParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                    final String profileFullPath = profileRepoParentDir + File.separator + deviceCommand.commandFilePath;
                    this.logger.log(Level.INFO, "generateQuery command: profileFullPath {0}: ", profileFullPath);
                    if (deviceCommand.dynamicVariable == Boolean.TRUE && (command.equals("InstallProfile") || command.equals("InstallApplication") || command.equals("ApplicationConfiguration") || command.equals("ManageApplication") || command.equals("InstallApplicationConfiguration"))) {
                        String fileData = null;
                        if (MDMFileUtil.fileCacheEnabled) {
                            final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(profileFullPath);
                            fileData = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                            if (fileData == null) {
                                fileData = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(profileFullPath);
                                if (command.equals("InstallProfile")) {
                                    final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
                                    NSDictionary root = (NSDictionary)DMSecurityUtil.parsePropertyList((InputStream)new ByteArrayInputStream(fileData.getBytes(StandardCharsets.UTF_8)));
                                    final NSDictionary cmdDict = (NSDictionary)root.objectForKey("Command");
                                    String pydContent = this.getPayloadContent(cmdDict);
                                    pydContent = ApplePayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(pydContent, customerID);
                                    root = this.replacePayloadContent(root, pydContent);
                                    fileData = root.toXMLPropertyList();
                                }
                                if (fileData.length() <= MDMFileUtil.fileSizeCacheThreshold) {
                                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)fileData, 2, (int)MDMFileUtil.fileCacheTimeTTL);
                                    this.logger.log(Level.INFO, "Profile File added to cache - {0}", profileFullPath);
                                }
                                else {
                                    this.logger.log(Level.INFO, "File size greater than threshold - {0}", profileFullPath);
                                }
                            }
                            else {
                                this.logger.log(Level.INFO, "Profile file read from cache - {0}", profileFullPath);
                            }
                        }
                        NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList((fileData != null) ? new ByteArrayInputStream(fileData.getBytes(StandardCharsets.UTF_8)) : ApiFactoryProvider.getFileAccessAPI().readFile(profileFullPath));
                        NSDictionary commandDict = (NSDictionary)rootDict.objectForKey("Command");
                        if (command.equals("InstallProfile")) {
                            String payloadContent = this.getPayloadContent(commandDict);
                            payloadContent = DynamicVariableHandler.replaceDynamicVariables(payloadContent, strUDID);
                            if (!MDMFileUtil.fileCacheEnabled) {
                                final Long customerID2 = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
                                payloadContent = ApplePayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(payloadContent, customerID2);
                            }
                            if (payloadContent.contains("%challenge_password%")) {
                                payloadContent = ThirdPartyCAUtil.replaceScepChallengePasswords(payloadContent, resourceID);
                            }
                            else if (payloadContent.contains("%zerotrust_password%")) {
                                this.logger.log(Level.INFO, "Zerotrust SCEP profile: Getting san and password");
                                final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                                this.logger.log(Level.INFO, "Zerotrust SCEP profile: Collection id: {0}", new Object[] { collectionId });
                                final Long profileID = new ProfileHandler().getProfileIDFromCollectionID(Long.parseLong(collectionId));
                                this.logger.log(Level.INFO, "Zerotrust SCEP profile: Profile id: {0}", new Object[] { profileID });
                                final JSONObject associatedUserJSON = ProfileUtil.getInstance().getAssociatedUserForProfile(profileID);
                                final Long associatedUserID = (Long)associatedUserJSON.get("UserID");
                                if (associatedUserID != null) {
                                    this.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user id : {0}", associatedUserID);
                                    final JSONObject scepDetails = ZeroTrustAPIHandler.getInstance().getSANandPasswordFromZeroTrust(resourceID, associatedUserID);
                                    if (scepDetails.getInt("http_response_code") == 200) {
                                        payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, "%zerotrust_password%", scepDetails.getString("ZEROTRUST_PASSWORD"));
                                        payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, "%zerotrust_san%", scepDetails.getString("ZEROTRUST_SAN"));
                                    }
                                    else {
                                        this.logger.log(Level.INFO, "Zerotrust SCEP profile: Failed to get san and password");
                                    }
                                }
                                else {
                                    this.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user not found");
                                }
                            }
                            rootDict = this.replacePayloadContent(rootDict, payloadContent);
                        }
                        else {
                            final JSONObject apiKeyJson = new JSONObject();
                            final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(strUDID);
                            apiKeyJson.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiKeyJson);
                            commandDict = DiscoveryServiceHandler.getInstance().setIOSAgentCommDetails(commandDict, key);
                            if (this.isDefaultMDMApp(commandDict, commandUUID)) {
                                PayloadHandler.getInstance().addKioskAppConfigurationCommand(commandDict, resourceID);
                            }
                            String payloadContent2 = commandDict.toXMLPropertyList();
                            if (key == null || key.getVersion() != APIKey.VERSION_2_0) {
                                this.logger.log(Level.SEVERE, "APIKey version is not v2.. The returned version is - {0}", (key == null) ? "NULL" : Integer.valueOf(key.getVersion()));
                            }
                            else {
                                payloadContent2 = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(payloadContent2, key, false);
                            }
                            if (command.equals("InstallApplication") || command.equals("ManageApplication")) {
                                final boolean isEncodingRequired = MDMDeviceAPIKeyGenerator.getInstance().isEncodingRequiredForIOSEnterpriseApp();
                                payloadContent2 = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceUDIDPlaceHolder(payloadContent2, strUDID, isEncodingRequired);
                            }
                            payloadContent2 = DynamicVariableHandler.replaceDynamicVariables(payloadContent2, strUDID);
                            if (agentType != -1) {
                                final AppAutoDeploymentHandler handler = AppsAutoDeployment.getInstance().getAgentHandler(agentType);
                                final Long customerID3 = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
                                payloadContent2 = handler.replaceDynamicVariables(payloadContent2, customerID3, strUDID);
                            }
                            commandDict = (NSDictionary)DMSecurityUtil.parsePropertyList(payloadContent2.getBytes("UTF-8"));
                            if (command.equals("InstallApplication")) {
                                final String collectionId2 = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                                final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId2));
                                final boolean isVppApp = new ManagedAppDataHandler().isAppPurchasedFromPortal(appGroupID);
                                if (isVppApp) {
                                    NSDictionary optionsDict = null;
                                    if (commandDict.containsKey("Options")) {
                                        optionsDict = (NSDictionary)commandDict.get((Object)"Options");
                                        optionsDict.put("PurchaseMethod", (Object)1);
                                    }
                                    else {
                                        optionsDict = new NSDictionary();
                                        optionsDict.put("PurchaseMethod", (Object)1);
                                    }
                                    commandDict.put("Options", (NSObject)optionsDict);
                                }
                                else if (commandDict.containsKey("Options")) {
                                    commandDict.remove("Options");
                                }
                            }
                            rootDict.put("Command", (NSObject)commandDict);
                        }
                        strQuery = rootDict.toXMLPropertyList();
                    }
                    else {
                        strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                        final APIKey key2 = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(requestMap);
                        if (key2 != null) {
                            strQuery = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(strQuery, key2, false, strUDID);
                        }
                        strQuery.replaceAll("%authtoken%", "");
                    }
                }
            }
            else if (command.equals("RemoveDevice") || command.equals("CorporateWipe")) {
                final IOSCommandPayload createRemoveDeviceCommand = PayloadHandler.getInstance().createRemoveDeviceCommand();
                createRemoveDeviceCommand.setCommandUUID(commandUUID, false);
                strQuery = createRemoveDeviceCommand.toString();
                DeviceCommandRepository.getInstance().deleteResourceCommand(command, strUDID);
                try {
                    final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
                    final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    if (commandStatusJSON.has("ADDED_BY")) {
                        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
                        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                        commandStatusJSON.put("COMMAND_STATUS", 2);
                        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, e, () -> "Exception in updating status for " + s);
                }
            }
            else if (command.equals("DefaultAppCatalogWebClips") || command.equals("DefaultAppCatalogWebClipsMigrate")) {
                final String serverUrl = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL();
                final JSONObject apiKeyJson2 = new JSONObject();
                final Long erid2 = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(strUDID);
                apiKeyJson2.put("ENROLLMENT_REQUEST_ID", (Object)erid2);
                final APIKey key3 = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiKeyJson2);
                String webClipsUrl = serverUrl + "/showAppsList.mobileapps" + "?udid=" + strUDID;
                if (key3 != null) {
                    webClipsUrl = webClipsUrl + "&" + key3.getAsURLParams();
                }
                strQuery = PayloadHandler.getInstance().getDefaultAppCatalogProfile(webClipsUrl, CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID), command);
                if (command.equals("DefaultAppCatalogWebClipsMigrate")) {
                    if (key3 == null || key3.getVersion() != APIKey.VERSION_2_0) {
                        this.logger.log(Level.SEVERE, "APIKey version is not v2.. The returned version is - {0}", (key3 == null) ? "Null" : Integer.valueOf(key3.getVersion()));
                        IOSMigrationUtil.getInstance().migrationFailed(resourceID, 1);
                    }
                    else {
                        IOSMigrationUtil.getInstance().migrationInitated(resourceID, 1);
                    }
                }
            }
            else if (command.equals("DefaultRemoveAppCatalogWebClips")) {
                strQuery = PayloadHandler.getInstance().getDefaultAppCatalogRemoveProfile(commandUUID);
            }
            else if (command.equals("GetLocation") || command.equals("SingleWebAppKioskFeedback") || command.equals("RemoveSingleWebAppKioskFeedback")) {
                final IOSCommandPayload createGeolocationCommand = PayloadHandler.getInstance().createGetLocationCommand();
                createGeolocationCommand.setCommandUUID(commandUUID, false);
                strQuery = createGeolocationCommand.toString();
            }
            else if (command.equals("DeviceName")) {
                final IOSCommandPayload createDeviceNameCommand = PayloadHandler.getInstance().createDeviceNameCommand();
                createDeviceNameCommand.setCommandUUID(commandUUID, false);
                strQuery = createDeviceNameCommand.toString();
                final String deviceName = (String)DBUtil.getValueFromDB("ManagedDeviceExtn", "MANAGED_DEVICE_ID", (Object)resourceID, "NAME");
                strQuery = strQuery.replaceAll("%device_name%", deviceName);
            }
            else if (command.equals("MDMDefaultApplicationConfiguration") || command.equals("MDMDefaultApplicationConfigMigrate")) {
                final JSONObject apiKeyJson3 = new JSONObject();
                final Long erid3 = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(strUDID);
                apiKeyJson3.put("ENROLLMENT_REQUEST_ID", (Object)erid3);
                final APIKey key2 = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiKeyJson3);
                final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload("Settings");
                final NSDictionary commandDict = new NSDictionary();
                commandDict.put("Item", (Object)"ApplicationConfiguration");
                commandDict.put("Identifier", (Object)"com.manageengine.mdm.iosagent");
                commandDict.put("Configuration", (NSObject)MDMiOSEntrollmentUtil.getMDMDefaultAppConfiguration());
                final NSDictionary payloadDict = DiscoveryServiceHandler.getInstance().setIOSAgentCommDetails(commandDict, key2);
                final NSArray configArray = new NSArray(1);
                configArray.setValue(0, (Object)commandDict);
                commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
                commandPayload.setCommandUUID(command, Boolean.FALSE);
                strQuery = DynamicVariableHandler.replaceDynamicVariables(commandPayload.toString(), strUDID);
                if (key2 == null || key2.getVersion() != APIKey.VERSION_2_0) {
                    this.logger.log(Level.SEVERE, "APIKey version is not v2.. The returned version is - {0}", (key2 == null) ? "NULL" : Integer.valueOf(key2.getVersion()));
                }
                else {
                    strQuery = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(strQuery, key2, false, strUDID);
                }
                if (command.equals("MDMDefaultApplicationConfigMigrate")) {
                    if (key2 == null || key2.getVersion() != APIKey.VERSION_2_0) {
                        IOSMigrationUtil.getInstance().migrationFailed(resourceID, 2);
                    }
                    else {
                        IOSMigrationUtil.getInstance().migrationInitated(resourceID, 2);
                    }
                }
            }
            else if (command.equals("InstallManagedSettings")) {
                final String profileRepoParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String profileFullPath = profileRepoParentDir + File.separator + deviceCommand.commandFilePath;
                strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                final IOSLockScreenHandler lockScreenHandler = new IOSLockScreenHandler();
                strQuery = lockScreenHandler.checkLockScreenConfForResource(resourceID, strQuery, commandUUID);
            }
            else if (command.equals("DefaultMDMKioskProfile")) {
                final IOSCommandPayload createDefaultMDMKiosk = PayloadHandler.getInstance().createDefaultMDMKiosk();
                createDefaultMDMKiosk.setCommandUUID(commandUUID, false);
                strQuery = createDefaultMDMKiosk.toString();
            }
            else if (command.equals("DefaultMDMRemoveKioskProfile")) {
                final IOSCommandPayload createRemoveProfile = PayloadHandler.getInstance().createRemoveDefaultMDMKiosk();
                createRemoveProfile.setCommandUUID(commandUUID, false);
                strQuery = createRemoveProfile.toString();
            }
            else if (command.equals("AvailableOSUpdates")) {
                final IOSCommandPayload availableOSUpdatesCommand = PayloadHandler.getInstance().createAvailableOSUpdatesCommand();
                availableOSUpdatesCommand.setCommandUUID(commandUUID, false);
                strQuery = availableOSUpdatesCommand.toString();
            }
            else if (command.equals("OSUpdateStatus")) {
                final IOSCommandPayload cmd = PayloadHandler.getInstance().createCommandPayload("OSUpdateStatus");
                cmd.setCommandUUID(commandUUID, false);
                strQuery = cmd.toString();
            }
            else if (command.equals("BlacklistAppInDevice") || command.equals("RemoveBlacklistAppInDevice")) {
                final HashMap hashMap = new HashMap();
                hashMap.put("RESOURCE_ID", resourceID);
                hashMap.put("COMMAND_TYPE", command);
                strQuery = (String)new IOSBlacklistAppProcessor().processBlackListRequest(hashMap);
            }
            else if (command.equals("DeviceConfigured")) {
                strQuery = DeviceConfiguredCommandHandler.getInstance().getDeviceConfiguredCommandAsString();
            }
            else {
                try {
                    final String className = CommandQueryGenerator.COMMANDQUERYCLASS.get(command.split(";")[0]);
                    final CommandQueryCreator queryGenerator = (CommandQueryCreator)Class.forName(className).newInstance();
                    strQuery = queryGenerator.createCmdQuery(deviceCommand, strUDID, resourceID, requestMap);
                }
                catch (final ClassNotFoundException e2) {
                    this.logger.log(Level.SEVERE, "CommandQueryGenerator: No Class Found error for query generator", e2);
                    throw e2;
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "CommandQueryGenerator: Exception while processing the command", ex);
                    throw ex;
                }
            }
            if (command.equals("InstallApplication") || command.equals("ManageApplication")) {
                final String iOSVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                final NSDictionary rootDict2 = (NSDictionary)DMSecurityUtil.parsePropertyList(strQuery.getBytes());
                final NSDictionary commandDict2 = (NSDictionary)rootDict2.objectForKey("Command");
                if (!new VersionChecker().isGreater(iOSVersion, "9.3") || new VersionChecker().isGreaterOrEqual(iOSVersion, "10")) {
                    this.logger.log(Level.INFO, "Going to add ChangeManagementState key...");
                    commandDict2.put("ChangeManagementState", (Object)"Managed");
                    rootDict2.put("Command", (NSObject)commandDict2);
                }
                final String requestType = commandDict2.get((Object)"RequestType").toString();
                if (!requestType.contains("InstallEnterpriseApplication") && !commandDict2.containsKey("InstallAsManaged")) {
                    commandDict2.put("InstallAsManaged", (Object)true);
                    rootDict2.put("Command", (NSObject)commandDict2);
                }
                if (command.equals("ManageApplication")) {
                    rootDict2.put("CommandUUID", (Object)commandUUID);
                }
                strQuery = rootDict2.toXMLPropertyList();
            }
            else if (command.equals("InstallProfile") || command.equals("DefaultAppCatalogWebClips") || command.equals("KioskDefaultRestriction") || command.equals("DefaultMDMKioskProfile") || CommandQueryGenerator.PROFILE_SIGN_COMMANDTYPE.contains(command) || (command.equals("ApplicationConfiguration") && agentType != -1)) {
                if (resourceID == null) {
                    resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                }
                String osVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                if (osVersion == null) {
                    osVersion = (String)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                }
                if (command.equals("InstallProfile")) {
                    final String matchedPattern = MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, "InstallProfile;Collection=[0-9]+");
                    if (matchedPattern != null) {
                        final String collectionId3 = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                        if (collectionId3 != null) {
                            final Long collectionID = Long.parseLong(collectionId3);
                            if (this.isModificationRequired(collectionID)) {
                                strQuery = this.modifyProfilePayloadXML(strQuery, resourceID, osVersion);
                            }
                        }
                    }
                }
                if (osVersion != null && !osVersion.matches("4.*") && !osVersion.matches("5.*") && !osVersion.matches("6.*")) {
                    this.logger.log(Level.INFO, "Going to sign Payload...");
                    final NSDictionary rootDict2 = (NSDictionary)DMSecurityUtil.parsePropertyList(strQuery.getBytes());
                    final NSDictionary commandDict2 = (NSDictionary)rootDict2.objectForKey("Command");
                    NSData nsdata = (NSData)commandDict2.objectForKey("Payload");
                    if (nsdata != null) {
                        final byte[] signedPayloadContent = PayloadSigningFactory.getInstance().signPayload(new String(nsdata.bytes(), "UTF-8"));
                        nsdata = new NSData(signedPayloadContent);
                        commandDict2.put("Payload", (NSObject)nsdata);
                        rootDict2.put("Command", (NSObject)commandDict2);
                        strQuery = rootDict2.toXMLPropertyList();
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in generateQuery -> Updating failure status for command, seq command and collection: ", ex);
            this.updateErrorStatusForResource(resourceID, commandUUID);
            strQuery = null;
        }
        return strQuery;
    }
    
    private String getPayloadContent(final NSDictionary commandDict) throws Exception {
        final NSData nsdata = (NSData)commandDict.objectForKey("Payload");
        String payloadContent = "";
        if (nsdata == null) {
            payloadContent = commandDict.toXMLPropertyList();
        }
        else {
            payloadContent = new String(nsdata.bytes(), "UTF-8");
        }
        return payloadContent;
    }
    
    private NSDictionary replacePayloadContent(final NSDictionary rootDict, final String payloadContent) throws Exception {
        NSDictionary commandDict = (NSDictionary)rootDict.objectForKey("Command");
        NSData nsdata = (NSData)commandDict.objectForKey("Payload");
        if (nsdata != null) {
            nsdata = new NSData(payloadContent.getBytes("UTF-8"));
            commandDict.put("Payload", (NSObject)nsdata);
        }
        else {
            commandDict = (NSDictionary)DMSecurityUtil.parsePropertyList(payloadContent.getBytes(StandardCharsets.UTF_8));
            rootDict.put("Command", (NSObject)commandDict);
        }
        return rootDict;
    }
    
    private String modifyProfilePayloadXML(String strQuery, final Long resourceID, final String osVersion) {
        try {
            final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(strQuery.getBytes());
            final NSDictionary commandDict = (NSDictionary)rootDict.objectForKey("Command");
            NSData nsdata = (NSData)commandDict.objectForKey("Payload");
            final String payloadContent = new String(nsdata.bytes(), "UTF-8");
            final NSDictionary payloadContentDict = (NSDictionary)DMSecurityUtil.parsePropertyList(payloadContent.getBytes());
            final NSArray payloadArray = (NSArray)payloadContentDict.objectForKey("PayloadContent");
            Integer payloadCount = payloadArray.count();
            for (int i = 0; i < payloadCount; ++i) {
                final NSDictionary payload = (NSDictionary)payloadArray.objectAtIndex(i);
                String payloadIdentifierKey = ((NSString)payload.objectForKey("PayloadIdentifier")).toString();
                payloadIdentifierKey = payloadIdentifierKey.substring(payloadIdentifierKey.lastIndexOf(".") + 1);
                if ("PPPC-payload".equalsIgnoreCase(payloadIdentifierKey)) {
                    if (osVersion.matches("10.*")) {
                        payloadArray.remove(i);
                        --i;
                        --payloadCount;
                    }
                }
                else if ("PPPC-legacy-payload".equalsIgnoreCase(payloadIdentifierKey) && !osVersion.matches("10.*")) {
                    payloadArray.remove(i);
                    --i;
                    --payloadCount;
                }
            }
            nsdata = new NSData(payloadContentDict.toXMLPropertyList().getBytes("UTF-8"));
            commandDict.put("Payload", (NSObject)nsdata);
            rootDict.put("Command", (NSObject)commandDict);
            strQuery = rootDict.toXMLPropertyList();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Profile Payload modification failed for Apple Profiles", e);
        }
        return strQuery;
    }
    
    public Boolean isModificationRequired(final Long collectionID) throws Exception {
        final Set<Integer> configIDSet = MDMConfigUtil.getConfigIdsForCollection(collectionID);
        if (configIDSet.isEmpty()) {
            return Boolean.FALSE;
        }
        configIDSet.retainAll(CommandQueryGenerator.CONFIGID_REQUIRE_MODIFICATION_IN_PAYLOAD);
        return !configIDSet.isEmpty();
    }
    
    public String getRedmptionCodeQuery(final Long resourceId, final String strIdentifier, final String strCommandUUID) {
        return PayloadHandler.getInstance().createRedemptionCodeCommand(resourceId, strIdentifier, strCommandUUID);
    }
    
    public String getQueryFromFile(final String filePath) throws IOException {
        String strQuery = null;
        FileInputStream fin = null;
        try {
            final File file = new File(filePath);
            final StringBuilder strContent = new StringBuilder("");
            try {
                fin = new FileInputStream(file);
                int ch;
                while ((ch = fin.read()) != -1) {
                    strContent.append((char)ch);
                }
                fin.close();
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception occurred readDataFromFile(){0}", e);
            }
            strQuery = strContent.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred readDataFromFile(){0}", ex);
        }
        finally {
            fin.close();
        }
        return strQuery;
    }
    
    public JSONObject constructMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    private boolean isDefaultMDMApp(final NSDictionary commandDict, final String commandUUID) {
        try {
            if (commandDict.containsKey("Configuration") && ((NSDictionary)commandDict.get((Object)"Configuration")).containsKey("Services")) {
                final Long collectionId = Long.valueOf(MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID));
                final String bundleIdentifier = AppsUtil.getInstance().getAppIdentifierFromCollection(collectionId);
                if (bundleIdentifier.equalsIgnoreCase("com.manageengine.mdm.iosagent")) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checking default mdm app", e);
        }
        return false;
    }
    
    private void updateErrorStatusForResource(final Long resourceID, final String commandUUID) {
        try {
            this.updateMdCommandToDeviceAsFailure(resourceID, commandUUID);
            this.failSequentialCommand(resourceID, commandUUID);
            this.updateCollectionAsFailure(resourceID, commandUUID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while updating command error status for resource: " + n + "command: " + s);
        }
    }
    
    private void updateCollectionAsFailure(final Long resourceID, final String commandUUID) throws SyMException {
        final String matchedPattern = MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, ";Collection=[0-9]+");
        if (matchedPattern != null) {
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            if (collectionId != null) {
                final MDMCollectionStatusUpdate collectionStatusUpdater = MDMCollectionStatusUpdate.getInstance();
                collectionStatusUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, I18N.getMsg("dc.mdm.other_error.msg", new Object[0]));
                this.logger.log(Level.WARNING, "Collection marked as failed for the device. Resource: {0}, CommandUUID: {1}", new Object[] { resourceID, commandUUID });
            }
        }
    }
    
    private void failSequentialCommand(final Long resourceID, final String commandUUID) {
        if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, commandUUID)) {
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
            this.logger.log(Level.WARNING, "Sequential command made to fail for the device. Resource: {0}, CommandUUID: {1}", new Object[] { resourceID, commandUUID });
        }
    }
    
    private void updateMdCommandToDeviceAsFailure(final Long resourceID, final String commandUUID) {
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, resourceID, 1, 7);
        this.logger.log(Level.WARNING, "Command for the device is marked as failure. Resource: {0}, CommandUUID: {1}", new Object[] { resourceID, commandUUID });
    }
    
    static {
        CommandQueryGenerator.commandQueryGenerator = null;
        PROFILE_SIGN_COMMANDTYPE = new ArrayList<String>() {
            {
                this.add("SingletonRestriction");
                this.add("RemoveSingletonRestriction");
                this.add("RemoveAffectedSingletonRestriction");
                this.add("IOSRemoveDeviceNameRestriction");
                this.add("DisablePasscode");
                this.add("RestrictPasscode");
                this.add("RemoveRestrictedPasscode");
            }
        };
        PARSE_FROM_FILE_COMMAND_TYPE = new ArrayList<String>() {
            {
                this.add("InstallProfile");
                this.add("RemoveProfile");
                this.add("InstallApplication");
                this.add("RemoveApplication");
                this.add("ApplicationConfiguration");
                this.add("KioskDefaultRestriction");
                this.add("RemoveKioskDefaultRestriction");
                this.add("ManageApplication");
                this.add("DisablePasscode");
                this.add("RemoveDisablePasscode");
                this.add("RestrictPasscode");
                this.add("InstallApplicationConfiguration");
                this.add("RemoveApplicationConfiguration");
                this.add("SharedDeviceRestrictions");
                this.add("RemoveSharedDeviceRestrictions");
            }
        };
        CONFIGID_REQUIRE_MODIFICATION_IN_PAYLOAD = new HashSet<Integer>() {
            {
                this.add(755);
                this.add(754);
            }
        };
        COMMANDQUERYCLASS = new HashMap<String, String>() {
            {
                this.put("RestrictOSUpdates", "com.me.mdm.server.command.ios.QueryGenerator.IOSRestrictOSUpdateQueryGenerator");
                this.put("RemoveRestrictOSUpdates", "com.me.mdm.server.command.ios.QueryGenerator.IOSRemoveRestOSUpdateQueryGenerator");
                this.put("AttemptOSUpdate", "com.me.mdm.server.updates.osupdates.ios.AttemptOSUpdateQueryGenerator");
                this.put("ScheduleOSUpdate", "com.me.mdm.server.updates.osupdates.ios.AttemptOSUpdateQueryGenerator");
                this.put("LockScreenMessages", "com.me.mdm.server.command.ios.QueryGenerator.LockScreenQueryGenerator");
                this.put("RemoveUserInstalledProfile", "com.me.mdm.server.command.ios.QueryGenerator.IOSRemoveUserProfileQueryGenerator");
                this.put("IOSRemoveDeviceNameRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSRemoveDeviceNameResQueryGenerator");
                this.put("SingletonRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSSingletonRestQueryGenerator");
                this.put("RemoveSingletonRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSSingletonRestQueryGenerator");
                this.put("RemoveAffectedSingletonRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSRemoveAffectedSingletonRestQueryGenerator");
                this.put("MacFirmwarePreSecurityInfo", "com.me.mdm.server.command.mac.querygenerator.firmware.MacFirmwarePreSecurityInfoGenerator");
                this.put("MacFirmwarePostSecurityInfo", "com.me.mdm.server.command.mac.querygenerator.firmware.MacFirmwarePostSecurityInfoGenerator");
                this.put("MacFirmwareSetPasscode", "com.me.mdm.server.command.mac.querygenerator.firmware.MacFirmwareSetFirmwareCommandGenerator");
                this.put("MacFirmwareClearPasscode", "com.me.mdm.server.command.mac.querygenerator.firmware.MacFirmwareClearPasswordGenerator");
                this.put("MacFirmwareVerifyPassword", "com.me.mdm.server.command.mac.querygenerator.firmware.MacFirmwareVerifyFirmwarePasswordGenerator");
                this.put("RemoveRestrictedPasscode", "com.me.mdm.server.command.ios.QueryGenerator.RemoveRestrictedPasscodeQueryGenerator");
                this.put("ClearPasscodeRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSClearPasscodeRestrictionQueryGenerator");
                this.put("FileVaultUserLoginSecurityInfo", "com.me.mdm.server.command.mac.querygenerator.filevault.MacFilevaultInventorySecurityUpdateOnUserLoginGenerator");
                this.put("MDMDefaultApplicationConfiguration", "com.me.mdm.server.command.ios.QueryGenerator.IOSDefaultAppConfigurationQueryGenerator");
                this.put("SingleWebAppKioskAppConfiguration", "com.me.mdm.server.command.ios.QueryGenerator.IOSDefaultAppConfigurationQueryGenerator");
                this.put("RemoveSingleWebAppKioskAppConfiguration", "com.me.mdm.server.command.ios.QueryGenerator.IOSDefaultAppConfigurationQueryGenerator");
                this.put("ClearPasscode", "com.me.mdm.server.command.ios.QueryGenerator.IOSClearPasscodeQueryGenerator");
                this.put("ClearPasscodeForPasscodeRestriction", "com.me.mdm.server.command.ios.QueryGenerator.IOSClearPasscodeQueryGenerator");
                this.put("DeviceInformation", "com.me.mdm.server.command.ios.QueryGenerator.IOSDeviceInformationQueryGenerator");
                this.put("Enrollment", "com.me.mdm.server.command.ios.QueryGenerator.IOSDeviceInformationQueryGenerator");
                this.put("UserList", "com.me.mdm.server.apple.command.querygenerator.AppleUserListCommandGenerator");
                this.put("SharedDeviceConfiguration", "com.me.mdm.server.apple.command.querygenerator.AppleSharedDeviceConfigurationCommandGenerator");
                this.put("LogOutUser", "com.me.mdm.server.apple.command.querygenerator.AppleLogoutUserCommandGenerator");
                this.put("MacFileVaultPersonalKeyRotate", "com.me.mdm.server.command.mac.querygenerator.filevault.MacFilevaultPersonalRecoveyKeyRotateGenerator");
                this.put(RecoveryLock.PRE_SECURITY.command, "com.me.mdm.server.command.mac.querygenerator.recoverylock.RecoveryLockPreSecurityInfoCommand");
                this.put(RecoveryLock.POST_SECURITY.command, "com.me.mdm.server.command.mac.querygenerator.recoverylock.RecoveryLockPostSecurityInfoCommand");
                this.put("SetRecoveryLock", "com.me.mdm.server.command.mac.querygenerator.recoverylock.RecoveryLockSetPasswordCommand");
                this.put(RecoveryLock.CLEAR_PASSWORD.command, "com.me.mdm.server.command.mac.querygenerator.recoverylock.RecoveryLockClearPasswordCommand");
                this.put(RecoveryLock.VERIFY_PASSWORD.command, "com.me.mdm.server.command.mac.querygenerator.recoverylock.RecoveryLockVerifyPasswordCommand");
                this.put("AppleDeviceAttestation", "com.me.mdm.server.command.ios.QueryGenerator.AppleDeviceAttestationQueryGenerator");
            }
        };
    }
}
