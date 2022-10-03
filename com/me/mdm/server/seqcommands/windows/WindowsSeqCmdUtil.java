package com.me.mdm.server.seqcommands.windows;

import java.util.Hashtable;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import java.util.logging.Level;
import java.util.List;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WindowsSeqCmdUtil
{
    public Logger logger;
    public static WindowsSeqCmdUtil windowsSequentialCommandUtil;
    public static final String WINDOWS_SEQ_CMD_HANDLER = "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler";
    public static final String IS_MSI_JSON = "isMSIJson";
    public static final String COLLN_TO_APPLICABLE_RES_LIST = "collectionToApplicableResource";
    
    public WindowsSeqCmdUtil() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public static WindowsSeqCmdUtil getInstance() {
        if (WindowsSeqCmdUtil.windowsSequentialCommandUtil == null) {
            WindowsSeqCmdUtil.windowsSequentialCommandUtil = new WindowsSeqCmdUtil();
        }
        return WindowsSeqCmdUtil.windowsSequentialCommandUtil;
    }
    
    public void addSeqCommand(final JSONObject cmdParams) throws Exception {
        final Long collectionID = cmdParams.optLong("CollectionID");
        final boolean isAppConfig = cmdParams.optBoolean("isAppPolicy", false);
        if (collectionID != null) {
            if (isAppConfig) {
                final List collectionList = new ArrayList();
                collectionList.add(collectionID);
                final List collectionCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
                final Long cmdID = collectionCmdList.get(0);
                final Long seqID = (Long)DBUtil.getValueFromDB("MdCommandToSequentialCommand", "COMMAND_ID", (Object)cmdID, "SEQUENTIAL_COMMAND_ID");
                if (seqID == null) {
                    this.addAppSeqCommand(collectionID);
                }
            }
            else {
                final JSONArray configIDArray = (JSONArray)cmdParams.get("configIDList");
                final List configIDList = new ArrayList();
                for (int i = 0; i < configIDArray.length(); ++i) {
                    configIDList.add(configIDArray.get(i));
                }
                if (configIDList.contains(608)) {
                    this.addKioskCommand(collectionID);
                }
            }
        }
    }
    
    private void addAppSeqCommand(final Long collectionID) throws Exception {
        this.logger.log(Level.INFO, "Generating Windows Sequential Cmd for app with Collection ID : {0}", collectionID);
        final HashMap commandMap = DeviceCommandRepository.getInstance().getCommandIdsForCollection(collectionID);
        final Long cmdID = commandMap.get("InstallApplication");
        final Long updateCmdId = commandMap.get("UpdateApplication");
        final Long appConfigCmdId = commandMap.get("ApplicationConfiguration");
        int order = 1;
        if (cmdID != null) {
            final JSONArray commands = new JSONArray();
            final Long enableCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("EnableSideloadApps", 40);
            final Long disableCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("DisableSideloadApps", 40);
            final Long notCnfCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("SideloadNotConfigured", 40);
            final Long ScanCmdID = DeviceCommandRepository.getInstance().addCommand("InstalledApplicationList");
            final Long StatusCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("WinAppInstallStatusQuery", 40);
            commands.put((Object)this.generateSubCommandJSON(enableCmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            commands.put((Object)this.generateSubCommandJSON(updateCmdId, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            commands.put((Object)this.generateSubCommandJSON(cmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            commands.put((Object)this.generateSubCommandJSON(StatusCmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            if (appConfigCmdId != null) {
                commands.put((Object)this.generateSubCommandJSON(appConfigCmdId, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            }
            commands.put((Object)this.generateSubCommandJSON(disableCmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            commands.put((Object)this.generateSubCommandJSON(notCnfCmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            commands.put((Object)this.generateSubCommandJSON(ScanCmdID, order++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            final JSONObject sequentialCommandJSON = new JSONObject();
            sequentialCommandJSON.put("subCommands", (Object)commands);
            sequentialCommandJSON.put("basecmdID", (Object)cmdID);
            sequentialCommandJSON.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential" + collectionID));
            sequentialCommandJSON.put("allowImmediateProcessing", true);
            sequentialCommandJSON.put("timeout", (Object)new Long(600000L));
            final JSONArray seqCommands = new JSONArray();
            seqCommands.put((Object)sequentialCommandJSON);
            final JSONObject param = new JSONObject();
            param.put("SequentialCommands", (Object)seqCommands);
            this.logger.log(Level.INFO, "Windows Sequential cmd getting Added  : {0}", param);
            SeqCmdDBUtil.getInstance().addSequentialCommands(param);
        }
    }
    
    private void addKioskCommand(final Long profileCollectionID) throws Exception {
        int i = 1;
        boolean hasSystemApps = false;
        final JSONArray subcommandsArray = new JSONArray();
        final Long enableCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("EnableSideloadApps", 40);
        final Long statusCmdID = DeviceCommandRepository.getInstance().addCommandWithPriority("WinAppInstallStatusQuery", 40);
        String appCmdUUID = null;
        final SelectQuery KioskAppsSelect = this.getKioskAppsSelectQuery(profileCollectionID);
        final DataObject dataObject1 = DataAccess.get(KioskAppsSelect);
        final Iterator it = dataObject1.getRows("WindowsKioskPolicyApps");
        while (it.hasNext()) {
            final Row row1 = it.next();
            final Long appgrpid = (Long)row1.get("APP_GROUP_ID");
            final Long collectionID = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appgrpid);
            final List collectionList = new ArrayList();
            collectionList.add(collectionID);
            final List collectionCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
            final Long cmdID = (collectionCmdList.size() == 0) ? null : collectionCmdList.get(0);
            if (cmdID != null) {
                appCmdUUID = SeqCmdUtils.getInstance().getUUIDforcommandID(cmdID);
                subcommandsArray.put((Object)this.generateSubCommandJSON(enableCmdID, i++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
                subcommandsArray.put((Object)this.generateSubCommandJSON(cmdID, i++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
                subcommandsArray.put((Object)this.generateSubCommandJSON(statusCmdID, i++, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
            }
            else {
                hasSystemApps = true;
                this.logger.log(Level.INFO, "App not Managed : {0}: not added in sequential command.", collectionID);
            }
        }
        if (dataObject1.isEmpty()) {
            hasSystemApps = true;
        }
        final List collectionList2 = new ArrayList();
        collectionList2.add(profileCollectionID);
        final Long cmdID2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList2, "InstallProfile").get(0);
        subcommandsArray.put((Object)this.generateSubCommandJSON(cmdID2, i, "com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler"));
        final JSONObject sequentialCommandJSON = new JSONObject();
        final JSONObject params = new JSONObject();
        params.put("hasSystemApps", hasSystemApps);
        if (appCmdUUID != null) {
            params.put("appCmdUUID", (Object)appCmdUUID);
            params.put("baseCollection", (Object)profileCollectionID);
        }
        sequentialCommandJSON.put("subCommands", (Object)subcommandsArray);
        sequentialCommandJSON.put("basecmdID", (Object)cmdID2);
        sequentialCommandJSON.put("params", (Object)params);
        sequentialCommandJSON.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential" + profileCollectionID));
        sequentialCommandJSON.put("allowImmediateProcessing", true);
        sequentialCommandJSON.put("timeout", (Object)new Long(600000L));
        final JSONArray commands = new JSONArray();
        commands.put((Object)sequentialCommandJSON);
        final JSONObject param = new JSONObject();
        param.put("SequentialCommands", (Object)commands);
        SeqCmdDBUtil.getInstance().addSequentialCommands(param);
    }
    
    private JSONObject generateSubCommandJSON(final Long cmdID, final int order, final String handler) throws JSONException {
        final JSONObject subCommand = new JSONObject();
        subCommand.put("handler", (Object)"com.me.mdm.server.seqcommands.windows.WindowsSeqCmdResponseHandler");
        subCommand.put("order", order);
        subCommand.put("cmd_id", (Object)cmdID);
        return subCommand;
    }
    
    protected Integer getTrustedAppsConfigForResource(final Long resID) {
        Integer retVal = null;
        try {
            retVal = (Integer)DBUtil.getValueFromDB("MdWindowsDeviceRestriction", "RESOURCE_ID", (Object)resID, "TRUSTED_APPS_INSTALL");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while get command uuid for id", e);
        }
        return (retVal == null) ? 65535 : retVal;
    }
    
    protected boolean isTrustedAppsCmdRequired(final Long resID, final String cmdUUID) {
        final int curSetting = this.getTrustedAppsConfigForResource(resID);
        final int oldConfig = (int)SeqCmdDBUtil.getInstance().getCmdScopeParamforResource(resID, "InitialSideloadConf");
        boolean iscmdreq = true;
        if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resID, 10.0f)) {
            if (cmdUUID.contains("EnableSideloadApps") && curSetting == 1) {
                iscmdreq = false;
            }
            else if (cmdUUID.contains("DisableSideloadApps") && oldConfig == 65535) {
                iscmdreq = false;
            }
            else if (cmdUUID.contains("SideloadNotConfigured") && oldConfig == 0) {
                iscmdreq = false;
            }
        }
        else {
            iscmdreq = false;
        }
        return iscmdreq;
    }
    
    protected boolean isAppInstallQueryCmdRequired(final Long resID, final Long commandID) {
        boolean isCmdReq = true;
        if (!ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resID, 10.0f)) {
            isCmdReq = false;
        }
        try {
            final String collectionID = (String)SeqCmdDBUtil.getInstance().getCmdScopeParamforResource(resID, "CollectionID");
            final Long appgroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionID));
            final String appFileLoc = (String)DBUtil.getValueFromDB("MdPackageToAppData", "APP_GROUP_ID", (Object)appgroupID, "APP_FILE_LOC");
            if (appFileLoc.contains(".xap")) {
                isCmdReq = false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "error in getting PFN : ", e);
        }
        return isCmdReq;
    }
    
    protected String getpackageFamilyNameForCollection(final Long cmdID) {
        String pfn = null;
        try {
            final Long appgroupID = AppsUtil.getInstance().getAppGroupIdFormCommandId(cmdID);
            pfn = (String)DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appgroupID, "IDENTIFIER");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "error in getting PFN : ", e);
        }
        return pfn;
    }
    
    public String removeTypeFromUUID(final String cmd) {
        String cmdUUID = cmd;
        if (cmd.toLowerCase().contains("type") && null != cmd && cmd.length() > 0) {
            final int endIndex = cmd.lastIndexOf(";");
            if (endIndex != -1) {
                cmdUUID = cmd.substring(0, endIndex);
            }
        }
        return cmdUUID;
    }
    
    public void processWinSeqCmd(final String commandUUID, final JSONObject seqParams) {
        this.logger.log(Level.INFO, "processing Seq response in windows commandUUID {0} seqParmas : {1}", new Object[] { commandUUID, seqParams });
        final JSONObject Seqresponse = new JSONObject();
        final JSONObject params = new JSONObject();
        Long resourceID = null;
        try {
            final int status = seqParams.getInt("status");
            resourceID = seqParams.getLong("resourceID");
            if (seqParams.has("statusMap")) {
                params.put("statusMap", (Object)seqParams.getJSONObject("statusMap"));
            }
            final JSONObject currentSeqCmdParams = SeqCmdDBUtil.getInstance().getParams(resourceID);
            if (status == 200) {
                if (commandUUID.contains("WinAppInstallStatusQuery")) {
                    final JSONObject statusJSON = seqParams.optJSONObject("installStatus");
                    params.put("installStatus", (Object)statusJSON);
                    final int installStatus = statusJSON.getInt("status");
                    final Long commandID = currentSeqCmdParams.getJSONObject("cmdScopeParams").getLong("InstallAppCommandID");
                    final JSONObject currentSeqCmdInitialParams = currentSeqCmdParams.optJSONObject("initialParams");
                    int action = 4;
                    if (currentSeqCmdInitialParams.has("isMSIJson") && currentSeqCmdInitialParams.getJSONObject("isMSIJson").getBoolean(String.valueOf(commandID))) {
                        if (installStatus == 10 || installStatus == 20 || installStatus == 25 || installStatus == 40 || installStatus == 48 || installStatus == 50 || installStatus == 55) {
                            action = 4;
                        }
                        else if (installStatus == 70) {
                            action = 1;
                        }
                        else if (installStatus == 30 || installStatus == 60) {
                            action = 2;
                        }
                    }
                    else if (installStatus == 1 || installStatus == 0) {
                        action = 4;
                    }
                    else if (installStatus == 3) {
                        action = 1;
                    }
                    else if (installStatus == 2) {
                        action = 2;
                    }
                    Seqresponse.put("action", (Object)new Integer(action));
                }
                else if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication")) {
                    Seqresponse.put("action", (Object)new Integer(1));
                }
                else {
                    Seqresponse.put("action", (Object)new Integer(1));
                }
            }
            else {
                Seqresponse.put("action", (Object)new Integer(2));
            }
            Seqresponse.put("resourceID", (Object)resourceID);
            Seqresponse.put("commandUUID", (Object)commandUUID);
            Seqresponse.put("params", (Object)params);
            SeqCmdRepository.getInstance().processSeqCommand(Seqresponse);
        }
        catch (final JSONException e) {
            this.sendFailureResponse(resourceID, commandUUID);
            this.logger.log(Level.WARNING, "error in generating Seq response ", (Throwable)e);
        }
    }
    
    private void sendFailureResponse(final Long resourceID, final String strCommandUuid) {
        this.logger.log(Level.WARNING, "failure Response being generated for {0}", resourceID);
        final JSONObject response = new JSONObject();
        try {
            response.put("action", (Object)new Integer(2));
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)strCommandUuid);
            final JSONObject params = new JSONObject();
            response.put("params", (Object)params);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Error in Json structure");
        }
    }
    
    public boolean isImmediateProcessingSubCommand(String cmdUUID) {
        boolean isImmediateProcessingSubCommand = true;
        if (cmdUUID.toLowerCase().contains("type")) {
            cmdUUID = getInstance().removeTypeFromUUID(cmdUUID);
        }
        final List<String> notImmidiateList = new ArrayList<String>() {
            {
                this.add("InstallApplication");
                this.add("WinAppInstallStatusQuery");
            }
        };
        if (notImmidiateList.contains(cmdUUID)) {
            isImmediateProcessingSubCommand = false;
        }
        return isImmediateProcessingSubCommand;
    }
    
    protected boolean isAppCommandRequiredforResource(final String cmdUUID, final Long resID) {
        boolean isCmdreq = true;
        final List resList = new ArrayList();
        resList.add(resID);
        final Long collectionID = Long.parseLong(MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID));
        try {
            final Properties props = AppsUtil.getInstance().getApplicableAppCommandForResources(collectionID, resList);
            final List updateList = ((Hashtable<K, ArrayList>)props).get("UpdateApplication");
            final List installList = ((Hashtable<K, ArrayList>)props).get("InstallApplication");
            if (cmdUUID.contains("UpdateApplication")) {
                if (updateList == null || updateList.isEmpty()) {
                    isCmdreq = false;
                }
            }
            else if (cmdUUID.contains("InstallApplication") && (installList == null || installList.isEmpty())) {
                isCmdreq = false;
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception in determining type of commadn required", (Throwable)e);
        }
        return isCmdreq;
    }
    
    public JSONObject checkCollectionProfileNotApplicableForResource(final JSONObject params) throws Exception {
        final JSONObject response = new JSONObject();
        String remarks = "";
        Boolean doNotSendCmd = Boolean.FALSE;
        final String commandUUID = (String)params.get("baseUUID");
        final Long resourceID = (Long)params.get("resourceID");
        final Boolean isMSI = (Boolean)params.get("isMSI");
        if (commandUUID.contains("InstallApplication") || commandUUID.contains("RemoveApplication") || commandUUID.contains("UpdateApplication")) {
            Long appID = null;
            final Long collectionID = Long.valueOf(commandUUID.substring(commandUUID.lastIndexOf("=") + 1));
            final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
            if (!isMSI) {
                appID = AppsUtil.getInstance().getCompatibleAppForResource(collectionID, deviceMap);
            }
            else if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(deviceMap.get("MODEL_TYPE"))) {
                appID = MDMUtil.getInstance().getAppIDsFromCollectionID(collectionID).get(0);
            }
            if (appID == null) {
                doNotSendCmd = true;
                remarks = "mdm.windows.app.no_compatible_package";
            }
            else {
                response.put("appID", (Object)appID);
                doNotSendCmd = false;
            }
        }
        try {
            response.put("doNotSendCmd", (Object)doNotSendCmd);
            response.put("remarks", (Object)remarks);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "error in checking if command applicable");
        }
        return response;
    }
    
    public boolean isAppConfigExists(final Long commandID) {
        boolean isAppConfigExists = false;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
        selectQuery.addJoin(new Join("MdCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppConfigTemplate", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AppConfigTemplate", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandID, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            isAppConfigExists = !dataObject.isEmpty();
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "error in checking if app config exists", (Throwable)e);
        }
        return isAppConfigExists;
    }
    
    private SelectQuery getKioskAppsSelectQuery(final Long collectionID) throws Exception {
        final List configDataIDs = ProfileConfigHandler.getConfigDataIds(collectionID, 608);
        final List configDataItemList = ProfileConfigHandler.getConfigDataItemIds(configDataIDs.get(0));
        final Table kioskpolicyTable = new Table("WindowsKioskPolicyApps");
        final SelectQuery KioskAppsSelect = (SelectQuery)new SelectQueryImpl(kioskpolicyTable);
        final Column all = new Column("WindowsKioskPolicyApps", "*");
        final Criteria configIDCriteria = new Criteria(new Column("WindowsKioskPolicyApps", 1), (Object)configDataItemList.get(0), 0);
        KioskAppsSelect.setCriteria(configIDCriteria);
        KioskAppsSelect.addSelectColumn(all);
        return KioskAppsSelect;
    }
    
    public String getErrorRemarkForAppInstalltion(final String lastError, final String lastErroDesc) {
        String remarks = null;
        if (lastError.contains("-2147023263") || lastErroDesc.contains("not supported by this processor type")) {
            remarks = "mdm.db.apps.status.failed_arch_mismatch";
        }
        else if (lastError.contains("-2147009281")) {
            remarks = "mdm.windows.app.failure2@@@http://mecms.manageengine.com/manageengine/mobile-device-management/kb/windows-unable-to-install-app.html#package-sign";
        }
        else if (lastError.contains("-2146762487")) {
            remarks = "mdm.windows.app.failure2@@@http://mecms.manageengine.com/manageengine/mobile-device-management/kb/windows-unable-to-install-app.html#package-sign";
        }
        else if (lastError.contains("-2147009293") || lastError.contains("-2147009287")) {
            remarks = "mdm.apps.dependency.certain_files_added";
        }
        else if (lastError.contains("-2147009285")) {
            remarks = "mdm.windows.app.failure4@@@http://mecms.manageengine.com/manageengine/mobile-device-management/kb/windows-unable-to-install-app.html#installed-package";
        }
        else if (lastError.contains("-2147009291")) {
            remarks = "mdm.windows.app.failure5";
        }
        else if (lastError.contains("-2147009283")) {
            remarks = "mdm.windows.app.failure6";
        }
        else if (lastError.contains("-2147009274")) {
            remarks = "mdm.windows.app.failure4@@@http://mecms.manageengine.com/manageengine/mobile-device-management/kb/windows-unable-to-install-app.html#installed-package";
        }
        else {
            this.logger.log(Level.INFO, "Windows App install Failure error code not found : lastError {0} lastErrorDesc {1}", new Object[] { lastError, lastErroDesc });
            remarks = "dc.db.mdm.apps.status.Failed";
        }
        return remarks;
    }
    
    public String getRedirectURLforApp(final Long commandID) throws SQLException {
        String retURL = "";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
        selectQuery.addJoin(new Join("MdCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        selectQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)commandID, 0));
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            Long packageID = null;
            Long releaseLabel = null;
            while (dataSet.next()) {
                packageID = (Long)dataSet.getValue("PACKAGE_ID");
                releaseLabel = (Long)dataSet.getValue("RELEASE_LABEL_ID");
            }
            retURL = "#/uems/mdm/manage/appRepo/apps/windows/enterprise?appId=" + packageID + "&labelId=" + releaseLabel;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "unable to get redirect for app dependency failure");
        }
        return retURL;
    }
    
    public HashMap getPreData(final Long resourceID) throws SQLException, QueryConstructionException {
        final HashMap hashMap = new HashMap();
        DMDataSetWrapper dataSet = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SequentialCmdExecutionStatus"));
            selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
            selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdCommandToSequentialCommand", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, "MdSequentialCommands", "MDSubCommand", 2));
            final Criteria criteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria statusCriteria = SeqCmdUtils.getInstance().getSeqCmdInprogressCriteria();
            selectQuery.setCriteria(criteria.and(statusCriteria));
            selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdCommands", "COMMAND_UUID"));
            selectQuery.addSelectColumn(Column.getColumn("MDSubCommand", "COMMAND_ID", "SubCommandID"));
            selectQuery.addSelectColumn(Column.getColumn("MDSubCommand", "COMMAND_UUID", "SubCommandUUID"));
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                hashMap.put("PARAMS", dataSet.getValue("PARAMS"));
                hashMap.put("baseCmdUUID", dataSet.getValue("COMMAND_UUID"));
                hashMap.put("baseCommandID", dataSet.getValue("COMMAND_ID"));
                hashMap.put("SubCommandID", dataSet.getValue("SubCommandID"));
                hashMap.put("SubCommandUUID", dataSet.getValue("SubCommandUUID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "error in getPreData()    ", e);
        }
        return hashMap;
    }
    
    static {
        WindowsSeqCmdUtil.windowsSequentialCommandUtil = null;
    }
}
