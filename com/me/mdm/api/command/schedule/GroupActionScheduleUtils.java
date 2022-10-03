package com.me.mdm.api.command.schedule;

import java.util.HashMap;
import java.util.Map;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Collections;
import com.adventnet.persistence.DataAccessException;
import java.util.Arrays;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class GroupActionScheduleUtils
{
    private static Logger logger;
    
    public static List getUnusedCollections(final List collectionIDs, final List groupActionIDs) throws Exception {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Fetching the unused Collections{0} for groupActionIds:{1}", new Object[] { collectionIDs, groupActionIDs });
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
            final Column groupActionIDCol = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
            final Column collectionIDCol = new Column("GroupActionToCollection", "COLLECTION_ID");
            final Column groupActionHistoryGroupActionIDCol = new Column("GroupActionHistory", "GROUP_ACTION_ID");
            final Join groupActionHistoryJoin = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            final Criteria groupActionIDCriteria = new Criteria(groupActionHistoryGroupActionIDCol, (Object)groupActionIDs.toArray(), 9);
            final Criteria collectionCriteria = new Criteria(collectionIDCol, (Object)collectionIDs.toArray(), 8);
            sq.addJoin(groupActionHistoryJoin);
            sq.addSelectColumn(groupActionIDCol);
            sq.addSelectColumn(collectionIDCol);
            sq.addSelectColumn(groupActionHistoryGroupActionIDCol);
            sq.setCriteria(groupActionIDCriteria.and(collectionCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (dataObject.isEmpty()) {
                return collectionIDs;
            }
            final Iterator<Row> iterator = dataObject.getRows("GroupActionToCollection");
            final List usedCollectionIDs = DBUtil.getColumnValuesAsList((Iterator)iterator, "COLLECTION_ID");
            final List<String> list = new ArrayList<String>(collectionIDs);
            list.removeAll(usedCollectionIDs);
            return list;
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Error while fetching unused collections", e);
            throw e;
        }
    }
    
    private static UpdateQuery getScheduledGroupActionsUpdateQuery(final List groupActionIDs) {
        GroupActionScheduleUtils.logger.log(Level.INFO, "getting updateQuery for the groupActionID:{0}", new Object[] { groupActionIDs });
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("CommandHistory");
        final Join commandHistoryJoin = new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
        final Join groupActionHistoryJoin = new Join("GroupActionToCommand", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Join groupActionToCollectionJoin = new Join("GroupActionToCommand", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Join collectionToResourceJoin = new Join("GroupActionToCollection", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Column groupActionCriteriaColumn = new Column("GroupActionHistory", "GROUP_ACTION_ID");
        final Criteria groupActionIDCriteria = new Criteria(groupActionCriteriaColumn, (Object)groupActionIDs.toArray(), 8);
        uq.addJoin(commandHistoryJoin);
        uq.addJoin(groupActionHistoryJoin);
        uq.addJoin(groupActionToCollectionJoin);
        uq.addJoin(collectionToResourceJoin);
        uq.setCriteria(groupActionIDCriteria);
        return uq;
    }
    
    public static Boolean isGroupActionSuspended(final Long groupActionID) throws Exception {
        try {
            final Integer status = (Integer)DBUtil.getValueFromDB("GroupActionHistory", "GROUP_ACTION_ID", (Object)groupActionID, "ACTION_STATUS");
            if (status == 6) {
                return true;
            }
            return false;
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Error while checking is the groupAction is suspened", e);
            throw e;
        }
    }
    
    public static void updateExpiredDeviceStatus() {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Updating status to failed for the expired devices", new Object[0]);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Column slotEndTimeCol = new Column("MdCommandsToDevice", "SLOT_END_TIME");
            final Column commandDeviceIDCol = new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID");
            final Column commandCol = new Column("MdCommandsToDevice", "COMMAND_ID");
            final Column resourceCol = new Column("MdCommandsToDevice", "RESOURCE_ID");
            final Criteria slotEndTimeCriteria = new Criteria(slotEndTimeCol, (Object)System.currentTimeMillis(), 6);
            sq.setCriteria(slotEndTimeCriteria);
            sq.addSelectColumn(commandDeviceIDCol);
            sq.addSelectColumn(slotEndTimeCol);
            sq.addSelectColumn(commandCol);
            sq.addSelectColumn(resourceCol);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final List collectionIDs = new ArrayList();
                final List resourceIDs = new ArrayList();
                final Iterator<Row> rows = dataObject.getRows("MdCommandsToDevice");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final Long resourceID = (Long)row.get("RESOURCE_ID");
                    final Long commandID = (Long)row.get("COMMAND_ID");
                    final Long collectionID = ScheduledActionsUtils.getCollectionIDFromCommandID(commandID);
                    collectionIDs.add(collectionID);
                    resourceIDs.add(resourceID);
                }
                final String remarks = "dc.mdm.general.command.invocation.failed";
                updateCommandHistoryStatus(collectionIDs, resourceIDs, Arrays.asList(1, 4, 7, 2), 0, remarks);
            }
            ScheduledActionsUtils.updateStatusForExpiredScheduledActions();
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Error while trying to update expired devices");
        }
    }
    
    public static List getCollectionsForScheduledCommand(final String commandName, final Long resourceID) throws DataAccessException {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Fetching collections for the commandName", commandName);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            final Column commandDeviceId = new Column("MdCommandsToDevice", "COMMAND_DEVICE_ID");
            final Column slotTimeColumn = new Column("MdCommandsToDevice", "SLOT_BEGIN_TIME");
            final Column resourceIDColumn = new Column("MdCommandsToDevice", "RESOURCE_ID");
            final Column resourceCommandStatusCol = new Column("MdCommandsToDevice", "RESOURCE_COMMAND_STATUS");
            final Column commandIdCol = new Column("MdCommands", "COMMAND_ID");
            final Join mdCommandsJoin = new Join("MdCommandsToDevice", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
            sq.addJoin(mdCommandsJoin);
            sq.addSelectColumn(commandDeviceId);
            sq.addSelectColumn(resourceIDColumn);
            sq.addSelectColumn(slotTimeColumn);
            sq.addSelectColumn(resourceCommandStatusCol);
            sq.addSelectColumn(commandIdCol);
            final Criteria resourceIDCriteria = new Criteria(resourceIDColumn, (Object)resourceID, 0);
            final Criteria resourceCommandStatusCriteria = new Criteria(resourceCommandStatusCol, (Object)3, 0);
            final Criteria slotTimeCriteria = new Criteria(slotTimeColumn, (Object)0, 4);
            final Criteria commandNameCriteria = new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)commandName, 10);
            sq.setCriteria(resourceCommandStatusCriteria.and(slotTimeCriteria).and(commandNameCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final List collections = new ArrayList();
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("MdCommands");
                final List<String> commandNames = DBUtil.getColumnValuesAsList((Iterator)rows, "COMMAND_UUID");
                for (final String cmdName : commandNames) {
                    final Long collection = Long.parseLong(MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdName));
                    collections.add(collection);
                }
            }
            return collections;
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Failed to fetch collections for the command:{0}", commandName);
            throw e;
        }
    }
    
    public static void updateCommandHistoryStatus(final List collections, final List resources, final List<Integer> fromStatus, final Integer toStatus, final String remarks) {
        try {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Updating the commandHistory status for devices:{0} beLonging to the collections:{1}", new Object[] { resources, collections });
            final List groupActionIds = GroupActionToCollectionHandler.getInstance().getGroupActionIDsforCollectionID(collections);
            final UpdateQuery groupActionUpdateQuery = getScheduledGroupActionsUpdateQuery(groupActionIds);
            final Criteria criteria = groupActionUpdateQuery.getCriteria();
            final Criteria resourceCriteria = new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)resources.toArray(), 8);
            final Criteria statusCriteria = new Criteria(new Column("CommandHistory", "COMMAND_STATUS"), (Object)fromStatus.toArray(), 8);
            groupActionUpdateQuery.setCriteria(criteria.and(resourceCriteria).and(statusCriteria));
            groupActionUpdateQuery.setUpdateColumn("REMARKS", (Object)remarks);
            groupActionUpdateQuery.setUpdateColumn("COMMAND_STATUS", (Object)toStatus);
            groupActionUpdateQuery.setUpdateColumn("UPDATED_TIME", (Object)System.currentTimeMillis());
            MDMUtil.getPersistence().update(groupActionUpdateQuery);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Error occured while updating the status for devices:{0} for the exception:{1}", new Object[] { resources, e });
        }
    }
    
    public static void updateCommandHistoryStatusByGroupActionIDs(final List groupActionIDs, final List resources, final List<Integer> fromStatus, final Integer toStatus, final String remarks) {
        try {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Updating the commandHistory status for devices:{0} beLonging to the collections:{1}", new Object[] { resources, groupActionIDs });
            final UpdateQuery groupActionUpdateQuery = getScheduledGroupActionsUpdateQuery(groupActionIDs);
            final Criteria criteria = groupActionUpdateQuery.getCriteria();
            final Criteria resourceCriteria = new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)resources.toArray(), 8);
            final Criteria statusCriteria = new Criteria(new Column("CommandHistory", "COMMAND_STATUS"), (Object)fromStatus.toArray(), 8);
            groupActionUpdateQuery.setCriteria(criteria.and(resourceCriteria).and(statusCriteria));
            groupActionUpdateQuery.setUpdateColumn("REMARKS", (Object)remarks);
            groupActionUpdateQuery.setUpdateColumn("COMMAND_STATUS", (Object)toStatus);
            groupActionUpdateQuery.setUpdateColumn("UPDATED_TIME", (Object)System.currentTimeMillis());
            MDMUtil.getPersistence().update(groupActionUpdateQuery);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Error occured while updating the status for devices:{0} for the exception:{1}", new Object[] { resources, e });
        }
    }
    
    public static void updateAndroidDeviceScheduledCommandStatus(final Long resourceID, final Integer status, final Long collectionID) throws Exception {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Updating status to {0} for android device {1} for the collection{2}", new Object[] { status, resourceID, collectionID });
            final List groupActionIDs = GroupActionToCollectionHandler.getInstance().getGroupActionIDsforCollectionID(Collections.singletonList(collectionID));
            final List resourceIds = new ArrayList();
            resourceIds.add(resourceID);
            updateDeviceActionStatus(resourceIds, status, groupActionIDs);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception while updating status for resources:{}", resourceID);
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in updateAndroidDeviceStatus", e);
            throw e;
        }
    }
    
    private static void updateDeviceActionStatus(final List resourceIDs, final int status, final List groupActionIDs) {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "updating the status for resourceIDs{0} for the groupActionIDs{1} to the status{2}", new Object[] { resourceIDs, groupActionIDs, status });
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("CommandHistory");
            final Join commandHistoryJoin = new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
            final Criteria resourceCriteria = new Criteria(new Column("CommandHistory", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria groupActionIDCriteria = new Criteria(new Column("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8);
            uq.addJoin(commandHistoryJoin);
            uq.setCriteria(resourceCriteria.and(groupActionIDCriteria));
            uq.setUpdateColumn("COMMAND_STATUS", (Object)status);
            uq.setUpdateColumn("UPDATED_TIME", (Object)SyMUtil.getCurrentTimeInMillis());
            MDMUtil.getPersistence().update(uq);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception while updating status for resources:{}", resourceIDs);
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in updateDeviceActionStatus", e);
        }
    }
    
    public static boolean isGroupActionScheduled(final Long groupActionID) {
        try {
            final DataObject dataObject = getGroupActionDataObject(groupActionID);
            return !dataObject.isEmpty();
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception occured while checking if groupActionID{0} is scheduled", groupActionID);
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Stack Trace for exception in isGroupActionScheduled", e);
            return false;
        }
    }
    
    private static DataObject getGroupActionDataObject(final Long groupActionID) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
        final Column groupActionColumn = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
        final Column actionTypeColumn = new Column("GroupActionHistory", "ACTION_ID");
        final Column collectionIDColumn = new Column("GroupActionToCollection", "COLLECTION_ID");
        final Column groupActionHistoryIDColumn = new Column("GroupActionHistory", "GROUP_ACTION_ID");
        final Column groupIDColumn = new Column("GroupActionHistory", "GROUP_ID");
        sq.addSelectColumn(groupActionColumn);
        sq.addSelectColumn(groupIDColumn);
        sq.addSelectColumn(actionTypeColumn);
        sq.addSelectColumn(collectionIDColumn);
        sq.addSelectColumn(groupActionHistoryIDColumn);
        final Join join = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Criteria criteria = new Criteria(groupActionColumn, (Object)groupActionID, 0);
        sq.addJoin(join);
        sq.setCriteria(criteria);
        return MDMUtil.getPersistence().get(sq);
    }
    
    public static Long getGroupActionIDForCommandHistoryID(final Long commandHistoryID) throws Exception {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Getting groupActionID for the commandHistoryId{0}", new Object[] { commandHistoryID });
            final Long groupActionID = (Long)DBUtil.getValueFromDB("GroupActionToCommand", "COMMAND_HISTORY_ID", (Object)commandHistoryID, "GROUP_ACTION_ID");
            return groupActionID;
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Getting GroupActionID for commandHistoryID{0}", commandHistoryID);
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getGroupActionIDForCommandHistoryID", e);
            throw e;
        }
    }
    
    public static Long getGroupIDForGroupActionID(final Long groupActionID) throws Exception {
        GroupActionScheduleUtils.logger.log(Level.INFO, "Getting groupID for the groupActionID:{0}", new Object[] { groupActionID });
        return (Long)DBUtil.getValueFromDB("GroupActionHistory", "GROUP_ACTION_ID", (Object)groupActionID, "GROUP_ID");
    }
    
    public static Boolean checkIfCollectionIsUsedInGroup(final Long collectionID, final Long groupActionID) throws Exception {
        GroupActionScheduleUtils.logger.log(Level.INFO, "Checking if the collection{0} is already used in the group for groupActionID:{1}", new Object[] { collectionID, groupActionID });
        final Long groupID = getGroupIDForGroupActionID(groupActionID);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
        final Column collectionColumn = new Column("GroupActionToCollection", "COLLECTION_ID");
        final Column groupActionIDCol = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
        final Join groupActionHisoryJoin = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Column groupIDCol = new Column("GroupActionHistory", "GROUP_ID");
        sq.addSelectColumn(groupActionIDCol);
        sq.addSelectColumn(collectionColumn);
        sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
        sq.addSelectColumn(groupIDCol);
        sq.addJoin(groupActionHisoryJoin);
        final Criteria collectionCriteria = new Criteria(collectionColumn, (Object)collectionID, 0);
        final Criteria groupActionIdCriteria = new Criteria(groupActionIDCol, (Object)groupActionID, 1);
        final Criteria groupIdCriteria = new Criteria(groupIDCol, (Object)groupID, 0);
        final Criteria groupActionStatusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)6, 1);
        sq.setCriteria(collectionCriteria.and(groupActionIdCriteria).and(groupIdCriteria).and(groupActionStatusCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        if (dataObject.isEmpty()) {
            return false;
        }
        return true;
    }
    
    public static List<Long> getResourceListForGroupActionID(final Long groupActionID) throws Exception {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Getting resourceList for the groupActionID:{0}", new Object[] { groupActionID });
            final Long groupID = getGroupIDForGroupActionID(groupActionID);
            final List<Integer> resourceTypeList = new ArrayList<Integer>();
            resourceTypeList.add(120);
            resourceTypeList.add(121);
            final List resList = MDMGroupHandler.getMemberIdListForGroups(Collections.singletonList(groupID), resourceTypeList);
            if (resList.size() == 0) {
                return new ArrayList<Long>();
            }
            return resList;
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getResourceListForGroupActionID", e);
            throw e;
        }
    }
    
    public static void removeDevicesFromGroupAction(final List resourceIDs, final List groupActionIDs) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
            sQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            sQuery.addJoin(new Join("CommandHistory", "GroupActionToCommand", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
            final Criteria resCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resourceIDs.toArray(), 8);
            final Criteria groupActionCriteria = new Criteria(Column.getColumn("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8);
            sQuery.setCriteria(resCriteria.and(groupActionCriteria));
            sQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupActionToCommand", "GROUP_ACTION_ID"));
            sQuery.addSelectColumn(Column.getColumn("GroupActionToCommand", "COMMAND_HISTORY_ID"));
            final DataObject commandDO = MDMUtil.getPersistence().get(sQuery);
            if (!commandDO.isEmpty()) {
                commandDO.deleteRows("CommandHistory", (Criteria)null);
                commandDO.deleteRows("GroupActionToCommand", (Criteria)null);
                MDMUtil.getPersistence().update(commandDO);
            }
        }
        catch (final Exception ex) {
            GroupActionScheduleUtils.logger.log(Level.WARNING, "Exception occurred in suspendCommandForDeviceList() method : {0}", ex);
        }
    }
    
    public static String getGroupActionName(final Long groupActionID) throws DataAccessException {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "getting GroupAction Name for the given groupActionID:{0}", new Object[] { groupActionID });
            final DataObject dataObject = getGroupActionDataObject(groupActionID);
            final Iterator iter = dataObject.getRows("GroupActionHistory");
            final List<Integer> ActionTypes = DBUtil.getColumnValuesAsList(iter, "ACTION_ID");
            return DeviceInvCommandHandler.getInstance().getScheduledActionNameForActionID(ActionTypes.get(0));
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getGroupActionName", e);
            throw e;
        }
    }
    
    public static void updateGroupActionStatus(final List groupActionIDs, final Integer status) {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Updating status for the groupActionIDs:{0}", new Object[] { groupActionIDs });
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("GroupActionHistory");
            final Criteria c = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8);
            uq.setCriteria(c);
            uq.setUpdateColumn("ACTION_STATUS", (Object)status);
            MDMUtil.getPersistence().update(uq);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in updateGroupActionStatus", e);
        }
    }
    
    public static List getNonDeletableResourceList(final List groupActionIds, final List collectionIDs) throws Exception {
        try {
            List resources = new ArrayList();
            GroupActionScheduleUtils.logger.log(Level.INFO, "Getting resource List that is only used only in the group", new Object[0]);
            final Criteria c = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionIds.toArray(), 8);
            final List groupIDs = DBUtil.getDistinctColumnValue("GroupActionHistory", "GROUP_ID", c);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Join customGroupJoin = new Join("GroupActionHistory", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join groupActionCollectionJoin = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            final Join groupMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
            final Criteria groupIDCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)groupIDs.toArray(), 9);
            final Criteria collectionCriteria = new Criteria(new Column("GroupActionToCollection", "COLLECTION_ID"), (Object)collectionIDs.toArray(), 8);
            sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("CustomGroup", "RESOURCE_ID"));
            sq.addSelectColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            sq.addSelectColumn(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            sq.addJoin(customGroupJoin);
            sq.addJoin(groupActionCollectionJoin);
            sq.addJoin(groupMemberJoin);
            sq.setCriteria(groupIDCriteria.and(collectionCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("CustomGroupMemberRel");
                resources = DBUtil.getColumnValuesAsList((Iterator)rows, "MEMBER_RESOURCE_ID");
            }
            return ScheduleCommandService.getUniqueListItems((ArrayList)resources);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getDeletableResourceList", e);
            throw e;
        }
    }
    
    public static List getNonDeletableResourceList(final Long groupActionId, final Long collectionID) throws Exception {
        try {
            List resources = new ArrayList();
            GroupActionScheduleUtils.logger.log(Level.INFO, "Getting resource List that is only used only in the group", new Object[0]);
            final Long groupID = getGroupIDForGroupActionID(groupActionId);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Join customGroupJoin = new Join("GroupActionHistory", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join groupActionCollectionJoin = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            final Join groupMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
            final Criteria groupIDCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)groupID, 1);
            final Criteria collectionCriteria = new Criteria(new Column("GroupActionToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            final Criteria statusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)6, 1);
            sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("CustomGroup", "RESOURCE_ID"));
            sq.addSelectColumn(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            sq.addSelectColumn(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            sq.addJoin(customGroupJoin);
            sq.addJoin(groupActionCollectionJoin);
            sq.addJoin(groupMemberJoin);
            sq.setCriteria(groupIDCriteria.and(collectionCriteria).and(statusCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> rows = dataObject.getRows("CustomGroupMemberRel");
                resources = DBUtil.getColumnValuesAsList((Iterator)rows, "MEMBER_RESOURCE_ID");
            }
            return ScheduleCommandService.getUniqueListItems((ArrayList)resources);
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getDeletableResourceList", e);
            throw e;
        }
    }
    
    public static List getUnsuspendedGroupActionIDs(final List groupActionIDs) {
        try {
            GroupActionScheduleUtils.logger.log(Level.INFO, "Getting Unsuspened groupActionids from the given groupActionIDs:{0}", new Object[] { groupActionIDs });
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Column groupActionIDCol = new Column("GroupActionHistory", "GROUP_ACTION_ID");
            final Column groupActionStatusCol = new Column("GroupActionHistory", "ACTION_STATUS");
            final Criteria statusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)6, 1);
            final Criteria groupActionIDCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8);
            sq.addSelectColumn(groupActionIDCol);
            sq.addSelectColumn(groupActionStatusCol);
            sq.setCriteria(statusCriteria.and(groupActionIDCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> rows = dataObject.getRows("GroupActionHistory");
            return DBUtil.getColumnValuesAsList((Iterator)rows, "GROUP_ACTION_ID");
        }
        catch (final Exception e) {
            GroupActionScheduleUtils.logger.log(Level.SEVERE, "Exception in getUnsuspendedGroupActionIDs", e);
            return groupActionIDs;
        }
    }
    
    public static Map getGroupIdsForCollectionIds(final List collections) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
        final Join groupActionHistoryJoin = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Join collectionToCustomerJoin = new Join("GroupActionToCollection", "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join collectionToCommandJoin = new Join("GroupActionToCollection", "ScheduledCommandToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join resourceJoin = new Join("GroupActionHistory", "Resource", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2);
        final Join mdCommandJoin = new Join("ScheduledCommandToCollection", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2);
        sq.addSelectColumn(new Column("GroupActionToCollection", "COLLECTION_ID"));
        sq.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
        sq.addSelectColumn(new Column("Resource", "NAME"));
        sq.addSelectColumn(new Column("ScheduledCommandToCollection", "COLLECTION_ID"));
        sq.addSelectColumn(new Column("ScheduledCommandToCollection", "COMMAND_ID"));
        sq.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
        sq.addSelectColumn(new Column("MdCommands", "COMMAND_UUID"));
        sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
        sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
        sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ID"));
        sq.addSelectColumn(new Column("GroupActionHistory", "ACTION_STATUS"));
        sq.addSelectColumn(new Column("CollnToCustomerRel", "COLLECTION_ID"));
        sq.addSelectColumn(new Column("CollnToCustomerRel", "CUSTOMER_ID"));
        sq.addJoin(groupActionHistoryJoin);
        sq.addJoin(resourceJoin);
        sq.addJoin(collectionToCustomerJoin);
        sq.addJoin(collectionToCommandJoin);
        sq.addJoin(mdCommandJoin);
        final Criteria c = new Criteria(new Column("GroupActionToCollection", "COLLECTION_ID"), (Object)collections.toArray(), 8);
        final Criteria actionStatusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)6, 1);
        sq.setCriteria(c.and(actionStatusCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        final Map result = new HashMap();
        if (!dataObject.isEmpty()) {
            final Iterator<Row> groupActionToCollectionRows = dataObject.getRows("GroupActionToCollection");
            while (groupActionToCollectionRows.hasNext()) {
                final Row r = groupActionToCollectionRows.next();
                final Long groupActionID = (Long)r.get("GROUP_ACTION_ID");
                final Long collectionID = (Long)r.get("COLLECTION_ID");
                final Row groupIDRow = dataObject.getRow("GroupActionHistory", new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionID, 0));
                final Long groupID = (Long)groupIDRow.get("GROUP_ID");
                final Row groupNameRow = dataObject.getRow("Resource", new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)groupID, 0));
                final String groupName = (String)groupNameRow.get("NAME");
                final Row customerIDRow = dataObject.getRow("CollnToCustomerRel", new Criteria(new Column("CollnToCustomerRel", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long customerID = (Long)customerIDRow.get("CUSTOMER_ID");
                final Row commandIDRow = dataObject.getRow("ScheduledCommandToCollection", new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long commandID = (Long)commandIDRow.get("COMMAND_ID");
                final Row commandNameRow = dataObject.getRow("MdCommands", new Criteria(new Column("MdCommands", "COMMAND_ID"), (Object)commandID, 0));
                final String commandName = (String)commandNameRow.get("COMMAND_UUID");
                final Map mapForCollection = new HashMap();
                mapForCollection.put("CUSTOMER_ID", customerID);
                mapForCollection.put("NAME", groupName);
                mapForCollection.put("COMMAND_ID", commandName);
                result.put(collectionID, mapForCollection);
            }
        }
        return result;
    }
    
    static {
        GroupActionScheduleUtils.logger = Logger.getLogger("ActionsLogger");
    }
}
