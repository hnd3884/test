package com.me.mdm.server.status;

import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
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
import com.me.mdm.server.config.ProfileAssociateHandler;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ManagedUserCollectionStatusSummary
{
    private static ManagedUserCollectionStatusSummary instance;
    public Logger mLogger;
    
    private ManagedUserCollectionStatusSummary() {
        this.mLogger = Logger.getLogger("MDMResourceSummaryLog");
    }
    
    public static ManagedUserCollectionStatusSummary getInstance() {
        if (ManagedUserCollectionStatusSummary.instance == null) {
            ManagedUserCollectionStatusSummary.instance = new ManagedUserCollectionStatusSummary();
        }
        return ManagedUserCollectionStatusSummary.instance;
    }
    
    public void computeAndUpdateManagedUserCollectionStatusSummary(final List collectionIDList) throws SyMException, DataAccessException {
        this.mLogger.log(Level.FINE, "computeAndUpdateManagedUserCollectionStatusSummary");
        if (collectionIDList.size() > 0) {
            final List userList = this.getManagedUserIdsForCollectionList(collectionIDList);
            this.updateManagedUserCollectionStatusSummary(userList);
        }
    }
    
    public void updateManagedUserCollnStatusSummaryForResource(final Long resourceID) throws SyMException, DataAccessException {
        this.mLogger.log(Level.FINE, "updateManagedUserCollnStatusSummaryForResource");
        final List mUserIdsList = this.getManagedUserIdsCollectionListForResource(resourceID);
        this.mLogger.log(Level.INFO, "updateManagedUserCollnStatusSummaryForResource - CustomGroupIds CollectionList {0} for ResourceID {1}", new Object[] { mUserIdsList, resourceID });
        if (mUserIdsList.size() > 0) {
            this.updateManagedUserCollectionStatusSummary(mUserIdsList);
        }
    }
    
    public void updateManagedUserCollectionStatusSummary(final List userList) {
        if (userList.size() > 0) {
            for (int i = 0; i < userList.size(); ++i) {
                final Properties properties = userList.get(i);
                final Long collectionID = ((Hashtable<K, Long>)properties).get("COLLECTION_ID");
                final Long userId = ((Hashtable<K, Long>)properties).get("RESOURCE_ID");
                this.addOrUpdateManagedUserCollectionStatusSummary(userId, collectionID);
            }
            ProfileAssociateHandler.getInstance().updateMDMResourceProfileSummary();
        }
    }
    
    public void updateManagedUserCollectionStatusSummary(final List userList, final Long collectionID) {
        if (userList.size() > 0) {
            for (int i = 0; i < userList.size(); ++i) {
                this.addOrUpdateManagedUserCollectionStatusSummary(userList.get(i), collectionID);
            }
        }
        ProfileAssociateHandler.getInstance().updateMDMResourceProfileSummary();
    }
    
    public void addOrUpdateManagedUserCollectionStatusSummary(final Long userId, final Long collectionID) {
        try {
            this.mLogger.log(Level.INFO, "addOrUpdateManagedUserCollectionStatusSummary: collectionId {0}", collectionID);
            final Hashtable collnStatusHash = this.getTargetManagedUserResourceCountWithStatus(collectionID, userId);
            if (collnStatusHash != null) {
                int collStatus = -1;
                int collnStatus = -1;
                final Column usrCol = Column.getColumn("MDMResourceCollnStatusSummary", "RESOURCE_ID");
                final Criteria usrCri = new Criteria(usrCol, (Object)userId, 0);
                final Column collnCol = Column.getColumn("MDMResourceCollnStatusSummary", "COLLECTION_ID");
                final Criteria collCri = new Criteria(collnCol, (Object)collectionID, 0);
                final Criteria criteria = usrCri.and(collCri);
                final DataObject userCollSummaryDO = MDMUtil.getPersistence().get("MDMResourceCollnStatusSummary", criteria);
                final int targetCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.TOTAL_TARGETS_COUNT, new Integer(0));
                final int yetToApplyTargetCount = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.YET_TO_APPLY_TARGETS_COUNT, new Integer(0));
                final int successCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.SUCCEEDED_TARGETS_COUNT, new Integer(0));
                final int failedCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.FAILED_TARGETS_COUNT, new Integer(0));
                final int notApplicableCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.NOT_APPLICABLE_TARGETS_COUNT, new Integer(0));
                final int notificationsentCnt = this.getValueFromHash(collnStatusHash, MDMCollectionUtil.NOTIFICATION_SENT_COUNT, new Integer(0));
                Row collSummaryRow = null;
                if (!userCollSummaryDO.isEmpty()) {
                    collSummaryRow = userCollSummaryDO.getRow("MDMResourceCollnStatusSummary");
                    collSummaryRow.set("COLLECTION_ID", (Object)collectionID);
                    collSummaryRow.set("RESOURCE_ID", (Object)userId);
                    collSummaryRow.set("TOTAL_TARGET_COUNT", (Object)targetCnt);
                    collSummaryRow.set("YET_TO_APPLY_COUNT", (Object)yetToApplyTargetCount);
                    collSummaryRow.set("SUCCESS_COUNT", (Object)successCnt);
                    collSummaryRow.set("FAILED_COUNT", (Object)failedCnt);
                    collSummaryRow.set("NOT_APPLICABLE_COUNT", (Object)notApplicableCnt);
                    collSummaryRow.set("NOTIFICATION_SENT_COUNT", (Object)notificationsentCnt);
                    userCollSummaryDO.updateRow(collSummaryRow);
                    MDMUtil.getPersistence().update(userCollSummaryDO);
                }
                else {
                    collSummaryRow = new Row("MDMResourceCollnStatusSummary");
                    collSummaryRow.set("COLLECTION_ID", (Object)collectionID);
                    collSummaryRow.set("RESOURCE_ID", (Object)userId);
                    collSummaryRow.set("TOTAL_TARGET_COUNT", (Object)targetCnt);
                    collSummaryRow.set("YET_TO_APPLY_COUNT", (Object)yetToApplyTargetCount);
                    collSummaryRow.set("SUCCESS_COUNT", (Object)successCnt);
                    collSummaryRow.set("FAILED_COUNT", (Object)failedCnt);
                    collSummaryRow.set("NOT_APPLICABLE_COUNT", (Object)notApplicableCnt);
                    collSummaryRow.set("NOTIFICATION_SENT_COUNT", (Object)notificationsentCnt);
                    userCollSummaryDO.addRow(collSummaryRow);
                    MDMUtil.getPersistence().add(userCollSummaryDO);
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
                MDMUtil.getInstance().addOrUpdateCollnToResources(userId, collectionID, collnStatus, remarks);
                if (collStatus == 4) {
                    final Criteria userIdCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)userId, 0, (boolean)Boolean.FALSE);
                    final Criteria collectionIDCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "COLLECTION_ID"), (Object)collectionID, 0, (boolean)Boolean.FALSE);
                    final Criteria markedForDeleteCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)Boolean.TRUE.toString(), 0, (boolean)Boolean.FALSE);
                    DataAccess.delete(userIdCriteria.and(collectionIDCriteria).and(markedForDeleteCriteria));
                }
                MDMCollectionStatusUpdate.getInstance().updateManagedUserProfileCollectionStatus(userId, collectionID, collStatus, remarks);
            }
            this.mLogger.log(Level.FINE, "addOrUpdateManagedUserCollectionStatusSummary: collnStatusHash{0}", collnStatusHash);
        }
        catch (final Exception ex) {
            this.mLogger.log(Level.WARNING, "Exception Occurred in addOrUpdateManagedUserCollectionStatusSummary(){0}", ex);
        }
    }
    
    private Hashtable getTargetManagedUserResourceCountWithStatus(final Long collectionId, final Long userId) {
        final Hashtable statusCntHash = new Hashtable();
        try {
            final LinkedHashMap map = this.getTargetManagedUserResourcesCountForColln(userId, collectionId);
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
            this.mLogger.log(Level.WARNING, ex, () -> "Caught exception while retrieving target resources counts with status for collection id: " + n);
        }
        return statusCntHash;
    }
    
    private LinkedHashMap getTargetManagedUserResourcesCountForColln(final Long userId, final Long collectionId) throws SyMException {
        LinkedHashMap hashMap = new LinkedHashMap();
        final Column userIdCol = Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID");
        final Criteria userCriteria = new Criteria(userIdCol, (Object)userId, 0);
        final Table baseTable = Table.getTable("ManagedUserToDevice");
        final SelectQueryImpl query = new SelectQueryImpl(baseTable);
        final Join collntoresourcesjoin = new Join("ManagedUserToDevice", "CollnToResources", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(collntoresourcesjoin);
        final Join collntomgddevicesjoin = new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        query.addJoin(collntomgddevicesjoin);
        final Column collectionIdCol = Column.getColumn("CollnToResources", "COLLECTION_ID");
        Criteria criteria = new Criteria(collectionIdCol, (Object)collectionId, 0);
        final Criteria dcriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        criteria = criteria.and(userCriteria).and(dcriteria);
        query.setCriteria(criteria);
        Column selCol = new Column("CollnToResources", "RESOURCE_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        selCol.setColumnAlias("RESOURCE_ID");
        query.addSelectColumn(selCol);
        query.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        query.addGroupByColumn(Column.getColumn("CollnToResources", "STATUS"));
        try {
            hashMap = this.getStatusMapFromQuery((SelectQuery)query);
        }
        catch (final Exception ex) {
            this.mLogger.log(Level.WARNING, ex, () -> "Caught exception while retrieving target resources count for collection Id : " + n);
        }
        return hashMap;
    }
    
    public LinkedHashMap getStatusMapFromQuery(final SelectQuery query) throws SQLException, QueryConstructionException {
        final LinkedHashMap hashMap = new LinkedHashMap();
        long yetToApplyCount = 0L;
        long successCount = 0L;
        long inProgressCount = 0L;
        long inProgressFailedCount = 0L;
        long retryCount = 0L;
        long failedCount = 0L;
        long notApplicableCount = 0L;
        long notificationCount = 0L;
        try {
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final int status = (int)ds.getValue("STATUS");
                if (status == 12) {
                    yetToApplyCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 6) {
                    successCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 3) {
                    inProgressCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 10) {
                    inProgressFailedCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 7) {
                    failedCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 16) {
                    retryCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 8) {
                    notApplicableCount = (int)ds.getValue("RESOURCE_ID");
                }
                if (status == 18) {
                    notificationCount = (int)ds.getValue("RESOURCE_ID");
                }
            }
        }
        catch (final Exception e) {
            this.mLogger.log(Level.WARNING, "Error getting dataSet in getStatusMapFromQuery");
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
    
    private List getManagedUserIdsForCollectionList(final List collectionList) {
        this.mLogger.log(Level.FINE, "Inside getManagedUserIdsForCollectionList Method");
        final List userList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceCollnStatusSummary"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceCollnStatusSummary", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceCollnStatusSummary", "COLLECTION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("MDMResourceCollnStatusSummary", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("MDMResourceCollnStatusSummary");
                while (rowIterator.hasNext()) {
                    final Row collntoresrow = rowIterator.next();
                    final Properties colusrProp = new Properties();
                    ((Hashtable<String, Object>)colusrProp).put("RESOURCE_ID", collntoresrow.get("RESOURCE_ID"));
                    ((Hashtable<String, Object>)colusrProp).put("COLLECTION_ID", collntoresrow.get("COLLECTION_ID"));
                    userList.add(colusrProp);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        this.mLogger.log(Level.FINE, "getManagedUserIdsForCollectionList : {0}", userList);
        return userList;
    }
    
    public List getManagedUserIdsCollectionListForResource(final Long resourceId) {
        this.mLogger.log(Level.INFO, "Inside getManagedUserIdsCollectionListForResource Method");
        final List mUserIdsCollectionList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDMResourceCollnStatusSummary"));
            final Join mdDeviceMemberReljoin = new Join("MDMResourceCollnStatusSummary", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
            selectQuery.addJoin(mdDeviceMemberReljoin);
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceCollnStatusSummary", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MDMResourceCollnStatusSummary", "COLLECTION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator rowIterator = dataObject.getRows("MDMResourceCollnStatusSummary");
                while (rowIterator.hasNext()) {
                    final Row collntoresrow = rowIterator.next();
                    final Properties colgrpProp = new Properties();
                    ((Hashtable<String, Object>)colgrpProp).put("RESOURCE_ID", collntoresrow.get("RESOURCE_ID"));
                    ((Hashtable<String, Object>)colgrpProp).put("COLLECTION_ID", collntoresrow.get("COLLECTION_ID"));
                    mUserIdsCollectionList.add(colgrpProp);
                }
            }
        }
        catch (final Exception exp) {
            this.mLogger.log(Level.SEVERE, "Exception occurred in getManagedUserIdsCollectionListForResource()", exp);
        }
        this.mLogger.log(Level.INFO, "getGroupIdsCollectionListForResource : {0}", mUserIdsCollectionList);
        return mUserIdsCollectionList;
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
}
