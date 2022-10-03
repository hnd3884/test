package com.me.mdm.server.seqcommands.windows;

import org.json.JSONArray;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.server.seqcommands.SeqCmdConstants;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler;

public class WindowsSeqCmdResponseHandler extends BaseSeqCmdResponseHandler
{
    public Logger logger;
    
    public WindowsSeqCmdResponseHandler() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public JSONObject processLater(final JSONObject params) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("taskClass", (Object)"com.me.mdm.server.seqcommands.windows.task.WindowsProcessSeqCmdScheduler");
        responseJSON.put("timeOffset", 30000);
        return responseJSON;
    }
    
    @Override
    public Long onFailure(final JSONObject params) throws Exception {
        final Long returnCommand = SeqCmdConstants.ABORT_COMMAND;
        final String commandUUID = String.valueOf(params.get("commandUUID"));
        Integer status = 7;
        final Long resID = params.getLong("resourceID");
        final String commandName = params.getJSONObject("initialParams").optString("commandName");
        final JSONObject currentCommandParams = params.getJSONObject("CurCmdParam");
        if (commandUUID.contains("InstallApplication") || commandUUID.contains("WinAppInstallStatusQuery") || commandUUID.contains("UpdateApplication")) {
            String remarks = "dc.db.mdm.apps.status.Failed";
            if (commandUUID.contains("WinAppInstallStatusQuery")) {
                final JSONObject statusJSON = currentCommandParams.getJSONObject("installStatus");
                final JSONObject baseCmdJSON = SeqCmdUtils.getInstance().getBaseCmdDetailsforResource(resID);
                final String redirectURL = WindowsSeqCmdUtil.getInstance().getRedirectURLforApp(baseCmdJSON.optLong("COMMAND_ID"));
                final String lastError = statusJSON.optString("lastError", "");
                final String lastErroDesc = statusJSON.optString("lastErrorDescription", "");
                remarks = WindowsSeqCmdUtil.getInstance().getErrorRemarkForAppInstalltion(lastError, lastErroDesc);
                if (lastError.contains("-2147009293") || lastError.contains("-2147009287")) {
                    remarks = remarks + "@@@" + redirectURL;
                }
            }
            else if ((commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication")) && currentCommandParams.has("statusMap") && WpCompanyHubAppHandler.getInstance().isMSIAlreadyInstalledStatus(currentCommandParams)) {
                remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                status = 6;
            }
            final String collectionId = (String)SeqCmdDBUtil.getInstance().getCmdScopeParamforResource(resID, "CollectionID");
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resID, collectionId, status, remarks);
            final Long appGrpID = MDMUtil.getInstance().getAppGroupIDFromCollection(new Long(collectionId));
            final Long appID = MDMUtil.getInstance().getAppIdAssociatedForResource(appGrpID, resID);
            if (appGrpID != null && appID != null && status != 6) {
                final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                handler.updateAppInstallationStatusFromDevice(resID, appGrpID, appID, 0, remarks, 0);
            }
            if (commandName != null && commandName.toLowerCase().contains("profile")) {
                final Long profileCollection = params.getJSONObject("CommandLevelParams").getLong("baseCollection");
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resID, profileCollection + "", 7, "mdm.profile.windows.kiosk_failure");
            }
        }
        else if (commandUUID.contains("InstallProfile")) {
            final String collectionId2 = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resID, collectionId2 + "", 7, "mdm.profile.windows.kiosk_failure_nouser@@@" + MDMUtil.getInstance().getUserNameforDevice(resID));
        }
        return returnCommand;
    }
    
    @Override
    public Long onSuccess(final JSONObject params) throws Exception {
        final String commandUUID = String.valueOf(params.get("commandUUID"));
        final Long resID = params.getLong("resourceID");
        final JSONObject initialParams = params.optJSONObject("initialParams");
        Long baseCommandID = null;
        try {
            final JSONObject baseCmdJSON = SeqCmdUtils.getInstance().getBaseCmdDetailsforResource(resID);
            baseCommandID = baseCmdJSON.getLong("COMMAND_ID");
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while obtaining base cmd JSON", (Throwable)ex);
        }
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        if (commandUUID.contains("WinAppInstallStatusQuery")) {
            final Long successCnt = params.getJSONObject("cmdScopeParams").optLong("successCnt", 0L);
            final String remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
            final String collectionId = (String)SeqCmdDBUtil.getInstance().getCmdScopeParamforResource(resID, "CollectionID");
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resID, collectionId, 6, remarks);
            final Long appgroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
            final Long appID = MDMUtil.getInstance().getAppIdAssociatedForResource(appgroupID, resID);
            if (appgroupID != null && appID != null) {
                handler.updateAppInstallationDetailsFromDevice(resID, appgroupID, appID, 2, remarks, 0);
                final List resList = new ArrayList();
                resList.add(resID);
                DeviceCommandRepository.getInstance().addSyncAppCatalogCommand(resList);
                DeviceCommandRepository.getInstance().addAppCatalogStatusSummaryCommand(resList);
                if (initialParams != null && initialParams.optJSONObject("isMSIJson") != null && initialParams.optJSONObject("isMSIJson").optBoolean(String.valueOf(baseCommandID), (boolean)Boolean.FALSE)) {
                    final Properties props = new Properties();
                    props.setProperty("SCOPE", String.valueOf(0));
                    new MDDeviceInstalledAppsHandler().addOrUpdateWpMSIInstalledAppRel(resID, appID, props);
                }
            }
            params.getJSONObject("cmdScopeParams").put("successCnt", successCnt + 1L);
        }
        else if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication")) {
            final String collectionId2 = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final Long appgroupID2 = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId2));
            final String appFileLoc = (String)DBUtil.getValueFromDB("MdPackageToAppData", "APP_GROUP_ID", (Object)appgroupID2, "APP_FILE_LOC");
            if (appFileLoc.contains(".xap")) {
                final String remarks2 = "dc.db.mdm.collection.Successfully_installed_the_app";
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resID, collectionId2, 6, remarks2);
                final Long appID = MDMUtil.getInstance().getAppIdAssociatedForResource(appgroupID2, resID);
                if (appgroupID2 != null && appID != null) {
                    handler.updateAppInstallationDetailsFromDevice(resID, appgroupID2, appID, 2, remarks2, 0);
                }
            }
        }
        return super.onSuccess(params);
    }
    
    @Override
    public boolean subCommandPreProcessor(final Long resourceID, final Long commandID, final SequentialSubCommand sequentialSubCommand) {
        String cmdUUID = null;
        boolean isApplicable = true;
        String remarks = "";
        boolean iscmdRequired = true;
        try {
            cmdUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            final HashMap predata = WindowsSeqCmdUtil.getInstance().getPreData(resourceID);
            final JSONObject cmdParms = sequentialSubCommand.params;
            this.saveRequiredParamsForCommand(resourceID, cmdUUID, commandID, cmdParms);
            String baseCmdUUID = "";
            Long baseCommandID = null;
            baseCmdUUID = predata.get("baseCmdUUID");
            baseCommandID = predata.get("baseCommandID");
            if (baseCmdUUID.contains("InstallApplication")) {
                try {
                    final JSONObject initialParams = cmdParms.optJSONObject("initialParams");
                    final Boolean isMSI = initialParams.optJSONObject("isMSIJson") != null && initialParams.optJSONObject("isMSIJson").optBoolean(String.valueOf(baseCommandID), (boolean)Boolean.FALSE);
                    if (!isMSI) {
                        final JSONObject collnToApplicableResList = initialParams.optJSONObject("collectionToApplicableResource");
                        final String sCollectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(baseCmdUUID);
                        if (collnToApplicableResList != null) {
                            JSONArray resArr = collnToApplicableResList.optJSONArray(sCollectionId);
                            resArr = JSONUtil.convertToLongJSONArray(resArr);
                            final Boolean isExists = JSONUtil.checkValueExistsInJSONArray(resArr, resourceID, null);
                            if (!isExists) {
                                return Boolean.FALSE;
                            }
                        }
                    }
                    final JSONObject compatibilityJSON = new JSONObject();
                    compatibilityJSON.put("isMSI", (Object)isMSI);
                    compatibilityJSON.put("resourceID", (Object)resourceID);
                    compatibilityJSON.put("baseUUID", (Object)baseCmdUUID);
                    final JSONObject cmdApplicable = WindowsSeqCmdUtil.getInstance().checkCollectionProfileNotApplicableForResource(compatibilityJSON);
                    isApplicable = !cmdApplicable.getBoolean("doNotSendCmd");
                    if (isApplicable) {
                        final Long appId = (Long)cmdApplicable.get("appID");
                        if (cmdUUID.contains("EnableSideloadApps") && new AppsUtil().isAppPurchasedFromPortal(appId)) {
                            isApplicable = false;
                        }
                    }
                    remarks = String.valueOf(cmdApplicable.get("remarks"));
                }
                catch (final JSONException e) {
                    this.logger.log(Level.WARNING, (Throwable)e, () -> "Applicability not verified, sending command anyway resID: " + n);
                }
                catch (final DataAccessException e2) {
                    this.logger.log(Level.WARNING, (Throwable)e2, () -> "Applicability not verified, sending command anyway resID: " + n2);
                }
                catch (final Exception e3) {
                    this.logger.log(Level.WARNING, e3, () -> "Applicability not verified, sending command anyway resID: " + n3);
                }
            }
            else if (baseCmdUUID.contains("InstallProfile")) {
                final JSONObject jsonObject = cmdParms.optJSONObject("CommandLevelParams");
                final String appInstallUUID = jsonObject.optString("appCmdUUID", (String)null);
                if (appInstallUUID != null) {
                    final JSONObject compatibilityJSON = new JSONObject();
                    compatibilityJSON.put("isMSI", false);
                    compatibilityJSON.put("resourceID", (Object)resourceID);
                    compatibilityJSON.put("baseUUID", (Object)appInstallUUID);
                    final JSONObject cmdApplicable = WindowsSeqCmdUtil.getInstance().checkCollectionProfileNotApplicableForResource(compatibilityJSON);
                    isApplicable = !cmdApplicable.getBoolean("doNotSendCmd");
                    if (cmdUUID.contains("InstallProfile")) {
                        isApplicable = true;
                    }
                }
            }
            if (cmdUUID.contains("EnableSideloadApps")) {
                final JSONObject initialParams = cmdParms.optJSONObject("initialParams");
                if (initialParams != null && initialParams.optJSONObject("isMSIJson") != null && initialParams.optJSONObject("isMSIJson").optBoolean(String.valueOf(baseCommandID), (boolean)Boolean.FALSE)) {
                    iscmdRequired = Boolean.FALSE;
                }
            }
            else if (cmdUUID.contains("DisableSideloadApps") || cmdUUID.contains("SideloadNotConfigured")) {
                iscmdRequired = false;
            }
            else if (cmdUUID.contains("WinAppInstallStatusQuery")) {
                iscmdRequired = true;
            }
            else if (cmdUUID.contains("InstallApplication")) {
                if (!isApplicable) {
                    try {
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID), 8, remarks);
                    }
                    catch (final SyMException e4) {
                        this.logger.log(Level.WARNING, (Throwable)e4, () -> "Exception in updating status " + n4);
                    }
                }
                else {
                    iscmdRequired = WindowsSeqCmdUtil.getInstance().isAppCommandRequiredforResource(cmdUUID, resourceID);
                }
            }
            else if (cmdUUID.contains("UpdateApplication")) {
                iscmdRequired = WindowsSeqCmdUtil.getInstance().isAppCommandRequiredforResource(cmdUUID, resourceID);
            }
            else if (cmdUUID.contains("InstalledApplicationList")) {
                final JSONObject initialParams = cmdParms.optJSONObject("initialParams");
                if (!ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f) || (initialParams != null && initialParams.optJSONObject("isMSIJson") != null && initialParams.optJSONObject("isMSIJson").optBoolean(String.valueOf(baseCommandID), (boolean)Boolean.FALSE))) {
                    iscmdRequired = false;
                }
            }
            else if (cmdUUID.contains("InstallProfile")) {
                final Long successCnt = cmdParms.getJSONObject("cmdScopeParams").optLong("successCnt", 0L);
                if (successCnt == 0L) {
                    final JSONObject jsonObject2 = cmdParms.optJSONObject("CommandLevelParams");
                    if (jsonObject2 != null && !jsonObject2.getBoolean("hasSystemApps")) {
                        try {
                            remarks = "mdm.profile.distribution.waitingfordeviceinfo";
                            int status = 18;
                            final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                            if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(deviceDetails.modelType)) {
                                iscmdRequired = false;
                                status = 7;
                                remarks = "mdm.windows.profile.kiosk_app_not_present";
                            }
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID), status, remarks);
                        }
                        catch (final SyMException e5) {
                            this.logger.log(Level.WARNING, (Throwable)e5, () -> "Exception in updating status " + n5);
                        }
                    }
                }
            }
            else if (cmdUUID.contains("ApplicationConfiguration")) {
                iscmdRequired = WindowsSeqCmdUtil.getInstance().isAppConfigExists(baseCommandID);
            }
        }
        catch (final Exception e6) {
            this.logger.log(Level.WARNING, e6, () -> "Exception in seq cmd pre handler " + n6);
        }
        Label_1067: {
            if (!isApplicable) {
                if (!cmdUUID.contains("InstallApplication")) {
                    if (!cmdUUID.contains("UpdateApplication")) {
                        break Label_1067;
                    }
                }
                try {
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID), 8, remarks);
                }
                catch (final SyMException e7) {
                    this.logger.log(Level.WARNING, "Exception in updating status {0}", resourceID);
                }
            }
        }
        this.logger.log(Level.INFO, "Returning {0} for command {1} for resource {2} from preprocessor", new Object[] { iscmdRequired && isApplicable, commandID, resourceID });
        return iscmdRequired && isApplicable;
    }
    
    private void saveRequiredParamsForCommand(final Long resID, final String cmdUUID, final Long commandID, final JSONObject params) {
        Boolean isUpdate = Boolean.FALSE;
        JSONObject cmdScopeParams = params.optJSONObject("cmdScopeParams");
        if (cmdScopeParams == null) {
            cmdScopeParams = new JSONObject();
        }
        try {
            if (cmdUUID.contains("InstallApplication") || cmdUUID.contains("UpdateApplication")) {
                cmdScopeParams.put("PackageFamilyName", (Object)WindowsSeqCmdUtil.getInstance().getpackageFamilyNameForCollection(DeviceCommandRepository.getInstance().getCommandID(cmdUUID)));
                cmdScopeParams.put("CollectionID", (Object)MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID));
                cmdScopeParams.put("InstallAppCommandID", (Object)commandID);
                isUpdate = Boolean.TRUE;
            }
            else if (cmdUUID.contains("WinAppInstallStatusQuery")) {
                cmdScopeParams.put("retryCount", cmdScopeParams.optInt("retryCount", 0) + 1);
                isUpdate = Boolean.TRUE;
            }
            params.put("cmdScopeParams", (Object)cmdScopeParams);
            if (isUpdate) {
                this.setParams(resID, params);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "error while setting params", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "error while setting params", e2);
        }
        this.logger.log(Level.WARNING, "setting params {0} for resource {1}", new Object[] { params, resID });
    }
}
