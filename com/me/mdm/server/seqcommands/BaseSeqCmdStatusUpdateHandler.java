package com.me.mdm.server.seqcommands;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.apps.AppsLicensesUtil;
import java.util.Properties;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.List;
import java.util.logging.Logger;

public class BaseSeqCmdStatusUpdateHandler implements SeqCmdStatusUpdateHandler
{
    public static Logger logger;
    
    @Override
    public void makeStatusUpdateforSubCommand(final List<SequentialSubCommand> cmdList) throws Exception {
        final List collectionList = DeviceCommandRepository.getInstance().getCollectionIDListForCmdID(SeqCmdUtils.getInstance().getCommandList(cmdList));
        if (!collectionList.isEmpty()) {
            this.makeStatusUpdatesForCollectionCommands(cmdList);
        }
    }
    
    @Override
    public void makeStatusUpdateforSubCommand(final Long commandID, final Long resourceID, final Long seqCmdID) throws Exception {
        final List commandList = new ArrayList();
        final SequentialSubCommand sequentialSubCommand = new SequentialSubCommand();
        sequentialSubCommand.resourceID = resourceID;
        sequentialSubCommand.CommandID = commandID;
        sequentialSubCommand.SequentialCommandID = seqCmdID;
        commandList.add(sequentialSubCommand);
        this.makeStatusUpdateforSubCommand(commandList);
    }
    
    private void makeStatusUpdatesForCollectionCommands(final List<SequentialSubCommand> cmdList) throws Exception {
        final DataObject dataObject = this.getStatusUpdateDO(cmdList);
        final DataObject baseObjectDO = this.getBaseObjectDO(SeqCmdUtils.getInstance().getSeqList(cmdList));
        final List profileList = this.getProfileListForUpdate(dataObject);
        final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
        profileAssociateDataHandler.init(SeqCmdUtils.getInstance().getResList(cmdList), profileList);
        final Iterator iterator = cmdList.iterator();
        final HashMap eventLogMap = new HashMap();
        boolean profileUpdated = Boolean.FALSE;
        while (iterator.hasNext()) {
            final SequentialSubCommand sequentialSubCommand = iterator.next();
            final Row row = dataObject.getRow("MdCollectionCommand", new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)sequentialSubCommand.CommandID, 0));
            if (row != null) {
                final Long collectionID = (Long)row.get("COLLECTION_ID");
                final Row profileRow = dataObject.getRow("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                final Row resRow = dataObject.getRow("Resource", new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0));
                final Long customerID = (Long)resRow.get("CUSTOMER_ID");
                final String resName = (String)resRow.get("NAME");
                final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0);
                final Criteria seqCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0);
                final Row seqRow = dataObject.getRow("SequentialCommandParams", seqCriteria.and(resourceCriteria));
                final String paramString = (String)seqRow.get("PARAMS");
                final Row baseIDRow = dataObject.getRow("MdCommandToSequentialCommand", new Criteria(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0));
                final Long baseID = (Long)baseIDRow.get("COMMAND_ID");
                JSONObject seqParams = new JSONObject();
                if (paramString != null) {
                    seqParams = new JSONObject(paramString);
                }
                Long appGroupID = null;
                final Row appRow = dataObject.getRow("AppGroupToCollection", new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                Boolean isApp = Boolean.FALSE;
                if (appRow != null) {
                    isApp = Boolean.TRUE;
                    appGroupID = (Long)appRow.get("APP_GROUP_ID");
                }
                final JSONObject initialParams = seqParams.optJSONObject("initialParams");
                final Properties params = new Properties();
                final Long baseProfileID = this.getBaseProfileID(baseObjectDO, baseID);
                final Long userID = SeqCmdUtils.getInstance().getAssociatedUserFromParamsFrombaseID(initialParams, baseProfileID);
                final List resourceList = new ArrayList();
                resourceList.add(sequentialSubCommand.resourceID);
                ((Hashtable<String, Long>)params).put("UserId", userID);
                ((Hashtable<String, List>)params).put("resourceList", resourceList);
                ((Hashtable<String, Long>)params).put("profileID", profileID);
                ((Hashtable<String, Long>)params).put("collectionId", collectionID);
                ((Hashtable<String, Boolean>)params).put("profileOrigin", false);
                ((Hashtable<String, Boolean>)params).put("isAppConfig", isApp);
                profileUpdated = Boolean.TRUE;
                if (isApp) {
                    final List collectionList = new ArrayList();
                    collectionList.add(collectionID);
                    final Row managedRow = dataObject.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0));
                    final int platform = (int)managedRow.get("PLATFORM_TYPE");
                    final Properties taskProps = new Properties();
                    ((Hashtable<String, Integer>)taskProps).put("platformtype", platform);
                    ((Hashtable<String, List>)taskProps).put("resourceList", resourceList);
                    ((Hashtable<String, List>)taskProps).put("collectionList", collectionList);
                    ((Hashtable<String, Long>)taskProps).put("customerId", customerID);
                    ((Hashtable<String, Boolean>)taskProps).put("isSilentInstall", initialParams != null && initialParams.optBoolean("IsAppSilentInstall", false));
                    ((Hashtable<String, Boolean>)taskProps).put("isNotify", false);
                    final AppLicenseMgmtHandler appLicenseMgmtHandler = AppsLicensesUtil.getInstance(platform);
                    appLicenseMgmtHandler.assignAppForDevices(taskProps);
                    eventLogMap.put(resName, appGroupID);
                }
                profileAssociateDataHandler.associateProfileFromSequencialCmd(params);
            }
        }
        if (profileUpdated) {
            profileAssociateDataHandler.commitChangestoDB();
        }
        for (final String resName2 : eventLogMap.keySet()) {
            final Long appGroupID2 = eventLogMap.get(resName2);
            final Row row2 = dataObject.getRow("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID2, 0));
            final String appName = (String)row2.get("GROUP_DISPLAY_NAME");
            final Long customerID2 = (Long)row2.get("CUSTOMER_ID");
            final String remarksArgs = appName + "@@@" + resName2;
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2033, null, MDMEventConstant.DC_SYSTEM_USER, "dc.mdm.actionlog.appmgmt.App_dist_for_kiosk", remarksArgs, customerID2);
        }
    }
    
    protected List getProfileListForUpdate(final DataObject dataObject) throws DataAccessException {
        final List profileList = new ArrayList();
        final Iterator iterator = dataObject.getRows("ProfileToCollection");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long profileID = (Long)row.get("PROFILE_ID");
            profileList.add(profileID);
        }
        return profileList;
    }
    
    protected DataObject getBaseObjectDO(final List cmdList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)cmdList.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    protected void getRemoveCommandList(final List<SequentialSubCommand> commandList) {
        try {
            final List removalList = new ArrayList();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("MdCommands"));
            final Criteria commandCriteria = new Criteria(new Column("MdCommands", "COMMAND_ID"), (Object)SeqCmdUtils.getInstance().getCommandList(commandList).toArray(), 8);
            final Criteria requestTypeCriteria = new Criteria(new Column("MdCommands", "COMMAND_TYPE"), (Object)new String[] { "RemoveProfile", "RemoveKioskDefaultRestriction", "RemoveSingletonRestriction", "RemoveRestrictedPasscode", "RemoveDisablePasscode", "RemoveSingleWebAppKioskAppConfiguration", "RemoveSingleWebAppKioskFeedback", "RestrictionProfileStatus", "RemoveSharedDeviceRestrictions" }, 8);
            query.setCriteria(commandCriteria.and(requestTypeCriteria));
            query.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                for (final SequentialSubCommand sequentialSubCommand : commandList) {
                    final Row row = dataObject.getRow("MdCommands", new Criteria(Column.getColumn("MdCommands", "COMMAND_ID"), (Object)sequentialSubCommand.CommandID, 0));
                    if (row != null) {
                        removalList.add(sequentialSubCommand);
                    }
                }
                commandList.removeAll(removalList);
            }
        }
        catch (final DataAccessException e) {
            BaseSeqCmdStatusUpdateHandler.logger.log(Level.SEVERE, "Exception in Seq command Remove status handling", (Throwable)e);
        }
    }
    
    public HashMap getCollectionsToUpdateForGroup(final HashMap params) throws Exception {
        final HashMap profileTocollectionHash = new HashMap();
        final HashMap hashMap = new HashMap();
        final List commandList = params.get("commandList");
        final String commandType = params.get("commandType");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery.addJoin(new Join("MdCommandToSequentialCommand", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdSequentialCommands", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"InstallApplication", 12);
        final Criteria criteria2 = new Criteria(Column.getColumn("MdCommandToSequentialCommand", "COMMAND_ID"), (Object)commandList.toArray(), 8);
        selectQuery.setCriteria(criteria.and(criteria2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "*"));
        DMDataSetWrapper ds = null;
        final HashMap subProfileTobase = new HashMap();
        final HashMap baseTosubProfile = new HashMap();
        final List baseCmdList = new ArrayList();
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (ds.next()) {
            final Long profileID = (Long)ds.getValue("PROFILE_ID");
            final Long collectionID = (Long)ds.getValue("COLLECTION_ID");
            final Long baseCmdID = (Long)ds.getValue("COMMAND_ID");
            List tempList = baseTosubProfile.get(baseCmdID);
            if (tempList == null) {
                tempList = new ArrayList();
            }
            if (!baseCmdList.contains(baseCmdID)) {
                baseCmdList.add(baseCmdID);
            }
            tempList.add(profileID);
            baseTosubProfile.put(baseCmdID, tempList);
            profileTocollectionHash.put(profileID, collectionID);
        }
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(new Table("MdCommandToSequentialCommand"));
        selectQuery2.addJoin(new Join("MdCommandToSequentialCommand", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery2.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery2.setCriteria(new Criteria(Column.getColumn("MdCommandToSequentialCommand", "COMMAND_ID"), (Object)baseCmdList.toArray(), 8));
        selectQuery2.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery2.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "*"));
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery2);
        while (ds.next()) {
            final Long baseProfileID = (Long)ds.getValue("PROFILE_ID");
            final Long baseCmdID = (Long)ds.getValue("COMMAND_ID");
            final List tempList = baseTosubProfile.get(baseCmdID);
            if (tempList != null) {
                final Iterator iterator = tempList.iterator();
                while (iterator.hasNext()) {
                    subProfileTobase.put(iterator.next(), baseProfileID);
                }
            }
        }
        hashMap.put("subProfileToBaseMap", subProfileTobase);
        hashMap.put("profileToCollectionMap", profileTocollectionHash);
        return hashMap;
    }
    
    protected DataObject getStatusUpdateDO(final List<SequentialSubCommand> commands) throws DataAccessException {
        final List resList = SeqCmdUtils.getInstance().getResList(commands);
        final List seqList = SeqCmdUtils.getInstance().getSeqList(commands);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        final Criteria runningCriteria = SeqCmdUtils.getInstance().getSeqCmdInprogressCriteria();
        final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "RESOURCE_ID"), (Object)Column.getColumn("Resource", "RESOURCE_ID"), 0);
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "SequentialCmdExecutionStatus", runningCriteria.and(resourceCriteria), 2));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdSequentialCommands", new String[] { "SEQUENTIAL_COMMAND_ID", "EXECUTION_STATUS" }, new String[] { "SEQUENTIAL_COMMAND_ID", "ORDER" }, 2));
        selectQuery.addJoin(new Join("MdSequentialCommands", "MdCollectionCommand", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("MdCollectionCommand", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "SequentialCommandParams", new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID", "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("SequentialCmdExecutionStatus", "MdCommandToSequentialCommand", new String[] { "SEQUENTIAL_COMMAND_ID" }, new String[] { "SEQUENTIAL_COMMAND_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCollectionCommand", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "PARAMS"));
        selectQuery.addSelectColumn(Column.getColumn("SequentialCommandParams", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdCommandToSequentialCommand", "COMMAND_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"));
        final Criteria rescriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resList.toArray(), 8);
        final Criteria seqcriteria = new Criteria(Column.getColumn("SequentialCmdExecutionStatus", "SEQUENTIAL_COMMAND_ID"), (Object)seqList.toArray(), 8);
        selectQuery.setCriteria(rescriteria.and(seqcriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        return dataObject;
    }
    
    protected Long getBaseProfileID(final DataObject dataObject, final Long baseID) throws DataAccessException {
        Long profileID = null;
        final Row collectionRow = dataObject.getRow("MdCollectionCommand", new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)baseID, 0));
        if (collectionRow != null) {
            final Long collectionID = (Long)collectionRow.get("COLLECTION_ID");
            final Row profileRow = dataObject.getRow("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
            profileID = (Long)profileRow.get("PROFILE_ID");
        }
        return profileID;
    }
    
    protected Long getBaseCollectionID(final DataObject dataObject, final Long baseID) throws DataAccessException {
        Long collectionID = null;
        final Row collectionRow = dataObject.getRow("MdCollectionCommand", new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)baseID, 0));
        if (collectionRow != null) {
            collectionID = (Long)collectionRow.get("COLLECTION_ID");
        }
        return collectionID;
    }
    
    static {
        BaseSeqCmdStatusUpdateHandler.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
