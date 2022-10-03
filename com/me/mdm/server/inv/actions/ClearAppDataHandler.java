package com.me.mdm.server.inv.actions;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Join;
import org.apache.commons.lang.BooleanUtils;
import com.adventnet.persistence.WritableDataObject;
import java.util.Collections;
import java.util.HashMap;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONArray;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.google.common.collect.Lists;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Map;
import org.json.JSONObject;
import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class ClearAppDataHandler
{
    private static ClearAppDataHandler clearAppDataHandler;
    private Logger logger;
    public static final int INCLUDE_APPS = 1;
    public static final int EXCLUDE_APPS = 2;
    private static final Boolean CLEAR_ALL_APPS_ALLOWED;
    private static final Boolean CLEAR_ALL_APPS_NOT_ALLOWED;
    private static final Boolean INCLUDE_ONLY_LISTED_APPS;
    private static final Boolean EXCLUDE_LISTED_APPS;
    private DataObject finalDO;
    private DataObject existingClearAppInfoDO;
    private DataObject existingClearAppPolicyDO;
    public Integer resChunkSize;
    public Integer defaultChunkSize;
    
    public ClearAppDataHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.finalDO = null;
        this.existingClearAppInfoDO = null;
        this.existingClearAppPolicyDO = null;
        this.defaultChunkSize = 500;
        String chunkSizeStr = null;
        try {
            chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("command_res_size");
            this.resChunkSize = ((chunkSizeStr == null) ? this.defaultChunkSize : Integer.parseInt(chunkSizeStr));
        }
        catch (final Exception e) {
            this.resChunkSize = 500;
        }
    }
    
    public static ClearAppDataHandler getInstance() {
        if (ClearAppDataHandler.clearAppDataHandler == null) {
            ClearAppDataHandler.clearAppDataHandler = new ClearAppDataHandler();
        }
        return ClearAppDataHandler.clearAppDataHandler;
    }
    
    public void validateClearDataApps(Collection<Long> appGroupIds, final Long customerID, final int platformType) throws APIHTTPException {
        if (appGroupIds.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            appGroupIds = new HashSet<Long>(appGroupIds);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            selectQuery.setCriteria(appGroupIdCriteria.and(customerCriteria).and(platformCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdAppGroupDetails");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("APP_GROUP_ID"))));
            }
            appGroupIds.removeAll(apps);
            if (appGroupIds.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(appGroupIds) });
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Issue on validating appGroupIds for clear app data", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public List<Long> getAppGroupIdsFromAppIds(final Collection<Long> appIds) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToGroupRel"));
        final Criteria appIdcriteria = new Criteria(Column.getColumn("MdAppToGroupRel", "APP_ID"), (Object)appIds.toArray(), 8);
        selectQuery.setCriteria(appIdcriteria);
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator<Row> rows = dataObject.getRows("MdAppToGroupRel");
        final ArrayList<Long> appGroupIds = new ArrayList<Long>();
        while (rows.hasNext()) {
            appGroupIds.add(Long.valueOf(String.valueOf(rows.next().get("APP_GROUP_ID"))));
        }
        return appGroupIds;
    }
    
    public void executeClearAppDataCommand(final JSONObject clearAppDataInfo, final Map infoMap) {
        try {
            this.logger.log(Level.INFO, "Going to execute clear app data command");
            final JSONArray devicesJSONArray = clearAppDataInfo.optJSONArray("devices");
            final List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
            Long commandId = DeviceCommandRepository.getInstance().getCommandID("ClearAppData");
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().addCommand("ClearAppData");
            }
            for (final List<Long> subResourceList : Lists.partition((List)resourceList, (int)this.resChunkSize)) {
                clearAppDataInfo.put("COMMAND_ID", (Object)commandId);
                this.addOrUpdateClearAppDataInfo(clearAppDataInfo);
                DeviceCommandRepository.getInstance().addClearAppDataCommand(subResourceList);
                final String command_name = clearAppDataInfo.optString("command_name");
                final Long userId = clearAppDataInfo.optLong("ADDED_BY");
                final Long action_id = clearAppDataInfo.optLong("action_id");
                DeviceInvCommandHandler.getInstance().addOrUpdateBulkActionsDetails(subResourceList, commandId, userId, command_name, action_id, infoMap);
                NotificationHandler.getInstance().SendNotification(subResourceList, NotificationHandler.getNotificationType(clearAppDataInfo.optInt("PLATFORM_TYPE")));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error occurred in executeClearAppDataCommand");
        }
    }
    
    public void updateClearAppDataEventLog(final Long resourceId, final Long customerId, final Long userId) {
        try {
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            final String eventLogRemarks = "dc.mdm.actionlog.securitycommands.initiate";
            final String commandDisplayName = CommandUtil.getInstance().getCommandDisplayName("ClearAppData");
            final Object remarksArgs = commandDisplayName + "@@@" + ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceId, userName, eventLogRemarks, remarksArgs, customerId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateClearAppDataEventLog", ex);
        }
    }
    
    private void addOrUpdateClearAppDataInfo(final JSONObject clearAppDataInfo) throws Exception {
        try {
            this.logger.log(Level.INFO, "Going to addOrUpdate tables CLEARAPPDATAPOLICY and CLEARDATAAPPSINFO {0}", clearAppDataInfo);
            final Long commandId = clearAppDataInfo.optLong("COMMAND_ID");
            final JSONArray devicesJSONArray = clearAppDataInfo.optJSONArray("devices");
            List<Long> resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(devicesJSONArray);
            final Boolean clearDataForAllApps = clearAppDataInfo.optBoolean("CLEAR_DATA_FOR_ALL_APPS", true);
            final Boolean inclusion = clearAppDataInfo.optBoolean("INCLUSION", false);
            List<Long> appGroupIds = null;
            if (clearAppDataInfo.has("app_group_ids")) {
                appGroupIds = JSONUtil.getInstance().convertLongJSONArrayTOList(clearAppDataInfo.getJSONArray("app_group_ids"));
            }
            final JSONObject statusJSON = new CommandStatusHandler().getRecentBulkCommandInfo(resourceList, commandId);
            final Map<Integer, List<Long>> resourceStatusMap = new HashMap<Integer, List<Long>>();
            for (final Object resId : resourceList) {
                final String resIdStr = String.valueOf(resId);
                JSONObject currentJSON = new JSONObject();
                final JSONArray currentJSONArray = statusJSON.optJSONArray(resIdStr);
                int recentCommandStatus = -1;
                if (currentJSONArray != null) {
                    currentJSON = currentJSONArray.getJSONObject(0);
                    recentCommandStatus = currentJSON.optInt("COMMAND_STATUS", -1);
                }
                if (recentCommandStatus == 1 || recentCommandStatus == 4) {
                    resourceStatusMap.putIfAbsent(4, new ArrayList<Long>());
                    resourceStatusMap.get(4).add(Long.valueOf(resIdStr));
                }
                else {
                    resourceStatusMap.putIfAbsent(recentCommandStatus, new ArrayList<Long>());
                    resourceStatusMap.get(recentCommandStatus).add(Long.valueOf(resIdStr));
                }
            }
            for (final int recentCommandStatus2 : resourceStatusMap.keySet()) {
                resourceList = resourceStatusMap.get(recentCommandStatus2);
                if (resourceList != null) {
                    this.logger.log(Level.INFO, "Going to update clear app data tables for resources {0} with command status {1}", new Object[] { resourceList.toString(), recentCommandStatus2 });
                    this.initExistingClearAppsDO(resourceList);
                }
                if (recentCommandStatus2 == 1 || recentCommandStatus2 == 4) {
                    this.logger.log(Level.INFO, "Previous command is still in progress");
                    if (clearDataForAllApps) {
                        this.addOrUpdateClearAppDataPolicyTableInBulk(resourceList, ClearAppDataHandler.CLEAR_ALL_APPS_ALLOWED, ClearAppDataHandler.EXCLUDE_LISTED_APPS);
                        this.deleteAppGroupIdsInBulk(resourceList, Collections.emptyList());
                    }
                    else {
                        final JSONObject previousClearAppDataCommand = this.getRecentClearAppDataCommandInBulk(resourceList);
                        final Map<Long, List<Long>> appResGroupMap = this.getAppGroupIdsForResourceIdInBulk(resourceList);
                        this.logger.log(Level.INFO, "Validating previous command {0}", previousClearAppDataCommand.toString());
                        final List<Long> appGroupIdsToBeAppended = new ArrayList<Long>(appGroupIds);
                        final List<Long> inclAppGroupIdsToBeAppendedResList = new ArrayList<Long>();
                        for (final Object resIdObj : previousClearAppDataCommand.keySet()) {
                            final String resIdStr2 = String.valueOf(resIdObj);
                            final Long resourceId = Long.valueOf(resIdStr2);
                            final JSONObject currentJSON2 = previousClearAppDataCommand.getJSONObject(resIdStr2);
                            final Boolean previousClearDataForAllApps = currentJSON2.optBoolean("CLEAR_DATA_FOR_ALL_APPS");
                            final Boolean previousInclusion = currentJSON2.optBoolean("INCLUSION");
                            final List<Long> previousAppGroupIds = appResGroupMap.getOrDefault(Long.parseLong(resIdStr2), new ArrayList<Long>());
                            if (inclusion) {
                                if (!previousClearDataForAllApps && previousInclusion) {
                                    this.logger.log(Level.INFO, "As both present and previous commands have inclusion = true, checking for new AppGroupIds and adding in table CLEARDATAAPPSINFO");
                                    appGroupIdsToBeAppended.removeAll(previousAppGroupIds);
                                    inclAppGroupIdsToBeAppendedResList.add(resourceId);
                                }
                                else {
                                    if (previousClearDataForAllApps) {
                                        continue;
                                    }
                                    final List<Long> commonAppGroupIds = new ArrayList<Long>(appGroupIds);
                                    commonAppGroupIds.retainAll(previousAppGroupIds);
                                    if (commonAppGroupIds.size() == previousAppGroupIds.size()) {
                                        this.addOrUpdateClearAppDataPolicy(resourceId, ClearAppDataHandler.CLEAR_ALL_APPS_ALLOWED, ClearAppDataHandler.EXCLUDE_LISTED_APPS);
                                        this.deleteAppGroupIds(resourceId, Collections.emptyList());
                                    }
                                    else {
                                        if (commonAppGroupIds.isEmpty()) {
                                            continue;
                                        }
                                        this.deleteAppGroupIds(resourceId, commonAppGroupIds);
                                    }
                                }
                            }
                            else if (!previousClearDataForAllApps && previousInclusion) {
                                this.logger.log(Level.INFO, "As present command is exclude apps and previous command is include apps,sending exclude_app command to device with present command app_group_ids");
                                final List<Long> newAppGroupIds = new ArrayList<Long>(appGroupIds);
                                newAppGroupIds.removeAll(previousAppGroupIds);
                                this.deleteAppGroupIds(resourceId, Collections.emptyList());
                                if (newAppGroupIds.isEmpty()) {
                                    this.addOrUpdateClearAppDataPolicy(resourceId, ClearAppDataHandler.CLEAR_ALL_APPS_ALLOWED, ClearAppDataHandler.EXCLUDE_LISTED_APPS);
                                }
                                else {
                                    this.deleteAppGroupIds(resourceId, newAppGroupIds);
                                    this.addOrUpdateClearAppDataPolicy(resourceId, ClearAppDataHandler.CLEAR_ALL_APPS_NOT_ALLOWED, ClearAppDataHandler.EXCLUDE_LISTED_APPS);
                                }
                            }
                            else {
                                if (previousClearDataForAllApps) {
                                    continue;
                                }
                                final List<Long> commonAppGroupIds = new ArrayList<Long>(appGroupIds);
                                commonAppGroupIds.retainAll(previousAppGroupIds);
                                if (commonAppGroupIds.isEmpty()) {
                                    this.addOrUpdateClearAppDataPolicy(resourceId, ClearAppDataHandler.CLEAR_ALL_APPS_ALLOWED, ClearAppDataHandler.EXCLUDE_LISTED_APPS);
                                    this.deleteAppGroupIds(resourceId, Collections.emptyList());
                                }
                                else {
                                    final List<Long> uncommonAppGroupIds = new ArrayList<Long>(previousAppGroupIds);
                                    uncommonAppGroupIds.removeAll(commonAppGroupIds);
                                    if (uncommonAppGroupIds.isEmpty()) {
                                        continue;
                                    }
                                    uncommonAppGroupIds.addAll(uncommonAppGroupIds);
                                    this.deleteAppGroupIds(resourceId, uncommonAppGroupIds);
                                }
                            }
                        }
                        if (!inclAppGroupIdsToBeAppendedResList.isEmpty()) {
                            this.logger.log(Level.INFO, "As both present and previous commands have inclusion = true, checking for new AppGroupIds and adding in table CLEARDATAAPPSINFO");
                            this.addAppGroupIdsInfoInBulk(inclAppGroupIdsToBeAppendedResList, appGroupIdsToBeAppended);
                        }
                    }
                }
                else {
                    this.logger.log(Level.INFO, "No pending clear app data command");
                    this.addOrUpdateClearAppDataPolicyTableInBulk(resourceList, clearDataForAllApps, inclusion);
                    this.deleteAppGroupIdsInBulk(resourceList, Collections.emptyList());
                    this.addAppGroupIdsInfoInBulk(resourceList, appGroupIds);
                }
                MDMUtil.getPersistence().update(this.existingClearAppInfoDO);
                if (!this.existingClearAppPolicyDO.isEmpty()) {
                    MDMUtil.getPersistence().update(this.existingClearAppPolicyDO);
                }
                if (!this.finalDO.isEmpty()) {
                    MDMUtil.getPersistence().update(this.finalDO);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error occurred while addOrUpdateClearAppDataInfo", ex);
            throw ex;
        }
    }
    
    private void initExistingClearAppsDO(final List resourceList) {
        try {
            SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ClearAppDataPolicy"));
            sQuery.addSelectColumn(Column.getColumn("ClearAppDataPolicy", "*"));
            Criteria resApps = new Criteria(new Column("ClearAppDataPolicy", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sQuery.setCriteria(resApps);
            this.existingClearAppPolicyDO = MDMUtil.getPersistence().get(sQuery);
            sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ClearDataAppsInfo"));
            sQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "*"));
            resApps = new Criteria(new Column("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sQuery.setCriteria(resApps);
            this.existingClearAppInfoDO = MDMUtil.getPersistence().get(sQuery);
            this.finalDO = (DataObject)new WritableDataObject();
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- initExistingClearAppsDO() >   Error   ", (Throwable)e);
        }
    }
    
    private JSONObject getRecentClearAppDataCommand(final Long resourceId) throws Exception {
        final JSONObject recentClearAppCommand = new JSONObject();
        final Criteria resCriteria = new Criteria(Column.getColumn("ClearAppDataPolicy", "RESOURCE_ID"), (Object)resourceId, 0);
        final DataObject clearAppPolicyDO = MDMUtil.getPersistence().get("ClearAppDataPolicy", resCriteria);
        if (!clearAppPolicyDO.isEmpty()) {
            final Row row = clearAppPolicyDO.getFirstRow("ClearAppDataPolicy");
            final Boolean recentClearDataForAllApps = (Boolean)row.get("CLEAR_DATA_FOR_ALL_APPS");
            final Boolean recentIncludeListedApps = (Boolean)row.get("INCLUSION");
            recentClearAppCommand.put("CLEAR_DATA_FOR_ALL_APPS", (Object)recentClearDataForAllApps);
            recentClearAppCommand.put("INCLUSION", (Object)recentIncludeListedApps);
        }
        this.logger.log(Level.INFO, "Recent Clear app command info : {0}", recentClearAppCommand.toString());
        return recentClearAppCommand;
    }
    
    private JSONObject getRecentClearAppDataCommandInBulk(final List<Long> resourceIds) throws Exception {
        final JSONObject recentClearAppCommand = new JSONObject();
        final Criteria resCriteria = new Criteria(Column.getColumn("ClearAppDataPolicy", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
        final DataObject clearAppPolicyDO = MDMUtil.getPersistence().get("ClearAppDataPolicy", resCriteria);
        if (!clearAppPolicyDO.isEmpty()) {
            JSONObject clearAppCommand = null;
            final Iterator<Row> clearAppDataRows = clearAppPolicyDO.getRows("ClearAppDataPolicy");
            while (clearAppDataRows.hasNext()) {
                final Row clearAppDataRow = clearAppDataRows.next();
                clearAppCommand = new JSONObject();
                final Boolean recentClearDataForAllApps = (Boolean)clearAppDataRow.get("CLEAR_DATA_FOR_ALL_APPS");
                final Boolean recentIncludeListedApps = (Boolean)clearAppDataRow.get("INCLUSION");
                clearAppCommand.put("CLEAR_DATA_FOR_ALL_APPS", (Object)recentClearDataForAllApps);
                clearAppCommand.put("INCLUSION", (Object)recentIncludeListedApps);
                recentClearAppCommand.put(clearAppDataRow.get("RESOURCE_ID").toString(), (Object)clearAppCommand);
            }
        }
        this.logger.log(Level.INFO, "Recent Clear app command info : {0}", recentClearAppCommand.toString());
        return recentClearAppCommand;
    }
    
    public List<Long> getAppGroupIdsForResourceId(final Long resourceId) {
        final List<Long> appGroupIds = new ArrayList<Long>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ClearDataAppsInfo"));
            final Criteria resCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(resCriteria);
            selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "CLEAR_APPS_INFO_ID"));
            final DataObject appGroupIdsDO = DataAccess.get(selectQuery);
            if (!appGroupIdsDO.isEmpty()) {
                final Iterator<Row> rows = appGroupIdsDO.getRows("ClearDataAppsInfo");
                while (rows.hasNext()) {
                    appGroupIds.add(Long.valueOf(String.valueOf(rows.next().get("APP_GROUP_ID"))));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while getAppGroupIdsForResourceId()", ex);
        }
        return appGroupIds;
    }
    
    private Map<Long, List<Long>> getAppGroupIdsForResourceIdInBulk(final List<Long> resourceIds) {
        final Map<Long, List<Long>> appResGroupMap = new HashMap<Long, List<Long>>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ClearDataAppsInfo"));
            final Criteria resCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            selectQuery.setCriteria(resCriteria);
            selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "CLEAR_APPS_INFO_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"));
            final DataObject appGroupIdsDO = MDMUtil.getPersistence().get(selectQuery);
            if (!appGroupIdsDO.isEmpty()) {
                final Iterator<Row> clearAppDataRows = appGroupIdsDO.getRows("ClearDataAppsInfo");
                while (clearAppDataRows.hasNext()) {
                    final Row clearAppDataRow = clearAppDataRows.next();
                    final Long resID = Long.valueOf(String.valueOf(clearAppDataRow.get("RESOURCE_ID")));
                    final Long appGroupId = Long.valueOf(String.valueOf(clearAppDataRow.get("APP_GROUP_ID")));
                    appResGroupMap.putIfAbsent(resID, new ArrayList<Long>());
                    appResGroupMap.get(resID).add(appGroupId);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while getAppGroupIdsForResourceIdInBulk()", ex);
        }
        return appResGroupMap;
    }
    
    private void addOrUpdateClearAppDataPolicyTableInBulk(final List<Long> resourceIds, final Boolean clearDataForAllApps, final Boolean inclusion) {
        try {
            for (final Long resourceId : resourceIds) {
                this.addOrUpdateClearAppDataPolicy(resourceId, clearDataForAllApps, inclusion);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while addOrUpdateClearAppDataPolicyTableInBulk", ex);
        }
    }
    
    public void addOrUpdateClearAppDataPolicy(final Long resourceId, final boolean clearDataForAllApps, final boolean inclusion) throws DataAccessException {
        try {
            if (resourceId != null) {
                final Criteria cResource = new Criteria(new Column("ClearAppDataPolicy", "RESOURCE_ID"), (Object)resourceId, 0);
                Row clearAppDataRow = null;
                clearAppDataRow = this.existingClearAppPolicyDO.getRow("ClearAppDataPolicy", cResource);
                if (clearAppDataRow == null) {
                    clearAppDataRow = new Row("ClearAppDataPolicy");
                    clearAppDataRow.set("RESOURCE_ID", (Object)resourceId);
                    clearAppDataRow.set("CLEAR_DATA_FOR_ALL_APPS", (Object)clearDataForAllApps);
                    clearAppDataRow.set("INCLUSION", (Object)inclusion);
                    this.finalDO.addRow(clearAppDataRow);
                }
                else {
                    clearAppDataRow.set("CLEAR_DATA_FOR_ALL_APPS", (Object)clearDataForAllApps);
                    clearAppDataRow.set("INCLUSION", (Object)inclusion);
                    this.existingClearAppPolicyDO.updateRow(clearAppDataRow);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, " -- addOrUpdateClearAppDataPolicy()    >   Error   ", (Throwable)e);
            throw e;
        }
    }
    
    public void deleteAppGroupIdsInBulk(final List<Long> resourceIds, final List<Long> appGroupIds) {
        try {
            this.logger.log(Level.INFO, "Going to deleteAppGroupIds from table CLEARDATAAPPSINFO");
            if (resourceIds != null && !resourceIds.isEmpty()) {
                Criteria deleteCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
                if (appGroupIds != null && !appGroupIds.isEmpty()) {
                    final Criteria appGroupIdsCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
                    deleteCriteria = appGroupIdsCriteria.and(deleteCriteria);
                }
                this.existingClearAppInfoDO.deleteRows("ClearDataAppsInfo", deleteCriteria);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in deleteAppGroupIdsInBulk", ex);
        }
    }
    
    private void deleteAppGroupIds(final Long resourceId, final List<Long> appGroupIds) {
        try {
            this.logger.log(Level.INFO, "Going to deleteAppGroupIds from table CLEARDATAAPPSINFO");
            if (resourceId != null) {
                Criteria deleteCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceId, 0);
                if (appGroupIds != null && !appGroupIds.isEmpty()) {
                    final Criteria appGroupIdsCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "APP_GROUP_ID"), (Object)appGroupIds.toArray(), 8);
                    deleteCriteria = appGroupIdsCriteria.and(deleteCriteria);
                }
                this.existingClearAppInfoDO.deleteRows("ClearDataAppsInfo", deleteCriteria);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in deleteAppGroupIds", ex);
        }
    }
    
    private void addAppGroupIdsInfoInBulk(final List<Long> resourceIds, final List<Long> appGroupIds) {
        try {
            if (appGroupIds != null && !appGroupIds.isEmpty()) {
                this.logger.log(Level.INFO, "Going to addAppGroupIds in table CLEARDATAAPPSINFO: {0}", appGroupIds);
                for (final Long resourceID : resourceIds) {
                    for (final Long appGroupId : appGroupIds) {
                        final Row appGroupIdRow = new Row("ClearDataAppsInfo");
                        appGroupIdRow.set("RESOURCE_ID", (Object)resourceID);
                        appGroupIdRow.set("APP_GROUP_ID", (Object)appGroupId);
                        this.finalDO.addRow(appGroupIdRow);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addAppGroupIdsInfoInBulk", ex);
        }
    }
    
    public JSONObject getClearAppCommandRequestData(final Long resourceId) throws Exception {
        final JSONObject recentClearAppDataCommand = this.getRecentClearAppDataCommand(resourceId);
        final JSONObject clearAppCommandRequestData = new JSONObject();
        final Boolean clearDataForAllApps = recentClearAppDataCommand.optBoolean("CLEAR_DATA_FOR_ALL_APPS");
        clearAppCommandRequestData.put("ClearDataForAllApps", (Object)BooleanUtils.toIntegerObject(BooleanUtils.negate(clearDataForAllApps)));
        if (!clearDataForAllApps) {
            final Boolean includeListedApps = recentClearAppDataCommand.optBoolean("INCLUSION");
            clearAppCommandRequestData.put("inclusion", ((boolean)includeListedApps) ? 1 : 2);
            clearAppCommandRequestData.put("ClearDataForPackages", (Object)this.getAppIdentifiers(resourceId));
        }
        return clearAppCommandRequestData;
    }
    
    private JSONArray getAppIdentifiers(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ClearDataAppsInfo"));
        selectQuery.addJoin(new Join("ClearDataAppsInfo", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(resCriteria);
        selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "CLEAR_APPS_INFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ClearDataAppsInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        final DataObject appIdentifiersDO = DataAccess.get(selectQuery);
        final JSONArray identifiers = new JSONArray();
        if (!appIdentifiersDO.isEmpty()) {
            final Iterator<Row> rows = appIdentifiersDO.getRows("MdAppGroupDetails");
            while (rows.hasNext()) {
                identifiers.put((Object)String.valueOf(rows.next().get("IDENTIFIER")));
            }
        }
        return identifiers;
    }
    
    public void processClearAppDataCommandResponse(final Long resourceID, final Long commandId, final HashMap agentResponse, final String deviceCommand, final Long customerId) throws Exception {
        final String cmdStatus = agentResponse.get("Status");
        String remarks = null;
        Integer errorCode = null;
        String actionLogRemarks = null;
        int commandStatus;
        if (cmdStatus.equalsIgnoreCase("Acknowledged")) {
            remarks = I18N.getMsg("dc.mdm.general.command.succeeded", new Object[0]);
            actionLogRemarks = "dc.mdm.actionlog.securitycommands.success";
            commandStatus = 2;
        }
        else {
            commandStatus = 0;
            errorCode = agentResponse.getOrDefault("ErrorCode", -1);
            remarks = I18N.getMsg("dc.mdm.safe.blwl.remarks.invalid_command", new Object[0]);
            actionLogRemarks = "dc.mdm.actionlog.securitycommands.failure";
        }
        final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
        if (errorCode != null && errorCode != -1) {
            commandStatusJSON.put("ERROR_CODE", (Object)errorCode);
        }
        commandStatusJSON.put("COMMAND_STATUS", commandStatus);
        commandStatusJSON.put("REMARKS", (Object)remarks);
        final Long userId = commandStatusJSON.optLong("ADDED_BY");
        final String commandDisplayName = CommandUtil.getInstance().getCommandDisplayName("ClearAppData");
        final Object remarksArgs = commandDisplayName + "@@@" + ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, resourceID, DMUserHandler.getUserNameFromUserID(userId), actionLogRemarks, remarksArgs, customerId);
        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
        DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand, resourceID);
    }
    
    public List getGroupActionClearedApps(final Long groupActionId) {
        final List appsList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionToResetApps"));
            selectQuery.addJoin(new Join("GroupActionToResetApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("GroupActionToResetApps", "GROUP_ACTION_ID"), (Object)groupActionId, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            final DataObject appIdentifiersDO = MDMUtil.getPersistence().get(selectQuery);
            if (!appIdentifiersDO.isEmpty()) {
                final Iterator<Row> rows = appIdentifiersDO.getRows("MdAppGroupDetails");
                while (rows.hasNext()) {
                    appsList.add(String.valueOf(rows.next().get("GROUP_DISPLAY_NAME")));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in getGroupActionClearedApps", ex);
        }
        return appsList;
    }
    
    public List getDeviceActionClearedApps(final Long deviceActionId) {
        final List appsList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceActionToResetApps"));
            selectQuery.addJoin(new Join("DeviceActionToResetApps", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceActionToResetApps", "DEVICE_ACTION_ID"), (Object)deviceActionId, 0));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            final DataObject appIdentifiersDO = MDMUtil.getPersistence().get(selectQuery);
            if (!appIdentifiersDO.isEmpty()) {
                final Iterator<Row> rows = appIdentifiersDO.getRows("MdAppGroupDetails");
                while (rows.hasNext()) {
                    appsList.add(String.valueOf(rows.next().get("GROUP_DISPLAY_NAME")));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in getDeviceActionClearedApps", ex);
        }
        return appsList;
    }
    
    public List fetchDeviceClearAppDataSuggestions(final Long device_id) {
        List latestAppsList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceActionHistory"));
            selectQuery.addJoin(new Join("DeviceActionHistory", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)device_id, 0));
            selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
            selectQuery.addSelectColumn(Column.getColumn("CommandHistory", "ADDED_TIME"));
            final SortColumn sortColumn = new SortColumn("CommandHistory", "ADDED_TIME", (boolean)Boolean.FALSE);
            selectQuery.addSortColumn(sortColumn);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSetWrapper.next()) {
                final Long recent_dev_action_id = (Long)dataSetWrapper.getValue("DEVICE_ACTION_ID");
                latestAppsList = this.getDeviceActionClearedApps(recent_dev_action_id);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in fetchDeviceClearAppDataSuggestions", ex);
        }
        return latestAppsList;
    }
    
    public List fetchGroupClearAppDataSuggestions(final Long group_id) {
        List latestAppsList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionHistory"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ID"), (Object)group_id, 0));
            selectQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "INITIATED_TIME"));
            final SortColumn sortColumn = new SortColumn("GroupActionHistory", "INITIATED_TIME", (boolean)Boolean.FALSE);
            selectQuery.addSortColumn(sortColumn);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSetWrapper.next()) {
                final Long recent_grp_action_id = (Long)dataSetWrapper.getValue("GROUP_ACTION_ID");
                latestAppsList = this.getGroupActionClearedApps(recent_grp_action_id);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in fetchGroupClearAppDataSuggestions", ex);
        }
        return latestAppsList;
    }
    
    static {
        ClearAppDataHandler.clearAppDataHandler = null;
        CLEAR_ALL_APPS_ALLOWED = Boolean.TRUE;
        CLEAR_ALL_APPS_NOT_ALLOWED = Boolean.FALSE;
        INCLUDE_ONLY_LISTED_APPS = Boolean.TRUE;
        EXCLUDE_LISTED_APPS = Boolean.FALSE;
    }
}
