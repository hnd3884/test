package com.me.mdm.server.seqcommands.android;

import com.adventnet.sym.server.mdm.api.MdmInvDataProcessor;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Persistence;
import org.json.JSONException;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidSeqCmdUtil
{
    public Logger logger;
    public static AndroidSeqCmdUtil androidSequentialCommandUtil;
    
    public AndroidSeqCmdUtil() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public static AndroidSeqCmdUtil getInstance() {
        if (AndroidSeqCmdUtil.androidSequentialCommandUtil == null) {
            AndroidSeqCmdUtil.androidSequentialCommandUtil = new AndroidSeqCmdUtil();
        }
        return AndroidSeqCmdUtil.androidSequentialCommandUtil;
    }
    
    private void addKioskCommand(final Long CollectionID) {
        try {
            this.logger.log(Level.INFO, "Adding Sequential Command for Kiosk Profile : {0}", CollectionID);
            final JSONArray subcommandsArray = new JSONArray();
            List collectionList = new ArrayList();
            collectionList.add(CollectionID);
            Long cmdID = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallProfile").get(0);
            final JSONObject profileCommand = new JSONObject();
            profileCommand.put("handler", (Object)"com.me.mdm.server.seqcommands.android.AndroidSeqCmdResponseHandler");
            profileCommand.put("order", 1);
            profileCommand.put("cmd_id", (Object)cmdID);
            subcommandsArray.put((Object)profileCommand);
            final JSONObject sequentialCommandJSON = new JSONObject();
            sequentialCommandJSON.put("basecmdID", (Object)cmdID);
            int i = 2;
            final SelectQuery KioskAppsSelect = this.getKioskAppsSelectQuery(CollectionID);
            final DataObject dataObject1 = DataAccess.get(KioskAppsSelect);
            final Iterator it = dataObject1.getRows("AndroidKioskPolicyApps");
            final JSONArray pendingCollnIDs = new JSONArray();
            while (it.hasNext()) {
                final Row row1 = it.next();
                final Long appgrpid = (Long)row1.get("APP_GROUP_ID");
                final Long collectionID = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appgrpid);
                collectionList = new ArrayList();
                collectionList.add(collectionID);
                final List collectionCmdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
                cmdID = (collectionCmdList.isEmpty() ? null : collectionCmdList.get(0));
                if (cmdID != null) {
                    final int appType = AppsUtil.getInstance().getAppPackageTypeFromCollectionId(collectionID);
                    if (appType == 2) {
                        final JSONObject subcommand = new JSONObject();
                        subcommand.put("handler", (Object)"com.me.mdm.server.seqcommands.android.AndroidSeqCmdResponseHandler");
                        subcommand.put("order", i);
                        subcommand.put("cmd_id", (Object)cmdID);
                        subcommandsArray.put((Object)subcommand);
                        ++i;
                    }
                    else {
                        pendingCollnIDs.put((Object)collectionID);
                        this.logger.log(Level.INFO, "Playstore app : {0}: not added in sequential command.", collectionID);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "App not Managed : {0}: not added in sequential command.", collectionID);
                }
            }
            final SelectQuery KioskHiddenAppsSelect = this.getKioskHiddenAppsSelectQuery(CollectionID);
            final DataObject dataObject2 = DataAccess.get(KioskHiddenAppsSelect);
            final Iterator iterator = dataObject2.getRows("AndroidKioskPolicyBackgroundApps");
            while (iterator.hasNext()) {
                final Row row2 = iterator.next();
                final Long appgrpid2 = (Long)row2.get("APP_GROUP_ID");
                final Long collectionID2 = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appgrpid2);
                collectionList = new ArrayList();
                collectionList.add(collectionID2);
                final List collectionCmdList2 = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, "InstallApplication");
                cmdID = (collectionCmdList2.isEmpty() ? null : collectionCmdList2.get(0));
                if (cmdID != null) {
                    final int appType2 = AppsUtil.getInstance().getAppPackageTypeFromCollectionId(collectionID2);
                    if (appType2 == 2) {
                        final JSONObject subcommand2 = new JSONObject();
                        subcommand2.put("handler", (Object)"com.me.mdm.server.seqcommands.android.AndroidSeqCmdResponseHandler");
                        subcommand2.put("order", i);
                        subcommand2.put("cmd_id", (Object)cmdID);
                        subcommandsArray.put((Object)subcommand2);
                        ++i;
                    }
                    else {
                        pendingCollnIDs.put((Object)collectionID2);
                        this.logger.log(Level.INFO, "Playstore app : {0}: not added in sequential command.", collectionID2);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "App not Managed : {0}: not added in sequential command.", collectionID2);
                }
            }
            final Long cmdId = DeviceCommandRepository.getInstance().addCommand("SyncAppCatalog");
            final JSONObject syncappCatalog = new JSONObject();
            syncappCatalog.put("handler", (Object)"com.me.mdm.server.seqcommands.android.AndroidSeqCmdResponseHandler");
            syncappCatalog.put("order", i++);
            syncappCatalog.put("cmd_id", (Object)cmdId);
            subcommandsArray.put((Object)syncappCatalog);
            sequentialCommandJSON.put("subCommands", (Object)subcommandsArray);
            sequentialCommandJSON.put("SequentialCommandId", (Object)DeviceCommandRepository.getInstance().addSequentialCommand("Sequential" + CollectionID));
            sequentialCommandJSON.put("allowImmediateProcessing", true);
            sequentialCommandJSON.put("timeout", (Object)new Long(600000L));
            final JSONObject params = new JSONObject();
            if (pendingCollnIDs.length() < 1) {
                pendingCollnIDs.put(-1L);
            }
            params.put("pendingCollnIDs", (Object)pendingCollnIDs);
            sequentialCommandJSON.put("params", (Object)params);
            final JSONArray commands = new JSONArray();
            commands.put((Object)sequentialCommandJSON);
            final JSONObject param = new JSONObject();
            param.put("SequentialCommands", (Object)commands);
            SeqCmdDBUtil.getInstance().addSequentialCommands(param);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Creation of Sequential Command has failed : The Collection : {0} cannot be Executed sequentially {1}", new Object[] { CollectionID, e });
        }
    }
    
    public SelectQuery getKioskAppsSelectQuery(final Long collectionID) throws Exception {
        final List configDataIDs = ProfileConfigHandler.getConfigDataIds(collectionID, 557);
        final List configDataItemList = ProfileConfigHandler.getConfigDataItemIds(configDataIDs.get(0));
        final Table kioskpolicyTable = new Table("AndroidKioskPolicyApps");
        final SelectQuery KioskAppsSelect = (SelectQuery)new SelectQueryImpl(kioskpolicyTable);
        final Column all = new Column("AndroidKioskPolicyApps", "*");
        final Criteria configIDCriteria = new Criteria(new Column("AndroidKioskPolicyApps", 1), (Object)configDataItemList.get(0), 0);
        KioskAppsSelect.setCriteria(configIDCriteria);
        KioskAppsSelect.addSelectColumn(all);
        return KioskAppsSelect;
    }
    
    public SelectQuery getKioskHiddenAppsSelectQuery(final Long collectionID) throws Exception {
        final List configDataIDs = ProfileConfigHandler.getConfigDataIds(collectionID, 557);
        final List configDataItemList = ProfileConfigHandler.getConfigDataItemIds(configDataIDs.get(0));
        final Table kioskpolicyTable = new Table("AndroidKioskPolicyBackgroundApps");
        final SelectQuery KioskAppsSelect = (SelectQuery)new SelectQueryImpl(kioskpolicyTable);
        final Column all = new Column("AndroidKioskPolicyBackgroundApps", "*");
        final Criteria configIDCriteria = new Criteria(new Column("AndroidKioskPolicyBackgroundApps", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemList.get(0), 0);
        KioskAppsSelect.setCriteria(configIDCriteria);
        KioskAppsSelect.addSelectColumn(all);
        return KioskAppsSelect;
    }
    
    public void addSeqCommand(final HashMap cmdParams) {
        final List configIdList = cmdParams.get("ConfigIDList");
        final Long collectionID = cmdParams.get("CollectionID");
        if (configIdList != null && collectionID != null) {
            final JSONObject androidKioskDetails = ProfileAssociateHandler.getInstance().profileDetailsForAndroidKioskAutomation(collectionID);
            if (androidKioskDetails.optInt("KIOSK_MODE") == 0) {
                this.addKioskCommand(collectionID);
            }
        }
    }
    
    private void processKioskSeqResponse(final String strCommandUuid, final Long resourceID) throws Exception {
        final JSONObject response = new JSONObject();
        this.logger.log(Level.INFO, "Processing Sub Command({0}) of Kiosk Sequential command for : {1}", new Object[] { strCommandUuid, resourceID });
        if (strCommandUuid.startsWith("InstallApplication")) {
            response.put("action", (Object)new Integer(1));
        }
        else if (strCommandUuid.startsWith("InstallProfile")) {
            response.put("action", (Object)new Integer(1));
        }
        else if (strCommandUuid.startsWith("RemoveProfile")) {
            response.put("action", (Object)new Integer(1));
        }
        else if (strCommandUuid.startsWith("RemoveApplication")) {
            response.put("action", (Object)new Integer(1));
        }
        else if (strCommandUuid.startsWith("SyncAppCatalog")) {
            response.put("action", (Object)new Integer(1));
        }
        response.put("resourceID", (Object)resourceID);
        response.put("commandUUID", (Object)strCommandUuid);
        final JSONObject params = new JSONObject();
        response.put("params", (Object)params);
        response.put("isNotify", false);
        SeqCmdRepository.getInstance().processSeqCommand(response);
    }
    
    public void processSeqCommandResponse(final HashMap hashMap) {
        final String strUDID = hashMap.get("UDID");
        final String strCommandUuid = hashMap.get("CommandUUID");
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
        final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID);
        final Long baseCmd = SeqCmdUtils.getInstance().getBaseCommandIDforSequentialID(subCommand.SequentialCommandID);
        final Long collectionID = new Long(MDMUtil.getInstance().getCollectionIdFromCommandUUID(MDMUtil.getInstance().getCommandUUIDFromCommandID(baseCmd)));
        this.logger.log(Level.INFO, "Processing Sequential Command Response {0}\t{1}", new Object[] { strUDID, resourceID });
        try {
            final List configIdList = MDMConfigUtil.getConfigIds(collectionID);
            if (configIdList.contains(new Integer(557))) {
                this.processKioskSeqResponse(strCommandUuid, resourceID);
            }
        }
        catch (final SyMException e) {
            this.logger.log(Level.WARNING, "Error in getting config IDS", (Throwable)e);
            this.sendFailureResponse(resourceID, strCommandUuid);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error in processing Sequential command response ", e2);
            this.sendFailureResponse(resourceID, strCommandUuid);
        }
    }
    
    public void processSeqCommandResponse(final Long resourceID, final String commandUUID) throws Exception {
        final HashMap hashMap = new HashMap();
        hashMap.put("UDID", ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID));
        hashMap.put("CommandUUID", commandUUID);
        this.processSeqCommandResponse(hashMap);
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
    
    public boolean isAppInstallSuccessfulforResource(final Long resourceID, final Long collectionID) throws DataAccessException {
        final Table table = new Table("CollnToResources");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(table);
        final Column column = new Column("CollnToResources", "*");
        selectQuery.addSelectColumn(column);
        Criteria criteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria criteria2 = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
        criteria = criteria.and(criteria2);
        selectQuery.setCriteria(criteria);
        final Persistence persistence = MDMUtil.getPersistence();
        final DataObject dataObject = persistence.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("CollnToResources");
            final String remarks = (String)row.get("REMARKS");
            if (remarks.equals("dc.db.mdm.collection.Successfully_installed_the_app") || remarks.equals("dc.db.mdm.collection.App_already_installed")) {
                return true;
            }
        }
        return false;
    }
    
    public DataObject getPreData(final Long resourceID, final Long commandID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCollectionCommand"));
        selectQuery.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)Column.getColumn("ProfileToCollection", "PROFILE_ID"), 0);
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", criteria.and(profileCriteria), 1));
        final Criteria commandCriteria = new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)commandID, 0);
        selectQuery.setCriteria(commandCriteria);
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    public boolean isAppPreInstalledInResource(final Long resourceID, final Long collectionID) {
        boolean isAppPresent = false;
        final List appIDList = MdmInvDataProcessor.getInstance().getAppIDFromResourceID(resourceID);
        final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
        if (appIDList != null && !appIDList.isEmpty() && appID != null) {
            final SelectQuery bundleIdentifierSelect = (SelectQuery)new SelectQueryImpl(new Table("MdAppDetails"));
            final Column all = new Column("MdAppDetails", "*");
            final Criteria criteria = new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)appIDList.toArray(new Long[appIDList.size()]), 8);
            bundleIdentifierSelect.addSelectColumn(all);
            bundleIdentifierSelect.setCriteria(criteria);
            try {
                final DataObject dataObject = MDMUtil.getPersistence().get(bundleIdentifierSelect);
                if (!dataObject.isEmpty()) {
                    final List<String> appIdentifierList = new ArrayList<String>();
                    final String curAppIdentifier = AppsUtil.getInstance().getAppIdentifier(appID);
                    final Iterator iterator = dataObject.getRows("MdAppDetails");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        appIdentifierList.add((String)row.get("IDENTIFIER"));
                    }
                    if (curAppIdentifier != null && appIdentifierList.contains(curAppIdentifier)) {
                        isAppPresent = true;
                    }
                }
            }
            catch (final DataAccessException e) {
                this.logger.log(Level.WARNING, "Exception in Determining if app installed in resource", (Throwable)e);
            }
            catch (final Exception e2) {
                this.logger.log(Level.WARNING, "Exception in Determining if app installed in resource", e2);
            }
        }
        return isAppPresent;
    }
    
    static {
        AndroidSeqCmdUtil.androidSequentialCommandUtil = null;
    }
}
