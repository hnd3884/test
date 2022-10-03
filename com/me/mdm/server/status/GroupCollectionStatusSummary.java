package com.me.mdm.server.status;

import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.DerivedTable;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.HashMap;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.ArrayList;
import com.me.uem.announcement.AnnouncementConstants;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.config.CollectionUtil;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class GroupCollectionStatusSummary
{
    private static GroupCollectionStatusSummary groupCollectionStatusSummary;
    public Logger logger;
    
    public GroupCollectionStatusSummary() {
        this.logger = Logger.getLogger("MDMResourceSummaryLog");
    }
    
    public static GroupCollectionStatusSummary getInstance() {
        if (GroupCollectionStatusSummary.groupCollectionStatusSummary == null) {
            GroupCollectionStatusSummary.groupCollectionStatusSummary = new GroupCollectionStatusSummary();
        }
        return GroupCollectionStatusSummary.groupCollectionStatusSummary;
    }
    
    public void computeAndUpdateGroupCollectionStatusSummary(final List collectionIDList) throws SyMException, DataAccessException {
        this.logger.log(Level.FINE, "computeAndUpdateGroupCollectionStatusSummary");
        if (collectionIDList.size() > 0) {
            final List groupIdList = this.getGroupIdsForCollectionList(collectionIDList);
            this.updateGroupCollectionStatusSummary(groupIdList);
        }
    }
    
    public void updateGroupCollnStatusSummaryForResource(final Long resourceID) throws SyMException, DataAccessException {
        this.logger.log(Level.FINE, "updateGroupCollnStatusSummaryForResource");
        final List groupIdsList = this.getGroupIdsCollectionListForResource(resourceID);
        this.logger.log(Level.INFO, "updateGroupCollnStatusSummaryForResource - CustomGroupIds CollectionList {0} for ResourceID {1}", new Object[] { groupIdsList, resourceID });
        if (groupIdsList.size() > 0) {
            this.updateGroupCollectionStatusSummary(groupIdsList);
        }
    }
    
    public void updateGroupCollectionStatusSummary(final List groupList) {
        if (groupList.size() > 0) {
            for (int i = 0; i < groupList.size(); ++i) {
                final Properties properties = groupList.get(i);
                final Long collectionID = ((Hashtable<K, Long>)properties).get("COLLECTION_ID");
                final Long groupID = ((Hashtable<K, Long>)properties).get("GROUP_ID");
                this.addOrUpdateGroupCollectionStatusSummary(groupID, collectionID);
            }
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
    }
    
    public void updateGroupCollectionStatusSummary(final List groupList, final Long collectionID) {
        if (groupList.size() > 0) {
            for (int i = 0; i < groupList.size(); ++i) {
                this.addOrUpdateGroupCollectionStatusSummary(groupList.get(i), collectionID);
            }
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
    }
    
    public void addOrUpdateGroupCollectionStatusSummary(final Long groupId, final Long collectionID) {
        this.logger.log(Level.FINE, "Inside updateGroupCollectionStatusSummary");
        try {
            this.logger.log(Level.INFO, "updateGroupCollectionStatusSummary: collectionId ", collectionID);
            final Hashtable collnStatusHash = this.getTargetGroupResourceCountWithStatus(collectionID, groupId);
            if (collnStatusHash != null) {
                int collStatus = -1;
                int collnStatus = -1;
                final Column grpCol = Column.getColumn("GroupCollnStatusSummary", "GROUP_ID");
                final Criteria grpCri = new Criteria(grpCol, (Object)groupId, 0);
                final Column collnCol = Column.getColumn("GroupCollnStatusSummary", "COLLECTION_ID");
                final Criteria collCri = new Criteria(collnCol, (Object)collectionID, 0);
                final Criteria criteria = grpCri.and(collCri);
                final DataObject groupCollSummaryDO = MDMUtil.getPersistence().get("GroupCollnStatusSummary", criteria);
                final int targetCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.TOTAL_TARGETS_COUNT, new Integer(0));
                final int yetToApplyTargetCount = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.YET_TO_APPLY_TARGETS_COUNT, new Integer(0));
                final int successCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.SUCCEEDED_TARGETS_COUNT, new Integer(0));
                final int failedCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.FAILED_TARGETS_COUNT, new Integer(0));
                final int notApplicableCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, new Integer(0));
                final int notificationsentCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.NOTIFICATION_SENT_COUNT, new Integer(0));
                Row collSummaryRow = null;
                if (!groupCollSummaryDO.isEmpty()) {
                    collSummaryRow = groupCollSummaryDO.getRow("GroupCollnStatusSummary");
                    collSummaryRow.set("COLLECTION_ID", (Object)collectionID);
                    collSummaryRow.set("GROUP_ID", (Object)groupId);
                    collSummaryRow.set("TOTAL_TARGET_COUNT", (Object)targetCnt);
                    collSummaryRow.set("YET_TO_APPLY_COUNT", (Object)yetToApplyTargetCount);
                    collSummaryRow.set("SUCCESS_COUNT", (Object)successCnt);
                    collSummaryRow.set("FAILED_COUNT", (Object)failedCnt);
                    collSummaryRow.set("NOT_APPLICABLE_COUNT", (Object)notApplicableCnt);
                    collSummaryRow.set("NOTIFICATION_SENT_COUNT", (Object)notificationsentCnt);
                    groupCollSummaryDO.updateRow(collSummaryRow);
                    MDMUtil.getPersistence().update(groupCollSummaryDO);
                }
                else {
                    collSummaryRow = new Row("GroupCollnStatusSummary");
                    collSummaryRow.set("COLLECTION_ID", (Object)collectionID);
                    collSummaryRow.set("GROUP_ID", (Object)groupId);
                    collSummaryRow.set("TOTAL_TARGET_COUNT", (Object)targetCnt);
                    collSummaryRow.set("YET_TO_APPLY_COUNT", (Object)yetToApplyTargetCount);
                    collSummaryRow.set("SUCCESS_COUNT", (Object)successCnt);
                    collSummaryRow.set("FAILED_COUNT", (Object)failedCnt);
                    collSummaryRow.set("NOT_APPLICABLE_COUNT", (Object)notApplicableCnt);
                    collSummaryRow.set("NOTIFICATION_SENT_COUNT", (Object)notificationsentCnt);
                    groupCollSummaryDO.addRow(collSummaryRow);
                    MDMUtil.getPersistence().add(groupCollSummaryDO);
                }
                String remarks = "--";
                if (targetCnt == successCnt || targetCnt == notApplicableCnt || targetCnt == successCnt + notApplicableCnt) {
                    collStatus = 4;
                    remarks = "dc.mdm.profile.remarks.executed_Sucessfully";
                }
                else if (targetCnt == successCnt + failedCnt + notApplicableCnt) {
                    collStatus = 11;
                }
                else if (targetCnt == yetToApplyTargetCount) {
                    collStatus = 2;
                }
                else {
                    collStatus = 3;
                }
                if (targetCnt == successCnt || targetCnt == notApplicableCnt || targetCnt == successCnt + notApplicableCnt) {
                    collnStatus = 6;
                    remarks = "dc.mdm.profile.remarks.executed_Sucessfully";
                }
                else if (targetCnt == successCnt + failedCnt + notApplicableCnt) {
                    collnStatus = 11;
                }
                else if (targetCnt == yetToApplyTargetCount) {
                    collnStatus = 12;
                }
                else {
                    collnStatus = 3;
                }
                MDMUtil.getInstance().addOrUpdateCollnToResources(groupId, collectionID, collnStatus, remarks);
                if (collStatus == 4) {
                    final Criteria groupIdCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0, (boolean)Boolean.FALSE);
                    final Criteria collectionIDCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0, (boolean)Boolean.FALSE);
                    final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.TRUE.toString(), 0, (boolean)Boolean.FALSE);
                    DataAccess.delete(groupIdCriteria.and(collectionIDCriteria).and(markedForDeleteCriteria));
                }
                MDMCollectionStatusUpdate.getInstance().updateGroupProfileCollectionStatus(groupId, collectionID, collStatus, remarks);
            }
            this.logger.log(Level.FINE, "updateGroupCollectionStatusSummary: collnStatusHash{0}", collnStatusHash);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception Occurred in updateGroupCollectionStatusSummary(){0}", ex);
        }
    }
    
    private Hashtable getTargetGroupResourceCountWithStatus(final Long collectionId, final Long groupID) {
        final Hashtable statusCntHash = new Hashtable();
        try {
            final LinkedHashMap map = this.getTargetGroupResourcesCountForColln(groupID, collectionId);
            statusCntHash.put(CollectionUtil.TOTAL_TARGETS_COUNT, Integer.valueOf(map.get("total").toString()));
            statusCntHash.put(CollectionUtil.YET_TO_APPLY_TARGETS_COUNT, Integer.valueOf(map.get("yet-to-apply").toString()));
            statusCntHash.put(CollectionUtil.SUCCEEDED_TARGETS_COUNT, Integer.valueOf(map.get("success").toString()));
            statusCntHash.put(CollectionUtil.INPROGRESS_TARGETS_COUNT, Integer.valueOf(map.get("inProgress").toString()));
            statusCntHash.put(CollectionUtil.INPROGRESS_FAILED_TARGETS_COUNT, Integer.valueOf(map.get("inProgressFailed").toString()));
            statusCntHash.put(CollectionUtil.FAILED_TARGETS_COUNT, Integer.valueOf(map.get("failed").toString()));
            statusCntHash.put(CollectionUtil.RETRY_INPROGRESS_TARGETS_COUNT, Integer.valueOf(map.get("retryCount").toString()));
            statusCntHash.put(CollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, Integer.valueOf(map.get("not-applicable").toString()));
            statusCntHash.put(CollectionUtil.NOTIFICATION_SENT_COUNT, Integer.valueOf(map.get("notification-sent").toString()));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Caught exception while retrieving target resources counts with status for collection id: " + n);
        }
        return statusCntHash;
    }
    
    private LinkedHashMap getTargetGroupResourcesCountForColln(final Long groupID, final Long collectionId) throws SyMException {
        final int groupType = MDMGroupHandler.getInstance().getGroupType(groupID);
        LinkedHashMap hashMap = new LinkedHashMap();
        final Column groupIdCol = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
        final Criteria groupCriteria = new Criteria(groupIdCol, (Object)groupID, 0);
        final Table baseTable = Table.getTable("CustomGroupMemberRel");
        final SelectQueryImpl query = new SelectQueryImpl(baseTable);
        final Join collntoresourcesjoin = new Join("CustomGroupMemberRel", "CollnToResources", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(collntoresourcesjoin);
        final Column collectionIdCol = Column.getColumn("CollnToResources", "COLLECTION_ID");
        Criteria criteria = new Criteria(collectionIdCol, (Object)collectionId, 0);
        if (groupType != 7) {
            final Join collntomgddevicesjoin = new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            query.addJoin(collntomgddevicesjoin);
            final Criteria dcriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            criteria = criteria.and(groupCriteria).and(dcriteria);
        }
        else if (groupType == 7) {
            final Join collntomgddevicesjoin = new Join("CustomGroupMemberRel", "Resource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            query.addJoin(collntomgddevicesjoin);
            final Criteria resourceTypeCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new int[] { 2, 101 }, 8);
            criteria = criteria.and(groupCriteria).and(resourceTypeCriteria);
        }
        query.setCriteria(criteria);
        Column selCol = new Column("CollnToResources", "RESOURCE_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        query.addSelectColumn(selCol);
        query.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        query.addGroupByColumn(Column.getColumn("CollnToResources", "STATUS"));
        try {
            hashMap = this.getStatusMapFromQuery((SelectQuery)query);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Caught exception while retrieving target resources count for collection Id : " + n);
        }
        return hashMap;
    }
    
    public LinkedHashMap getActionStatusMapFromQuery(final SelectQuery query, final int actionType, final Long groupId) throws Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        DMDataSetWrapper ds = null;
        long yetToApplyCount = 0L;
        long successCount = 0L;
        long inProgressCount = 0L;
        long scheduledCount = 0L;
        long failedCount = 0L;
        long suspendedCount = 0L;
        final long totalCount = MDMCustomGroupUtil.getInstance().getGroupMemberCount(groupId);
        ds = DMDataSetWrapper.executeQuery((Object)query);
        while (ds.next()) {
            final int status = (int)ds.getValue(2);
            if (status == 1 || status == 5) {
                yetToApplyCount = (int)ds.getValue(1);
            }
            if (status == 7) {
                scheduledCount = (int)ds.getValue(1);
            }
            if (status == 2) {
                successCount = (int)ds.getValue(1);
            }
            if (status == 4) {
                inProgressCount = (int)ds.getValue(1);
            }
            if (status == 0) {
                failedCount = (int)ds.getValue(1);
            }
            if (status == 6) {
                suspendedCount = (int)ds.getValue(1);
            }
        }
        hashMap.put("success", successCount);
        hashMap.put("inProgress", inProgressCount);
        hashMap.put("yetToApply", yetToApplyCount);
        hashMap.put("failed", failedCount);
        hashMap.put("suspended", suspendedCount);
        hashMap.put("scheduled", scheduledCount);
        hashMap.put("notApplicable", totalCount - (yetToApplyCount + successCount + failedCount + inProgressCount + suspendedCount + scheduledCount));
        hashMap.put("total", totalCount);
        return hashMap;
    }
    
    public LinkedHashMap getStatusMapFromQuery(final SelectQuery query) throws SQLException, QueryConstructionException {
        final LinkedHashMap hashMap = new LinkedHashMap();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        long yetToApplyCount = 0L;
        long successCount = 0L;
        long inProgressCount = 0L;
        long inProgressFailedCount = 0L;
        long retryCount = 0L;
        long failedCount = 0L;
        long notApplicableCount = 0L;
        long notificationCount = 0L;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final int status = (int)ds.getValue(2);
                if (status == 12) {
                    yetToApplyCount = (int)ds.getValue(1);
                }
                if (status == 6 || status == 961 || status == 962) {
                    successCount = (int)ds.getValue(1);
                }
                if (status == 3) {
                    inProgressCount = (int)ds.getValue(1);
                }
                if (status == 10) {
                    inProgressFailedCount = (int)ds.getValue(1);
                }
                if (status == 7) {
                    failedCount = (int)ds.getValue(1);
                }
                if (status == 16) {
                    retryCount = (int)ds.getValue(1);
                }
                if (status == 8) {
                    notApplicableCount = (int)ds.getValue(1);
                }
                if (status == 18) {
                    notificationCount = (int)ds.getValue(1);
                }
            }
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
        hashMap.put("yet-to-apply", yetToApplyCount);
        hashMap.put("success", successCount);
        hashMap.put("failed", failedCount);
        hashMap.put("inProgress", inProgressCount);
        hashMap.put("inProgressFailed", inProgressFailedCount);
        hashMap.put("retryCount", retryCount);
        hashMap.put("not-applicable", notApplicableCount);
        hashMap.put("notification-sent", notificationCount);
        hashMap.put("total", yetToApplyCount + successCount + failedCount + notApplicableCount + notificationCount + retryCount + inProgressCount + inProgressFailedCount);
        return hashMap;
    }
    
    public LinkedHashMap getAnnouncementStatusMapFromQuery(final SelectQuery query) throws SQLException, QueryConstructionException, Exception {
        final LinkedHashMap hashMap = new LinkedHashMap();
        long yetToDeliverCount = 0L;
        long deliveredCount = 0L;
        long readCount = 0L;
        long acknowledgedCount = 0L;
        long failedCount = 0L;
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final int status = (int)ds.getValue(2);
                if (AnnouncementConstants.ANNOUNCEMENT_YET_TO_DELIVER.contains(status)) {
                    yetToDeliverCount += (int)ds.getValue(1);
                }
                else if (AnnouncementConstants.ANNOUNCEMENT_DELIVERED.contains(status)) {
                    deliveredCount += (int)ds.getValue(1);
                }
                else if (AnnouncementConstants.ANNOUNCEMENT_FAILED.contains(status)) {
                    failedCount += (int)ds.getValue(1);
                }
                else if (status == 961) {
                    readCount = (int)ds.getValue(1);
                }
                else {
                    if (status != 962) {
                        continue;
                    }
                    acknowledgedCount = (int)ds.getValue(1);
                }
            }
            hashMap.put("yet-to-deliver", yetToDeliverCount);
            hashMap.put("delivered", deliveredCount);
            hashMap.put("failed", failedCount);
            hashMap.put("read", readCount);
            hashMap.put("acknowledged", acknowledgedCount);
            hashMap.put("total", yetToDeliverCount + deliveredCount + failedCount + readCount + acknowledgedCount);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAnnouncementStatusMapFromQuery(){0}", ex);
            throw ex;
        }
        return hashMap;
    }
    
    private List getGroupIdsForCollectionList(final List collectionList) {
        this.logger.log(Level.FINE, "Inside getGroupIdsForCollectionList Method");
        final List groupList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupCollnStatusSummary"));
            selectQuery.addSelectColumn(Column.getColumn("GroupCollnStatusSummary", "GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GroupCollnStatusSummary", "COLLECTION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("GroupCollnStatusSummary", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("GroupCollnStatusSummary");
                while (rowIterator.hasNext()) {
                    final Row collntoresrow = rowIterator.next();
                    final Properties colgrpProp = new Properties();
                    ((Hashtable<String, Object>)colgrpProp).put("GROUP_ID", collntoresrow.get("GROUP_ID"));
                    ((Hashtable<String, Object>)colgrpProp).put("COLLECTION_ID", collntoresrow.get("COLLECTION_ID"));
                    groupList.add(colgrpProp);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        this.logger.log(Level.FINE, "getGroupIdsForCollectionList : {0}", groupList);
        return groupList;
    }
    
    public List getGroupIdsCollectionListForResource(final Long resourceId) {
        this.logger.log(Level.INFO, "Inside getGroupIdsCollectionListForResource Method");
        final List groupIdsCollectionList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupCollnStatusSummary"));
            final Join customGroupMemberReljoin = new Join("GroupCollnStatusSummary", "CustomGroupMemberRel", new String[] { "GROUP_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
            selectQuery.addJoin(customGroupMemberReljoin);
            selectQuery.addSelectColumn(Column.getColumn("GroupCollnStatusSummary", "GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GroupCollnStatusSummary", "COLLECTION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("GroupCollnStatusSummary");
                while (rowIterator.hasNext()) {
                    final Row collntoresrow = rowIterator.next();
                    final Properties colgrpProp = new Properties();
                    ((Hashtable<String, Object>)colgrpProp).put("GROUP_ID", collntoresrow.get("GROUP_ID"));
                    ((Hashtable<String, Object>)colgrpProp).put("COLLECTION_ID", collntoresrow.get("COLLECTION_ID"));
                    groupIdsCollectionList.add(colgrpProp);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        this.logger.log(Level.INFO, "getGroupIdsCollectionListForResource : {0}", groupIdsCollectionList);
        return groupIdsCollectionList;
    }
    
    private int getValueFromHash(final Hashtable inputHash, final Object key, final Object defaultValue) {
        final Integer defValue = (Integer)defaultValue;
        if (inputHash == null) {
            return defValue;
        }
        Integer valueObj = inputHash.get(key);
        if (valueObj == null) {
            valueObj = (Integer)defaultValue;
        }
        return valueObj;
    }
    
    private List getGroupIdsForActionList(final List actionList) {
        this.logger.log(Level.FINE, "Inside getGroupIdsForActionList Method");
        final List groupList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            selectQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionList.toArray(), 8);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("GroupActionHistory");
                while (rowIterator.hasNext()) {
                    final Row collntoresrow = rowIterator.next();
                    final Properties colgrpProp = new Properties();
                    ((Hashtable<String, Object>)colgrpProp).put("GROUP_ID", collntoresrow.get("GROUP_ID"));
                    ((Hashtable<String, Object>)colgrpProp).put("GROUP_ACTION_ID", collntoresrow.get("GROUP_ACTION_ID"));
                    groupList.add(colgrpProp);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        this.logger.log(Level.FINE, "getGroupIdsForActionList : {0}", groupList);
        return groupList;
    }
    
    public void computeAndUpdateManagedUserActionStatusSummary(final List actionIdList) throws Exception {
        this.logger.log(Level.FINE, "Inside computeAndUpdateManagedUserActionStatusSummary");
        this.logger.log(Level.INFO, "computeAndUpdateManagedUserActionStatusSummary: actionIdList ", actionIdList);
        final LinkedHashMap actionStatusCountMap = this.getTargetGroupResourcesCountForAction(actionIdList);
        if (actionStatusCountMap != null) {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("GroupActionHistory"));
            query.addSelectColumn(new Column("GroupActionHistory", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionIdList.toArray(), 8);
            query.setCriteria(criteria);
            final DataObject groupActionSummaryDO = MDMUtil.getPersistence().get(query);
            for (final Long actionID : actionStatusCountMap.keySet()) {
                final Criteria actionIdCri = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionID, 0);
                final Row actionSummaryRow = groupActionSummaryDO.getRow("GroupActionHistory", actionIdCri);
                final JSONObject actionStatusJSON = actionStatusCountMap.get(actionID);
                final int inprogress_count = actionStatusJSON.optInt("INPROGRESS_COUNT", 0);
                final int success_count = actionStatusJSON.optInt("SUCCESS_COUNT", 0);
                final int failure_count = actionStatusJSON.optInt("FAILURE_COUNT", 0);
                final int suspend_count = actionStatusJSON.optInt("SUSPEND_COUNT", 0);
                final int scheduled_count = actionStatusJSON.optInt("schedule_count", 0);
                final int total_count = Integer.valueOf(actionSummaryRow.get("INITIATED_COUNT").toString());
                final int curr_action_status = Integer.valueOf(actionSummaryRow.get("ACTION_STATUS").toString());
                int actionStatus;
                if (curr_action_status == 6) {
                    actionStatus = 6;
                    actionSummaryRow.set("SUSPEND_COUNT", (Object)suspend_count);
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.device.manual_suspend");
                }
                else if (scheduled_count != 0 && inprogress_count <= 0 && failure_count <= 0) {
                    actionStatus = 7;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.device_schedule_remarks");
                }
                else if (inprogress_count > 0 && failure_count > 0) {
                    actionStatus = 4;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.inProgress_failure_remarks");
                }
                else if (inprogress_count > 0) {
                    actionStatus = 4;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.inProgress_remarks");
                }
                else if (failure_count > 0) {
                    actionStatus = 0;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.failure_remarks");
                }
                else if (success_count > 0) {
                    actionStatus = 2;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.successful_remarks");
                }
                else if (GroupActionScheduleUtils.isGroupActionScheduled(actionID)) {
                    actionStatus = 7;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.device_schedule_remarks");
                }
                else {
                    actionStatus = 1000;
                    actionSummaryRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.notApplicable_group_remarks");
                }
                actionSummaryRow.set("SUCCESS_COUNT", (Object)success_count);
                actionSummaryRow.set("FAILURE_COUNT", (Object)failure_count);
                actionSummaryRow.set("INPROGRESS_COUNT", (Object)inprogress_count);
                actionSummaryRow.set("ACTION_STATUS", (Object)actionStatus);
                groupActionSummaryDO.updateRow(actionSummaryRow);
            }
            MDMUtil.getPersistence().update(groupActionSummaryDO);
        }
        this.logger.log(Level.FINE, "computeAndUpdateManagedUserActionStatusSummary: ActionStatusHash{0}", actionStatusCountMap);
    }
    
    private LinkedHashMap getTargetGroupResourcesCountForAction(final List actionIdList) throws Exception {
        final HashMap<Long, JSONObject> test = (HashMap<Long, JSONObject>)actionIdList.stream().collect(Collectors.toMap(p -> p, q -> new JSONObject()));
        final LinkedHashMap hashMap = new LinkedHashMap((Map<? extends K, ? extends V>)test);
        DMDataSetWrapper ds = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
        subQuery.addJoin(new Join("CustomGroupMemberRel", "GroupActionHistory", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "GROUP_ID" }, 2));
        final Column resorceCol = new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
        resorceCol.setColumnAlias("res_id");
        subQuery.addSelectColumn(resorceCol);
        subQuery.setCriteria(new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionIdList.toArray(), 8));
        final Column group_Action_id = new Column("GroupActionToCommand", "GROUP_ACTION_ID");
        final DerivedTable commandDerievedTab = new DerivedTable("subQuery", (Query)subQuery);
        query.addJoin(new Join(Table.getTable("CommandHistory"), (Table)commandDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "res_id" }, 2));
        query.addJoin(new Join(Table.getTable("CommandHistory"), Table.getTable("GroupActionToCommand"), new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
        query.addJoin(new Join(Table.getTable("CommandHistory"), Table.getTable("MdCommands"), new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
        final Column cmdType = Column.getColumn("MdCommands", "COMMAND_UUID");
        final Column status_col = Column.getColumn("CommandHistory", "COMMAND_STATUS");
        final Column group_action_id_col = Column.getColumn("GroupActionToCommand", "GROUP_ACTION_ID");
        final Column status_count = new Column("MdCommands", "COMMAND_UUID").count();
        status_count.setColumnAlias("STATUS_COUNT");
        final List groupByList = new ArrayList();
        groupByList.add(cmdType);
        groupByList.add(status_col);
        groupByList.add(group_action_id_col);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        query.setGroupByClause(groupByClause);
        query.addSelectColumn(status_col);
        query.addSelectColumn(group_action_id_col);
        query.addSelectColumn(status_count);
        final Criteria actionCriteria = new Criteria(new Column("GroupActionToCommand", "GROUP_ACTION_ID"), (Object)actionIdList.toArray(), 8);
        query.setCriteria(actionCriteria);
        ds = DMDataSetWrapper.executeQuery((Object)query);
        while (ds.next()) {
            final long actionId = Long.valueOf(ds.getValue("GROUP_ACTION_ID").toString());
            final JSONObject actionStatusCount = hashMap.getOrDefault(actionId, new JSONObject());
            final int status = Integer.valueOf(ds.getValue("COMMAND_STATUS").toString());
            final int count = Integer.valueOf(ds.getValue("STATUS_COUNT").toString());
            switch (status) {
                case 1:
                case 4: {
                    final int progCount = actionStatusCount.optInt("INPROGRESS_COUNT", 0);
                    actionStatusCount.put("INPROGRESS_COUNT", count + progCount);
                    actionStatusCount.put("ACTION_STATUS", 4);
                    break;
                }
                case 2: {
                    actionStatusCount.put("SUCCESS_COUNT", count);
                    actionStatusCount.put("ACTION_STATUS", 2);
                    break;
                }
                case 7: {
                    actionStatusCount.put("schedule_count", count);
                    actionStatusCount.put("ACTION_STATUS", 7);
                    break;
                }
                case 0: {
                    actionStatusCount.put("FAILURE_COUNT", count);
                    actionStatusCount.put("ACTION_STATUS", 0);
                    break;
                }
                case 6: {
                    final int progCount = actionStatusCount.optInt("INPROGRESS_COUNT", 0);
                    actionStatusCount.put("SUSPEND_COUNT", progCount + count);
                    actionStatusCount.put("INPROGRESS_COUNT", 0);
                    actionStatusCount.put("ACTION_STATUS", 6);
                    break;
                }
            }
            hashMap.put(actionId, actionStatusCount);
        }
        return hashMap;
    }
    
    static {
        GroupCollectionStatusSummary.groupCollectionStatusSummary = null;
    }
}
