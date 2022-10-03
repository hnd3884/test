package com.me.mdm.server.config;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.config.CollectionUtil;

public class MDMCollectionUtil extends CollectionUtil
{
    private static Logger logger;
    
    public static DataObject getCollection(final Long collectionId) throws SyMException {
        DataObject collDO = null;
        SelectQuery query = null;
        final String baseTableName = "Collection";
        try {
            final Table baseTable = Table.getTable(baseTableName);
            query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join(baseTableName, "CollectionVersion", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addJoin(new Join(baseTableName, "CollectionRetry", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addJoin(new Join(baseTableName, "CollSchExecution", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addJoin(new Join(baseTableName, "CollnToCustomerRel", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addJoin(new Join(baseTableName, "IOSCollectionPayload", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
            query.addSelectColumn(new Column((String)null, "*"));
            final Column col = Column.getColumn(baseTableName, "COLLECTION_ID");
            final Criteria criteria = new Criteria(col, (Object)collectionId, 0);
            query.setCriteria(criteria);
            collDO = MDMUtil.getPersistence().get(query);
        }
        catch (final DataAccessException ex) {
            MDMCollectionUtil.logger.log(Level.SEVERE, (Throwable)ex, () -> "Error while retrieving collection for  collection id: " + n);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            MDMCollectionUtil.logger.log(Level.SEVERE, ex2, () -> "Error while retrieving collection for  collection id: " + n2);
            throw new SyMException(1001, (Throwable)ex2);
        }
        return collDO;
    }
    
    public static List getResourceNotAssociatedWithCollection(final List resourceList, final Long collectionID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
            Criteria criteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("CollnToResources");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long resourceId = (Long)row.get("RESOURCE_ID");
                    resourceList.remove(resourceId);
                }
            }
            return new ArrayList(resourceList);
        }
        catch (final Exception e) {
            MDMCollectionUtil.logger.log(Level.SEVERE, e, () -> "Exception in getResourceNotAssociatedWithCollection for resourceList: " + Arrays.toString(list.toArray()) + " and collectionId : " + n);
            return null;
        }
    }
    
    public JSONObject getAssociatedCollectionStatusForResource(final Long collectionId, final Long resourceId, final Criteria criteria) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        query.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        query.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "RESOURCE_ID", "PROFILE_ID" }, new String[] { "RESOURCE_ID", "PROFILE_ID" }, 2));
        final Criteria collectionCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria markedForCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        Criteria getCriteria = collectionCriteria.and(resourceCriteria).and(markedForCriteria);
        if (criteria != null) {
            getCriteria = getCriteria.and(criteria);
        }
        query.setCriteria(getCriteria);
        query.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final JSONObject collectionStatus = new JSONObject();
            final Row historyRow = dataObject.getFirstRow("ResourceToProfileHistory");
            final Row collectionRow = dataObject.getFirstRow("CollnToResources");
            final Row profileRow = dataObject.getFirstRow("RecentProfileForResource");
            collectionStatus.put("ResourceToProfileHistory", (Object)historyRow.getAsJSON());
            collectionStatus.put("CollnToResources", (Object)collectionRow.getAsJSON());
            collectionStatus.put("RecentProfileForResource", (Object)profileRow.getAsJSON());
            return collectionStatus;
        }
        return null;
    }
    
    public static Boolean isCollectionAssociatedWithResource(final Long collectionID, final Long resourceID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CollnToResources"));
            Criteria criteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("CollnToResources", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("CollnToResources", "COLLECTION_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            return !dataObject.isEmpty();
        }
        catch (final Exception e) {
            MDMCollectionUtil.logger.log(Level.SEVERE, "Exception in getting collection to resources", e);
            return Boolean.FALSE;
        }
    }
    
    public static List<Long> getCollectionIdsAssociatedWithResources(final List<Long> resourceIdList) {
        List<Long> collectionIdList = new ArrayList<Long>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
            collectionIdList = DBUtil.getDistinctColumnValue("RecentProfileForResource", "COLLECTION_ID", criteria);
            return collectionIdList;
        }
        catch (final Exception e) {
            MDMCollectionUtil.logger.log(Level.SEVERE, "Exception in getting collection for resources", e);
            return collectionIdList;
        }
    }
    
    public static boolean isCustomerEligible(final Long customerId, final List collectionList) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
            final Criteria eligibleCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8).and(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.setCriteria(eligibleCriteria);
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            MDMCollectionUtil.logger.log(Level.INFO, "Exception while checking eligible customer", ex);
        }
        return false;
    }
    
    public static boolean isCustomerEligible(final Long customerId, final Long collectionId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
            final Criteria eligibleCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionId, 0).and(new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.setCriteria(eligibleCriteria);
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("ProfileToCustomerRel", "CUSTOMER_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            MDMCollectionUtil.logger.log(Level.INFO, "Exception while checking eligible customer", ex);
        }
        return false;
    }
    
    public List getResoucesWithFailedCollection(final Long collectionId, final List resourceList) {
        ArrayList failedresourceList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
            final Criteria statusCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)7, 0);
            final Criteria resCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria collnCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            selectQuery.addSelectColumn(new Column("CollnToResources", "*"));
            selectQuery.setCriteria(statusCriteria.and(resCriteria).and(collnCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            failedresourceList = (ArrayList)DBUtil.getColumnValuesAsList(dataObject.getRows("CollnToResources"), "RESOURCE_ID");
        }
        catch (final Exception ex) {
            MDMCollectionUtil.logger.log(Level.INFO, "Exception in getResoucesWithFailedCollection", ex);
        }
        return failedresourceList;
    }
    
    public static JSONObject getCollectionsStatusForResources(final List<Long> collectionIds, final List<Long> resourceIds, final Criteria criteria) {
        final JSONObject resourceObject = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CollnToResources"));
            selectQuery.addSelectColumn(new Column("CollnToResources", "COLLECTION_ID"));
            selectQuery.addSelectColumn(new Column("CollnToResources", "RESOURCE_ID"));
            selectQuery.addSelectColumn(new Column("CollnToResources", "STATUS"));
            selectQuery.addSelectColumn(new Column("CollnToResources", "REMARKS"));
            final Criteria collectionCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionIds.toArray(), 8);
            final Criteria resourceCriteria = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8);
            Criteria finalCriteria = collectionCriteria.and(resourceCriteria);
            if (criteria != null) {
                finalCriteria = finalCriteria.and(criteria);
            }
            selectQuery.setCriteria(finalCriteria);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                for (final Long resourceId : resourceIds) {
                    final Iterator collectionIterator = dataObject.getRows("CollnToResources", new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0));
                    final JSONObject collectionObject = new JSONObject();
                    while (collectionIterator.hasNext()) {
                        final Row collectionRow = collectionIterator.next();
                        if (collectionRow != null) {
                            final JSONObject collectionStatusObject = new JSONObject();
                            final Integer collectionStatus = (Integer)collectionRow.get("STATUS");
                            final String collectionRemark = (String)collectionRow.get("REMARKS");
                            final Long collectionId = (Long)collectionRow.get("COLLECTION_ID");
                            collectionStatusObject.put("status", (Object)collectionStatus);
                            collectionStatusObject.put("remark", (Object)collectionRemark);
                            collectionObject.put(collectionId.toString(), (Object)collectionStatusObject);
                        }
                    }
                    resourceObject.put(resourceId.toString(), (Object)collectionObject);
                }
            }
        }
        catch (final DataAccessException e) {
            MDMCollectionUtil.logger.log(Level.SEVERE, "Exception in getting collection status", (Throwable)e);
        }
        catch (final JSONException e2) {
            MDMCollectionUtil.logger.log(Level.SEVERE, "Exception in getting collection status", (Throwable)e2);
        }
        return resourceObject;
    }
    
    public static int getPlatformType(final Long collectionID) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        query.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria criteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
        query.setCriteria(criteria);
        query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        query.addSelectColumn(new Column("Profile", "PLATFORM_TYPE"));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        final Row profile = dataObject.getRow("Profile");
        final int platformType = (int)profile.get("PLATFORM_TYPE");
        return platformType;
    }
    
    public static void addOrUpdateCollnToResErrorCode(final List resourceList, final Long collnId, final int errorCode) {
        try {
            if (!resourceList.isEmpty()) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMCollnToResErrorCode"));
                selectQuery.addSelectColumn(new Column("MDMCollnToResErrorCode", "*"));
                final Criteria cResList = new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
                final Criteria cColl = new Criteria(new Column("MDMCollnToResErrorCode", "COLLECTION_ID"), (Object)collnId, 0);
                final DataObject collectionDO = MDMUtil.getPersistence().get("MDMCollnToResErrorCode", cResList.and(cColl));
                if (collectionDO.isEmpty()) {
                    for (int i = 0; i < resourceList.size(); ++i) {
                        final Long resId = resourceList.get(i);
                        final Row collnToResErrorRow = new Row("MDMCollnToResErrorCode");
                        collnToResErrorRow.set("RESOURCE_ID", (Object)resId);
                        collnToResErrorRow.set("COLLECTION_ID", (Object)collnId);
                        collnToResErrorRow.set("ERROR_CODE", (Object)errorCode);
                        collectionDO.addRow(collnToResErrorRow);
                    }
                }
                else {
                    for (int i = 0; i < resourceList.size(); ++i) {
                        final Long resId = resourceList.get(i);
                        final Criteria resCriteria = new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resId, 0);
                        final Row collnToResErrorRow2 = collectionDO.getRow("MDMCollnToResErrorCode", resCriteria.and(cColl));
                        collnToResErrorRow2.set("RESOURCE_ID", (Object)resId);
                        collnToResErrorRow2.set("COLLECTION_ID", (Object)collnId);
                        collnToResErrorRow2.set("ERROR_CODE", (Object)errorCode);
                        collectionDO.updateRow(collnToResErrorRow2);
                    }
                }
                MDMUtil.getPersistence().update(collectionDO);
            }
        }
        catch (final Exception e) {
            MDMCollectionUtil.logger.log(Level.SEVERE, "Exception in addOrUpdateCollnToResErrorCode", e);
        }
    }
    
    static {
        MDMCollectionUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
