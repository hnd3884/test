package com.me.mdm.agent.handlers.chromeos;

import com.adventnet.sym.server.mdm.chrome.payload.ChromeCommandPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import com.adventnet.sym.server.mdm.chrome.payload.ChromeSecurityCommandPayloadHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
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

public class ChromeServerCommandRequestHandler extends BaseProcessDeviceRequestHandler
{
    private Logger logger;
    private Logger accesslogger;
    public Logger seqlogger;
    private String separator;
    
    public ChromeServerCommandRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.seqlogger = Logger.getLogger("MDMSequentialCommandsLogger");
        this.separator = "\t";
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
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
            final int dataQueueType = 106;
            if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, String.valueOf(jsonObject.get("CommandUUID")))) {
                final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
                SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 120);
                if (SeqCmdUtils.getInstance().isSequentialCommandProcessImmediately(sequentialSubCommand.SequentialCommandID)) {
                    this.seqlogger.log(Level.INFO, "Processing Sequential command in thread resource : {0} , Seqcommand :  {1}", new Object[] { resourceID, sequentialSubCommand.SequentialCommandID });
                    AndroidSeqCmdUtil.getInstance().processSeqCommandResponse(resourceID, String.valueOf(jsonObject.get("CommandUUID")));
                }
            }
            this.addResponseToQueue(request, jsonObject.toString(), dataQueueType);
        }
        final String responseData = this.getNextDeviceCommandQuery(request);
        if (responseData == null || responseData.isEmpty()) {
            this.accesslogger.log(Level.INFO, "DEVICE-OUT: TerminatingSession {0} {1} {2} {3} {4} TerminateSession {5} {6}", new Object[] { this.separator, resourceID, this.separator, deviceUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
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
        this.logger.log(Level.INFO, "getNextDeviceCommandQuery command: {0} commandUUID: {1} UDID: {2} resourceID: {3}", new Object[] { command, commandUUID, deviceUDID, resourceID });
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
            else if (command.equals("InstallApplication") || command.equals("RemoveApplication") || command.equals("InstallProfile") || command.equals("RemoveProfile") || command.equals("ChromeOsUpdatePolicy") || command.equals("RemoveChromeOsUpdatePolicy")) {
                final String profileParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String profileFullPath = profileParentDir + File.separator + deviceCommand.commandFilePath;
                this.logger.log(Level.INFO, "generateQuery command: profileFullPath{0}: ", profileFullPath);
                if (command.equals("InstallProfile")) {
                    final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(deviceCommand.commandFilePath);
                    strQuery = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                    if (strQuery == null) {
                        strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                        strQuery = PayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(strQuery, customerID);
                        if (strQuery.length() <= MDMFileUtil.fileSizeCacheThreshold) {
                            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)strQuery, 2, (int)MDMFileUtil.fileCacheTimeTTL);
                            this.logger.log(Level.INFO, "File added in cache - {0}", profileFullPath);
                        }
                        else {
                            this.logger.log(Level.INFO, "File size greater than threshold - {0}", profileFullPath);
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "{0} file read from cache - {1}", new Object[] { command, profileFullPath });
                    }
                }
                else {
                    strQuery = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                }
                if (deviceCommand.dynamicVariable) {
                    strQuery = DynamicVariableHandler.replaceDynamicVariables(strQuery, deviceUDID);
                }
                else if (command.equals("InstallApplication") || command.equals("RemoveApplication")) {
                    final Long appGroupId = AppsUtil.getInstance().getAppGroupIdFormCommandId(DeviceCommandRepository.getInstance().getCommandID(commandUUID));
                    final JSONObject tempJSON = new JSONObject(strQuery);
                    tempJSON.put("CommandScope", (Object)((AppsUtil.getInstance().getScopeForApp(resourceID, appGroupId) == 0) ? "device" : "container"));
                    strQuery = tempJSON.toString();
                }
            }
            else if (command.equalsIgnoreCase("SyncAgentSettings")) {
                final DeviceDetails device = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload createAgentSettingsCommand = AndroidPayloadHandler.getInstance().createSyncAgentSettingCommand(device);
                createAgentSettingsCommand.setCommandUUID(commandUUID, false);
                strQuery = createAgentSettingsCommand.toString();
            }
            else if (command.equalsIgnoreCase("LocationConfiguration")) {
                final DeviceDetails device = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload locationConfigCommand = AndroidPayloadHandler.getInstance().createLocationConfigurationCommand(device);
                locationConfigCommand.setCommandUUID(commandUUID, false);
                strQuery = locationConfigCommand.toString();
            }
            else if (command.equalsIgnoreCase("AgentUpgrade")) {
                final DeviceDetails device2 = new DeviceDetails(deviceUDID);
                final AndroidCommandPayload createAgentUpgradeCommand = AndroidPayloadHandler.getInstance().createAgentUpgradeCommand(request, device2.agentType);
                createAgentUpgradeCommand.setCommandUUID(commandUUID, false);
                strQuery = createAgentUpgradeCommand.toString();
            }
            else if (command.equalsIgnoreCase("ServerUpgraded")) {
                final AndroidCommandPayload commandPayload = AndroidPayloadHandler.getInstance().createServerUpgradedCommand();
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
            else if (command.equalsIgnoreCase("DeviceInfo")) {
                final AndroidCommandPayload createRestrictionsCommand = AndroidPayloadHandler.getInstance().createDeviceInfoCommand();
                createRestrictionsCommand.setCommandUUID(commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else if (command.equalsIgnoreCase("EnableLostMode")) {
                final ChromeCommandPayload enableLostModeCommand = ChromeSecurityCommandPayloadHandler.getInstance().createEnableLostModeCommand(resourceID);
                enableLostModeCommand.setCommandUUID(commandUUID, false);
                strQuery = enableLostModeCommand.toString();
            }
            else if (command.equalsIgnoreCase("DisableLostMode")) {
                final ChromeCommandPayload disableLostModeCommand = ChromeSecurityCommandPayloadHandler.getInstance().createDisableLostModeCommand(resourceID);
                disableLostModeCommand.setCommandUUID(commandUUID, false);
                strQuery = disableLostModeCommand.toString();
            }
            else if (command.equalsIgnoreCase("SavePublicKey")) {
                final AndroidCommandPayload distributeKeysCommand = AndroidPayloadHandler.getInstance().createSmsPublicKeyCommand(customerID);
                distributeKeysCommand.setCommandUUID(commandUUID, false);
                strQuery = distributeKeysCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncPrivacySettings")) {
                final ChromeCommandPayload createPrivacySettingsCommand = ChromePayloadHandler.getInstance().createPrivacySettingsCommand(customerID);
                createPrivacySettingsCommand.setCommandUUID(commandUUID, false);
                strQuery = createPrivacySettingsCommand.toString();
            }
            else if (command.startsWith("RestartDevice")) {
                final ChromeCommandPayload restartCommand = ChromeSecurityCommandPayloadHandler.getInstance().createRestartDeviceCommand(resourceID);
                restartCommand.setCommandUUID(commandUUID, false);
                strQuery = restartCommand.toString();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating android command", e);
        }
        return strQuery;
    }
}
