package com.me.mdm.api.command.schedule;

import java.util.Hashtable;
import org.glassfish.jersey.internal.guava.Iterators;
import com.me.mdm.server.inv.actions.InvActionUtilProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import java.util.UUID;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccess;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.LocalDateTime;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.Collections;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduledActionsUtils
{
    private static Logger logger;
    
    public static void removeCommandsFromMdCommandsToDevice() {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "removing expired scheduled commands");
            final DeleteQuery dq = (DeleteQuery)new DeleteQueryImpl("MdCommandsToDevice");
            final Column slotEndTimeColumn = new Column("MdCommandsToDevice", "SLOT_END_TIME");
            final Criteria slotEndTimeCriteria = new Criteria(slotEndTimeColumn, (Object)System.currentTimeMillis(), 6);
            final Criteria slotEndTimeNullCriteria = new Criteria(slotEndTimeColumn, (Object)null, 1);
            final Criteria c = slotEndTimeCriteria.and(slotEndTimeNullCriteria);
            dq.setCriteria(c);
            MDMUtil.getPersistence().delete(dq);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception while removing the expired data from mdcommandstodevice", e);
        }
    }
    
    public static String getCommandNameFromScheduledCommandName(final String tempcommandName) {
        ScheduledActionsUtils.logger.log(Level.INFO, "Retrieving original commandName from the tempCommandName:{0}", tempcommandName);
        return tempcommandName.split("Scheduled;collection=")[0];
    }
    
    public static Long getCommandIDForTempCommandID(final Long tempCommandID) {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "getting original commandID from the temporary commandID:{0}", tempCommandID);
            final String tempcommandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(tempCommandID);
            final String commandName = getCommandNameFromScheduledCommandName(tempcommandUUID);
            return CommandUtil.getCommandIDFromCommandUUID(commandName);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception while fetching original commandID for {0}", tempCommandID);
            return null;
        }
    }
    
    public static Long getCollectionIDFromCommandID(final Long commandID) {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "getting collectionID from the commandID:{0}", commandID);
            final String commandName = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            if (commandName.contains("Scheduled;collection=")) {
                return Long.parseLong(commandName.split(";")[1].split("=")[1]);
            }
            ScheduledActionsUtils.logger.log(Level.SEVERE, "CommandName:{0} is not a Scheduled Commands specific command", commandName);
        }
        catch (final Exception e) {
            throw e;
        }
        return null;
    }
    
    public static void updateStatusForExpiredScheduledActions() {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Updating status for expired schedule once action for which status is scheduled");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCommandToCollection"));
            final Join groupActiontoCollectionJoin = new Join("ScheduledCommandToCollection", "GroupActionToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join groupActionHistoryJoin = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            final Column CollectionIDColumn = new Column("ScheduledCommandToCollection", "COLLECTION_ID");
            final Column executionTimeColumn = new Column("ScheduledCommandToCollection", "EXECUTION_TIME");
            final Column expiryTimeColumn = new Column("ScheduledCommandToCollection", "EXPIRES");
            final Column grpToCollectionGroupActionID = new Column("GroupActionToCollection", "GROUP_ACTION_ID");
            final Column grpToCollectionCollectionID = new Column("GroupActionToCollection", "COLLECTION_ID");
            final Column groupActionHistoryGroupActionID = new Column("GroupActionHistory", "GROUP_ACTION_ID");
            final Column groupActionHistoryActionStatus = new Column("GroupActionHistory", "ACTION_STATUS");
            sq.addJoin(groupActiontoCollectionJoin);
            sq.addJoin(groupActionHistoryJoin);
            sq.addSelectColumn(CollectionIDColumn);
            sq.addSelectColumn(expiryTimeColumn);
            sq.addSelectColumn(executionTimeColumn);
            sq.addSelectColumn(grpToCollectionGroupActionID);
            sq.addSelectColumn(grpToCollectionCollectionID);
            sq.addSelectColumn(groupActionHistoryGroupActionID);
            sq.addSelectColumn(groupActionHistoryActionStatus);
            final Criteria executionTimeNullCriteria = new Criteria(executionTimeColumn, (Object)null, 1);
            final Criteria executionTimeCriteria = new Criteria(new Column("ScheduledCommandToCollection", "EXECUTION_TIME"), (Object)System.currentTimeMillis(), 6);
            final Criteria executionStatusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)7, 0);
            final Criteria c = executionTimeCriteria.and(executionTimeNullCriteria);
            sq.setCriteria(c.and(executionStatusCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> scheduledCommandToCollectionToRows = dataObject.getRows("ScheduledCommandToCollection");
                while (scheduledCommandToCollectionToRows.hasNext()) {
                    final Row scheduledCommandToCollectionToRow = scheduledCommandToCollectionToRows.next();
                    final Long executionTime = (Long)scheduledCommandToCollectionToRow.get("EXECUTION_TIME");
                    final Long collectionID = (Long)scheduledCommandToCollectionToRow.get("COLLECTION_ID");
                    final Long expiryTime = (Long)scheduledCommandToCollectionToRow.get("EXPIRES");
                    if (System.currentTimeMillis() > executionTime + expiryTime) {
                        final List groupActionIds = GroupActionToCollectionHandler.getInstance().getGroupActionIDsforCollectionID(Collections.singletonList(collectionID));
                        GroupActionScheduleUtils.updateGroupActionStatus(groupActionIds, 6);
                    }
                }
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Error while updating the status for expired scheduled actions", e);
        }
    }
    
    public static List excludeAndroidDevicesForResourceList(final List resourceList) {
        final List androidExcludedResourceList = new ArrayList();
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Excluding android devices from the resourceList:{0}", resourceList);
            final HashMap platformBasedDevices = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceList);
            final Set platforms = platformBasedDevices.keySet();
            for (final int platform : platforms) {
                if (platform == 2) {
                    continue;
                }
                final Set resourceIdSet = platformBasedDevices.get(platform);
                androidExcludedResourceList.addAll(resourceIdSet);
            }
            return androidExcludedResourceList;
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception while removing the android devices from resourceList:{0}; Thus returning the entire resourceList", resourceList);
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in excludeAndroidDevicesForResourceList", e);
            return resourceList;
        }
    }
    
    public static Integer getScheduleExecutionTypeForCollection(final Long collectionID) {
        Integer scheduleExcecutionType = -1;
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "getting schedule Execution Type for the given CollectionID:{0}", collectionID);
            final Long scheduleID = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForCollection(collectionID);
            scheduleExcecutionType = ScheduleRepositoryHandler.getInstance().getScheduleExecutionTypeForSchedule(scheduleID);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Error while fetching scheduleType for collection:{0}", collectionID);
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getScheduleExecutionTypeForCollection", e);
        }
        return scheduleExcecutionType;
    }
    
    public static Long getNextExecutionTimeForSchedule(final Long scheduleID) {
        Long executionTime = -1L;
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "getting Next Execution Time for the given scheduleID:{0}", scheduleID);
            final String scheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleID);
            executionTime = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName);
            if (executionTime == null) {
                executionTime = -1L;
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getNextExecutionTimeForSchedule", e);
        }
        return executionTime;
    }
    
    public static String getScheduleTime(final Long scheduleID) {
        String scheduleTime = null;
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "getting Schedule Time for the given scheduleID:{0}", scheduleID);
            final String scheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleID);
            final HashMap scheduleMap = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(scheduleName);
            scheduleTime = scheduleMap.get("exeHours") + ":" + scheduleMap.get("exeMinutes");
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getScheduleTime", e);
        }
        return scheduleTime;
    }
    
    public static void addCommmandsToDeviceForSchedule(final Long scheduleID) {
        ScheduledActionsUtils.logger.log(Level.INFO, "addCommandsToDeviceForSchedule(): scheduleID{0}", new Object[] { scheduleID });
        try {
            final List collectionIDs = ScheduledCommandToCollectionHandler.getInstance().getCollectionsForSchedule(scheduleID);
            final Long slotTimeInMillis = getNextExecutionTimeForSchedule(scheduleID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Join join = new Join("ScheduledCollectionToResource", "ScheduledCommandToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            selectQuery.addJoin(join);
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCollectionToResource", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCollectionToResource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCommandToCollection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCommandToCollection", "EXPIRES"));
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCommandToCollection", "COMMAND_ID"));
            final Criteria collectionListCriteria = new Criteria(Column.getColumn("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionIDs.toArray(), 8);
            final Criteria scheduleCriteria = new Criteria(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"), (Object)scheduleID, 0);
            selectQuery.setCriteria(collectionListCriteria.and(scheduleCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            for (final Long collectionID : (ArrayList)collectionIDs) {
                Row r = null;
                final Criteria collectionCriteria = new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
                r = dataObject.getRow("ScheduledCommandToCollection", collectionCriteria);
                final Long commandID = (Long)r.get("COMMAND_ID");
                final Long expiry = (Long)r.get("EXPIRES");
                final List commandRepositoryTypes = ScheduledCollectionToResourceHandler.getInstance().getCommandRespositoryTypeForCollection(collectionID);
                for (final int commandRepositoryType : (ArrayList)commandRepositoryTypes) {
                    List resourceIDs = ScheduledCollectionToResourceHandler.getInstance().getResourcesForCollectionAndCommandRepositoryType(collectionID, (long)commandRepositoryType);
                    resourceIDs = MDMGroupHandler.getInstance().getDeviceListFromResourceList(resourceIDs);
                    final HashMap platformBasedDevices = ManagedDeviceHandler.getInstance().getPlatformBasedMemberId(resourceIDs);
                    final Set platforms = platformBasedDevices.keySet();
                    final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
                    final String commandName = commandUUID + "Scheduled;collection=" + collectionID;
                    final Long newCommandID = DeviceCommandRepository.getInstance().addCommand(commandName);
                    for (final int platform : platforms) {
                        if (platform == 2) {
                            continue;
                        }
                        final Set resourceIdSet = platformBasedDevices.get(platform);
                        final List resourceIds = new ArrayList();
                        for (final Long resourceID : resourceIdSet) {
                            resourceIds.add(resourceID);
                        }
                        DeviceCommandRepository.getInstance().assignCommandToDevicesWithSlot(newCommandID, resourceIds, commandRepositoryType, slotTimeInMillis, slotTimeInMillis + expiry);
                    }
                }
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in addMdCommandsToDeviceForSchedule", e);
        }
    }
    
    public static String getUserNameForUserID(final Long userID) throws Exception {
        ScheduledActionsUtils.logger.log(Level.INFO, "getting User Name for the given userID:{0}", userID);
        return (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)userID, "FIRST_NAME");
    }
    
    public static void deleteSchedule(final List collectionIDs, final List resourceIds) throws Exception {
        ScheduledActionsUtils.logger.log(Level.INFO, "Proceeding to delete resources:{0} for collection:{1}", new Object[] { resourceIds, collectionIDs });
        for (final Long collectionID : collectionIDs) {
            DeviceCommandRepository.getInstance().removeMdCommandsForScheduledCommands(collectionID, resourceIds);
        }
        ScheduledCollectionToResourceHandler.getInstance().deleteScheduledCollectionToResource(collectionIDs, resourceIds);
    }
    
    public static String getActionNameForCommandName(final String commandName) {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Getting actionname for the command:{0}", new Object[] { commandName });
            switch (commandName) {
                case "RestartDevice": {
                    return "restart";
                }
                case "ShutDownDevice": {
                    return "shutdown";
                }
                default: {
                    return commandName;
                }
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getActionNameForCommandName", e);
            return commandName;
        }
    }
    
    public static String convertTimeZone(final String time, final String zone) {
        ScheduledActionsUtils.logger.log(Level.INFO, "Converting Time:{0} for the zone:{1}", new Object[] { time, zone });
        final String[] hoursAndMins = time.split(":");
        final DateTimeFormatter date = DateTimeFormatter.ofPattern("HH:mm");
        final ZoneId clientZoneID = ZoneId.of(zone, ZoneId.SHORT_IDS);
        final ZoneId serverZoneID = MDMApiFactoryProvider.getMDMUtilAPI().getZoneForCreatingSchedule();
        final LocalDateTime Localtime = LocalDateTime.of(2021, 6, 6, Integer.parseInt(hoursAndMins[0]), Integer.parseInt(hoursAndMins[1]));
        final ZonedDateTime clientZonedTime = Localtime.atZone(clientZoneID);
        final ZonedDateTime serverZoneTime = clientZonedTime.withZoneSameInstant(serverZoneID);
        return date.format(serverZoneTime);
    }
    
    public static void disableScheduleForCollection(final List collections) {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Disabling the schedules for the collections:{0}", collections);
            final List execScheduleIds = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDsForCollections(collections);
            final List preScheduleIDs = ScheduleMapperHandler.getInstance().getPreScheduleIdsForExecScheduleIds(execScheduleIds);
            execScheduleIds.addAll(preScheduleIDs);
            final List scheduleNames = ScheduleRepositoryHandler.getInstance().getScheduleNames(execScheduleIds);
            for (final String scheduleName : scheduleNames) {
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState(false, scheduleName);
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Error while disabling the scheduled for the collecitons{0}", collections);
        }
    }
    
    public static void deleteScheduledActionsForCollections(final List collections) throws Exception {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Deleting entries from SCHEDULEDCOMMANDTOCOLLECTION and SCHEDULEDCOLLECTIONTORESOURCE and disabling schedules for the collections:{0}", collections);
            disableScheduleForCollection(collections);
            DataAccess.delete("ScheduledCollectionToResource", new Criteria(new Column("ScheduledCollectionToResource", "COLLECTION_ID"), (Object)collections.toArray(), 8));
            DataAccess.delete("ScheduledCommandToCollection", new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collections.toArray(), 8));
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Error while deleting scheduled actions for the collections:{0}", collections);
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in deleteScheduledActionsForCollections", e);
            throw e;
        }
    }
    
    public static Long getExistingScheduleID(final JSONObject scheduleParams) {
        ScheduledActionsUtils.logger.log(Level.INFO, "getting existing scheduleIDs for params:{0}", scheduleParams.toMap());
        try {
            final Table scheduleMapperTable = new Table("ScheduleMapper");
            final Column execscheduleidColumn = new Column("ScheduleMapper", "EXECUTION_SCHEDULE_ID");
            final Column setupscheduleidColumn = new Column("ScheduleMapper", "SETUP_SCHEDULE_ID");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(scheduleMapperTable);
            sq.addSelectColumn(execscheduleidColumn);
            sq.addSelectColumn(setupscheduleidColumn);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("ScheduleMapper");
                final List<Long> scheduleList = DBUtil.getColumnValuesAsList(iter, "EXECUTION_SCHEDULE_ID");
                if (!scheduleList.isEmpty()) {
                    for (final Long i : scheduleList) {
                        if (checkScheduleParamsAreEqual(scheduleParams, i)) {
                            return i;
                        }
                    }
                }
            }
        }
        catch (final DataAccessException e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Going wrong coz of ", (Throwable)e);
        }
        return -1L;
    }
    
    private static Long getCollectionID(final Long customerId, final Properties properties) {
        final JSONObject collectionJSON = new JSONObject();
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Generating a collection for the given properties{0} and customerID{1}", new Object[] { properties, customerId });
            final Long userId = ((Hashtable<K, Long>)properties).get("userID");
            final String profilePayloadIdentifier = "com.mdm." + UUID.randomUUID().toString() + ".ScheduledActions";
            collectionJSON.put("PROFILE_DESCRIPTION", (Object)"ScheduledAction collection");
            collectionJSON.put("PROFILE_NAME", (Object)("Scheduled Action " + System.currentTimeMillis()));
            collectionJSON.put("PROFILE_TYPE", 11);
            collectionJSON.put("PLATFORM_TYPE", 0);
            collectionJSON.put("CUSTOMER_ID", (Object)customerId);
            collectionJSON.put("CREATED_BY", (Object)userId);
            collectionJSON.put("LAST_MODIFIED_BY", (Object)userId);
            collectionJSON.put("SECURITY_TYPE", -1);
            collectionJSON.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)profilePayloadIdentifier);
            ProfileConfigHandler.addProfileCollection(collectionJSON);
            ((Hashtable<String, Object>)properties).put("COLLECTION_ID", collectionJSON.get("COLLECTION_ID"));
            ScheduledCommandToCollectionHandler.getInstance().addCollectionToCommand(properties);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("COLLECTION_ID", collectionJSON.getLong("COLLECTION_ID"));
            ScheduledTimeZoneHandler.getInstance().addCollectionForTimeZone(jsonObject.getLong("COLLECTION_ID"), ((Hashtable<K, String>)properties).get("time_zone"));
            jsonObject.put("PROFILE_ID", collectionJSON.getLong("PROFILE_ID"));
            jsonObject.put("PLATFORM_TYPE", 0);
            jsonObject.put("APP_CONFIG", false);
            jsonObject.put("LAST_MODIFIED_BY", (Object)userId);
            jsonObject.put("PROFILE_TYPE", 11);
            jsonObject.put("PROFILE_PAYLOAD_IDENTIFIER", (Object)profilePayloadIdentifier);
            ProfileConfigHandler.publishProfile(jsonObject);
            ScheduledActionsUtils.logger.log(Level.INFO, "The new collection is published ");
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Get collection is not working ", e);
        }
        return (Long)collectionJSON.get("COLLECTION_ID");
    }
    
    public static List ScheduleCollectionsToResource(final JSONArray commandIDs, final JSONArray resourceIDs, final Long scheduleID, final Long expiry, final int commandRepositoryType, final Long customerID, final Long userID, final int scheduleType, final Long executionTime, final String timeZone) {
        final List collectionIDList = new ArrayList();
        ScheduledActionsUtils.logger.log(Level.INFO, "ScheduleCollectionsToResource(): commandIDList:{0} resourceList {1} executionTime{2}", new Object[] { commandIDs.toList(), resourceIDs.toList(), executionTime });
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCollectionToResource"));
            final Join join = new Join("ScheduledCollectionToResource", "ScheduledCommandToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            selectQuery.addSelectColumn(Column.getColumn("ScheduledCollectionToResource", "*"));
            final ArrayList resourceList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(resourceIDs);
            final ArrayList commandList = (ArrayList)JSONUtil.getInstance().convertLongJSONArrayTOList(commandIDs);
            final Criteria resCriteria = new Criteria(Column.getColumn("ScheduledCollectionToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cmdCriteria = new Criteria(Column.getColumn("ScheduledCommandToCollection", "COMMAND_ID"), (Object)commandList.toArray(), 8);
            final Criteria scheduleCriteria = new Criteria(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"), (Object)scheduleID, 0);
            selectQuery.addJoin(join);
            selectQuery.setCriteria(resCriteria.and(cmdCriteria).and(scheduleCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final DataObject ScheduledCommandToCollectionDataObject = DataAccess.get("ScheduledCommandToCollection", scheduleCriteria);
            for (int i = 0; i < commandIDs.length(); ++i) {
                final Long commandID = commandIDs.getLong(i);
                Row r = null;
                if (!ScheduledCommandToCollectionDataObject.isEmpty()) {
                    final Criteria commandCriteria = new Criteria(new Column("ScheduledCommandToCollection", "COMMAND_ID"), (Object)commandID, 0);
                    final Criteria expiryCriteria = new Criteria(new Column("ScheduledCommandToCollection", "EXPIRES"), (Object)expiry, 0);
                    r = ScheduledCommandToCollectionDataObject.getRow("ScheduledCommandToCollection", commandCriteria.and(expiryCriteria));
                }
                Long collectionID;
                if (r == null) {
                    final Properties collectionProps = new Properties();
                    ((Hashtable<String, Long>)collectionProps).put("COMMAND_ID", commandID);
                    ((Hashtable<String, Long>)collectionProps).put("SCHEDULE_ID", scheduleID);
                    ((Hashtable<String, Long>)collectionProps).put("EXPIRES", expiry);
                    ((Hashtable<String, Integer>)collectionProps).put("execution_type", scheduleType);
                    ((Hashtable<String, String>)collectionProps).put("time_zone", timeZone);
                    if (scheduleType == 2) {
                        ((Hashtable<String, Long>)collectionProps).put("EXECUTION_TIME", executionTime);
                    }
                    ((Hashtable<String, Long>)collectionProps).put("userID", userID);
                    collectionID = getCollectionID(customerID, collectionProps);
                    collectionIDList.add(collectionID);
                }
                else {
                    collectionID = (Long)r.get("COLLECTION_ID");
                    collectionIDList.add(collectionID);
                }
                ProfileAssociateHandler.getInstance().associateCollectionToResources(collectionID, resourceList, customerID, userID);
                for (int j = 0; j < resourceIDs.length(); ++j) {
                    final Long resourceID = resourceIDs.getLong(j);
                    Row collectionToResourceRow = null;
                    final Criteria resourceCriteria = new Criteria(new Column("ScheduledCollectionToResource", "RESOURCE_ID"), (Object)resourceID, 0);
                    final Criteria collectionCriteria = new Criteria(new Column("ScheduledCollectionToResource", "COLLECTION_ID"), (Object)collectionID, 0);
                    final Criteria commandRepositoryTypeCriteria = new Criteria(new Column("ScheduledCollectionToResource", "COMMAND_REPOSITORY_TYPE"), (Object)commandRepositoryType, 0);
                    collectionToResourceRow = dataObject.getRow("ScheduledCollectionToResource", resourceCriteria.and(collectionCriteria).and(commandRepositoryTypeCriteria));
                    if (collectionToResourceRow == null) {
                        collectionToResourceRow = new Row("ScheduledCollectionToResource");
                        collectionToResourceRow.set("COLLECTION_ID", (Object)collectionID);
                        collectionToResourceRow.set("RESOURCE_ID", (Object)resourceID);
                        collectionToResourceRow.set("COMMAND_REPOSITORY_TYPE", (Object)commandRepositoryType);
                        collectionToResourceRow.set("STATUS", (Object)1);
                        dataObject.addRow(collectionToResourceRow);
                    }
                }
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in ScheduleCollectionsToResource", e);
        }
        return collectionIDList;
    }
    
    public static Long getTempCommandIDForCommandID(final Long commandID, final Long collectionID) {
        final Long tempCommandID = -1L;
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Getting temporary commandID for the given CommandID{0}", commandID);
            final String commandUUID = MDMUtil.getInstance().getCommandUUIDFromCommandID(commandID);
            final String commandName = commandUUID + "Scheduled;collection=" + collectionID;
            return DeviceCommandRepository.getInstance().getCommandID(commandName);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception while retrieving Temporary commandID for commandID{0}", commandID);
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getTempCommandIDForCommandID", e);
            return tempCommandID;
        }
    }
    
    public static Integer getResourceTypeForResourceID(final Long resourceID) throws Exception {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Getting resoure type for the resourceID{0}", new Object[] { resourceID });
            return (Integer)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)resourceID, "RESOURCE_TYPE");
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getResourceTypeForResourceID", e);
            throw e;
        }
    }
    
    public static Boolean checkScheduleParamsAreEqual(final JSONObject scheduleParams, final Long scheduleId) {
        ScheduledActionsUtils.logger.log(Level.INFO, "checking the schedule with scheuledID:{0} and scheduleParams:{1} are equal", new Object[] { scheduleId, scheduleParams });
        String scheduleName = "";
        try {
            scheduleName = ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleId);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "error in checking existing schedule", e);
        }
        final HashMap map = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(scheduleName);
        final String scheduleType = scheduleParams.getString("schedule_type");
        if (scheduleType.equals(map.get("schedType"))) {
            try {
                final String s = scheduleType;
                switch (s) {
                    case "Daily": {
                        if (!scheduleParams.getString("daily_interval_type").equals(map.get("dailyIntervalType"))) {
                            break;
                        }
                        final String dailyTime = scheduleParams.getString("daily_time");
                        final String slotTime = dailyTime.substring(dailyTime.length() - 5);
                        final String[] timeList = slotTime.split(":");
                        final int minutes = map.get("exeMinutes");
                        final int hours = map.get("exeHours");
                        final int totalTime = hours * 60 + minutes;
                        final long totalTime2 = Long.parseLong(timeList[0]) * 60L + Long.parseLong(timeList[1]);
                        if (totalTime == (int)totalTime2) {
                            return true;
                        }
                        break;
                    }
                    case "Monthly": {
                        final int monthlyMinutes = map.get("exeMinutes");
                        final int monthlyHours = map.get("exeHours");
                        final int monthlytotalTime = monthlyHours * 60 + monthlyMinutes;
                        final String monthlySlotTime = scheduleParams.getString("monthly_time");
                        final String[] monthlyTimeList = monthlySlotTime.split(":");
                        final Long monthlyTotalTime2 = Long.parseLong(monthlyTimeList[0]) * 60L + Long.parseLong(monthlyTimeList[1]);
                        final String monthsListString = scheduleParams.getString("months_list");
                        final ArrayList monthsList = new ArrayList();
                        monthsList.addAll(Arrays.asList(monthsListString.split(",")));
                        final int[] monthsListDB = map.get("months");
                        final ArrayList monthsListDB2 = new ArrayList();
                        for (final int i : monthsListDB) {
                            monthsListDB2.add(String.valueOf(i));
                        }
                        if (!CollectionUtils.isEqualCollection((Collection)monthsList, (Collection)monthsListDB2) || monthlytotalTime != monthlyTotalTime2) {
                            return false;
                        }
                        if (!scheduleParams.getString("monthly_perform").equals(map.get("monthlyPerform"))) {
                            break;
                        }
                        if (scheduleParams.getString("monthly_perform").equals("WeekDay")) {
                            final int monthlyWeekDay = map.get("monthlyWeekDay");
                            final int[] monthlyWeekNum = map.get("monthlyWeekNum");
                            final ArrayList weekNum = new ArrayList();
                            weekNum.addAll(Arrays.asList(scheduleParams.getString("monthly_week_num").split(",")));
                            final ArrayList monthlyWeekNum2 = new ArrayList();
                            for (final int j : monthlyWeekNum) {
                                monthlyWeekNum2.add(String.valueOf(j));
                            }
                            if (String.valueOf(monthlyWeekDay).equals(scheduleParams.getString("monthly_week_day")) && CollectionUtils.isEqualCollection((Collection)monthlyWeekNum2, (Collection)weekNum)) {
                                return true;
                            }
                            break;
                        }
                        else {
                            if (!scheduleParams.getString("monthly_perform").equals("Day")) {
                                return false;
                            }
                            final int monthlyDay = map.get("dates");
                            if (String.valueOf(monthlyDay).equals(scheduleParams.getString("monthly_day"))) {
                                return true;
                            }
                            break;
                        }
                        break;
                    }
                    case "Weekly": {
                        final String daysOfWeekString = scheduleParams.getString("days_of_week");
                        final ArrayList daysOfWeek = new ArrayList();
                        daysOfWeek.addAll(Arrays.asList(daysOfWeekString.split(",")));
                        final int[] daysOfWeekDB = map.get("daysOfWeek");
                        final ArrayList daysofWeekDB2 = new ArrayList();
                        for (final int j : daysOfWeekDB) {
                            daysofWeekDB2.add(String.valueOf(j));
                        }
                        if (!CollectionUtils.isEqualCollection((Collection)daysOfWeek, (Collection)daysofWeekDB2)) {
                            return false;
                        }
                        final int minutes2 = map.get("exeMinutes");
                        final int hours2 = map.get("exeHours");
                        final int totalTime3 = hours2 * 60 + minutes2;
                        final String slotTime2 = scheduleParams.getString("weekly_time");
                        final String[] timeList2 = slotTime2.split(":");
                        final long totalTime4 = Long.parseLong(timeList2[0]) * 60L + Long.parseLong(timeList2[1]);
                        if (totalTime3 == (int)totalTime4) {
                            return true;
                        }
                        break;
                    }
                }
            }
            catch (final Exception e2) {
                ScheduledActionsUtils.logger.log(Level.SEVERE, "Something wrong with ", e2);
            }
        }
        return false;
    }
    
    public static Long getProfileIDForCollection(final Long collection) throws Exception {
        ScheduledActionsUtils.logger.log(Level.INFO, "Getting profileID for the given collectionID{0}", collection);
        return (Long)DBUtil.getValueFromDB("ProfileToCollection", "COLLECTION_ID", (Object)collection, "PROFILE_ID");
    }
    
    public static String getProfilePayloadIdentifierForProfile(final Long profileID) throws Exception {
        ScheduledActionsUtils.logger.log(Level.INFO, "Getting payLoadIdentifier for the given profileID{0}", profileID);
        return (String)DBUtil.getValueFromDB("Profile", "PROFILE_ID", (Object)profileID, "PROFILE_PAYLOAD_IDENTIFIER");
    }
    
    public static JSONObject getScheduleDetailsAsJSON(final Long collectionID) {
        JSONObject schedulerJSON = null;
        final int executionType = getScheduleExecutionTypeForCollection(collectionID);
        final JSONObject responseJSON = new JSONObject();
        ScheduledActionsUtils.logger.log(Level.INFO, "getting scheduled command details for the given collectionID{0}", collectionID);
        try {
            final Long commandID = ScheduledCommandToCollectionHandler.getInstance().getCommandForCollection(collectionID);
            final List commandList = new ArrayList();
            commandList.add(commandID);
            final JSONObject commandInfo = new JSONObject();
            final String payloadUUID = UUID.randomUUID().toString();
            responseJSON.put("payloadUUID", (Object)payloadUUID);
            final HashMap commandMap = (HashMap)DeviceCommandRepository.getInstance().getCommandInfoMap(commandList);
            final Long scheduleID = ScheduledCommandToCollectionHandler.getInstance().getScheduleIDForCollection(collectionID);
            responseJSON.put("scheduledTimeZone", (Object)ScheduledTimeZoneHandler.getInstance().getTimeZoneForCollection(collectionID));
            responseJSON.put("serverTimeZone", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getZoneForCreatingSchedule());
            if (executionType == 2) {
                final Long executionTime = ScheduledCommandToCollectionHandler.getInstance().getExecutionTimeForCollection(collectionID);
                responseJSON.put("executionType", 2);
                commandInfo.put("commandType", commandMap.get(commandID));
                final List commandInfoList = new ArrayList();
                commandInfoList.add(commandInfo);
                responseJSON.put("commandData", (Collection)commandInfoList);
                responseJSON.put("scheduleOnceTime", (Object)executionTime);
                return responseJSON;
            }
            final HashMap map = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(ScheduleRepositoryHandler.getInstance().getScheduleName(scheduleID));
            schedulerJSON = new JSONObject((Map)map);
            final String time = schedulerJSON.get("exeHours") + ":" + schedulerJSON.get("exeMinutes") + ":" + schedulerJSON.get("exeSeconds");
            responseJSON.put("executionType", 1);
            responseJSON.put("execTime", (Object)time);
            if (schedulerJSON.get("schedType").equals("Daily")) {
                final String date = schedulerJSON.get("startDate") + "/" + schedulerJSON.get("startMonth") + "/" + schedulerJSON.get("startYear");
                responseJSON.put("startDate", (Object)date);
                responseJSON.put("schedType", 1);
                if (schedulerJSON.get("dailyIntervalType").equals("everyDay")) {
                    responseJSON.put("dailyIntervalType", 1);
                }
                else if (schedulerJSON.get("dailyIntervalType").equals("alternativeDays")) {
                    responseJSON.put("dailyIntervalType", 2);
                }
                else {
                    responseJSON.put("dailyIntervalType", 3);
                }
            }
            else if (schedulerJSON.get("schedType").equals("Weekly")) {
                responseJSON.put("schedType", 2);
                responseJSON.put("daysOfWeek", schedulerJSON.get("daysOfWeek"));
            }
            else {
                responseJSON.put("schedType", 3);
                responseJSON.put("months", schedulerJSON.get("months"));
                if (schedulerJSON.get("monthlyPerform").equals("Day")) {
                    responseJSON.put("monthlyPerform", 1);
                    responseJSON.put("dates", schedulerJSON.get("dates"));
                }
                else {
                    responseJSON.put("monthlyPerform", 2);
                    responseJSON.put("monthlyWeekDay", schedulerJSON.get("monthlyWeekDay"));
                    responseJSON.put("monthlyWeekNum", schedulerJSON.get("monthlyWeekNum"));
                }
            }
            commandInfo.put("commandType", commandMap.get(commandID));
            final List commandInfoList2 = new ArrayList();
            commandInfoList2.add(commandInfo);
            responseJSON.put("commandData", (Collection)commandInfoList2);
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in getScheduleDetailsAsJSON", e);
        }
        return responseJSON;
    }
    
    public static Boolean checkIfActionIsApplicableForResource(final String action, final Long resourceId) {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Checking if the resource{1} is Applicable for the action:{0}", new Object[] { action, resourceId });
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("ManagedDevice", criteria);
            String actionName = null;
            if (action.equalsIgnoreCase("scheduled_restart")) {
                actionName = "restart";
            }
            else if (action.equalsIgnoreCase("scheduled_shutdown")) {
                actionName = "shutdown";
            }
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
            int platform = 0;
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("ManagedDevice");
                platform = (int)row.get("PLATFORM_TYPE");
            }
            final org.json.simple.JSONArray resource = InvActionUtilProvider.getInvActionUtil(platform).getApplicableBulkActionDevices(Collections.singleton(resourceId), actionName, customerId);
            return !resource.isEmpty();
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in checkIfActionIsApplicableForResource", e);
            return false;
        }
    }
    
    public static void disableIfScheduleIsUnused(final Long collectionID) throws Exception {
        try {
            ScheduledActionsUtils.logger.log(Level.INFO, "Disabling if the schedule for collectionID{0} is not used by other actions", new Object[] { collectionID });
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ScheduledCommandToCollection"));
            final Join groupActionToCollectionJoin = new Join("ScheduledCommandToCollection", "GroupActionToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join groupActionHistoryJoin = new Join("GroupActionToCollection", "GroupActionHistory", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2);
            sq.addSelectColumn(new Column("ScheduledCommandToCollection", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "COLLECTION_ID"));
            sq.addSelectColumn(new Column("GroupActionToCollection", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionHistory", "GROUP_ACTION_ID"));
            sq.addSelectColumn(new Column("GroupActionHistory", "ACTION_STATUS"));
            final Criteria c = new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            sq.addJoin(groupActionToCollectionJoin);
            sq.addJoin(groupActionHistoryJoin);
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row scheduleRow = dataObject.getRow("ScheduledCommandToCollection", c);
                final Long scheduleID = (Long)scheduleRow.get("SCHEDULE_ID");
                final Row groupActionRow = dataObject.getRow("GroupActionToCollection", new Criteria(new Column("GroupActionToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long groupActionID = (Long)groupActionRow.get("GROUP_ACTION_ID");
                final Criteria groupActionIDCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionID, 0);
                final Criteria groupActionStatusCriteria = new Criteria(new Column("GroupActionHistory", "ACTION_STATUS"), (Object)6, 1);
                final Criteria groupActionCriteria = groupActionIDCriteria.or(groupActionStatusCriteria);
                final Criteria scheduleIDCriteria = new Criteria(new Column("ScheduledCommandToCollection", "SCHEDULE_ID"), (Object)scheduleID, 0);
                final Iterator<Row> collectionIDRows = dataObject.getRows("ScheduledCommandToCollection", scheduleIDCriteria);
                final List collectionIDs = DBUtil.getColumnValuesAsList((Iterator)collectionIDRows, "COLLECTION_ID");
                final Criteria collectionIDsCriteria = new Criteria(new Column("ScheduledCommandToCollection", "COLLECTION_ID"), (Object)collectionIDs.toArray(), 8);
                final Iterator<Row> groupActionIDRows = dataObject.getRows("GroupActionToCollection", collectionIDsCriteria);
                final List groupActionIDs = DBUtil.getColumnValuesAsList((Iterator)groupActionIDRows, "GROUP_ACTION_ID");
                final Criteria groupActionIDsCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionIDs.toArray(), 8);
                final Iterator<Row> suspendableRows = dataObject.getRows("GroupActionToCollection", groupActionIDsCriteria.and(groupActionCriteria));
                final int size = Iterators.size((Iterator)suspendableRows);
                if (size < 2 && size != 0) {
                    final ScheduleCommandService service = new ScheduleCommandService();
                    service.disableSchedule(scheduleID);
                }
            }
        }
        catch (final Exception e) {
            ScheduledActionsUtils.logger.log(Level.SEVERE, "Exception in disableIfScheduleIsUnused", e);
        }
    }
    
    static {
        ScheduledActionsUtils.logger = Logger.getLogger("ActionsLogger");
    }
}
