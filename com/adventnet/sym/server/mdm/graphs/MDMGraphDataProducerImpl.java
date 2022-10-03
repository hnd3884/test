package com.adventnet.sym.server.mdm.graphs;

import com.adventnet.sym.server.mdm.util.InactiveDevicePolicyConstants;
import com.me.mdm.server.util.CalendarUtil;
import java.util.Arrays;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.me.mdm.server.announcement.handler.AnnouncementDBHandler;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Map;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.graphs.data.GraphDataProducer;

public class MDMGraphDataProducerImpl extends GraphDataProducer
{
    public HashMap<String, Long> getGraphValues(final String graphName, final HashMap parameterMap) throws Exception {
        CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        LinkedHashMap dataHashMap = new LinkedHashMap();
        try {
            if (graphName.equalsIgnoreCase("mdmdevicescan")) {
                dataHashMap = this.getMDMDeviceScanStatus();
            }
            else if (graphName.equalsIgnoreCase("mdmdevices")) {
                dataHashMap = this.getMDMDeviceTypeGraphData();
            }
            else if (graphName.equalsIgnoreCase("mdmplatform")) {
                dataHashMap = this.getMDMDevicePlatformGraphData();
            }
            else if (graphName.equalsIgnoreCase("mdmenroll")) {
                dataHashMap = this.getMDMDeviceEnrollStatus();
            }
            else if (graphName.equalsIgnoreCase("mdmprofilestatus")) {
                final Boolean isAPI = parameterMap.getOrDefault("isAPI", Boolean.FALSE);
                if (!isAPI) {
                    final String filterParams = parameterMap.get("filterParams");
                    final String[] filterParamSplit = filterParams.split("_");
                    if (filterParamSplit[0] != null && filterParamSplit[1] != null) {
                        final Long groupId = Long.valueOf(filterParamSplit[0]);
                        final Long collectionId = Long.valueOf(filterParamSplit[1]);
                        parameterMap.put("groupId", groupId);
                        parameterMap.put("collectionId", collectionId);
                    }
                }
                dataHashMap = this.getMDMGroupProfileStatus(new JSONObject((Map)parameterMap));
            }
            else if (graphName.equalsIgnoreCase("mdmprofileexecstatus")) {
                final Boolean isAPI = parameterMap.getOrDefault("isAPI", Boolean.FALSE);
                if (!isAPI) {
                    final String filterParams = parameterMap.get("filterParams");
                    final String[] filterParamSplit = filterParams.split("_");
                    if (filterParamSplit[0] != null) {
                        final Long profileId = Long.valueOf(filterParamSplit[0]);
                        parameterMap.put("profileId", profileId);
                    }
                }
                dataHashMap = this.getMDMProfileStatus(new JSONObject((Map)parameterMap));
            }
            else if (graphName.equalsIgnoreCase("mdmBYODSummary")) {
                dataHashMap = MDMUtil.getInstance().getMDMByodStatus();
            }
            else if (graphName.equalsIgnoreCase("mdmDeviceMemoryUsage")) {
                final String sResourceID = parameterMap.get("resourceID");
                if (sResourceID != null) {
                    final Long resourceID = Long.valueOf(sResourceID);
                    dataHashMap = this.getDeviceMemoryUsage(resourceID);
                }
            }
            else if (graphName.equalsIgnoreCase("mdmappsummary")) {
                dataHashMap = this.getMDMAppStatusSummary();
            }
            else if (graphName.equalsIgnoreCase("mdmLastSeenBreakdownCount")) {
                dataHashMap = this.getInactiveDeviceCountsInRanges(parameterMap);
            }
            else if (graphName.equalsIgnoreCase("announcement_execution_summary")) {
                dataHashMap = this.getMDMAnnouncementStatus(parameterMap);
            }
            else if (graphName.equalsIgnoreCase("mdmGroupActionSummary")) {
                dataHashMap = this.getMDMGroupActionStatus(parameterMap);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return dataHashMap;
    }
    
    private LinkedHashMap getMDMGroupProfileStatus(final JSONObject filterParams) throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final Long groupId = filterParams.getLong("groupId");
        final Long collectionId = filterParams.optLong("collectionId");
        final Long profileId = filterParams.optLong("profileId");
        if (groupId != null && (collectionId != 0L || profileId != 0L)) {
            final ProfileHandler profileHandler = new ProfileHandler();
            Long recentlyAppliedCollectionId = null;
            if (profileId != 0L) {
                recentlyAppliedCollectionId = profileHandler.getRecentlyAppliedCollectionIdForGroup(groupId, profileId);
            }
            else {
                recentlyAppliedCollectionId = profileHandler.getRecentlyAppliedCollectionIdForGroup(groupId, profileHandler.getProfileInfoFromCollectionID(collectionId).get("PROFILE_ID"));
            }
            final Criteria groupCri = new Criteria(Column.getColumn("GroupCollnStatusSummary", "GROUP_ID"), (Object)groupId, 0);
            final Criteria collnCri = new Criteria(Column.getColumn("GroupCollnStatusSummary", "COLLECTION_ID"), (Object)recentlyAppliedCollectionId, 0);
            final Criteria cri = groupCri.and(collnCri);
            long yetToApplyCount = 0L;
            long successCount = 0L;
            long failedCount = 0L;
            long notApplicableCount = 0L;
            long notificationCount = 0L;
            long totalCount = 0L;
            final DataObject dObj = DataAccess.get("GroupCollnStatusSummary", cri);
            if (dObj.isEmpty()) {
                return null;
            }
            final Row summaryRow = dObj.getFirstRow("GroupCollnStatusSummary");
            yetToApplyCount = (int)summaryRow.get("YET_TO_APPLY_COUNT");
            successCount = (int)summaryRow.get("SUCCESS_COUNT");
            failedCount = (int)summaryRow.get("FAILED_COUNT");
            notApplicableCount = (int)summaryRow.get("NOT_APPLICABLE_COUNT");
            notificationCount = (int)summaryRow.get("NOTIFICATION_SENT_COUNT");
            totalCount = (int)summaryRow.get("TOTAL_TARGET_COUNT");
            hashMap.put("yet-to-apply", yetToApplyCount);
            hashMap.put("success", successCount);
            hashMap.put("failed", failedCount);
            hashMap.put("not-applicable", notApplicableCount);
            hashMap.put("notification-sent", notificationCount);
            hashMap.put("in-progress", totalCount - (yetToApplyCount + successCount + failedCount + notApplicableCount + notificationCount));
        }
        return hashMap;
    }
    
    private SelectQuery getBulkActionsGroupStatusQuery(final Long groupId, final Long groupActionId) {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionToCommand"));
        query.addJoin(new Join("GroupActionToCommand", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
        query.addJoin(new Join("CommandHistory", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
        query.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria groupIdCri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
        final Criteria actionIdCri = new Criteria(Column.getColumn("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)groupActionId, 0);
        final Criteria managedDeviceStatusCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        query.setCriteria(groupIdCri.and(managedDeviceStatusCri).and(actionIdCri));
        Column col = Column.getColumn("CommandHistory", "RESOURCE_ID");
        col = col.distinct().count();
        col.setColumnAlias("count");
        query.addSelectColumn(col);
        query.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_STATUS"));
        query.addGroupByColumn(Column.getColumn("CommandHistory", "COMMAND_STATUS"));
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        return query;
    }
    
    private SelectQuery getProfileStatusQuery(final Long profileId) {
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
        query.addJoin(new Join("CollnToResources", "RecentProfileForResource", new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0), 2));
        query.addJoin(new Join("CollnToResources", "ConfigStatusDefn", new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)Column.getColumn("ConfigStatusDefn", "STATUS_ID"), 0), 2));
        query.addJoin(new Join("CollnToResources", "ManagedDevice", new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0), 2));
        Column col = Column.getColumn("CollnToResources", "RESOURCE_ID");
        col = col.distinct().count();
        col.setColumnAlias("count");
        query.addSelectColumn(col);
        query.addSelectColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        Criteria prof = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
        final Criteria coll = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), 0);
        final Criteria unmanaged = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        prof = prof.and(unmanaged);
        prof = prof.and(coll);
        query.setCriteria(prof);
        query.addGroupByColumn(Column.getColumn("ConfigStatusDefn", "STATUS_ID"));
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        return query;
    }
    
    public LinkedHashMap getMDMProfileStatus(final JSONObject filterParams) throws Exception {
        LinkedHashMap hashMap = new LinkedHashMap();
        final Long profileId = filterParams.optLong("profileId");
        if (profileId != null && profileId != 0L) {
            final SelectQuery query = this.getProfileStatusQuery(profileId);
            hashMap = GroupCollectionStatusSummary.getInstance().getStatusMapFromQuery(query);
        }
        return hashMap;
    }
    
    public LinkedHashMap getMDMGroupActionStatus(final HashMap filterParams) throws Exception {
        LinkedHashMap hashMap = new LinkedHashMap();
        final Long groupId = filterParams.get("groupId");
        final int actionType = filterParams.get("actionType");
        final Long groupActionId = filterParams.get("groupActionId");
        if (groupId != null && groupId != 0L) {
            final SelectQuery query = this.getBulkActionsGroupStatusQuery(groupId, groupActionId);
            hashMap = GroupCollectionStatusSummary.getInstance().getActionStatusMapFromQuery(query, actionType, groupId);
        }
        return hashMap;
    }
    
    public LinkedHashMap getMDMAnnouncementStatus(final HashMap filterParams) throws Exception {
        LinkedHashMap hashMap = new LinkedHashMap();
        final Long announcementId = filterParams.get("announcementId");
        if (announcementId != null) {
            final AnnouncementDBHandler announcementHandler = AnnouncementDBHandler.newInstance();
            final JSONObject profileJson = announcementHandler.getCollectionIdForAnnouncement(announcementId);
            final SelectQuery query = this.getProfileStatusQuery((Long)profileJson.get("PROFILE_ID"));
            final Criteria notDeleted = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            query.setCriteria(query.getCriteria().and(notDeleted));
            hashMap = GroupCollectionStatusSummary.getInstance().getAnnouncementStatusMapFromQuery(query);
        }
        return hashMap;
    }
    
    private LinkedHashMap getMDMDeviceScanStatus() throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.put("scan-success", 0L);
        hashMap.put("scan-failure", 0L);
        hashMap.put("scan-initiated", 0L);
        hashMap.put("scan-inprogress", 0L);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceScanStatus"));
        query.addJoin(new Join("MdDeviceScanStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        query.setCriteria(criteria);
        final Column scanStatusColumn = new Column("MdDeviceScanStatus", "SCAN_STATUS");
        query.addSelectColumn(scanStatusColumn);
        Column countCol = new Column("Resource", "RESOURCE_ID");
        countCol = countCol.distinct();
        countCol = countCol.count();
        query.addSelectColumn(countCol);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(scanStatusColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        query.setGroupByClause(grpByCls);
        final HashMap graphData = this.executeCountQuery(query);
        for (final Map.Entry pairs : graphData.entrySet()) {
            final int scanStatus = pairs.getKey();
            final long scanCount = pairs.getValue();
            if (scanStatus == 0) {
                hashMap.put("scan-failure", scanCount);
            }
            else if (scanStatus == 2) {
                hashMap.put("scan-success", scanCount);
            }
            else if (scanStatus == 1) {
                hashMap.put("scan-initiated", scanCount);
            }
            else {
                if (scanStatus != 4) {
                    continue;
                }
                hashMap.put("scan-inprogress", scanCount);
            }
        }
        return hashMap;
    }
    
    private LinkedHashMap getMDMDeviceEnrollStatus() throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.put("enroll-pending", 0L);
        hashMap.put("enroll-success", 0L);
        hashMap.put("profile-removed", 0L);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        query.setCriteria(userNotInTrashCriteria);
        final Column managedStatusColumn = new Column("ManagedDevice", "MANAGED_STATUS");
        query.addSelectColumn(managedStatusColumn);
        Column countCol = new Column("Resource", "RESOURCE_ID");
        countCol = countCol.distinct();
        countCol = countCol.count();
        query.addSelectColumn(countCol);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(managedStatusColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        query.setGroupByClause(grpByCls);
        final HashMap graphData = this.executeCountQuery(query);
        for (final Map.Entry pairs : graphData.entrySet()) {
            final int enrollStatus = pairs.getKey();
            final long enrollCount = pairs.getValue();
            if (enrollStatus == 1) {
                hashMap.put("enroll-pending", enrollCount);
            }
            else if (enrollStatus == 2) {
                hashMap.put("enroll-success", enrollCount);
            }
            else {
                if (enrollStatus != 4) {
                    continue;
                }
                hashMap.put("profile-removed", enrollCount);
            }
        }
        return hashMap;
    }
    
    private LinkedHashMap getMDMDeviceOSGraphData() throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        long ios4Count = 0L;
        long ios5Count = 0L;
        long ios6Count = 0L;
        long othersCount = 0L;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        query.addJoin(new Join("MdDeviceInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        query.setCriteria(criteria);
        final Column osVersionColumn = new Column("MdDeviceInfo", "OS_VERSION");
        query.addSelectColumn(osVersionColumn);
        Column countCol = new Column("Resource", "RESOURCE_ID");
        countCol = countCol.distinct();
        countCol = countCol.count();
        query.addSelectColumn(countCol);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(osVersionColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        query.setGroupByClause(grpByCls);
        final HashMap graphData = this.executeCountQuery(query);
        for (final Map.Entry pairs : graphData.entrySet()) {
            final String osCategory = pairs.getKey();
            final long osCount = pairs.getValue();
            if (osCategory.matches("4.*")) {
                ios4Count += osCount;
            }
            else if (osCategory.matches("5.*")) {
                ios5Count += osCount;
            }
            else if (osCategory.matches("6.*")) {
                ios6Count += osCount;
            }
            else {
                othersCount += osCount;
            }
        }
        hashMap.put("ios4", ios4Count);
        hashMap.put("ios5", ios5Count);
        hashMap.put("ios6", ios6Count);
        hashMap.put("others", othersCount);
        return hashMap;
    }
    
    private LinkedHashMap getMDMDeviceTypeGraphData() throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        long smartPhonesCount = 0L;
        long tabletsCount = 0L;
        long laptopsCount = 0L;
        long desktopsCount = 0L;
        long tvCount = 0L;
        long othersCount = 0L;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
        query.addJoin(new Join("MdDeviceInfo", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        query.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(2), 0);
        query.setCriteria(criteria);
        final Column modelNameColumn = new Column("MdModelInfo", "MODEL_TYPE");
        query.addSelectColumn(modelNameColumn);
        Column countCol = new Column("Resource", "RESOURCE_ID");
        countCol = countCol.distinct();
        countCol = countCol.count();
        query.addSelectColumn(countCol);
        final List groupByColumns = new ArrayList();
        groupByColumns.add(modelNameColumn);
        final GroupByClause grpByCls = new GroupByClause(groupByColumns);
        query.setGroupByClause(grpByCls);
        final HashMap graphData = this.executeCountQuery(query);
        for (final Map.Entry pairs : graphData.entrySet()) {
            final int modeltype = pairs.getKey();
            final long modelCount = pairs.getValue();
            if (modeltype == 1) {
                smartPhonesCount += modelCount;
            }
            else if (modeltype == 2) {
                tabletsCount += modelCount;
            }
            else if (modeltype == 3) {
                laptopsCount += modelCount;
            }
            else if (modeltype == 4) {
                desktopsCount += modelCount;
            }
            else if (modeltype == 5) {
                tvCount += modelCount;
            }
            else {
                if (modeltype != 0) {
                    continue;
                }
                othersCount += modelCount;
            }
        }
        hashMap.put("smartphones", smartPhonesCount);
        hashMap.put("tablets", tabletsCount);
        hashMap.put("laptops", laptopsCount);
        hashMap.put("desktops", desktopsCount);
        hashMap.put("tv", tvCount);
        hashMap.put("others", othersCount);
        return hashMap;
    }
    
    private HashMap executeCountQuery(SelectQuery query) {
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        final HashMap graphData = new HashMap();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                Object key = ds.getValue(1);
                Object value = ds.getValue(2);
                if (value != null) {
                    if (key == null) {
                        key = new Integer(1);
                    }
                    if (graphData.get(key) != null) {
                        value = (int)value + graphData.get(key);
                    }
                    graphData.put(key, value);
                }
            }
            ds.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        return graphData;
    }
    
    private LinkedHashMap getDeviceMemoryUsage(final Long resourceID) {
        final LinkedHashMap hashMap = new LinkedHashMap();
        hashMap.put("used_space", 0);
        hashMap.put("free_space", 0);
        try {
            final HashMap diskHash = InventoryUtil.getInstance().getDiskInfo(resourceID);
            hashMap.put("used_space", diskHash.get("usedSpace"));
            hashMap.put("free_space", diskHash.get("freeSpace"));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return hashMap;
    }
    
    private LinkedHashMap getMDMDevicePlatformGraphData() {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final MDMTrackerUtil distictCountUtil = new MDMTrackerUtil();
        final String GRAPH_COLUMN_IOS = "iOS";
        final String GRAPH_COLUMN_ANDROID = "Android";
        final String GRAPH_COLUMN_WINDOWS = "Windows";
        final String GRAPH_COLUMN_APPLE_MAC = "ApplemacOS";
        final String GRAPH_COLUMN_CHROMEBOOK = "Chromebook";
        final String GRAPH_COLUMN_APPLE_TV = "AppletvOS";
        final String[] array;
        final String[] graphColumns = array = new String[] { "iOS", "Android", "Windows", "ApplemacOS", "Chromebook", "AppletvOS" };
        for (final String columnName : array) {
            hashMap.put(columnName, 0L);
        }
        SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final Criteria iOSPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria androidPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria windowsPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria chromePlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)4, 0);
        final Criteria smartPhoneCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)1, 0);
        final Criteria tabletCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)2, 0);
        final Criteria laptopCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)3, 0);
        final Criteria desktopCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)4, 0);
        final Criteria tvCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
        final Criteria otherCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)0, 0);
        final Criteria managed = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final CaseExpression iosColumnExpression = new CaseExpression("iOS");
        iosColumnExpression.addWhen(managed.and(iOSPlatform.and(smartPhoneCriteria.or(tabletCriteria).or(otherCriteria))), (Object)new Column("Resource", "RESOURCE_ID"));
        final CaseExpression androidColumnExpression = new CaseExpression("Android");
        androidColumnExpression.addWhen(managed.and(androidPlatform), (Object)new Column("Resource", "RESOURCE_ID"));
        final CaseExpression windowsColumnExpression = new CaseExpression("Windows");
        windowsColumnExpression.addWhen(managed.and(windowsPlatform), (Object)new Column("Resource", "RESOURCE_ID"));
        final CaseExpression chromeColumnExpression = new CaseExpression("Chromebook");
        chromeColumnExpression.addWhen(managed.and(chromePlatform), (Object)new Column("Resource", "RESOURCE_ID"));
        final CaseExpression applemacColumnExpression = new CaseExpression("ApplemacOS");
        applemacColumnExpression.addWhen(managed.and(iOSPlatform.and(laptopCriteria.or(desktopCriteria))), (Object)new Column("Resource", "RESOURCE_ID"));
        final CaseExpression appletvColumnExpression = new CaseExpression("AppletvOS");
        appletvColumnExpression.addWhen(managed.and(iOSPlatform.and(tvCriteria)), (Object)new Column("Resource", "RESOURCE_ID"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(iosColumnExpression, -5, "iOS"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(androidColumnExpression, -5, "Android"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(windowsColumnExpression, -5, "Windows"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(applemacColumnExpression, -5, "ApplemacOS"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(chromeColumnExpression, -5, "Chromebook"));
        query.addSelectColumn(distictCountUtil.getDistinctCountCaseExpressionColumn(appletvColumnExpression, -5, "AppletvOS"));
        query = RBDAUtil.getInstance().getRBDAQuery(query);
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            if (ds.next()) {
                for (final String columnName2 : graphColumns) {
                    hashMap.put(columnName2, ds.getValue(columnName2));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error executing query in getMDMDevicePlatformGraphData()  ", ex);
        }
        return hashMap;
    }
    
    private LinkedHashMap getMDMAppStatusSummary() throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final BlacklistAppHandler blacklistHandler = new BlacklistAppHandler();
        final HashMap appDetails = blacklistHandler.appSummaryDetails(customerId, Arrays.asList("discoveredCount", "blacklistCount"));
        final Long whitelist = Long.parseLong(String.valueOf(appDetails.get("discoveredCount") - appDetails.get("blacklistCount")));
        final Long blacklist = Long.parseLong(String.valueOf(appDetails.get("blacklistCount")));
        hashMap.put("whiteList", whitelist);
        hashMap.put("blacklist", blacklist);
        return hashMap;
    }
    
    private LinkedHashMap getInactiveDeviceCounts(final HashMap parameterMap) throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        final Long customerID = (parameterMap.get("customerID") == null || parameterMap.get("customerID").equals("all")) ? CustomerInfoUtil.getInstance().getCustomerId() : Long.parseLong(parameterMap.get("customerID").toString());
        final Long oneDay = 86400000L;
        try {
            final long presentDayStartinMillis = CalendarUtil.getInstance().getStartTimeOfTheDay(currentTime).getTime();
            final long presentDayEndinMillis = CalendarUtil.getInstance().getEndTimeOfTheDay(currentTime).getTime();
            final Long startRange = (parameterMap.get("startRange") != null && parameterMap.get("startRange") != -1L) ? parameterMap.get("startRange") : (presentDayStartinMillis - InactiveDevicePolicyConstants.GRAPH_START_RANGE_DEFAULT);
            final Long endRange = (parameterMap.get("endRange") != null && parameterMap.get("endRange") != -1L) ? parameterMap.get("endRange") : (presentDayEndinMillis - oneDay);
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            squery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_TYPE"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            squery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
            squery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
            squery.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria enrollSuccessCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Long startIter = startRange;
            Long currentEnd = startIter + oneDay;
            final int defaultCount = 0;
            while (currentEnd <= endRange) {
                final Criteria rangeCriteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { startIter, currentEnd }, 14);
                squery.setCriteria(custCri.and(enrollSuccessCriteria).and(rangeCriteria));
                final DataObject inactiveDO = MDMUtil.getPersistence().get(squery);
                int dObjSize = inactiveDO.size("Resource");
                if (hashMap.get(startIter) != null) {
                    final int existingCount = hashMap.get(startIter);
                    dObjSize += existingCount;
                }
                if (dObjSize == -1) {
                    dObjSize = defaultCount;
                }
                hashMap.put(String.valueOf(startIter), dObjSize);
                startIter = currentEnd;
                currentEnd += oneDay;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getInactiveDeviceCounts :{0}", ex);
        }
        return hashMap;
    }
    
    private LinkedHashMap getInactiveDeviceCountsInRanges(final HashMap parameterMap) {
        final LinkedHashMap lastContactHashMap = new LinkedHashMap();
        DMDataSetWrapper ds = null;
        final Long currentTime = MDMUtil.getCurrentTimeInMillis();
        final Long oneDay = 86400000L;
        final String inactive0to3 = "inactive0to3";
        final String inactive4to7 = "inactive4to7";
        final String inactive8to15 = "inactive8to15";
        final String inactive16to30 = "inactive16to30";
        final String inactiveAbove30 = "inactiveAbove30";
        final String[] array;
        final String[] graphColumns = array = new String[] { "inactive0to3", "inactive4to7", "inactive8to15", "inactive16to30", "inactiveAbove30" };
        for (final String columnName : array) {
            lastContactHashMap.put(columnName, 0L);
        }
        final Long customerID = (parameterMap.get("customerID") == null || parameterMap.get("customerID").equals("all")) ? CustomerInfoUtil.getInstance().getCustomerId() : Long.parseLong(parameterMap.get("customerID").toString());
        final long presentDayStartinMillis = CalendarUtil.getInstance().getStartTimeOfTheDay(currentTime).getTime();
        final long presentDayEndinMillis = CalendarUtil.getInstance().getEndTimeOfTheDay(currentTime).getTime();
        try {
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            squery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria custCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria enrollSuccessCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria range0to3Criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { presentDayStartinMillis - 3L * oneDay, presentDayEndinMillis }, 14);
            final Criteria range4to7Criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { presentDayStartinMillis - 7L * oneDay, presentDayStartinMillis - 3L * oneDay - 1L }, 14);
            final Criteria range8to15Criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { presentDayStartinMillis - 15L * oneDay, presentDayStartinMillis - 7L * oneDay - 1L }, 14);
            final Criteria range16to30Criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { presentDayStartinMillis - 30L * oneDay, presentDayStartinMillis - 15L * oneDay - 1L }, 14);
            final Criteria rangeAbove30Criteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)(presentDayStartinMillis - 30L * oneDay), 7);
            squery.setCriteria(custCri.and(enrollSuccessCriteria));
            final Column managedDeviceColumn = new Column("ManagedDevice", "RESOURCE_ID");
            final CaseExpression inactive0to3Expression = new CaseExpression("inactive0to3");
            inactive0to3Expression.addWhen(range0to3Criteria, (Object)managedDeviceColumn);
            final CaseExpression inactive4to7Expression = new CaseExpression("inactive4to7");
            inactive4to7Expression.addWhen(range4to7Criteria, (Object)managedDeviceColumn);
            final CaseExpression inactive8to15Expression = new CaseExpression("inactive8to15");
            inactive8to15Expression.addWhen(range8to15Criteria, (Object)managedDeviceColumn);
            final CaseExpression inactive16to30Expression = new CaseExpression("inactive16to30");
            inactive16to30Expression.addWhen(range16to30Criteria, (Object)managedDeviceColumn);
            final CaseExpression inactiveAbove30Expression = new CaseExpression("inactiveAbove30");
            inactiveAbove30Expression.addWhen(rangeAbove30Criteria, (Object)managedDeviceColumn);
            squery.addSelectColumn(MDMUtil.getInstance().getCountCaseExpressionColumn(inactive0to3Expression, -5, "inactive0to3"));
            squery.addSelectColumn(MDMUtil.getInstance().getCountCaseExpressionColumn(inactive4to7Expression, -5, "inactive4to7"));
            squery.addSelectColumn(MDMUtil.getInstance().getCountCaseExpressionColumn(inactive8to15Expression, -5, "inactive8to15"));
            squery.addSelectColumn(MDMUtil.getInstance().getCountCaseExpressionColumn(inactive16to30Expression, -5, "inactive16to30"));
            squery.addSelectColumn(MDMUtil.getInstance().getCountCaseExpressionColumn(inactiveAbove30Expression, -5, "inactiveAbove30"));
            RBDAUtil.getInstance().getRBDAQuery(squery);
            ds = DMDataSetWrapper.executeQuery((Object)squery);
            if (ds.next()) {
                for (final String columnName2 : graphColumns) {
                    lastContactHashMap.put(columnName2, ds.getValue(columnName2));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getInactiveDeviceCountsInRanges :{0}", ex);
        }
        return lastContactHashMap;
    }
}
