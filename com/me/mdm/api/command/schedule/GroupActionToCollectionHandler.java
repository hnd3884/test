package com.me.mdm.api.command.schedule;

import java.util.Map;
import com.adventnet.persistence.DataAccess;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.Collections;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.Set;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GroupActionToCollectionHandler
{
    private static Logger logger;
    private static GroupActionToCollectionHandler groupActionToCollectionHandler;
    
    public static GroupActionToCollectionHandler getInstance() {
        if (GroupActionToCollectionHandler.groupActionToCollectionHandler == null) {
            GroupActionToCollectionHandler.groupActionToCollectionHandler = new GroupActionToCollectionHandler();
        }
        return GroupActionToCollectionHandler.groupActionToCollectionHandler;
    }
    
    public List getGroupActionIDsforCollectionID(final List collectionIDs) {
        List groupActionIds = new ArrayList();
        try {
            GroupActionToCollectionHandler.logger.log(Level.INFO, "Getting group action IDs for the collectionIDs:{0}", collectionIDs);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
            final Column groupActionIDColumn = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
            final Column collectionIDColumn = new Column("GroupActionToCollection", "COLLECTION_ID");
            sq.addSelectColumn(groupActionIDColumn);
            sq.addSelectColumn(collectionIDColumn);
            final Criteria c = new Criteria(collectionIDColumn, (Object)collectionIDs.toArray(), 8);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> rows = dataObject.getRows("GroupActionToCollection");
            final List redundantGroupActionIds = DBUtil.getColumnValuesAsList((Iterator)rows, "GROUP_ACTION_ID");
            final Set newGroupActionIDs = new HashSet(redundantGroupActionIds);
            groupActionIds = Arrays.asList(newGroupActionIDs.toArray());
        }
        catch (final Exception e) {
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Error while getting groupActionID for collectionID:{0}", collectionIDs);
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception in getGroupActionIDsforCollectionID", e);
        }
        return groupActionIds;
    }
    
    public void addOrUpdateCollectionForGroupAction(final Long groupActionID, final Long collectionID) {
        GroupActionToCollectionHandler.logger.log(Level.INFO, "Updating the collectionID{0} for groupActionID{1}", new Object[] { collectionID, groupActionID });
        try {
            final Criteria groupActionCriteria = new Criteria(new Column("GroupActionToCollection", "GROUP_ACTION_ID"), (Object)groupActionID, 0);
            final DataObject groupActionToCollectionDO = MDMUtil.getPersistence().get("GroupActionToCollection", groupActionCriteria);
            if (groupActionToCollectionDO.isEmpty()) {
                final Row row = new Row("GroupActionToCollection");
                row.set("GROUP_ACTION_ID", (Object)groupActionID);
                row.set("COLLECTION_ID", (Object)collectionID);
                groupActionToCollectionDO.addRow(row);
                MDMUtil.getPersistence().add(groupActionToCollectionDO);
            }
            else {
                final Row row = groupActionToCollectionDO.getFirstRow("GroupActionToCollection");
                row.set("COLLECTION_ID", (Object)collectionID);
                groupActionToCollectionDO.updateRow(row);
                MDMUtil.getPersistence().update(groupActionToCollectionDO);
            }
        }
        catch (final Exception e) {
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while updating the collectionID{1} for the groupActionID{0}", new Object[] { groupActionID, collectionID });
        }
    }
    
    public List getGroupActionIDsForGroupIDs(final List groupIDs) {
        List groupActionIDs = new ArrayList();
        try {
            GroupActionToCollectionHandler.logger.log(Level.INFO, "getting group actionIDs for the groupIDs:{0}", groupIDs);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Column groupActionIDColumn = new Column("GroupActionHistory", "GROUP_ACTION_ID");
            final Column groupActionToCollectionGroupActionIDColumn = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
            final Column groupIDColumn = new Column("GroupActionHistory", "GROUP_ID");
            final Column collectionIDColumn = new Column("GroupActionToCollection", "COLLECTION_ID");
            final Join join = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            sq.addJoin(join);
            sq.addSelectColumn(groupActionIDColumn);
            sq.addSelectColumn(groupActionToCollectionGroupActionIDColumn);
            sq.addSelectColumn(groupIDColumn);
            sq.addSelectColumn(collectionIDColumn);
            final Criteria groupIDCriteria = new Criteria(groupIDColumn, (Object)groupIDs.toArray(), 8);
            sq.setCriteria(groupIDCriteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> rows = dataObject.getRows("GroupActionToCollection");
            groupActionIDs = DBUtil.getColumnValuesAsList((Iterator)rows, "GROUP_ACTION_ID");
        }
        catch (final Exception e) {
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while getting dataObject for group Action for the groupIDs{0}", groupIDs);
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception in getGroupActionDataObject", e);
        }
        return groupActionIDs;
    }
    
    private List getCollectionIDsForGroupActionIds(final List groupActionIDs) {
        List collectionIDs = new ArrayList();
        try {
            GroupActionToCollectionHandler.logger.log(Level.INFO, "Getting collectionIDs for the given groupActionIDs:{0}", groupActionIDs);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionToCollection"));
            final Column groupActionIDColumn = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
            final Column collectionIDColumn = new Column("GroupActionToCollection", "COLLECTION_ID");
            sq.addSelectColumn(groupActionIDColumn);
            sq.addSelectColumn(collectionIDColumn);
            final Criteria c = new Criteria(groupActionIDColumn, (Object)groupActionIDs.toArray(), 8);
            sq.setCriteria(c);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            final Iterator<Row> rows = dataObject.getRows("GroupActionToCollection");
            final List redundantCollectionIDs = DBUtil.getColumnValuesAsList((Iterator)rows, "COLLECTION_ID");
            final Set newCollectionIDs = new HashSet(redundantCollectionIDs);
            collectionIDs = Arrays.asList(newCollectionIDs.toArray());
        }
        catch (final Exception e) {
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while getting collectionIDs for groupActionIDs{0}", groupActionIDs);
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception in getGroupActionDataObject", e);
        }
        return collectionIDs;
    }
    
    public HashMap seperateEmptyGroupsFromNonEmptyGroups(final List groupIDs) {
        final HashMap segregatedGroups = new HashMap();
        final List emptyList = new ArrayList();
        final List nonEmptyList = new ArrayList();
        for (final Long group : groupIDs) {
            final List<Integer> resourceTypeList = new ArrayList<Integer>();
            resourceTypeList.add(120);
            resourceTypeList.add(121);
            final List<Long> deviceList = MDMGroupHandler.getMemberIdListForGroups(Collections.singletonList(group), resourceTypeList);
            if (deviceList.isEmpty()) {
                emptyList.add(group);
            }
            else {
                nonEmptyList.add(group);
            }
        }
        segregatedGroups.put("emptyGroups", emptyList);
        segregatedGroups.put("nonEmptyGroups", nonEmptyList);
        return segregatedGroups;
    }
    
    private void removeGroupActionToCollectionForEmptyGroups(final List groupIDs) throws Exception {
        GroupActionToCollectionHandler.logger.log(Level.INFO, "Removing Scheduled group actions for the empty groups{0}", new Object[] { groupIDs });
        final List groupActionIDs = this.getGroupActionIDsForGroupIDs(groupIDs);
        final List collections = this.getCollectionIDsForGroupActionIds(groupActionIDs);
        final List deletableCollections = GroupActionScheduleUtils.getUnusedCollections(collections, groupActionIDs);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
        sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
        sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
        sq.addSelectColumn(new Column("GroupActionToCollection", "COLLECTION_ID"));
        final Join groupActionToCollectionJoin = new Join("GroupActionHistory", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        sq.addJoin(groupActionToCollectionJoin);
        sq.setCriteria(new Criteria(new Column("GroupActionToCollection", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        dataObject.deleteRows("GroupActionToCollection", (Criteria)null);
        dataObject.deleteRows("GroupActionHistory", (Criteria)null);
        MDMUtil.getPersistence().update(dataObject);
        ScheduledActionsUtils.deleteScheduledActionsForCollections(deletableCollections);
    }
    
    private void removeGroupActionToCollectionForNonEmptyGroups(final List groupIDs) throws Exception {
        GroupActionToCollectionHandler.logger.log(Level.INFO, "Removing Scheduled group actions for the non empty groups{0}", new Object[] { groupIDs });
        final List groupActionIDs = this.getGroupActionIDsForGroupIDs(groupIDs);
        final List<Integer> resourceTypeList = new ArrayList<Integer>();
        resourceTypeList.add(120);
        resourceTypeList.add(121);
        final List<Long> deviceList = MDMGroupHandler.getMemberIdListForGroups(groupIDs, resourceTypeList);
        final List collections = this.getCollectionIDsForGroupActionIds(groupActionIDs);
        final List deletableDeviceList = GroupActionScheduleUtils.getNonDeletableResourceList(groupActionIDs, collections);
        deviceList.removeAll(deletableDeviceList);
        final List deletableCollections = GroupActionScheduleUtils.getUnusedCollections(collections, groupActionIDs);
        final List nonDeletableCollection = collections;
        if (!nonDeletableCollection.equals(deletableCollections)) {
            nonDeletableCollection.removeAll(deletableCollections);
        }
        ScheduledCollectionToResourceHandler.getInstance().deleteScheduledCollectionToResource(nonDeletableCollection, deviceList);
        GroupActionToCollectionHandler.logger.log(Level.INFO, "removing Scheduled group actions associated with the groupIDs", groupIDs);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
        final Join groupActionToCommandJoin = new Join("GroupActionHistory", "GroupActionToCommand", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        final Join groupActionToHistory = new Join("GroupActionToCommand", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2);
        final Join groupActionToCollection = new Join("GroupActionToCommand", "GroupActionToCollection", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
        sq.addSelectColumn(new Column("GroupActionToCollection", "*"));
        sq.addSelectColumn(new Column("GroupActionToCommand", "*"));
        sq.addSelectColumn(new Column("CommandHistory", "*"));
        sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
        sq.addJoin(groupActionToCommandJoin);
        sq.addJoin(groupActionToHistory);
        sq.addJoin(groupActionToCollection);
        sq.setCriteria(new Criteria(new Column("GroupActionToCollection", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8));
        final DataObject dataObject = MDMUtil.getPersistence().get(sq);
        dataObject.deleteRows("GroupActionToCollection", (Criteria)null);
        dataObject.deleteRows("CommandHistory", (Criteria)null);
        dataObject.deleteRows("GroupActionToCommand", (Criteria)null);
        dataObject.deleteRows("GroupActionHistory", (Criteria)null);
        ScheduledActionsUtils.deleteScheduledActionsForCollections(deletableCollections);
        MDMUtil.getPersistence().update(dataObject);
    }
    
    public void removeGroupActionToCollectionForGroupID(final List groupIDs, final Long customerID, final Long userID) {
        try {
            final List groupActionIds = DBUtil.getDistinctColumnValue("GroupActionHistory", "GROUP_ACTION_ID", new Criteria(new Column("GroupActionHistory", "GROUP_ID"), (Object)groupIDs.toArray(), 8));
            final Map info = new HashMap();
            info.put("customer_id", customerID);
            info.put("user_id", userID);
            for (final Object groupActionID : groupActionIds) {
                final Long collectionID = this.getCollectionForGroupAction(Long.parseLong((String)groupActionID));
                info.put("collection_id", collectionID);
                final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collectionID);
                final String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
                DeviceInvCommandHandler.getInstance().suspendBulkCommandExecution(-1L, Long.parseLong((String)groupActionID), commandName, customerID, info);
            }
            final HashMap map = this.seperateEmptyGroupsFromNonEmptyGroups(groupIDs);
            this.removeGroupActionToCollectionForEmptyGroups(map.get("emptyGroups"));
            this.removeGroupActionToCollectionForNonEmptyGroups(map.get("nonEmptyGroups"));
            DataAccess.delete("GroupActionHistory", new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionIds.toArray(), 8));
        }
        catch (final Exception e) {
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception while deleting group Action to collection for the groupID{0}", groupIDs);
            GroupActionToCollectionHandler.logger.log(Level.SEVERE, "Exception in removeGroupActionToCollectionForGroupID", e);
        }
    }
    
    public Long getCollectionForGroupAction(final Long groupActionID) throws Exception {
        return (Long)DBUtil.getValueFromDB("GroupActionToCollection", "GROUP_ACTION_ID", (Object)groupActionID, "COLLECTION_ID");
    }
    
    static {
        GroupActionToCollectionHandler.logger = Logger.getLogger("ActionsLogger");
        GroupActionToCollectionHandler.groupActionToCollectionHandler = null;
    }
}
