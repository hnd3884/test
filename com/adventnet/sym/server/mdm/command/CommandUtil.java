package com.adventnet.sym.server.mdm.command;

import java.util.Hashtable;
import com.me.mdm.server.security.mac.recoverylock.RecoveryLock;
import com.me.mdm.server.updates.osupdates.ios.IOSUpdateCommandQueryGenerator;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.StringTokenizer;
import com.dd.plist.NSObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.server.command.CommandResponseProcessor;
import com.dd.plist.NSArray;
import com.me.mdm.agent.handlers.android.servletmigration.AndroidServletMigrationUtil;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import com.me.mdm.server.compliance.ComplianceHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.me.mdm.server.updates.osupdates.ios.AvailableUpdatesResponseProcessor;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.updates.osupdates.ios.ScheduleOSUpdateResponseProcessor;
import com.me.mdm.server.command.ios.MGSettingCmdResponseProcessor;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import java.util.Map;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.location.lostmode.LostModeCommandResponseProcessor;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import com.me.mdm.server.apps.blacklist.ios.IOSBlacklistAppProcessor;
import com.me.mdm.server.apps.blacklist.android.AndroidBlacklistProcessor;
import com.adventnet.sym.server.mdm.enroll.BaseRemoveDeviceHandler;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.me.mdm.agent.handlers.ios.IOSMigrationUtil;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.profiles.MDMProfileResponseListenerHandler;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.apps.ManagedAppStatusHandler;
import com.me.mdm.server.acp.IOSAppCatalogHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.me.mdm.server.apps.IOSInstallApplicationResponseProcessor;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import java.util.Properties;
import com.me.mdm.server.inv.ios.AppleDeviceLockHandler;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import java.util.List;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import java.util.Arrays;
import java.util.Collections;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import org.json.JSONException;
import com.me.mdm.server.android.knox.enroll.KnoxActivationManager;
import com.adventnet.sym.server.mdm.inv.android.DeviceDetailsMDMInventoryImpl;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import org.json.JSONArray;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.samsung.SamsungInventory;
import com.adventnet.sym.server.mdm.android.AndroidInventory;
import com.me.mdm.server.deviceaccounts.AccountConfigResponseProcessor;
import com.me.mdm.server.apps.MacManagedAppListResponseProcessor;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.adventnet.sym.server.mdm.inv.MDMInvDataHandler;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.command.CommandStatusHandler;
import com.me.mdm.server.inv.ios.DeviceAttestation.DeviceAttestationHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.android.AndroidSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class CommandUtil
{
    private static CommandUtil commandUtil;
    public static final long SCH_MANAGED_APP_LIST_COMMAND_TIME = 180000L;
    Logger logger;
    Logger accesslogger;
    String separator;
    private static final HashMap<String, String> ERROR_CODES_AND_REMARKS;
    private static final HashMap<String, String> GET_CLASS_FOR_PROCESSING;
    
    public static Long getCommandIDFromCommandUUID(final String commandUUID) throws Exception {
        return (Long)DBUtil.getValueFromDB("MdCommands", "COMMAND_UUID", (Object)commandUUID, "COMMAND_ID");
    }
    
    public CommandUtil() {
        this.logger = Logger.getLogger("MDMLogger");
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.separator = "\t";
    }
    
    public static CommandUtil getInstance() {
        if (CommandUtil.commandUtil == null) {
            CommandUtil.commandUtil = new CommandUtil();
        }
        return CommandUtil.commandUtil;
    }
    
    public void processCommand(String strData, final Long customerId, final HashMap hashMap, final Integer queueDataType, final DCQueueData dcqueueData) {
        String deviceCommand = null;
        String collectionId = null;
        String remarks = null;
        Integer errorCode = null;
        Long resourceID = null;
        Long enrollmentRequestId = null;
        JSONObject cmdParams = new JSONObject();
        final String strUDID = hashMap.get("UDID");
        String strCommandUuid = hashMap.get("CommandUUID");
        final String strStatus = hashMap.get("Status");
        final String strState = hashMap.get("State");
        boolean isAppleConfig = false;
        final Long startTime = System.currentTimeMillis();
        String fileName;
        if (dcqueueData == null) {
            fileName = "EnrollmentData";
        }
        else {
            fileName = dcqueueData.fileName;
        }
        this.logger.log(Level.INFO, "processCommand: processing data -> strUDID: {0} strCommandUuid: {1} fileName: {2} strStatus: {3}", new Object[] { strUDID, strCommandUuid, fileName, strStatus });
        try {
            final List valueList = MDMUtil.getInstance().getStringList(strCommandUuid, ";");
            deviceCommand = valueList.get(0);
            if (strStatus.equalsIgnoreCase("Error") || strStatus.equalsIgnoreCase("CommandFormatError")) {
                if (queueDataType == 100) {
                    if (!deviceCommand.equalsIgnoreCase("InstallApplication")) {
                        final IOSErrorStatusHandler errorHandler = new IOSErrorStatusHandler();
                        final JSONObject errorHash = errorHandler.getIOSErrors(strUDID, strData, strStatus);
                        remarks = errorHash.optString("EnglishRemarks", "mdm.content.gen.error");
                        if (errorHash.has("ErrorCode")) {
                            errorCode = Integer.parseInt((String)errorHash.get("ErrorCode"));
                        }
                    }
                }
                else {
                    remarks = hashMap.get("ErrorMsg");
                    final String errorCodeStr = hashMap.get("ErrorCode");
                    errorCode = Integer.parseInt((errorCodeStr == null) ? "-1" : errorCodeStr);
                }
            }
            Long commandId = DeviceCommandRepository.getInstance().getCommandID(strCommandUuid);
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().getCommandID(deviceCommand);
            }
            this.logger.log(Level.INFO, "processCommand: processing data -> deviceCommand: {0}", deviceCommand);
            resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            if (resourceID == null && !deviceCommand.equals("Enrollment")) {
                this.logger.log(Level.INFO, "Returning since Resouce ID is null for the UDID: {0}", strUDID);
                return;
            }
            this.logger.log(Level.INFO, "ProcessData() -- resourceID ->{0}", resourceID);
            Long postTime;
            if (dcqueueData == null) {
                postTime = System.currentTimeMillis();
            }
            else {
                postTime = dcqueueData.postTime;
            }
            final String accessMessage = "DATA-IN: " + strCommandUuid + this.separator + resourceID + this.separator + strUDID + this.separator + strStatus + this.separator + postTime + this.separator + (System.currentTimeMillis() - postTime);
            this.accesslogger.log(Level.INFO, accessMessage);
            final int platformType = this.getPlatformFromQueueType(queueDataType);
            if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid) && platformType == 2) {
                AndroidSeqCmdUtil.getInstance().processSeqCommandResponse(hashMap);
            }
            final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
            final int queueType = (dcqueueData == null) ? 1 : dcqueueData.queueDataType;
            int commandRepositoryType = 1;
            if (queueType == 1) {
                commandRepositoryType = 1;
            }
            else if (queueType == 140) {
                commandRepositoryType = 2;
            }
            if (strStatus.equalsIgnoreCase("NotNow")) {
                this.logger.log(Level.INFO, "-------------------------------------------------------");
                this.logger.log(Level.INFO, "-- Received \"NotNow\" Status from the resource: {0} for the Command: {1}", new Object[] { resourceID, deviceCommand });
                this.logger.log(Level.INFO, "-------------------------------------------------------");
                final String scope = hashMap.get("CommandScope");
                int scopeInt = 0;
                if (scope != null) {
                    scopeInt = (scope.equalsIgnoreCase("container") ? 1 : 0);
                }
                if (strCommandUuid.contains("DefaultWebClipsPayload")) {
                    commandId = DeviceCommandRepository.getInstance().getCommandID("DefaultAppCatalogWebClips");
                }
                if (deviceCommand.equals("Enrollment")) {
                    DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, strUDID, commandRepositoryType, 12);
                }
                else {
                    DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, resourceID, commandRepositoryType, 12);
                }
                if (!strCommandUuid.contains("DefaultWebClipsPayload") && (deviceCommand.equals("InstallProfile") || deviceCommand.equals("KioskDefaultRestriction") || deviceCommand.equals("RestrictPasscode") || deviceCommand.equals("DisablePasscode")) && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5") && !deviceCommand.equals("Enrollment") && !deviceCommand.equals("DeviceInformation") && !deviceCommand.equals("InstalledApplicationList") && !deviceCommand.equals("FetchAppleAgentDetails")) {
                    collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                    remarks = "dc.db.mdm.collection.waiting_for_device_unlock";
                    remarks = "dc.db.mdm.collection.waiting_for_device_unlock";
                    if (platformType == 2 && hashMap.containsKey("Remarks")) {
                        remarks = hashMap.get("Remarks");
                        if (collectionId != null && remarks.indexOf("@@@") != -1) {
                            remarks = this.constructKioskNotNowMsg(Long.valueOf(collectionId), remarks, customerId);
                        }
                    }
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 16, remarks);
                }
                else if (deviceCommand.equalsIgnoreCase("SingletonRestriction") || deviceCommand.equalsIgnoreCase("RemoveSingletonRestriction") || deviceCommand.equalsIgnoreCase("RemoveRestrictedPasscode") || deviceCommand.equalsIgnoreCase("RemoveRestrictedPasscode")) {
                    collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                    remarks = "mdm.collection.waiting_for_device_unlock_remove";
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 16, remarks);
                }
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final JSONObject response = new JSONObject();
                    response.put("resourceID", (Object)resourceID);
                    response.put("action", 5);
                    response.put("commandUUID", (Object)strCommandUuid);
                    final JSONObject params = new JSONObject();
                    response.put("params", (Object)params);
                    SeqCmdRepository.getInstance().processSeqCommand(response);
                }
            }
            else if (deviceCommand.equals("DeviceInformation")) {
                final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("QueryResponses", strData);
                HashMap hsmap = new HashMap();
                hsmap = PlistWrapper.getInstance().getHashFromDict(nsDict);
                if (hsmap.containsKey("ServiceSubscriptions")) {
                    hsmap.replace("ServiceSubscriptions", this.getServiceSubscriptionsMap(nsDict));
                }
                if (customerId != null) {
                    hsmap.put("CUSTOMER_ID", customerId);
                }
                this.logger.log(Level.FINE, "ProcessData() -- DeviceInformation ->{0}", hsmap);
                MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(resourceID);
                int commandStatus;
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    remarks = "mdm.scan.scanning_successful";
                    commandStatus = 2;
                    hsmap.put("DEVICE_LOCAL_TIME", postTime);
                    MDMInvDataPopulator.getInstance().updateIOSInventory(resourceID, hsmap, nsDict);
                    MDMInvDataPopulator.getInstance().updateDeviceName(resourceID, hsmap, 1);
                    final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                    if (deviceDetails.modelType == 1 || deviceDetails.modelType == 2) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(dcqueueData, 18);
                        }
                    }
                    new DeviceAttestationHandler().verifyDeviceAttestation(hsmap, resourceID);
                }
                else {
                    MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resourceID, errorCode);
                    commandStatus = 0;
                }
                MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, commandStatus, remarks);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                final String sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                if (strCommandUuid.contains("USER_INVOKED")) {
                    final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    if (commandStatusJSON.has("ADDED_BY")) {
                        final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                        commandStatusJSON.put("COMMAND_STATUS", commandStatus);
                        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
                        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, userName, "dc.mdm.actionlog.inv.device_scan_success", sDeviceName, customerId);
                    }
                }
                new ResourceOSUpdateDataHandler().deleteDeviceAvailableUpdate(resourceID, hsmap.get("OSVersion"));
            }
            else if (platformType == 2 && deviceCommand.equals("SecurityInfo")) {
                final MDMInvDataHandler androidInventory = new MDMInvDataHandler();
                try {
                    final JSONObject jsonObject = new JSONObject(strData);
                    jsonObject.getJSONObject("ResponseData").getJSONObject("DeviceDetails").put("DEVICE_LOCAL_TIME", (Object)postTime);
                    strData = jsonObject.toString();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception while adding device local time for battery tracking", e);
                }
                final Map<String, String> parsedData = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
                androidInventory.mdmInventoryDataPopulator(resourceID, parsedData, 0);
            }
            else if (deviceCommand.equals("InstalledApplicationList") || deviceCommand.equals("FetchAppleAgentDetails")) {
                final Boolean isFetchAgentDetailsCommand = strCommandUuid.contains("FetchAppleAgentDetails");
                final NSArray nsArr = PlistWrapper.getInstance().getArrayForKey("InstalledApplicationList", strData);
                MDMInvDataPopulator.getInstance().processSoftwares(resourceID, nsArr, isFetchAgentDetailsCommand);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                final JSONObject params2 = new JSONObject();
                params2.put("resourceId", (Object)resourceID);
                params2.put("strCommandUuid", (Object)strCommandUuid);
                params2.put("strStatus", (Object)strStatus);
                params2.put("strData", (Object)strData);
                params2.put("customerId", (Object)customerId);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final CommandResponseProcessor.SeqQueuedResponseProcessor processor = this.getInstanceForSeqQueueResponse(deviceCommand);
                    processor.processSeqQueuedCommand(params2);
                }
                final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
                final int modelType = deviceMap.get("MODEL_TYPE");
                final String osVersion = deviceMap.get("OS_VERSION");
                if ((modelType == 3 || modelType == 4) && !ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.16f) && !osVersion.equals("11.0") && !new VersionChecker().isGreater(osVersion, "11.0")) {
                    new MacManagedAppListResponseProcessor().processQueuedCommand(params2);
                }
            }
            else if (deviceCommand.equals("ManagedApplicationList")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
                    if (!SeqCmdUtils.getInstance().isSequentialCommandProcessImmediately(sequentialSubCommand.SequentialCommandID)) {
                        final CommandResponseProcessor.SeqQueuedResponseProcessor processor2 = this.getInstanceForSeqQueueResponse(deviceCommand);
                        processor2.processSeqQueuedCommand(params3);
                    }
                }
                else {
                    final CommandResponseProcessor.QueuedResponseProcessor processor3 = this.getInstanseForQueueResponse(deviceCommand);
                    processor3.processQueuedCommand(params3);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.contains("InstallAgentID=") || deviceCommand.equals("InstallEnterpriseApplication")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                final CommandResponseProcessor.QueuedResponseProcessor processor3 = this.getInstanseForQueueResponse("InstallEnterpriseApplication");
                processor3.processQueuedCommand(params3);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("AccountConfiguration")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                final CommandResponseProcessor.QueuedResponseProcessor processor3 = new AccountConfigResponseProcessor();
                processor3.processQueuedCommand(params3);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("CertificateList")) {
                final NSArray nsArr2 = PlistWrapper.getInstance().getArrayForKey("CertificateList", strData);
                MDMInvDataPopulator.getInstance().processCertificates(resourceID, nsArr2);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("AndroidInvScan") || deviceCommand.equals("AndroidInvScanContainer")) {
                final int scopeInt2 = 0;
                int cmdStatus = -1;
                String remark = "";
                if (strStatus.equalsIgnoreCase("Error")) {
                    if (errorCode == 12070L) {
                        remark = "dc.db.mdm.remarks.api_not_compaitable";
                    }
                    else if (errorCode == 12115L) {
                        remark = "dc.db.mdm.remarks.api_not_compaitable";
                    }
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, 0, remark);
                    MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resourceID, 12202);
                    cmdStatus = 0;
                }
                else {
                    int scanFlag = 2;
                    cmdStatus = 2;
                    String scope2 = hashMap.get("CommandScope");
                    String scanRemarks = "mdm.scan.scanning_successful";
                    MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(resourceID);
                    if (queueDataType == 101) {
                        AndroidInventory.getInstance().updateAndroidInventory(strData, resourceID);
                        MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, scanFlag, scanRemarks);
                        final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                        if (deviceDetails2.modelType == 1 || deviceDetails2.modelType == 2) {
                            CustomerInfoUtil.getInstance();
                            if (!CustomerInfoUtil.isSAS()) {
                                MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(dcqueueData, 19);
                            }
                        }
                    }
                    else if (queueDataType == 102) {
                        scope2 = hashMap.get("CommandScope");
                        final SamsungInventory samsungInventory = SamsungInventory.getSamsungInventoryInstance(hashMap.get("CommandScope"));
                        samsungInventory.parseInventoryData(resourceID, strData);
                        if (!samsungInventory.isDataProcessSuccess()) {
                            scanFlag = 0;
                            scanRemarks = "dc.db.mdm.scan.remarks.fetch_data_failed";
                            MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, scanFlag, scanRemarks);
                            MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resourceID, 12203);
                            cmdStatus = 0;
                            errorCode = 12203;
                            remark = scanRemarks;
                        }
                        else if (KnoxUtil.getInstance().doesContainerActive(resourceID) && scope2 != null && !scope2.equalsIgnoreCase("container")) {
                            this.logger.log(Level.INFO, " ");
                        }
                        else {
                            MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, scanFlag, scanRemarks);
                            final DeviceDetails deviceDetails3 = new DeviceDetails(resourceID);
                            if (deviceDetails3.modelType == 1 || deviceDetails3.modelType == 2) {
                                CustomerInfoUtil.getInstance();
                                if (!CustomerInfoUtil.isSAS()) {
                                    MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(dcqueueData, 19);
                                }
                            }
                        }
                    }
                    DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                    remark = "dc.mdm.actionlog.inv.device_scan_success";
                }
                if (strCommandUuid.contains("USER_INVOKED")) {
                    final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    if (commandStatusJSON.has("ADDED_BY")) {
                        commandStatusJSON.put("COMMAND_STATUS", cmdStatus);
                        if (errorCode != null && errorCode != -1) {
                            commandStatusJSON.put("ERROR_CODE", (Object)errorCode);
                        }
                        final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                        commandStatusJSON.put("REMARKS", (Object)remark);
                        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
                        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                        final String sDeviceName2 = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, userName, remark, sDeviceName2, customerId);
                    }
                }
            }
            else if (deviceCommand.equals("AssetScan") || deviceCommand.equals("AssetScanContainer")) {
                int scopeInt2 = 0;
                int cmdStatus = -1;
                String remark = "";
                if (strStatus.equalsIgnoreCase("Error")) {
                    if (errorCode == 12070L) {
                        remark = "dc.db.mdm.remarks.api_not_compaitable";
                    }
                    else if (errorCode == 12115L) {
                        remark = "dc.db.mdm.remarks.api_not_compaitable";
                    }
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, 0, remark);
                    MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resourceID, 12202);
                    cmdStatus = 0;
                }
                else {
                    int scanFlag = 2;
                    String scope2 = hashMap.get("CommandScope");
                    String scanRemarks = "mdm.scan.scanning_successful";
                    MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(resourceID);
                    scope2 = hashMap.get("CommandScope");
                    if (scope2 != null) {
                        scopeInt2 = (scope2.equalsIgnoreCase("container") ? 1 : 0);
                    }
                    final MDMInvDataHandler androidInventory2 = new MDMInvDataHandler();
                    try {
                        final JSONObject jsonObject2 = new JSONObject(strData);
                        jsonObject2.getJSONObject("ResponseData").getJSONObject("DeviceDetails").put("DEVICE_LOCAL_TIME", (Object)postTime);
                        strData = jsonObject2.toString();
                    }
                    catch (final Exception e2) {
                        this.logger.log(Level.WARNING, "Exception while adding device time for battery level tracking ", e2);
                    }
                    final Map<String, String> parsedData2 = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
                    final boolean isPopulationSuccess = androidInventory2.mdmInventoryDataPopulator(resourceID, parsedData2, scopeInt2);
                    if (!isPopulationSuccess) {
                        scanFlag = 0;
                        scanRemarks = "dc.db.mdm.scan.remarks.fetch_data_failed";
                        MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, scanFlag, scanRemarks);
                        MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(resourceID, 12203);
                        errorCode = 12203;
                        cmdStatus = 0;
                    }
                    else {
                        if (KnoxUtil.getInstance().doesContainerActive(resourceID) && scope2 != null && !scope2.equalsIgnoreCase("container")) {
                            this.logger.log(Level.INFO, " ");
                        }
                        else {
                            MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, scanFlag, scanRemarks);
                            final DeviceDetails deviceDetails4 = new DeviceDetails(resourceID);
                            if (deviceDetails4.modelType == 1 || deviceDetails4.modelType == 2) {
                                CustomerInfoUtil.getInstance();
                                if (!CustomerInfoUtil.isSAS()) {
                                    MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(dcqueueData, 19);
                                }
                            }
                        }
                        cmdStatus = 2;
                    }
                    remark = "dc.mdm.actionlog.inv.device_scan_success";
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                final String sDeviceName3 = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                if (strCommandUuid.contains("USER_INVOKED")) {
                    final JSONObject commandStatusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    if (commandStatusJSON2.has("ADDED_BY")) {
                        commandStatusJSON2.put("COMMAND_ID", (Object)commandId);
                        commandStatusJSON2.put("COMMAND_STATUS", cmdStatus);
                        commandStatusJSON2.put("RESOURCE_ID", (Object)resourceID);
                        if (errorCode != null && errorCode != -1) {
                            commandStatusJSON2.put("ERROR_CODE", (Object)errorCode);
                        }
                        commandStatusJSON2.put("COMMAND_ID", (Object)commandId);
                        commandStatusJSON2.put("RESOURCE_ID", (Object)resourceID);
                        new CommandStatusHandler().populateCommandStatus(commandStatusJSON2);
                        final String userName2 = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON2, "ADDED_BY", Long.valueOf(-1L)));
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, userName2, remark, sDeviceName3, customerId);
                    }
                }
            }
            else if (deviceCommand.equalsIgnoreCase("PreloadedAppsInfo")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                Integer scope3 = 0;
                if (ManagedDeviceHandler.getInstance().isProfileOwner(resourceID)) {
                    scope3 = 1;
                }
                SamsungInventory.getSamsungInventoryInstance(null).handleSystemApps(resourceID, strData, scope3, 2);
            }
            else if (deviceCommand.equalsIgnoreCase("PreloadedContainerAppsInfo")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                SamsungInventory.getSamsungInventoryInstance(null).handleSystemApps(resourceID, strData, 1, 2);
            }
            else if (deviceCommand.equals("PersonalAppsInfo")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                final JSONObject personalAppCmdResp = new JSONObject(strData);
                final String personalAppResp = String.valueOf(personalAppCmdResp.get("ResponseData"));
                final JSONObject perosnalApps = new JSONObject(personalAppResp);
                final JSONObject systemAppsInf = new JSONObject(String.valueOf(perosnalApps.get("SoftwareDetails")));
                final JSONArray appListArray = new JSONArray(String.valueOf(systemAppsInf.get("AppList")));
                final AppDataHandler appHandler = new AppDataHandler();
                final String UUID = hashMap.get("UDID");
                final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UUID);
                appHandler.processAndroidSoftwares(resourceId, customerId, appListArray, 0, 1);
            }
            else if (deviceCommand.equals("DeviceInfo")) {
                final JSONObject resData = new JSONObject(strData);
                JSONObject deviceInfoData = resData.getJSONObject("ResponseData");
                deviceInfoData = deviceInfoData.getJSONObject("DeviceDetails");
                deviceInfoData.put("DEVICE_LOCAL_TIME", (Object)postTime);
                final MDMInvdetails invdetails = new MDMInvdetails(resourceID, deviceInfoData.toString());
                new DeviceDetailsMDMInventoryImpl().populateInventoryData(invdetails);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.startsWith("GetKnoxAvailability")) {
                final JSONObject data = new JSONObject(strData);
                try {
                    final JSONObject respData = data.getJSONObject("ResponseData");
                    if (respData.optBoolean("isKnoxAvailable", false)) {
                        KnoxActivationManager.getInstance().processIamKnoxMsg(hashMap);
                    }
                    else {
                        this.logger.log(Level.INFO, "KNOX Not available in Resource :{0}", resourceID);
                    }
                }
                catch (final JSONException e3) {
                    this.logger.log(Level.INFO, "KNOX Not available in Resource :{0}", resourceID);
                }
                final String UUID2 = hashMap.get("UDID");
                final Long resourceId2 = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UUID2);
                KnoxActivationManager.getInstance().deleteKNOXToEnrollmentRel(resourceId2);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (platformType == 2 && deviceCommand.equals("InstallScheduleConfiguration")) {
                final Long collectionID = ScheduledActionsUtils.getCollectionIDFromCommandID(commandId);
                if (collectionID != null) {
                    if (strStatus.equalsIgnoreCase("Acknowledged")) {
                        remarks = "dc.mdm.general.command.initiated";
                        GroupActionScheduleUtils.updateCommandHistoryStatus(Collections.singletonList(collectionID), Collections.singletonList(resourceID), Arrays.asList(1, 4, 7), 1, remarks);
                    }
                }
                else {
                    this.logger.log(Level.SEVERE, "Collection ID for the commandID{0} is null", commandId);
                }
            }
            else if (platformType == 2 && deviceCommand.equals("RemoveScheduleConfiguration")) {
                final Long collectionID = ScheduledActionsUtils.getCollectionIDFromCommandID(commandId);
                if (collectionID != null && strStatus.equalsIgnoreCase("Acknowleged")) {
                    remarks = "mdm.bulkactions.suspend";
                    GroupActionScheduleUtils.updateCommandHistoryStatus(Collections.singletonList(collectionID), Collections.singletonList(resourceID), Arrays.asList(1, 4, 7), 6, remarks);
                }
            }
            else if (deviceCommand.equals("DeviceLock") || deviceCommand.equals("EraseDevice") || deviceCommand.equals("CorporateWipe") || deviceCommand.equals("ClearPasscode") || deviceCommand.contains("GetLocation") || deviceCommand.equals("ResetPasscode") || deviceCommand.equals("DeviceRing") || deviceCommand.equals("PlayLostModeSound") || deviceCommand.startsWith("RestartDevice") || deviceCommand.startsWith("ShutDownDevice") || deviceCommand.equals("ResumeKioskCommand") || deviceCommand.equals("PauseKioskCommand") || deviceCommand.equals("UnlockUserAccount") || deviceCommand.equalsIgnoreCase("LogOutUser")) {
                String userName3 = EventConstant.DC_SYSTEM_USER;
                final String commandDisplayName = this.getCommandDisplayName(deviceCommand);
                final String deviceNameValue = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                String scheduledCommandDisplayName = null;
                if (deviceCommand.contains("Scheduled")) {
                    collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                    final Long genericCommandID = ScheduledActionsUtils.getCommandIDForTempCommandID(commandId);
                    if (genericCommandID != null) {
                        commandId = genericCommandID;
                    }
                    if (deviceCommand.startsWith("RestartDevice")) {
                        scheduledCommandDisplayName = "RestartDevice";
                    }
                    else if (deviceCommand.startsWith("ShutDownDevice")) {
                        scheduledCommandDisplayName = "ShutDownDevice";
                    }
                }
                Object remarksArgs = ((scheduledCommandDisplayName != null) ? scheduledCommandDisplayName : commandDisplayName) + "@@@" + deviceNameValue;
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
                int commandStatus;
                int cmdStatus;
                String actionLogRemarks;
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    remarks = "dc.mdm.general.command.succeeded";
                    commandStatus = 2;
                    actionLogRemarks = "dc.mdm.actionlog.securitycommands.success";
                    if (deviceCommand.equals("DeviceLock")) {
                        if ("NoPasscodeSet".equalsIgnoreCase(hashMap.get("MessageResult"))) {
                            actionLogRemarks = "dc.mdm.actionlog.actions.success_without_msg";
                        }
                        else if ("Success".equalsIgnoreCase(hashMap.get("MessageResult"))) {
                            actionLogRemarks = "dc.mdm.actionlog.securitycommands.success_with_msg";
                        }
                        if (platformType == 1) {
                            final JSONObject emailDetails = new JSONObject();
                            emailDetails.put("resourceId", (Object)resourceID);
                            emailDetails.put("customerId", (Object)customerId);
                            final AppleDeviceLockHandler deviceLockHandler = AppleDeviceLockHandler.getDeviceLockHandler(resourceID);
                            deviceLockHandler.checkAndSendEmail(emailDetails);
                        }
                    }
                    if (deviceCommand.equals("EraseDevice") || deviceCommand.equals("CorporateWipe")) {
                        String sRemarks = "mdm.deprovision.old_remark";
                        final Boolean WipeCmdFromServer = true;
                        final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
                        if (ownedby == 1) {
                            sRemarks = "mdm.deprovision.old_remark";
                        }
                        else {
                            sRemarks = "mdm.deprovision.retire_remark";
                        }
                        int managedStatus = -1;
                        String deprovisionRemarks = null;
                        final Properties properties = new Properties();
                        ((Hashtable<String, String>)properties).put("UDID", strUDID);
                        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platformType);
                        ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", WipeCmdFromServer);
                        final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
                        if (json != null) {
                            managedStatus = json.optInt("MANAGED_STATUS", -1);
                            deprovisionRemarks = json.optString("REMARKS", "");
                        }
                        if (json != null && managedStatus != -1 && deprovisionRemarks != null && deprovisionRemarks != "") {
                            ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                            ((Hashtable<String, String>)properties).put("REMARKS", deprovisionRemarks);
                        }
                        else {
                            if (ownedby == 1) {
                                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", new Integer(10));
                            }
                            else {
                                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", new Integer(11));
                            }
                            ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
                        }
                        ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                        final JSONObject deprovisionJson = new JSONObject();
                        deprovisionJson.put("RESOURCE_ID", (Object)resourceID);
                        deprovisionJson.put("WIPE_PENDING", (Object)Boolean.FALSE);
                        ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson);
                        KnoxUtil.getInstance().removeAsKnox(resourceID);
                        ManagedDeviceHandler.getInstance().removeResourceAssociationsOnUnmanage(resourceID);
                        if (platformType == 2) {
                            new RemoteWipeHandler().deleteWipeOptionForResource(resourceID);
                        }
                        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
                        secLog.put((Object)"RESOURCE_ID", (Object)resourceID);
                        secLog.put((Object)"REMARKS", (Object)"command-success");
                        if (deviceCommand.equals("EraseDevice")) {
                            MDMOneLineLogger.log(Level.INFO, "COMPLETE_WIPE", secLog);
                        }
                        else {
                            MDMOneLineLogger.log(Level.INFO, "CORPORATE_WIPE", secLog);
                        }
                    }
                    if ((deviceCommand.equals("ClearPasscode") && platformType == 1) || deviceCommand.equals("UnlockUserAccount")) {
                        final JSONObject params4 = new JSONObject();
                        params4.put("resourceId", (Object)resourceID);
                        params4.put("strCommandUuid", (Object)strCommandUuid);
                        params4.put("strStatus", (Object)strStatus);
                        params4.put("strData", (Object)strData);
                        params4.put("customerId", (Object)customerId);
                        final CommandResponseProcessor.QueuedResponseProcessor processor4 = this.getInstanseForQueueResponse(deviceCommand);
                        processor4.processQueuedCommand(params4);
                        if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                            final JSONObject baseCommandDetails = SeqCmdUtils.getInstance().getBaseCmdDetailsforResource(resourceID);
                            final String commandUUID = baseCommandDetails.optString("COMMAND_UUID", "");
                            if (commandUUID.contains("ScheduleOSUpdate")) {
                                this.logger.log(Level.INFO, "Passcode cleared in the device due to OSUpdate policy");
                                actionLogRemarks = "mdm.osupdate.action.clearpasscode";
                                remarksArgs = deviceNameValue;
                            }
                            else if (commandUUID.contains("InstallProfile")) {
                                this.logger.log(Level.INFO, "Passcode cleared in the device due to Passcode policy");
                            }
                        }
                    }
                    if (deviceCommand.equalsIgnoreCase("GetLocationForLostDevice")) {
                        actionLogRemarks = "dc.mdm.actionlog.actions.lost_mode_loc_success";
                        remarksArgs = deviceNameValue;
                    }
                    cmdStatus = 2;
                    logJSON.put((Object)"REMARKS", (Object)"account-config-success");
                }
                else {
                    commandStatus = 0;
                    cmdStatus = 0;
                    logJSON.put((Object)"REMARKS", (Object)"account-config-failed");
                    if (errorCode != null && errorCode == 12100 && (deviceCommand.contains("GetLocation") || deviceCommand.startsWith("RestartDevice"))) {
                        remarks = I18N.getMsg("dc.mdm.safe.blwl.remarks.invalid_command", new Object[0]);
                    }
                    if (errorCode != null && (errorCode == 12140 || errorCode == 12141)) {
                        remarks = I18N.getMsg(remarks, new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl"), ProductUrlLoader.getInstance().getValue("trackingcode") });
                    }
                    actionLogRemarks = "dc.mdm.actionlog.securitycommands.failure";
                    if (deviceCommand.contains("EraseDevice") || deviceCommand.contains("CorporateWipe")) {
                        ManagedDeviceHandler.getInstance().handleDeprovisionFailure(resourceID);
                    }
                    final org.json.simple.JSONObject secLog2 = new org.json.simple.JSONObject();
                    secLog2.put((Object)"RESOURCE_ID", (Object)resourceID);
                    secLog2.put((Object)"REMARKS", (Object)"command-failed");
                    if (deviceCommand.equals("EraseDevice")) {
                        MDMOneLineLogger.log(Level.INFO, "COMPLETE_WIPE", secLog2);
                    }
                    else {
                        MDMOneLineLogger.log(Level.INFO, "CORPORATE_WIPE", secLog2);
                    }
                }
                if (deviceCommand.equals("UnlockUserAccount")) {
                    MDMOneLineLogger.log(Level.INFO, "UNLOCK_USER_ACCOUNT", logJSON);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                if (deviceCommand.contains("GetLocation")) {
                    if (commandStatus == 0) {
                        final int errorCodeInt = Integer.parseInt(hashMap.get("ErrorCode"));
                        MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, errorCodeInt);
                    }
                    else {
                        MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceID);
                        if (queueDataType == 100) {
                            final NSArray nsDict2 = PlistWrapper.getInstance().getArrayForKey("ManagedApplicationFeedback", strData);
                            final Boolean isSucess = MDMInvDataPopulator.getInstance().processIosLocationCommand(nsDict2, strUDID);
                            MDMInvDataPopulator.getInstance().processMDMAppAnalyticData(nsDict2, resourceID);
                            if (!isSucess) {
                                cmdStatus = 0;
                                commandStatus = 0;
                                remarks = I18N.getMsg("dc.mdm.db.agent.location.error_msg.location_service_disabled_client", new Object[0]);
                                actionLogRemarks = "dc.mdm.actionlog.securitycommands.failure";
                            }
                        }
                        else {
                            AndroidInventory.getInstance().processAndroidLocationCommand(strData, strUDID);
                        }
                    }
                    if (deviceCommand.equalsIgnoreCase("GetLocationForLostDevice")) {
                        if (commandStatus == 0) {
                            actionLogRemarks = "dc.mdm.actionlog.actions.lost_mode_loc_failure";
                        }
                        else if (commandStatus == 2) {
                            actionLogRemarks = "dc.mdm.actionlog.actions.lost_mode_loc_success";
                        }
                        remarksArgs = deviceNameValue;
                    }
                }
                if (deviceCommand.equals("ResetPasscode")) {
                    final ResetPasscodeHandler passcodeHandler = new ResetPasscodeHandler();
                    if (commandStatus != 0 && passcodeHandler.isSendEmailToUser(resourceID)) {
                        passcodeHandler.sendEmail(resourceID);
                    }
                    passcodeHandler.deletePasscodeForResource(resourceID);
                }
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid) && deviceCommand.equals("ClearPasscode")) {
                    final JSONObject json2 = new JSONObject();
                    json2.put("action", 1);
                    json2.put("resourceID", (Object)resourceID);
                    json2.put("commandUUID", (Object)strCommandUuid);
                    json2.put("params", (Object)new JSONObject());
                    SeqCmdRepository.getInstance().processSeqCommand(json2);
                }
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final CommandResponseProcessor.SeqQueuedResponseProcessor processor5 = this.getInstanceForSeqQueueResponse(deviceCommand);
                    final JSONObject params5 = new JSONObject();
                    params5.put("resourceId", (Object)resourceID);
                    params5.put("strCommandUuid", (Object)strCommandUuid);
                    params5.put("strStatus", (Object)strStatus);
                    params5.put("strData", (Object)strData);
                    params5.put("customerId", (Object)customerId);
                    processor5.processSeqQueuedCommand(params5);
                }
                final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                if (statusJSON.has("ADDED_BY")) {
                    statusJSON.put("RESOURCE_ID", (Object)resourceID);
                    final Long userId = JSONUtil.optLongForUVH(statusJSON, "ADDED_BY", Long.valueOf(-1L));
                    if (userId != -1L) {
                        userName3 = DMUserHandler.getUserNameFromUserID(userId);
                    }
                    statusJSON.put("COMMAND_STATUS", commandStatus);
                    if (errorCode != null && errorCode != -1L) {
                        statusJSON.put("ERROR_CODE", (Object)errorCode);
                    }
                    statusJSON.put("RESOURCE_ID", (Object)resourceID);
                    statusJSON.put("REMARKS_ARGS", remarksArgs);
                    statusJSON.put("REMARKS", (Object)remarks);
                    statusJSON.put("COMMAND_ID", (Object)commandId);
                    new CommandStatusHandler().populateCommandStatus(statusJSON);
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, userName3, actionLogRemarks, remarksArgs, customerId);
                }
                else if (collectionId != null) {
                    final List collectionIDs = GroupActionScheduleUtils.getCollectionsForScheduledCommand(deviceCommand, resourceID);
                    final List fromStatus = new ArrayList();
                    fromStatus.add(1);
                    fromStatus.add(0);
                    fromStatus.add(2);
                    GroupActionScheduleUtils.updateCommandHistoryStatus(collectionIDs, Collections.singletonList(resourceID), fromStatus, cmdStatus, remarks);
                }
            }
            else if (deviceCommand.equalsIgnoreCase("ActivateKnoxLicense") || deviceCommand.equalsIgnoreCase("DeactivateKnoxLicense") || deviceCommand.equalsIgnoreCase("CreateContainer") || deviceCommand.equalsIgnoreCase("RemoveContainer") || deviceCommand.equalsIgnoreCase("ContainerLock") || deviceCommand.equalsIgnoreCase("ContainerUnlock") || deviceCommand.equalsIgnoreCase("ClearContainerPasscode") || deviceCommand.equalsIgnoreCase("DeactivateKnox") || deviceCommand.equalsIgnoreCase("ActivateKnox")) {
                final JSONObject statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                int commandStatus;
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    remarks = "dc.mdm.general.command.succeeded";
                    commandStatus = 2;
                }
                else {
                    commandStatus = 0;
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                if (statusJSON2.has("ADDED_BY")) {
                    final JSONObject requestJSON = new JSONObject();
                    requestJSON.put("COMMAND_TYPE", (Object)deviceCommand);
                    requestJSON.put("COMMAND_STATUS", commandStatus);
                    requestJSON.put("NAME", (Object)ManagedDeviceHandler.getInstance().getDeviceName(resourceID));
                    requestJSON.put("RESOURCE_ID", (Object)resourceID);
                    remarks = DeviceCommandRepository.getInstance().getCommandRemarks(requestJSON);
                    statusJSON2.put("RESOURCE_ID", (Object)resourceID);
                    statusJSON2.put("COMMAND_ID", (Object)commandId);
                    statusJSON2.put("COMMAND_STATUS", commandStatus);
                    statusJSON2.put("REMARKS", (Object)remarks);
                    final Long userId2 = JSONUtil.optLongForUVH(statusJSON2, "ADDED_BY", Long.valueOf(-1L));
                    new CommandStatusHandler().populateCommandStatus(statusJSON2);
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, DMUserHandler.getUserNameFromUserID(userId2), remarks, null, customerId);
                }
            }
            else if (deviceCommand.equals("Enrollment")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, strUDID);
                final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("QueryResponses", strData);
                HashMap hash = new HashMap();
                hash = PlistWrapper.getInstance().getHashFromDict(nsDict);
                hash.put("RequestType", "DeviceInformation");
                this.logger.log(Level.INFO, "ProcessData() -- DeviceInformation - Converted From PList to Hash");
                this.logger.log(Level.FINE, "ProcessData() -- DeviceInformation ->{0}", hash);
                if (customerId != null) {
                    hash.put("CUSTOMER_ID", customerId);
                }
                if (hashMap.get("ENROLLMENT_REQUEST_ID") != null) {
                    enrollmentRequestId = hashMap.get("ENROLLMENT_REQUEST_ID");
                    hash.put("ENROLLMENT_REQUEST_ID", enrollmentRequestId);
                }
                if (hashMap.get("MANAGED_USER_ID") != null) {
                    hash.put("MANAGED_USER_ID", hashMap.get("MANAGED_USER_ID"));
                }
                if (hashMap.get("isAppleConfig") != null) {
                    hash.put("isAppleConfig", hashMap.get("isAppleConfig"));
                    isAppleConfig = Boolean.valueOf(hashMap.get("isAppleConfig"));
                }
                if (hashMap.get("appleConfigId") != null) {
                    hash.put("appleConfigId", hashMap.get("appleConfigId"));
                }
                MDMEntrollment.getInstance().enrolliOSDevice(hash);
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                if (resourceID != null) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
                }
                hash.put("DEVICE_LOCAL_TIME", postTime);
                MDMInvDataPopulator.getInstance().updateIOSInventory(resourceID, hash, nsDict);
                MDMInvDataPopulator.getInstance().updateDeviceName(resourceID, hash, 1);
            }
            else if ((deviceCommand.equals("InstallApplication") || deviceCommand.equals("ManageApplication")) && queueDataType == 100) {
                final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                this.logger.log(Level.INFO, "ProcessData(): deviceCommand: {0}", deviceCommand);
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                final Long appId = MDMUtil.getInstance().getAppIDFromCollection(Long.parseLong(collectionId));
                int installationStatus = 0;
                int collectionStatus = 7;
                final IOSInstallApplicationResponseProcessor processor6 = new IOSInstallApplicationResponseProcessor();
                final JSONObject param = new JSONObject();
                param.put("strData", (Object)strData);
                param.put("strCommandUuid", (Object)strCommandUuid);
                param.put("resourceId", (Object)resourceID);
                param.put("remarks", (Object)remarks);
                param.put("customerId", (Object)customerId);
                final JSONObject installResponse = processor6.processQueuedCommand(param);
                collectionStatus = installResponse.optInt("collectionStatus");
                installationStatus = installResponse.optInt("installationStatus");
                remarks = installResponse.optString("remarks");
                errorCode = installResponse.optInt("ErrorCode");
                final boolean isLicenseUpdate = installResponse.optBoolean("isLicenseUpdate");
                if (isLicenseUpdate) {
                    new AppleAppLicenseMgmtHandler().updateLicenseStatus(resourceID, appGroupId, 1);
                }
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId, collectionStatus, remarks);
                handler.updateAppInstallationStatusFromDevice(resourceID, appGroupId, appId, installationStatus, remarks, 0);
                collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final SequentialSubCommand sequentialSubCommand2 = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
                    if (!SeqCmdUtils.getInstance().isSequentialCommandProcessImmediately(sequentialSubCommand2.SequentialCommandID)) {
                        processor6.processSeqQueuedCommand(param);
                    }
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                if (deviceCommand.equals("InstallApplication")) {
                    new IOSAppCatalogHandler().scheduleAppCatalogSync(null, resourceID, 180000L);
                }
            }
            else if (deviceCommand.equals("InstallApplication") && platformType == 2) {
                if (strStatus.equalsIgnoreCase("Error")) {
                    collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                    if (errorCode == 12042) {
                        final Long appId2 = MDMUtil.getInstance().getAppIdFromCollectionId(Long.parseLong(collectionId));
                        final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                        if (remarks != null) {
                            remarks = new ManagedAppStatusHandler().getRemarksForAppInstallFailure(remarks);
                        }
                        final AppInstallationStatusHandler handler2 = new AppInstallationStatusHandler();
                        handler2.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, appId2, 2, remarks, 0);
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                    }
                    else {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                        collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                    }
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if ((deviceCommand.equals("InstallProfile") || deviceCommand.equals("KioskDefaultRestriction")) && !strCommandUuid.contains("DefaultWebClipsPayload") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5")) {
                this.logger.log(Level.INFO, "ProcessData - StatusUpdate received from device {0} for commandUUID {1}", new Object[] { strUDID, deviceCommand });
                this.logger.log(Level.INFO, "ProcessData() -> deviceCommand: {0}", deviceCommand);
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    final int status = 6;
                    final String agentRemarks = hashMap.get("Remarks");
                    if (agentRemarks != null) {
                        remarks = agentRemarks;
                    }
                    else if (platformType == 1 && !collectionId.equals("DefaultWebClipsPayload") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5")) {
                        final IOSInstallProfileResponseProcessor iOSProcessor = new IOSInstallProfileResponseProcessor();
                        iOSProcessor.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
                    }
                    else {
                        remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                    }
                    if (!collectionId.equals("DefaultWebClipsPayload") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") && !strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5") && platformType != 1) {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, status, remarks);
                        final JSONObject params2 = new JSONObject();
                        params2.put("collectionId", (Object)collectionId);
                        params2.put("resourceId", (Object)resourceID);
                        params2.put("handler", 1);
                        params2.put("additionalParams", (Object)new JSONObject());
                        params2.put("platformType", platformType);
                        params2.put("customerId", (Object)customerId);
                        MDMProfileResponseListenerHandler.getInstance().invokeProfileListener(params2);
                    }
                }
                else {
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                    boolean addErrorCode = true;
                    if (platformType == 1 && !IOSErrorStatusHandler.IOS_PROFILE_ERROR_CODE_KB_LIST.contains(errorCode)) {
                        addErrorCode = false;
                        this.logger.log(Level.INFO, "Not adding error code to iOS profiles");
                    }
                    if (addErrorCode) {
                        collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                    }
                }
                if (collectionId.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") || collectionId.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(collectionId, resourceID);
                }
                else {
                    final String scope = hashMap.get("CommandScope");
                    DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                }
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final CommandResponseProcessor.SeqQueuedResponseProcessor processor7 = this.getInstanceForSeqQueueResponse(deviceCommand);
                    final JSONObject params6 = new JSONObject();
                    params6.put("resourceId", (Object)resourceID);
                    params6.put("strCommandUuid", (Object)strCommandUuid);
                    params6.put("strStatus", (Object)strStatus);
                    params6.put("strData", (Object)strData);
                    params6.put("customerId", (Object)customerId);
                    processor7.processSeqQueuedCommand(params6);
                }
            }
            else if ((deviceCommand.equals("RemoveProfile") || deviceCommand.equals("RemoveApplication")) && !strCommandUuid.contains("DefaultWebClipsPayload")) {
                this.logger.log(Level.INFO, "ProcessData - StatusUpdate received from device {0} for commandUUID {1}", new Object[] { strUDID, deviceCommand });
                this.logger.log(Level.INFO, "ProcessData() -> deviceCommand: {0}", deviceCommand);
                final String scope = hashMap.get("CommandScope");
                boolean isNeedToRemove = true;
                boolean isProfileAvailable = false;
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final CommandResponseProcessor.SeqQueuedResponseProcessor processor = this.getInstanceForSeqQueueResponse(deviceCommand);
                    final JSONObject params7 = new JSONObject();
                    params7.put("resourceId", (Object)resourceID);
                    params7.put("strCommandUuid", (Object)strCommandUuid);
                    params7.put("strStatus", (Object)strStatus);
                    params7.put("strData", (Object)strData);
                    params7.put("customerId", (Object)customerId);
                    final JSONObject object = processor.processSeqQueuedCommand(params7);
                    isNeedToRemove = object.optBoolean("isNeedToAddQueue");
                }
                isProfileAvailable = ProfileAssociateHandler.getInstance().isIPCommandAvailableForResource(Long.parseLong(collectionId), resourceID);
                if (strStatus.equalsIgnoreCase("Acknowledged") && deviceCommand.equals("RemoveApplication") && platformType == 2) {
                    remarks = "dc.db.mdm.apps.status.uninstall";
                    if (!isProfileAvailable) {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 3, remarks);
                    }
                }
                else if (strStatus.equalsIgnoreCase("Acknowledged") || (queueDataType == 100 && (errorCode == 12029 || errorCode == 21009))) {
                    if (deviceCommand.equals("RemoveApplication") && strState != null && strState.equalsIgnoreCase("Queued")) {
                        remarks = "dc.db.mdm.collection.Queued_removed_the_app";
                    }
                    else if (deviceCommand.equals("RemoveApplication")) {
                        remarks = "dc.db.mdm.collection.Successfully_removed_the_app";
                    }
                    else if (deviceCommand.equalsIgnoreCase("RemoveProfile")) {
                        remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                    }
                    if (remarks != null && !collectionId.equals("DefaultWebClipsPayload") && isNeedToRemove && !isProfileAvailable) {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                    }
                }
                else if (strStatus.equalsIgnoreCase("Error") && deviceCommand.equals("RemoveApplication") && (queueDataType == 101 || queueDataType == 102)) {
                    remarks = "dc.db.som.status.uninstallation_failed";
                    final String agentErrorMsg = hashMap.get("ErrorMsg");
                    final int platFormBasedOnUDID = ManagedDeviceHandler.getInstance().getPlatformType(strUDID);
                    if (platFormBasedOnUDID == 2 && agentErrorMsg != null) {
                        remarks = agentErrorMsg + "@@@<l>" + "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)#uninstallation";
                    }
                    if (collnUpdater.isCollectionPresent(Long.parseLong(collectionId))) {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                        collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                    }
                }
                else if (strStatus.equalsIgnoreCase("Error") && deviceCommand.equals("RemoveApplication") && queueDataType == 100 && collnUpdater.isCollectionPresent(Long.parseLong(collectionId))) {
                    if (errorCode != 12029 && errorCode != 21009) {
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                        collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                    }
                    final Long appGroupId2 = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                    AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupId2);
                    new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupId2);
                    ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.parseLong(collectionId));
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                if ((deviceCommand.equalsIgnoreCase("RemoveProfile") || queueDataType == 100) && !strCommandUuid.contains("DefaultWebClipsPayload")) {
                    if (strStatus.equalsIgnoreCase("Error") && (errorCode == 12013 || errorCode == 12075) && isNeedToRemove && !isProfileAvailable) {
                        remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                        collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                    }
                    if (isNeedToRemove && !isProfileAvailable) {
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                    }
                }
                if (deviceCommand.equals("RemoveApplication") && queueDataType == 100) {
                    this.logger.log(Level.INFO, "ProcessData: Remove App - Going to update app installation status in MdAppResRel");
                    final Long appGroupId2 = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                    if (ProfileAssociateHandler.getInstance().isCollectionDeleteSafe(resourceID, Long.parseLong(collectionId))) {
                        AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupId2);
                        new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupId2);
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.parseLong(collectionId));
                    }
                    else {
                        new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupId2);
                        AppsUtil.getInstance().revertInstalledAppStatus(appGroupId2, resourceID);
                    }
                }
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                if (deviceCommand.equals("RemoveProfile")) {
                    final JSONObject params7 = new JSONObject();
                    params7.put("collectionId", (Object)collectionId);
                    params7.put("resourceId", (Object)resourceID);
                    params7.put("additionalParams", (Object)new JSONObject());
                    params7.put("platformType", platformType);
                    params7.put("customerId", (Object)customerId);
                    if (strStatus.equalsIgnoreCase("Acknowledged")) {
                        params7.put("handler", 1);
                    }
                    else if (strStatus.equalsIgnoreCase("Error")) {
                        params7.put("handler", 2);
                    }
                    MDMProfileResponseListenerHandler.getInstance().invokeRemoveProfileListener(params7);
                }
            }
            else if (deviceCommand.equalsIgnoreCase("InstallApplicationConfiguration") && platformType == 2) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                int statusConstants;
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                    statusConstants = 6;
                }
                else {
                    statusConstants = 7;
                }
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId, statusConstants, remarks);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("InstallApplicationConfiguration") && platformType == 1) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                final JSONObject settingsResponse = new IOSErrorStatusHandler().getIOSSettingError(strData);
                final String configStatus = settingsResponse.getString("Status");
                int statusConstants;
                if (configStatus.equalsIgnoreCase("Acknowledged")) {
                    remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                    statusConstants = 6;
                }
                else {
                    statusConstants = 7;
                    remarks = settingsResponse.getString("LocalizedRemarks");
                }
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId, statusConstants, remarks);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("RemoveApplicationConfiguration")) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                if (ProfileAssociateHandler.getInstance().isCollectionDeleteSafe(resourceID, Long.valueOf(collectionId))) {
                    int statusConstants;
                    if (strStatus.equalsIgnoreCase("Acknowledged")) {
                        remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                        statusConstants = 6;
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                        final JSONObject apps = AppConfigPolicyDBHandler.getInstance().getApplicableAppDetails(Long.valueOf(collectionId));
                        final List appGroupIds = JSONUtil.convertJSONArrayToList(apps.getJSONArray("APP_GROUP_ID"));
                        AppConfigPolicyDBHandler.getInstance().deleteAppConfigFeedback(resourceID, appGroupIds);
                    }
                    else {
                        statusConstants = 7;
                    }
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, statusConstants, remarks);
                    ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, Long.valueOf(collectionId));
            }
            else if (deviceCommand.equalsIgnoreCase("ApplicationConfiguration")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("MDMDefaultApplicationConfiguration")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand("MDMDefaultApplicationConfiguration", resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("MDMDefaultApplicationConfigMigrate")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand("MDMDefaultApplicationConfigMigrate", resourceID);
                IOSMigrationUtil.getInstance().processAppConfigResponse(strData, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("AgentUpgrade")) {
                final MDMAgentUpdateHandler agentUpdateHandler = new MDMAgentUpdateHandler();
                final HashMap dataMap = new HashMap();
                if (strStatus.equalsIgnoreCase("Error")) {
                    dataMap.put("REMARKS", agentUpdateHandler.getAndroidAgentUpgradeErrRemarks((String)dcqueueData.queueData));
                    dataMap.put("State", "Failed");
                }
                else {
                    dataMap.put("REMARKS", "dc.mdm.db.agent.enroll.agent_enroll_finished");
                    final String notifiedVersion = (queueDataType != 102) ? agentUpdateHandler.getAgentNotifiedVersion(queueDataType) : agentUpdateHandler.getSafeAgentVersion(resourceID);
                    dataMap.put("NOTIFIED_AGENT_VERSION", notifiedVersion);
                }
                dataMap.put("RESOURCE_ID", resourceID);
                final Object version = DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_VERSION_CODE");
                dataMap.put("AgentVersionCode", (version != null) ? version.toString() : null);
                agentUpdateHandler.updateAgentUpgradeStatus(dataMap);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("RemoveDevice")) {
                this.logger.log(Level.INFO, "ProcessData - StatusUpdate remove device from device {0} for commandUUID {1}", new Object[] { strUDID, deviceCommand });
                final int platFormBasedOnUDID2 = ManagedDeviceHandler.getInstance().getPlatformType(strUDID);
                final BaseRemoveDeviceHandler handler3 = BaseRemoveDeviceHandler.getInstance(platFormBasedOnUDID2);
                handler3.handleRemoveDevice(hashMap, customerId);
            }
            else if (deviceCommand.equals("BlacklistAppInDevice") || deviceCommand.equals("RemoveBlacklistAppInDevice") || deviceCommand.equals("BlacklistWhitelistApp")) {
                if (platformType == 2) {
                    final String scope = hashMap.get("CommandScope");
                    int scopeInt = 0;
                    if (scope != null) {
                        scopeInt = (scope.equalsIgnoreCase("container") ? 1 : 0);
                    }
                    final JSONObject params2 = new JSONObject(strData);
                    params2.put("scope", scopeInt);
                    params2.put("RESOURCE_ID", (Object)resourceID);
                    new AndroidBlacklistProcessor().processResponse(params2);
                }
                else {
                    final JSONObject resp = new JSONObject();
                    Boolean success = true;
                    Boolean profileNotAvailable = false;
                    if (!strStatus.equalsIgnoreCase("Acknowledged")) {
                        success = false;
                    }
                    if (strStatus.equalsIgnoreCase("Error") && errorCode == 12075) {
                        profileNotAvailable = true;
                    }
                    resp.put("success", (Object)success);
                    resp.put("profileNotAvailable", (Object)profileNotAvailable);
                    resp.put("RESOURCE_ID", (Object)resourceID);
                    new IOSBlacklistAppProcessor().processResponse(resp);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("ReregisterNotificationToken")) {
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                this.logger.log(Level.INFO, "GCM re register command status : {0}", strStatus);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("SyncAgentSettings")) {
                strCommandUuid = "SyncAgentSettings";
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("SyncDownloadSettings")) {
                if (strStatus.equalsIgnoreCase("Acknowledged") || errorCode == 12100) {
                    strCommandUuid = "SyncDownloadSettings";
                    resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                }
            }
            else if (deviceCommand.equals("LocationConfiguration")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equals("BATTERY_CONFIGURATION")) {
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("RemoveOldAgent")) {
                this.logger.log(Level.INFO, "ProcessData() -> deviceCommand: {0}", deviceCommand);
                AgentMigrationHandler.getInstance().handleUnamanageOldAgentResponse(hashMap);
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equals("RemoveDataProfile")) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("InstallDataProfile")) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    final int status = 6;
                    remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, status, remarks);
                }
                else {
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                    collnUpdater.updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                }
            }
            else if (deviceCommand.equals("EnableLostMode") || deviceCommand.equals("LostModeCommand")) {
                final JSONObject statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                final Long userId3 = JSONUtil.optLongForUVH(statusJSON2, "ADDED_BY", Long.valueOf(-1L));
                if (platformType == 2 && errorCode != null && errorCode == 35001) {
                    final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
                    statusJSON2.put("COMMAND_STATUS", 0);
                    statusJSON2.put("RESOURCE_ID", (Object)resourceID);
                    statusJSON2.put("REMARKS", (Object)remarks);
                    statusJSON2.put("ERROR_CODE", (Object)errorCode);
                    commandStatusHandler.populateCommandStatus(statusJSON2);
                }
                if (platformType == 2 && errorCode != null && errorCode == 12100) {
                    new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processEnableLostModeResponseForOlderAgent();
                }
                else {
                    new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processEnableLostModeResponse();
                }
                if (!strCommandUuid.equalsIgnoreCase(deviceCommand)) {
                    final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
                    statusJSON2.put("COMMAND_STATUS", 2);
                    statusJSON2.put("RESOURCE_ID", (Object)resourceID);
                    commandStatusHandler.populateCommandStatus(statusJSON2);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equals("DisableLostMode")) {
                final JSONObject statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                final Long userId3 = JSONUtil.optLongForUVH(statusJSON2, "ADDED_BY", Long.valueOf(-1L));
                if (platformType == 2 && errorCode != null && errorCode == 12100) {
                    new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processDisableLostModeResponseForOlderAgent();
                }
                else {
                    new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processDisableLostModeResponse();
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equals("LostModeDeviceLocation")) {
                JSONObject statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                if (!statusJSON2.has("ADDED_BY")) {
                    commandId = DeviceCommandRepository.getInstance().getCommandID("EnableLostMode");
                    statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                }
                final Long userId3 = JSONUtil.optLongForUVH(statusJSON2, "ADDED_BY", Long.valueOf(-1L));
                int status2 = -1;
                if (hashMap.containsKey("ManagedApplicationFeedback")) {
                    final NSArray nsArray = PlistWrapper.getInstance().getArrayForKey("ManagedApplicationFeedback", strData);
                    if (nsArray.count() > 0) {
                        final NSDictionary appfeedbackDict = (NSDictionary)nsArray.objectAtIndex(0);
                        final NSDictionary feedbackDict = (NSDictionary)appfeedbackDict.objectForKey("Feedback");
                        if (feedbackDict != null) {
                            final HashMap locationMap = PlistWrapper.getInstance().getHashFromDict(feedbackDict);
                            hashMap.putAll(locationMap);
                        }
                        if (hashMap.containsKey("LocationUpdationTime")) {
                            hashMap.put("Timestamp", hashMap.get("LocationUpdationTime"));
                        }
                        status2 = new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processDeviceLocationResponse();
                        DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
                    }
                }
                else {
                    status2 = new LostModeCommandResponseProcessor(resourceID, platformType, customerId, hashMap, userId3).processDeviceLocationResponse();
                    DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
                }
                String actionLogRemarks2 = "";
                if (status2 == 2) {
                    actionLogRemarks2 = "dc.mdm.actionlog.actions.lost_mode_loc_success";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, DMUserHandler.getUserNameFromUserID(userId3), actionLogRemarks2, ManagedDeviceHandler.getInstance().getDeviceName(resourceID), customerId);
                }
                else if (status2 == 0) {
                    actionLogRemarks2 = "dc.mdm.actionlog.actions.lost_mode_loc_failure";
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, DMUserHandler.getUserNameFromUserID(userId3), actionLogRemarks2, ManagedDeviceHandler.getInstance().getDeviceName(resourceID), customerId);
                }
                statusJSON2.put("RESOURCE_ID", (Object)resourceID);
                statusJSON2.put("COMMAND_ID", (Object)commandId);
                statusJSON2.put("COMMAND_STATUS", status2);
                statusJSON2.put("REMARKS", (Object)actionLogRemarks2);
                new CommandStatusHandler().populateCommandStatus(statusJSON2);
            }
            else if (deviceCommand.equalsIgnoreCase("RemoteSession")) {
                final JSONObject statusJSON2 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                statusJSON2.put("RESOURCE_ID", (Object)resourceID);
                statusJSON2.put("COMMAND_ID", (Object)commandId);
                final Long userId3 = JSONUtil.optLongForUVH(statusJSON2, "ADDED_BY", Long.valueOf(-1L));
                final String userName3 = DMUserHandler.getUserNameFromUserID(userId3);
                final Object remarksArgs2 = this.getCommandDisplayName(deviceCommand) + "@@@" + ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                if (strStatus.equalsIgnoreCase("Error")) {
                    statusJSON2.put("COMMAND_STATUS", 0);
                    if (errorCode == 12100) {
                        final JSONObject constructedMsg = new JSONObject();
                        constructedMsg.put("StatusCode", 6007);
                        new RemoteSessionManager().handleSessionUpdateFromAgent(resourceID, constructedMsg);
                    }
                    final String actionLogRemarks3 = "dc.mdm.actionlog.securitycommands.failure";
                    if (statusJSON2.has("ADDED_BY")) {
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, userName3, actionLogRemarks3, remarksArgs2, customerId);
                    }
                    statusJSON2.put("REMARKS", (Object)actionLogRemarks3);
                }
                else if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    statusJSON2.put("COMMAND_STATUS", 2);
                    final String actionLogRemarks3 = "dc.mdm.actionlog.securitycommands.success";
                    if (statusJSON2.has("ADDED_BY")) {
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, userName3, actionLogRemarks3, remarksArgs2, customerId);
                    }
                    statusJSON2.put("REMARKS", (Object)actionLogRemarks3);
                }
                new CommandStatusHandler().populateCommandStatus(statusJSON2);
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("RemoteDebug") && strStatus.equalsIgnoreCase("error")) {
                final JSONObject commandStatusJSON3 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                final String sDeviceName4 = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                commandStatusJSON3.put("COMMAND_ID", (Object)commandId);
                commandStatusJSON3.put("RESOURCE_ID", (Object)resourceID);
                final String userName3 = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON3, "ADDED_BY", Long.valueOf(-1L)));
                commandStatusJSON3.put("COMMAND_STATUS", 0);
                commandStatusJSON3.put("REMARKS", (Object)"dc.mdm.inv.remote_debug_failed_agent_update");
                commandStatusJSON3.put("ERROR_CODE", (Object)errorCode);
                new CommandStatusHandler().populateCommandStatus(commandStatusJSON3);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2172, null, userName3, "dc.mdm.actionlog.inv.remote_debug_failed", sDeviceName4, customerId);
            }
            else if (deviceCommand.equalsIgnoreCase("AddAFWAccount")) {
                final JSONObject afwStatusJSON = new JSONObject();
                afwStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                if (strStatus.equalsIgnoreCase("Error")) {
                    afwStatusJSON.put("ACCOUNT_STATUS", 3);
                    afwStatusJSON.put("ERROR_CODE", (Object)errorCode);
                }
                else {
                    afwStatusJSON.put("ACCOUNT_STATUS", 1);
                }
                new AFWAccountStatusHandler().addOrUpdateAFWAccountStatus(afwStatusJSON);
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("InstallManagedSettings")) {
                final MGSettingCmdResponseProcessor commandProcessor = new MGSettingCmdResponseProcessor();
                commandProcessor.processCommand(resourceID, strData, strCommandUuid, customerId);
            }
            else if (strCommandUuid.contains("DefaultWebClipsPayload")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                final CommandResponseProcessor.QueuedResponseProcessor processor3 = this.getInstanseForQueueResponse(strCommandUuid);
                processor3.processQueuedCommand(params3);
                if (deviceCommand.equalsIgnoreCase("InstallProfile")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("DefaultAppCatalogWebClips", resourceID);
                }
                else {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("DefaultMDMRemoveKioskProfile", resourceID);
                }
            }
            else if (deviceCommand.equalsIgnoreCase("DefaultMDMKioskProfile") || deviceCommand.equalsIgnoreCase("DefaultMDMRemoveKioskProfile") || deviceCommand.equalsIgnoreCase("DefaultAppCatalogWebClips") || deviceCommand.equalsIgnoreCase("DefaultRemoveAppCatalogWebClips")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    final CommandResponseProcessor.SeqQueuedResponseProcessor processor8 = this.getInstanceForSeqQueueResponse(deviceCommand);
                    processor8.processSeqQueuedCommand(params3);
                }
                else {
                    final CommandResponseProcessor.QueuedResponseProcessor processor3 = this.getInstanseForQueueResponse(deviceCommand);
                    processor3.processQueuedCommand(params3);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equalsIgnoreCase("DefaultAppCatalogWebClipsMigrate")) {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                final CommandResponseProcessor.QueuedResponseProcessor processor3 = this.getInstanseForQueueResponse(deviceCommand);
                processor3.processQueuedCommand(params3);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                IOSMigrationUtil.getInstance().urlMigratedSuccessfullyOndevice(resourceID, 1);
            }
            else if (deviceCommand.equals("ScheduleOSUpdate")) {
                final ScheduleOSUpdateResponseProcessor processor9 = new ScheduleOSUpdateResponseProcessor();
                final JSONObject json3 = new JSONObject();
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    cmdParams = SeqCmdDBUtil.getInstance().getParams(resourceID).getJSONObject("initialParams");
                    processor9.processResponseForSeqCmd(resourceID, strData, strCommandUuid, cmdParams);
                }
                else {
                    processor9.processResponse(resourceID, strData, strCommandUuid);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("AttemptOSUpdate")) {
                final JSONObject json4 = new JSONObject();
                cmdParams = SeqCmdDBUtil.getInstance().getParams(resourceID).getJSONObject("initialParams");
                new ScheduleOSUpdateResponseProcessor().processAttemptUpdateResponseForSeqCmd(resourceID, strData, strCommandUuid, cmdParams);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("AvailableOSUpdates")) {
                final AvailableUpdatesResponseProcessor processor10 = new AvailableUpdatesResponseProcessor();
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, strCommandUuid)) {
                    cmdParams = SeqCmdDBUtil.getInstance().getParams(resourceID).getJSONObject("initialParams");
                    processor10.processResponseForSeqCmd(resourceID, strData, strCommandUuid, cmdParams);
                }
                else {
                    processor10.processResponse(resourceID, strData, strCommandUuid);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else if (deviceCommand.equals("OsUpdatePolicy")) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    final String agentRemarks2 = hashMap.get("Remarks");
                    if (agentRemarks2 != null) {
                        remarks = agentRemarks2;
                    }
                    else {
                        remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                    }
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                }
                else {
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                }
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
            }
            else if (deviceCommand.equals("RemoveOsUpdatePolicy")) {
                collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(strCommandUuid);
                remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
                new OSUpdatePolicyHandler().deleteRecentProfileForResourceListCollection(Arrays.asList(resourceID), Arrays.asList(Long.parseLong(collectionId)));
                final List resourceList2 = new ArrayList();
                resourceList2.add(resourceID);
                ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
            }
            else if (deviceCommand.equalsIgnoreCase("TokenUpdate")) {
                if (queueDataType == 121 || queueDataType == 122 || queueDataType == 124 || queueDataType == 125) {
                    MDMEnrollmentUtil.getInstance().processGCMReregistration(hashMap);
                }
                else {
                    this.logger.log(Level.INFO, "Token update received for non android devices.");
                }
            }
            else if (strCommandUuid.contains("DeviceCompliance")) {
                if (!strCommandUuid.contains("RemoveDeviceCompliance")) {
                    if (errorCode == null) {
                        errorCode = 0;
                    }
                    ComplianceHandler.getInstance().updateComplianceStateForProfileAssociation(strCommandUuid, resourceID, strStatus, errorCode);
                }
            }
            else if (deviceCommand.equalsIgnoreCase("CapabilitiesInfo")) {
                MDMInvDataPopulator.getInstance().addOrUpdateCapabilitiesInfo(resourceID, new JSONObject(strData).getJSONObject("ResponseData").getJSONObject("CapabilitiesInfo"));
            }
            else if (deviceCommand.equalsIgnoreCase("ClearAppData")) {
                ClearAppDataHandler.getInstance().processClearAppDataCommandResponse(resourceID, commandId, hashMap, deviceCommand, customerId);
            }
            else if (deviceCommand.equalsIgnoreCase("MigrateUrl")) {
                new AndroidServletMigrationUtil().handleResponse(resourceID, strStatus);
            }
            else if (deviceCommand.equalsIgnoreCase("SyncAnnouncement")) {
                if (strStatus.equalsIgnoreCase("Acknowledged")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
                }
                else if (errorCode.equals(12100)) {
                    this.logger.log(Level.INFO, "Invalid announcement command");
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId, 12, "dc.db.mdm.apps.status.UpgradeApp");
                    DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandId, resourceID, commandRepositoryType, 12);
                }
            }
            else if ((platformType == 1 && strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4")) || strCommandUuid.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig5")) {
                final JSONObject commandStatusJSON3 = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                commandStatusJSON3.put("COMMAND_ID", (Object)commandId);
                commandStatusJSON3.put("RESOURCE_ID", (Object)resourceID);
                commandStatusJSON3.put("COMMAND_STATUS", strStatus.contains("Acknowledged") ? 2 : 0);
                new CommandStatusHandler().populateCommandStatus(commandStatusJSON3);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
            else {
                final JSONObject params3 = new JSONObject();
                params3.put("resourceId", (Object)resourceID);
                params3.put("strCommandUuid", (Object)strCommandUuid);
                params3.put("strStatus", (Object)strStatus);
                params3.put("strData", (Object)strData);
                params3.put("customerId", (Object)customerId);
                params3.put("strUDID", (Object)strUDID);
                try {
                    final SequentialSubCommand subcommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, strCommandUuid);
                    if (subcommand != null) {
                        final CommandResponseProcessor.SeqQueuedResponseProcessor processor2 = this.getInstanceForSeqQueueResponse(deviceCommand);
                        params3.put("PARAMS", (Object)subcommand.params);
                        processor2.processSeqQueuedCommand(params3);
                    }
                    else {
                        final CommandResponseProcessor.QueuedResponseProcessor processor11 = this.getInstanseForQueueResponse(deviceCommand);
                        processor11.processQueuedCommand(params3);
                    }
                }
                catch (final ClassNotFoundException e4) {
                    this.logger.log(Level.SEVERE, "No Class for processing", e4);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.SEVERE, "Exception when ProcessData", ex);
                }
                this.logger.log(Level.INFO, "ProcessData - StatusUpdate received from device {0} for commandUUID {1}", new Object[] { strUDID, deviceCommand });
                this.logger.log(Level.INFO, "ProcessData() -> deviceCommand: {0}", deviceCommand);
                DeviceCommandRepository.getInstance().deleteResourceCommand(strCommandUuid, resourceID);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception occurred in ProcessData()", ex2);
        }
        this.logger.log(Level.INFO, "Time taken for processing Command {0} on status {1} is {2}", new Object[] { deviceCommand, strStatus, System.currentTimeMillis() - startTime });
    }
    
    private ArrayList<HashMap> getServiceSubscriptionsMap(final NSDictionary nsObj) {
        final NSObject nsObject = nsObj.objectForKey("ServiceSubscriptions");
        try {
            final ArrayList<HashMap> hashMap_List = new ArrayList<HashMap>();
            if (nsObject instanceof NSArray) {
                final NSArray nsArray = (NSArray)nsObject;
                for (int c = 0; c < nsArray.count(); ++c) {
                    final NSObject tempNsObject = nsArray.objectAtIndex(c);
                    if (tempNsObject instanceof NSDictionary) {
                        final HashMap temp_Hm = new HashMap();
                        final NSDictionary tempNSDic = (NSDictionary)tempNsObject;
                        for (int i = 0; i < tempNSDic.allKeys().length; ++i) {
                            final String key = tempNSDic.allKeys()[i];
                            final NSObject tempNSObjForKey = tempNSDic.objectForKey(key);
                            final String value = tempNSObjForKey.toString();
                            temp_Hm.put(key, value);
                        }
                        hashMap_List.add(temp_Hm);
                    }
                }
            }
            return hashMap_List;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting service subscriptions: ", e);
            return null;
        }
    }
    
    public HashMap parseCommandUUID(final String commandUUID) {
        final HashMap hashMap = new HashMap();
        final StringTokenizer strToken = new StringTokenizer(commandUUID, ";");
        while (strToken.hasMoreTokens()) {
            final String tokenString = strToken.nextToken();
            final String[] keyValueArray = tokenString.split("=");
            if (keyValueArray.length == 2) {
                hashMap.put(keyValueArray[0], keyValueArray[1]);
            }
        }
        return hashMap;
    }
    
    public HashMap getCommandStatus(final Long resourceID, final Criteria inprogressCriteria) {
        final HashMap commandStatusHash = new HashMap();
        try {
            Criteria criteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceID, 0);
            if (inprogressCriteria != null) {
                criteria = criteria.and(inprogressCriteria);
            }
            final DataObject dobj = SyMUtil.getPersistence().get("CommandHistory", criteria);
            if (!dobj.isEmpty()) {
                String StatusStr = "failed";
                final int commandStatus = (int)dobj.getFirstValue("CommandHistory", "COMMAND_STATUS");
                if (commandStatus == 1) {
                    StatusStr = "inProgress";
                }
                else if (commandStatus == 2) {
                    StatusStr = "success";
                }
                else if (commandStatus == -1) {
                    StatusStr = "timeout";
                }
                String remarks = (String)dobj.getFirstValue("CommandHistory", "REMARKS");
                if (remarks != null) {
                    remarks = I18N.getMsg(remarks, new Object[0]);
                }
                else {
                    remarks = "--";
                }
                final Long commandID = (Long)dobj.getFirstValue("CommandHistory", "COMMAND_ID");
                String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
                commandName = commandName.split(";")[0];
                final String commandDisplayName = DeviceCommandRepository.getInstance().getI18NCommandName(commandName);
                commandStatusHash.put("COMMAND", commandDisplayName);
                commandStatusHash.put("COMMAND_NAME", commandName);
                commandStatusHash.put("COMMAND_REMARKS", remarks);
                commandStatusHash.put("COMMAND_STATUS", StatusStr);
                commandStatusHash.put("COMMAND_STATUS_INT", commandStatus);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "CommandUtil : getCommandStatus : Exception while getting Command Status hash...", exp);
        }
        return commandStatusHash;
    }
    
    private String getCommandDisplayName(final Integer commandID) throws Exception {
        String commandDisplayName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
        if (commandID == 0) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_lock", new Object[0]);
        }
        else if (commandID == 1) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_wipe", new Object[0]);
        }
        else if (commandID == 2) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.corporate_wipe", new Object[0]);
        }
        else if (commandID == 3) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.clear_passcode", new Object[0]);
        }
        else if (commandID == 4) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.get_Location", new Object[0]);
        }
        else if (commandID == 5) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.reset_passcode", new Object[0]);
        }
        else if (commandID == 6) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.ring_device", new Object[0]);
        }
        else if (commandID == 7) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.ring_device", new Object[0]);
        }
        else if (commandID == 8) {
            commandDisplayName = I18N.getMsg("dc.common.RESTART", new Object[0]);
        }
        else if (commandID == 9) {
            commandDisplayName = I18N.getMsg("dc.common.SHUTDOWN", new Object[0]);
        }
        else if (commandID == 11) {
            commandDisplayName = I18N.getMsg("mdm.inv.pause_kiosk", new Object[0]);
        }
        else if (commandID == 12) {
            commandDisplayName = I18N.getMsg("mdm.inv.resume_kiosk", new Object[0]);
        }
        return commandDisplayName;
    }
    
    public String getCommandDisplayName(final String commandName) throws Exception {
        String commandDisplayName = I18N.getMsg("dc.common.UNKNOWN", new Object[0]);
        if (commandName.equalsIgnoreCase("DeviceLock")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_lock", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("RemoteSession")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("EraseDevice")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_wipe", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("CorporateWipe")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.corporate_wipe", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("ClearPasscode")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.clear_passcode", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("ResetPasscode")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.reset_passcode", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("GetLocation")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.get_Location", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("DeviceRing") || commandName.equalsIgnoreCase("PlayLostModeSound")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.ring_device", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("RestartDevice")) {
            commandDisplayName = I18N.getMsg("dc.common.RESTART", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("ShutDownDevice")) {
            commandDisplayName = I18N.getMsg("dc.common.SHUTDOWN", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("PauseKioskCommand")) {
            commandDisplayName = I18N.getMsg("mdm.inv.pause_kiosk", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("ResumeKioskCommand")) {
            commandDisplayName = I18N.getMsg("mdm.inv.resume_kiosk", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("RemoteDebug")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.remote_debug", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("UnlockUserAccount")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.unlock_user_account", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("MacFileVaultPersonalKeyRotate")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.filevault_personal_rotate", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("ClearAppData")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.clear_app_data", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("LogOutUser")) {
            commandDisplayName = I18N.getMsg("dc.mdm.inv.logout_user", new Object[0]);
        }
        else if (commandName.equalsIgnoreCase("LostModeDeviceLocation")) {
            commandDisplayName = I18N.getMsg("mdm.inv.get_Location_lost", new Object[0]);
        }
        return commandDisplayName;
    }
    
    public String getCommandName(final Integer commandID) {
        String commandName = null;
        if (commandID == 0) {
            commandName = "DeviceLock";
        }
        else if (commandID == 1) {
            commandName = "EraseDevice";
        }
        else if (commandID == 2) {
            commandName = "CorporateWipe";
        }
        else if (commandID == 3) {
            commandName = "ClearPasscode";
        }
        else if (commandID == 4) {
            commandName = "GetLocation";
        }
        else if (commandID == 5) {
            commandName = "ResetPasscode";
        }
        else if (commandID == 6) {
            commandName = "DeviceRing";
        }
        else if (commandID == 7) {
            commandName = "PlayLostModeSound";
        }
        else if (commandID == 9) {
            commandName = "ShutDownDevice";
        }
        else if (commandID == 8) {
            commandName = "RestartDevice";
        }
        else if (commandID == 10) {
            commandName = "RemoteSession";
        }
        else if (commandID == 11) {
            commandName = "PauseKioskCommand";
        }
        else if (commandID == 12) {
            commandName = "ResumeKioskCommand";
        }
        else if (commandID == 13) {
            commandName = "UnlockUserAccount";
        }
        else if (commandID == 14) {
            commandName = "MacFileVaultPersonalKeyRotate";
        }
        return commandName;
    }
    
    public Integer getCommandID(final String commandName) {
        Integer commandID = null;
        if (commandName.equalsIgnoreCase("DeviceLock")) {
            commandID = 0;
        }
        else if (commandName.equalsIgnoreCase("RemoteSession")) {
            commandID = 10;
        }
        else if (commandName.equalsIgnoreCase("EraseDevice")) {
            commandID = 1;
        }
        else if (commandName.equalsIgnoreCase("CorporateWipe")) {
            commandID = 2;
        }
        else if (commandName.equalsIgnoreCase("ClearPasscode")) {
            commandID = 3;
        }
        else if (commandName.equalsIgnoreCase("GetLocation")) {
            commandID = 4;
        }
        else if (commandName.equalsIgnoreCase("ResetPasscode")) {
            commandID = 5;
        }
        else if (commandName.equalsIgnoreCase("DeviceRing")) {
            commandID = 6;
        }
        else if (commandName.equalsIgnoreCase("PlayLostModeSound")) {
            commandID = 7;
        }
        else if (commandName.equalsIgnoreCase("ShutDownDevice")) {
            commandID = 9;
        }
        else if (commandName.startsWith("RestartDevice")) {
            commandID = 8;
        }
        else if (commandName.equalsIgnoreCase("ActivateKnoxLicense")) {
            commandID = 16;
        }
        else if (commandName.equalsIgnoreCase("DeactivateKnoxLicense")) {
            commandID = 17;
        }
        else if (commandName.equalsIgnoreCase("CreateContainer")) {
            commandID = 11;
        }
        else if (commandName.equalsIgnoreCase("RemoveContainer")) {
            commandID = 12;
        }
        else if (commandName.equalsIgnoreCase("ContainerLock")) {
            commandID = 13;
        }
        else if (commandName.equalsIgnoreCase("ContainerUnlock")) {
            commandID = 14;
        }
        else if (commandName.equalsIgnoreCase("ClearContainerPasscode")) {
            commandID = 15;
        }
        else if (commandName.equalsIgnoreCase("PauseKioskCommand")) {
            commandID = 11;
        }
        else if (commandName.equalsIgnoreCase("ResumeKioskCommand")) {
            commandID = 12;
        }
        else if (commandName.equalsIgnoreCase("UnlockUserAccount")) {
            commandID = 13;
        }
        else if (commandName.equalsIgnoreCase("MacFileVaultPersonalKeyRotate")) {
            commandID = 14;
        }
        return commandID;
    }
    
    private int getPlatformFromQueueType(final Integer queueDataType) {
        int platform = 2;
        if (queueDataType == 100) {
            platform = 1;
        }
        return platform;
    }
    
    public void installAppCatalogWebclip(final Long resourceID) {
        try {
            final boolean restricted = new IOSKioskProfileDataHandler().isMEMDMIsRestricted(resourceID);
            if (!restricted) {
                DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(resourceID, "DefaultAppCatalogWebClips");
                final List resourceList = new ArrayList();
                resourceList.add(resourceID);
                NotificationHandler.getInstance().SendNotification(resourceList, 1);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in installAppCatalogWebclip", ex);
        }
    }
    
    private String constructKioskNotNowMsg(final Long collectionID, String remarks, final Long customerId) {
        final String[] args = remarks.split("@@@");
        final JSONObject configurtions = MDMConfigUtil.getConfiuguredPolicyInfo(collectionID);
        if (configurtions.has("557")) {
            final AppsUtil appUtil = new AppsUtil();
            final List<String> appName = appUtil.getAppNameFromIdentifier(args[1].trim().split(" "), 2, customerId);
            remarks = args[0] + "@@@" + appUtil.getAppNames(appName);
        }
        return remarks;
    }
    
    public CommandQueryCreator fetchCommandQueryCreator(final String commandType) throws Exception {
        if (commandType.equals("ScheduleOSUpdate")) {
            return new IOSUpdateCommandQueryGenerator();
        }
        throw new UnsupportedOperationException("Command Query Creator instance not found for command: " + commandType);
    }
    
    public CommandResponseProcessor.QueuedResponseProcessor getInstanseForQueueResponse(final String commandName) throws Exception {
        final String className = CommandUtil.GET_CLASS_FOR_PROCESSING.get(commandName);
        return (CommandResponseProcessor.QueuedResponseProcessor)Class.forName(className).newInstance();
    }
    
    public CommandResponseProcessor.SeqQueuedResponseProcessor getInstanceForSeqQueueResponse(final String commandName) throws Exception {
        final String className = CommandUtil.GET_CLASS_FOR_PROCESSING.get(commandName);
        return (CommandResponseProcessor.SeqQueuedResponseProcessor)Class.forName(className).newInstance();
    }
    
    public CommandResponseProcessor.ImmediateSeqResponseProcessor getInstanceForImmeSeqResponse(final String commandName) throws Exception {
        final String className = CommandUtil.GET_CLASS_FOR_PROCESSING.get(commandName);
        return (CommandResponseProcessor.ImmediateSeqResponseProcessor)Class.forName(className).newInstance();
    }
    
    static {
        CommandUtil.commandUtil = null;
        ERROR_CODES_AND_REMARKS = new HashMap<String, String>() {
            {
                this.put("21005", "dc.mdm.identical_profile_already_exist");
                this.put("4018", "mdm.profile.apn_already_exist_error_msg");
                this.put("4019", "dc.mdm.kiosk.error.msg.multiple.kiosk.payloads");
                this.put("48000", "dc.mdm.kiosk.conflicting.kiosk.payload");
                this.put("3002", "dc.mdm.kiosk.conflicting.kiosk.payload");
            }
        };
        GET_CLASS_FOR_PROCESSING = new HashMap<String, String>() {
            {
                this.put("InstallManagedSettings", "com.me.mdm.server.command.ios.MGSettingCmdResponseProcessor");
                this.put("InstallProfile", "com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor");
                this.put("KioskDefaultRestriction", "com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor");
                this.put("ManagedApplicationList", "com.me.mdm.server.apps.IOSManagedAppListResponseProcessor");
                this.put("InstallEnterpriseApplication", "com.me.mdm.server.apps.MacOSInstallEnterpriseAppResponseProcessor");
                this.put("InstallApplication", "com.me.mdm.server.apps.IOSInstallApplicationResponseProcessor");
                this.put("InstalledApplicationList", "com.me.mdm.server.apps.InstalledAppListResponseProcessor");
                this.put("RemoveProfile", "com.me.mdm.server.profiles.RemoveProfileResponseProcessor");
                this.put("RemoveKioskDefaultRestriction", "com.me.mdm.server.profiles.RemoveProfileResponseProcessor");
                this.put("DefaultMDMKioskProfile", "com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor");
                this.put("DefaultMDMRemoveKioskProfile", "com.me.mdm.server.profiles.RemoveProfileResponseProcessor");
                this.put("DeviceLock", "com.me.mdm.server.command.RemoteLockResponseProcessor");
                this.put("UnlockUserAccount", "com.me.mdm.server.command.DeviceUserUnlockResponseProcessor");
                this.put("DefaultAppCatalogWebClips", "com.me.mdm.server.apps.IOSAppCatalogResponseProcessor");
                this.put("DefaultAppCatalogWebClipsMigrate", "com.me.mdm.server.apps.IOSAppCatalogResponseProcessor");
                this.put("DefaultRemoveAppCatalogWebClips", "com.me.mdm.server.apps.IOSRemoveAppCatalogProcessor");
                this.put("AccountConfiguration", "com.me.mdm.server.deviceaccounts.AccountConfigResponseProcessor");
                this.put("InstallProfile;Collection=DefaultWebClipsPayload", "com.me.mdm.server.apps.IOSAppCatalogResponseProcessor");
                this.put("RemoveProfile;Collection=DefaultWebClipsPayload", "com.me.mdm.server.apps.IOSRemoveAppCatalogProcessor");
                this.put("ProfileList", "com.me.mdm.server.profiles.ios.IOSProfileListResponseProcessor");
                this.put("ProvisioningProfileList", "com.me.mdm.server.apps.provisioningprofiles.ProvProfileListResponseProcessor");
                this.put("RestrictOSUpdates", "com.me.mdm.server.updates.osupdates.ios.IOSRestrictOSUpdateResponseProcessor");
                this.put("RemoveRestrictOSUpdates", "com.me.mdm.server.updates.osupdates.ios.IOSRemoveRestrictOSUpdateResProcessor");
                this.put("LockScreenMessages", "com.me.mdm.server.profiles.ios.response.IOSLockScreenResponseProcessor");
                this.put("RemoveUserInstalledProfile", "com.me.mdm.server.profiles.ios.IOSRemUserInstalledProfileResProcessor");
                this.put("SingletonRestriction", "com.me.mdm.server.profiles.ios.IOSSingletonRestrictResProcessor");
                this.put("IOSRemoveDeviceNameRestriction", "com.me.mdm.server.inv.ios.IOSDevNameRemoveResProcessor");
                this.put("RemoveSingletonRestriction", "com.me.mdm.server.profiles.ios.IOSSingletonRemRestrictResProcessor");
                this.put("RemoveAffectedSingletonRestriction", "com.me.mdm.server.profiles.ios.IOSSingletonRemAffectedResProcessor");
                this.put("DeviceName", "com.me.mdm.server.inv.ios.IOSDevNameResProcessor");
                this.put("Restrictions", "com.me.mdm.server.inv.ios.ResponseProcessor.DeviceRestrictionAppliedListResponseProcessor");
                this.put("RestrictionProfileStatus", "com.me.mdm.server.inv.ios.ResponseProcessor.DeviceRestrictionAppliedListResponseProcessor");
                this.put("MacFirmwarePreSecurityInfo", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFirmwareFirmwarePreSecurityInfoResponseProcessor");
                this.put("MacFirmwareVerifyPassword", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFirmwareFirmwareVerifyFirmwarePasswordResponseProcessor");
                this.put("MacFirmwareSetPasscode", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFirmwareFirmwareSetFirmwarePasswordResponseProcessor");
                this.put("MacFirmwareClearPasscode", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFirmwareFirmwareClearFirmwarePasswordResponseProcessor");
                this.put("MacFirmwarePostSecurityInfo", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFirmwareFirmwarePostSecurityInfoResponseProcessor");
                this.put("DisablePasscode", "com.me.mdm.server.profiles.ios.response.IOSPasscodeDisableResponseProcessor");
                this.put("RemoveDisablePasscode", "com.me.mdm.server.profiles.ios.response.IOSRemovePasscodeDisableResponseProcessor");
                this.put("ClearPasscodeForPasscodeRestriction", "com.me.mdm.server.inv.ios.IOSClearDisablePasscodeResProcessor");
                this.put("SecurityInfo", "com.me.mdm.server.inv.ios.IOSSecurityInfoResponseProcessor");
                this.put("RestrictPasscode", "com.me.mdm.server.profiles.ios.response.IOSRestrictPasscodeResponseProcessor");
                this.put("RemoveRestrictedPasscode", "com.me.mdm.server.profiles.ios.response.IOSRemoveRestrictPasscodeResponseProcessor");
                this.put("ClearPasscodeRestriction", "com.me.mdm.server.profiles.ios.response.IOSClearRestrictPasscodeResponseProcessor");
                this.put("ClearPasscode", "com.me.mdm.server.inv.ios.IOSClearPasscodeResponseProcessor");
                this.put("SingleWebAppKioskAppConfiguration", "com.me.mdm.server.profiles.ios.response.IOSSingleWebAppConfigurationResponseProcessor");
                this.put("SingleWebAppKioskFeedback", "com.me.mdm.server.profiles.ios.response.IOSSingleWebAppFeedbackResponseProcessor");
                this.put("RemoveSingleWebAppKioskAppConfiguration", "com.me.mdm.server.profiles.RemoveProfileResponseProcessor");
                this.put("RemoveSingleWebAppKioskFeedback", "com.me.mdm.server.profiles.RemoveProfileResponseProcessor");
                this.put("OSUpdateStatus", "com.me.mdm.server.updates.osupdates.ios.IOSUpdateStatusResponseHandler");
                this.put("UserList", "com.me.mdm.server.apple.command.response.responseprocessor.AppleUserListResponseProcessor");
                this.put("SharedDeviceConfiguration", "com.me.mdm.server.apple.command.response.responseprocessor.AppleSharedDeviceConfigurationResponseProcessor");
                this.put("MacFileVaultPersonalKeyRotate", "com.me.mdm.server.profiles.mac.configresponseprocessor.MacFilevaultPersonalRecoveryKeyRotateResponseProcessor");
                this.put(RecoveryLock.PRE_SECURITY.command, "com.me.mdm.server.profiles.mac.recoverylock.RecoveryLockPreSecurityInfoResponse");
                this.put("SetRecoveryLock", "com.me.mdm.server.profiles.mac.recoverylock.RecoveryLockSetPasswordResponse");
                this.put(RecoveryLock.VERIFY_PASSWORD.command, "com.me.mdm.server.profiles.mac.recoverylock.RecoveryLockVerifyPasswordResponse");
                this.put(RecoveryLock.CLEAR_PASSWORD.command, "com.me.mdm.server.profiles.mac.recoverylock.RecoveryLockClearPasswordResponse");
                this.put(RecoveryLock.POST_SECURITY.command, "com.me.mdm.server.profiles.mac.recoverylock.RecoveryLockPostSecurityInfoResponse");
                this.put("SharedDeviceRestrictions", "com.me.mdm.server.apple.command.response.responseprocessor.AppleSharedDeviceRestrictionResponseProcessor");
                this.put("RemoveSharedDeviceRestrictions", "com.me.mdm.server.apple.command.response.responseprocessor.AppleSharedDeviceRestrictionRemoveResponseProcessor");
                this.put("AppleDeviceAttestation", "com.me.mdm.server.inv.ios.DeviceAttestation.AppleDeviceAttestationResponseHandler");
            }
        };
    }
}
