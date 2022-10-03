package com.me.mdm.agent.handlers.android;

import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidSecurityCommandPayloadHandler;
import com.me.mdm.server.android.knox.core.KnoxPayloadHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.agent.AndroidCommandUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.zerotrust.ZeroTrustAPIHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.server.seqcommands.android.AndroidSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class AndroidServerCommandRequestHandler extends BaseProcessDeviceRequestHandler
{
    private static Logger logger;
    private Logger accesslogger;
    public Logger seqlogger;
    private String separator;
    
    public AndroidServerCommandRequestHandler() {
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.seqlogger = Logger.getLogger("MDMSequentialCommandsLogger");
        this.separator = "\t";
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = "";
        final JSONObject jsonObject = (JSONObject)request.deviceRequestData;
        final String requestStatus = jsonObject.optString("Status", (String)null);
        final String deviceUDID = jsonObject.optString("UDID", (String)null);
        request.initDeviceRequest(deviceUDID);
        final Long resourceID = request.resourceID;
        if (resourceID != null) {
            jsonObject.put("RESOURCE_ID", (Object)resourceID);
        }
        if (requestStatus != null && requestStatus.equals("Idle")) {
            this.accesslogger.log(Level.INFO, "DEVICE-IN: IdleRequestReceived{0}{1}{2}{3}{4}IdleReceived{5}{6}", new Object[] { this.separator, resourceID, this.separator, deviceUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
            this.initServerRequest(request, request.repositoryType);
        }
        else {
            int dataQueueType = 101;
            final int agentType = jsonObject.optInt("AGENT_TYPE");
            if (agentType == 2) {
                dataQueueType = 101;
            }
            else if (agentType == 3) {
                dataQueueType = 102;
            }
            final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, String.valueOf(jsonObject.get("CommandUUID")));
            if (sequentialSubCommand != null) {
                SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 120);
                if (sequentialSubCommand.isImmidiate) {
                    this.seqlogger.log(Level.INFO, "Processing Sequential command in thread resource : {0} , Seqcommand :  {1}", new Object[] { resourceID, sequentialSubCommand.SequentialCommandID });
                    AndroidSeqCmdUtil.getInstance().processSeqCommandResponse(resourceID, String.valueOf(jsonObject.get("CommandUUID")));
                }
            }
            this.addResponseToQueue(request, jsonObject.toString(), dataQueueType);
        }
        responseData = this.getNextDeviceCommandQuery(request);
        if (responseData == null || responseData.isEmpty()) {
            this.accesslogger.log(Level.INFO, "DEVICE-OUT: TerminatingSession{0}{1}{2}{3}{4}TerminateSession{5}{6}", new Object[] { this.separator, resourceID, this.separator, deviceUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
        }
        return responseData;
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand deviceCommand, final DeviceRequest request) {
        final String command = deviceCommand.commandType;
        final String commandUUID = deviceCommand.commandUUID;
        final String deviceUDID = request.deviceUDID;
        final Long resourceID = request.resourceID;
        final Long customerID = request.customerID;
        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "getNextDeviceCommandQuery command: {0} commandUUID: {1} UDID: {2} resourceID: {3}", new Object[] { command, commandUUID, deviceUDID, resourceID });
        String strQuery = null;
        try {
            if (command.equalsIgnoreCase("AndroidInvScan")) {
                final AndroidCommandPayload invScanCommand = AndroidPayloadHandler.getInstance().createDeviceScanCommand();
                invScanCommand.setCommandUUID(commandUUID, false);
                invScanCommand.setScope(0);
                strQuery = invScanCommand.toString();
            }
            else if (command.equalsIgnoreCase("AndroidInvScanContainer")) {
                final AndroidCommandPayload invScanCommand = AndroidPayloadHandler.getInstance().createContainerScanCommand();
                invScanCommand.setCommandUUID(commandUUID, false);
                invScanCommand.setScope(1);
                strQuery = invScanCommand.toString();
            }
            else if (command.equalsIgnoreCase("AssetScan")) {
                final AndroidCommandPayload invScanCommand = AndroidPayloadHandler.getInstance().createAssetScanCommand();
                invScanCommand.setCommandUUID(commandUUID, false);
                invScanCommand.setScope(0);
                strQuery = invScanCommand.toString();
            }
            else if (command.equalsIgnoreCase("AssetScanContainer")) {
                final AndroidCommandPayload invScanCommand = AndroidPayloadHandler.getInstance().createAssetScanContainerCommand();
                invScanCommand.setCommandUUID(commandUUID, false);
                invScanCommand.setScope(1);
                strQuery = invScanCommand.toString();
            }
            else if (command.equalsIgnoreCase("DeviceLock")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createDeviceLockCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("DeviceRing")) {
                final AndroidCommandPayload createDeviceRingCommand = AndroidPayloadHandler.getInstance().createDeviceRingCommand();
                createDeviceRingCommand.setCommandUUID(commandUUID, false);
                strQuery = createDeviceRingCommand.toString();
            }
            else if (command.equalsIgnoreCase("EraseDevice")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createEraseDeviceCommand(resourceID, deviceCommand);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("ClearPasscode")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createClearPasscodeCommand(resourceID);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("ResetPasscode")) {
                final AndroidCommandPayload createResetPasscodeCommand = AndroidPayloadHandler.getInstance().createResetPasscodeCommand(resourceID);
                createResetPasscodeCommand.setCommandUUID(commandUUID);
                strQuery = createResetPasscodeCommand.toString();
            }
            else if (command.equalsIgnoreCase("CorporateWipe")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createCorporateWipeCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                final JSONObject wipeReason = ManagedDeviceHandler.getInstance().getCommandReasonJson(deviceUDID, DeviceCommandRepository.getInstance().getCommandID("CorporateWipe"));
                if (wipeReason.has("WipeReason")) {
                    final JSONObject cmdPayload = new JSONObject();
                    cmdPayload.put("ReasonForWipe", (Object)wipeReason);
                    createRestrictionsCommand.setRequestData(cmdPayload);
                }
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equals("RemoveDevice")) {
                final AndroidCommandPayload createRemoveDeviceCommand = AndroidPayloadHandler.getInstance().createRemoveDeviceCommand();
                createRemoveDeviceCommand.setCommandUUID(commandUUID, false);
                strQuery = createRemoveDeviceCommand.toString();
                DeviceCommandRepository.getInstance().deleteResourceCommand(command, deviceUDID);
            }
            else if (command.equals("GetLocation")) {
                final AndroidCommandPayload createLocationCommand = AndroidPayloadHandler.getInstance().createLocationDeviceCommand();
                createLocationCommand.setCommandUUID(commandUUID, false);
                strQuery = createLocationCommand.toString();
                DeviceCommandRepository.getInstance().deleteResourceCommand(command, deviceUDID);
            }
            else if (command.equals("InstallApplication") || command.equals("RemoveApplication") || command.equals("InstallProfile") || command.equals("RemoveProfile") || command.equals("OsUpdatePolicy") || command.equals("RemoveOsUpdatePolicy") || command.equals("InstallDataProfile") || command.equals("RemoveDataProfile") || command.equals("InstallApplicationConfiguration") || command.equals("RemoveApplicationConfiguration") || command.startsWith("InstallScheduleConfiguration") || command.startsWith("RemoveScheduleConfiguration")) {
                final String profileRepoParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String profileFullPath = profileRepoParentDir + File.separator + deviceCommand.commandFilePath;
                AndroidServerCommandRequestHandler.logger.log(Level.INFO, "generateQuery command: profileFullPath{0}: ", profileFullPath);
                if (command.equals("InstallApplication") || command.equals("InstallProfile")) {
                    final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(deviceCommand.commandFilePath);
                    strQuery = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                    if (strQuery == null) {
                        strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                        if (command.equals("InstallProfile")) {
                            strQuery = PayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(strQuery, customerID);
                        }
                        if (strQuery.length() <= MDMFileUtil.fileSizeCacheThreshold) {
                            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)strQuery, 2, (int)MDMFileUtil.fileCacheTimeTTL);
                            AndroidServerCommandRequestHandler.logger.log(Level.INFO, "File added in cache - {0}", profileFullPath);
                        }
                        else {
                            AndroidServerCommandRequestHandler.logger.log(Level.INFO, "File size greater than threshold - {0}", profileFullPath);
                        }
                    }
                    else {
                        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "{0} file read from cache - {1}", new Object[] { command, profileFullPath });
                    }
                }
                else {
                    strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                }
                if (deviceCommand.dynamicVariable) {
                    strQuery = DynamicVariableHandler.replaceDynamicVariables(strQuery, deviceUDID);
                    if (strQuery.contains("%challenge_password%")) {
                        strQuery = ThirdPartyCAUtil.replaceScepChallengePasswords(strQuery, resourceID);
                    }
                    else if (strQuery.contains("%zerotrust_password%")) {
                        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Getting san and password");
                        final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Collection id: {0}", new Object[] { collectionId });
                        final Long profileID = new ProfileHandler().getProfileIDFromCollectionID(Long.parseLong(collectionId));
                        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Profile id: {0}", new Object[] { profileID });
                        final JSONObject associatedUserJSON = ProfileUtil.getInstance().getAssociatedUserForProfile(profileID);
                        final Long associatedUserID = (Long)associatedUserJSON.get("UserID");
                        if (associatedUserID != null) {
                            AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user id : {0}", associatedUserID);
                            final JSONObject scepDetails = ZeroTrustAPIHandler.getInstance().getSANandPasswordFromZeroTrust(resourceID, associatedUserID);
                            if (scepDetails.getInt("http_response_code") == 200) {
                                strQuery = DynamicVariableHandler.replaceDynamicVariable(strQuery, "%zerotrust_password%", scepDetails.getString("ZEROTRUST_PASSWORD"));
                                strQuery = DynamicVariableHandler.replaceDynamicVariable(strQuery, "%zerotrust_san%", scepDetails.getString("ZEROTRUST_SAN"));
                            }
                            else {
                                AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Failed to get san and password");
                            }
                        }
                        else {
                            AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user not found");
                        }
                    }
                }
                else if (command.equals("InstallApplication") || command.equals("RemoveApplication")) {
                    final Long appGroupId = AppsUtil.getInstance().getAppGroupIdFormCommandId(DeviceCommandRepository.getInstance().getCommandID(commandUUID));
                    JSONObject tempJSON = new JSONObject(strQuery);
                    tempJSON = AndroidCommandUtil.getInstance().addAbsoluteUrlInAndroidInstallCommand(deviceCommand, tempJSON);
                    AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Added AbsoluteUrl in Install application command ");
                    tempJSON.put("CommandScope", (Object)((AppsUtil.getInstance().getScopeForApp(resourceID, appGroupId) == 0) ? "device" : "container"));
                    final Long collectionId2 = (Long)DBUtil.getValueFromDB("MdCollectionCommand", "COMMAND_ID", (Object)DeviceCommandRepository.getInstance().getCommandID(commandUUID), "COLLECTION_ID");
                    strQuery = tempJSON.toString();
                }
                if (command.equals("RemoveApplication")) {
                    AndroidCommandPayload createAgentUpgradeCommand = null;
                    final DeviceDetails device = new DeviceDetails(deviceUDID);
                    if (device.agentVersionCode >= 2300436L && device.agentVersionCode <= 2300446L) {
                        createAgentUpgradeCommand = AndroidPayloadHandler.getInstance().createAgentUpgradeCommand(request, device.agentType);
                        createAgentUpgradeCommand.setCommandUUID("AgentUpgrade", false);
                        final JSONObject updateData = (JSONObject)createAgentUpgradeCommand.getRequestData();
                        updateData.put("VersionCode", 2300447);
                        updateData.put("AgentDownloadURL", (Object)"https://mdmdatabase.manageengine.com/MDMAPPS/2300447/MDMAndroidAgent.apk");
                        updateData.put("VersionName", (Object)"9.2.447.A");
                        createAgentUpgradeCommand.setRequestData(updateData);
                        final Long commandId = (Long)DBUtil.getValueFromDB("MdCommands", "COMMAND_UUID", (Object)commandUUID, "COMMAND_ID");
                        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, deviceUDID, 1, 12);
                        strQuery = createAgentUpgradeCommand.toString();
                        AndroidServerCommandRequestHandler.logger.log(Level.INFO, "Device {0} in version {1} is requesting RemoveApplication, so sending back AgentUpgrade", new Object[] { resourceID, device.agentVersionCode });
                    }
                }
            }
            else if (command.equalsIgnoreCase("SyncAgentSettings")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload createAgentSettingsCommand = AndroidPayloadHandler.getInstance().createSyncAgentSettingCommand(device2);
                createAgentSettingsCommand.setCommandUUID(commandUUID, false);
                strQuery = createAgentSettingsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncDownloadSettings")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload createDownloadSettingsCommand = AndroidPayloadHandler.getInstance().createDownloadSettingsForAgentCommand(device2);
                createDownloadSettingsCommand.setCommandUUID(commandUUID, false);
                strQuery = createDownloadSettingsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncPrivacySettings")) {
                final AndroidCommandPayload createPrivacySettingsCommand = AndroidPayloadHandler.getInstance().createSyncPrivacySettingsCommand();
                createPrivacySettingsCommand.setCommandUUID(commandUUID, false);
                strQuery = createPrivacySettingsCommand.toString();
            }
            else if (command.equalsIgnoreCase("LocationConfiguration")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload locationConfigCommand = AndroidPayloadHandler.getInstance().createLocationConfigurationCommand(device2);
                locationConfigCommand.setCommandUUID(commandUUID, false);
                strQuery = locationConfigCommand.toString();
            }
            else if (command.equalsIgnoreCase("AgentUpgrade")) {
                AndroidCommandPayload createAgentUpgradeCommand2 = null;
                final DeviceDetails device3 = new DeviceDetails(deviceUDID);
                createAgentUpgradeCommand2 = AndroidPayloadHandler.getInstance().createAgentUpgradeCommand(request, device3.agentType);
                createAgentUpgradeCommand2.setCommandUUID(commandUUID, false);
                strQuery = createAgentUpgradeCommand2.toString();
            }
            else if (command.equalsIgnoreCase("BlacklistAppInDevice") || command.equalsIgnoreCase("RemoveBlacklistAppInDevice") || command.equalsIgnoreCase("BlacklistAppInContainer") || command.equalsIgnoreCase("RemoveBlacklistAppInContainer")) {
                final int scope = (!command.equalsIgnoreCase("BlacklistAppInDevice") && !command.equalsIgnoreCase("RemoveBlacklistAppInDevice")) ? 1 : 0;
                final DeviceDetails deviceDetails = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload createBlacklistWhitelistCommand = AndroidPayloadHandler.getInstance().createBlacklistWhitelistCommand(deviceDetails, "BlacklistWhitelistApp", scope);
                createBlacklistWhitelistCommand.setCommandUUID("BlacklistWhitelistApp");
                createBlacklistWhitelistCommand.setScope(scope);
                strQuery = createBlacklistWhitelistCommand.toString();
            }
            else if (command.equalsIgnoreCase("ActivateKnoxLicense")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createApplyLicensePayload(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("DeactivateKnoxLicense")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createRevokeLicensePayload(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("CreateContainer")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createCreateContainerPayload(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("RemoveContainer")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createRemoveContainerPayload();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ContainerLock")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createLockContainerPayload();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ContainerUnlock")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createUnlockContainerPayload();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ClearContainerPasscode")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createClearContainerPasswordPayload();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ActivateKnox")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createActivateKnoxCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("DeactivateKnox")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createDeactivateKnoxCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("MigrateAppToContainer")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createAppMigrationCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ServerUpgraded")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createServerUpgradedCommand();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.startsWith("GetKnoxAvailability")) {
                final AndroidCommandPayload commandPayload = KnoxPayloadHandler.getInstance().createGetKnoxAvailabilityCommand(command.substring("GetKnoxAvailability".length()));
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.startsWith("UploadAgentLogs")) {
                final AndroidCommandPayload agentLogUploadPayload = AndroidPayloadHandler.getInstance().createUploadAgentLogCommand();
                agentLogUploadPayload.setCommandUUID(commandUUID, false);
                strQuery = agentLogUploadPayload.toString();
            }
            else if (command.equalsIgnoreCase("LanguagePackUpdate")) {
                final AndroidCommandPayload langLicenseCommand = AndroidPayloadHandler.getInstance().createLanguageLicenseCommand();
                strQuery = langLicenseCommand.toString();
            }
            else if (command.equalsIgnoreCase("AgentMigrate")) {
                final AndroidCommandPayload agentMigrateCmd = AndroidPayloadHandler.getInstance().createAgentMigrateCommand();
                strQuery = agentMigrateCmd.toString();
            }
            else if (command.equalsIgnoreCase("RemoveOldAgent")) {
                final AndroidCommandPayload unManageAgentCmd = AndroidPayloadHandler.getInstance().createUnManageOldAgentCommand();
                strQuery = unManageAgentCmd.toString();
            }
            else if (command.equalsIgnoreCase("UpdateUserInfo")) {
                final AndroidCommandPayload updateUserInfoCommand = AndroidPayloadHandler.getInstance().createUpdateUserInfoCommand(resourceID, deviceUDID);
                strQuery = updateUserInfoCommand.toString();
            }
            else if (command.equalsIgnoreCase("ReregisterNotificationToken")) {
                final AndroidCommandPayload createGCMReregisterCommand = AndroidPayloadHandler.getInstance().createGCMReregisterCommand();
                createGCMReregisterCommand.setCommandUUID(commandUUID, false);
                strQuery = createGCMReregisterCommand.toString();
            }
            else if (command.equalsIgnoreCase("PersonalAppsInfo")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createPersonalAppsInfoCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("PreloadedAppsInfo")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createSystemAppScanCommand();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("PreloadedContainerAppsInfo")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createSystemAppContainerScanCommand();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("ManagedApplicationConfiguration")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createManagedAppConfigCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
                strQuery = DynamicVariableHandler.replaceDynamicVariables(strQuery, deviceUDID);
            }
            else if (command.equalsIgnoreCase("ManagedAppPermissionPolicy")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createAppPermissionPolicyCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("DeviceApproval")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createDeviceApprovalCommand(resourceID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("SyncAppCatalog")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createSyncAppCatalogCommand();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("AddAFWAccount")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createAddAFWAccountCommand(customerID, deviceUDID);
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
            else if (command.equalsIgnoreCase("DeviceInfo")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createDeviceInfoCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("EnableLostMode")) {
                final AndroidCommandPayload enableLostModeCommand = AndroidPayloadHandler.getInstance().createEnableLostModeCommand(resourceID, deviceCommand);
                enableLostModeCommand.setCommandUUID(commandUUID, false);
                strQuery = enableLostModeCommand.toString();
            }
            else if (command.equalsIgnoreCase("DisableLostMode")) {
                final AndroidCommandPayload disableLostModeCommand = AndroidPayloadHandler.getInstance().createDisableLostModeCommand(resourceID);
                disableLostModeCommand.setCommandUUID(commandUUID, false);
                strQuery = disableLostModeCommand.toString();
            }
            else if (command.equalsIgnoreCase("RemoteSession")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createRemoteSessionCommand(resourceID, customerID);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("TermsOfUse")) {
                final AndroidCommandPayload syncDocumentsCommand = AndroidPayloadHandler.getInstance().createTermsSyncCommand(resourceID);
                syncDocumentsCommand.setCommandUUID(commandUUID, false);
                strQuery = syncDocumentsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncDocuments")) {
                final AndroidCommandPayload syncDocumentsCommand = AndroidPayloadHandler.getInstance().createSyncDocumentsCommand(resourceID);
                syncDocumentsCommand.setCommandUUID(commandUUID, false);
                strQuery = syncDocumentsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SavePublicKey")) {
                final AndroidCommandPayload distributeKeysCommand = AndroidPayloadHandler.getInstance().createSmsPublicKeyCommand(customerID);
                distributeKeysCommand.setCommandUUID(commandUUID, false);
                strQuery = distributeKeysCommand.toString();
            }
            else if (command.equalsIgnoreCase("CertificateRequest")) {
                final AndroidCommandPayload distributeKeysCommand = AndroidPayloadHandler.getInstance().createServerCertificateJSON(customerID);
                distributeKeysCommand.setCommandUUID(commandUUID, false);
                strQuery = distributeKeysCommand.toString();
            }
            else if (command.startsWith("RestartDevice")) {
                final AndroidCommandPayload restartCommand = AndroidPayloadHandler.getInstance().createRestartDeviceCommand(resourceID);
                restartCommand.setCommandUUID(commandUUID, false);
                strQuery = restartCommand.toString();
            }
            else if (command.equalsIgnoreCase("PauseKioskCommand")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createPauseKioskCommand(resourceID);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("ResumeKioskCommand")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createResumeKioskCommand(resourceID);
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SecurityInfo")) {
                final AndroidCommandPayload securityInfoCommand = AndroidPayloadHandler.getInstance().createSecurityInfoCommand();
                securityInfoCommand.setCommandUUID(commandUUID, false);
                securityInfoCommand.setScope(0);
                strQuery = securityInfoCommand.toString();
            }
            else if (command.equalsIgnoreCase("CapabilitiesInfo")) {
                final AndroidCommandPayload capabilitiesInfoCommand = AndroidPayloadHandler.getInstance().createCapabilitiesInfoCommand();
                capabilitiesInfoCommand.setCommandUUID(commandUUID, false);
                capabilitiesInfoCommand.setScope(0);
                strQuery = capabilitiesInfoCommand.toString();
            }
            else if (command.equalsIgnoreCase("DeviceCompliance") || command.equalsIgnoreCase("RemoveDeviceCompliance")) {
                final String profileParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String complianceFullPath = profileParentDir + File.separator + deviceCommand.commandFilePath;
                AndroidServerCommandRequestHandler.logger.log(Level.INFO, " generateQuery Command: complianceFullPath:  ", complianceFullPath);
                strQuery = PayloadHandler.getInstance().readProfileFromFile(complianceFullPath);
            }
            else if (command.equalsIgnoreCase("RemoteDebug")) {
                final AndroidCommandPayload remoteDebugCommand = AndroidPayloadHandler.getInstance().createRemoteDebugCommand(resourceID);
                remoteDebugCommand.setCommandUUID(commandUUID);
                strQuery = remoteDebugCommand.toString();
            }
            else if (command.equalsIgnoreCase("BATTERY_CONFIGURATION")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload batteryCommand = AndroidPayloadHandler.getInstance().createBatteryConfigurationCommand(device2);
                batteryCommand.setCommandUUID(commandUUID);
                strQuery = batteryCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncAnnouncement")) {
                final AndroidCommandPayload syncDocumentsCommand = AndroidPayloadHandler.getInstance().createSyncAnnouncementCommand(resourceID);
                syncDocumentsCommand.setCommandUUID(commandUUID, false);
                strQuery = syncDocumentsCommand.toString();
            }
            else if (command.equalsIgnoreCase("ClearAppData")) {
                final AndroidCommandPayload clearAppDataCommandPayload = AndroidSecurityCommandPayloadHandler.getInstance().createClearAppDataCommandPayload(resourceID);
                clearAppDataCommandPayload.setCommandUUID(commandUUID);
                strQuery = clearAppDataCommandPayload.toString();
            }
            else if (command.equalsIgnoreCase("MigrateUrl")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload migrationCommand = AndroidPayloadHandler.getInstance().createUrlMigrationCommand(device2.resourceId);
                migrationCommand.setCommandUUID(commandUUID);
                strQuery = migrationCommand.toString();
            }
            else if (command.equalsIgnoreCase("AndroidPasscodeRecoveryCommand")) {
                final DeviceDetails deviceDetails2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload androidPasscodeRecoveryCommand = AndroidPayloadHandler.getInstance().createAndroidPasscodeRecoveryCommand(deviceDetails2);
                androidPasscodeRecoveryCommand.setCommandUUID(commandUUID);
                strQuery = androidPasscodeRecoveryCommand.toString();
            }
            else if (command.equals("DetectUserGSuiteAccount")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createGSuiteAccountDetectCommand();
                commandPayload.setCommandUUID(commandUUID, false);
                strQuery = commandPayload.toString();
            }
        }
        catch (final Exception e) {
            AndroidServerCommandRequestHandler.logger.log(Level.SEVERE, "Exception while creating android command", e);
        }
        return strQuery;
    }
    
    static {
        AndroidServerCommandRequestHandler.logger = Logger.getLogger("MDMLogger");
    }
}
