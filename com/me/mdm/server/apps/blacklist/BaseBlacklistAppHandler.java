package com.me.mdm.server.apps.blacklist;

import java.util.Hashtable;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import java.util.HashSet;
import java.util.logging.Level;
import com.me.mdm.server.apps.blacklist.batchprocessor.BaseQueryBatchProcessor;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import com.me.mdm.server.apps.blacklist.batchprocessor.BatchProcessorInterface;

public abstract class BaseBlacklistAppHandler implements BlacklistAppInterface, BatchProcessorInterface
{
    public static final int YET_TO_APPLY = 1;
    public static final int NOTIFIED = 2;
    public static final int IN_PROGRESS = 3;
    public static final int APPLIED = 4;
    public static final int NOT_APPLICABLE = 5;
    public static final int YET_TO_REMOVE = 6;
    public static final int REMOVE_IN_PROGRESS = 7;
    public static final int REMOVED = 8;
    public static final int FAILED = 9;
    public static final int REMOVAL_FAILED = 10;
    public static final int DISABLED = 11;
    public static final int PROCESS_COLLECTION_LIST = 1;
    public static final int PROCESS_DEVICE_LIST = 2;
    private static final String RES_SIZE_KEY = "blacklist_res_size";
    private static final String COLLN_SIZE_KEY = "blacklist_colln_size";
    Properties associationParams;
    List resourceList;
    HashMap profileCollectionMap;
    HashMap platformMap;
    private Logger logger;
    Long customerID;
    private Integer resSize;
    private Integer collnSize;
    private Integer defaultChunkSize;
    public static final int GLOBAL_BLACKLIST = 0;
    public static final int DEVICE_BLACKLIST = 1;
    public static final int NON_BLACKLIST = 2;
    
    public BaseBlacklistAppHandler() {
        this.associationParams = new Properties();
        this.resourceList = null;
        this.profileCollectionMap = null;
        this.platformMap = null;
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
        this.customerID = null;
        this.defaultChunkSize = 500;
        String chunkSizeStr = null;
        try {
            chunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("blacklist_res_size");
            this.resSize = ((chunkSizeStr == null) ? this.defaultChunkSize : Integer.parseInt(chunkSizeStr));
            final String profileChunkSizeStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("blacklist_colln_size");
            this.collnSize = ((profileChunkSizeStr == null) ? this.defaultChunkSize : Integer.parseInt(profileChunkSizeStr));
        }
        catch (final Exception e) {
            this.resSize = 100;
            this.collnSize = 5;
        }
    }
    
    @Override
    public SelectQuery getDeviceList(final List resourceList) {
        final List profileList = new ArrayList(this.profileCollectionMap.keySet());
        final Criteria resourceCustomerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
        final Criteria resIDCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", resIDCriteria.and(resourceCustomerCriteria), 2));
        final Criteria enrollSuccess = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
        selectQuery.setCriteria(enrollSuccess.and(customerCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        return selectQuery;
    }
    
    @Override
    public void blacklistAppInResource(final HashMap params) throws Exception {
        this.profileCollectionMap = params.get("profileCollectionMap");
        final Long userID = params.get("userID");
        this.resourceList = params.get("resourceIDs");
        this.customerID = params.get("CUSTOMER_ID");
        final HashMap profileProperties = params.get("profileProperties");
        if (profileProperties != null) {
            ((Hashtable<String, HashMap>)this.associationParams).put("profileProperties", profileProperties);
        }
        ((Hashtable<String, Boolean>)this.associationParams).put("isAppConfig", false);
        ((Hashtable<String, Long>)this.associationParams).put("customerId", this.customerID);
        if (this.resourceList != null) {
            ((Hashtable<String, List>)this.associationParams).put("resourceList", this.resourceList);
        }
        ((Hashtable<String, HashMap>)this.associationParams).put("profileCollectionMap", this.profileCollectionMap);
        if (userID != null) {
            ((Hashtable<String, Long>)this.associationParams).put("loggedOnUser", userID);
        }
        ((Hashtable<String, Boolean>)this.associationParams).put("associateToDevice", true);
        ((Hashtable<String, String>)this.associationParams).put("commandName", "BlacklistAppInDevice");
        final SelectQuery batchProcessingQuery = this.getDeviceList(this.resourceList);
        final BaseQueryBatchProcessor baseQueryBatchProcessor = new BaseQueryBatchProcessor(this);
        baseQueryBatchProcessor.setSelectQuery(batchProcessingQuery, Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        (this.platformMap = new HashMap()).put("operation", 2);
        baseQueryBatchProcessor.performBatchProcessing(2, 500, this.platformMap);
        final List profileList = new ArrayList(this.profileCollectionMap.keySet());
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        final Criteria criteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        selectQuery.setCriteria(criteria);
        baseQueryBatchProcessor.setSelectQuery(selectQuery, Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        this.platformMap.put("operation", 1);
        baseQueryBatchProcessor.performBatchProcessing(2, 500, this.platformMap);
        this.logger.log(Level.INFO, "Basic info added for Blacklist Profile assocation profile collection Map : {0} Resource ids : {1}", new Object[] { this.profileCollectionMap, this.resourceList });
    }
    
    @Override
    public void removeBlacklistAppInResource(final HashMap params) throws Exception {
        this.profileCollectionMap = params.get("profileCollectionMap");
        final Long userID = params.get("userID");
        this.resourceList = params.get("resourceIDs");
        this.customerID = params.get("CUSTOMER_ID");
        ((Hashtable<String, Boolean>)this.associationParams).put("isAppConfig", false);
        ((Hashtable<String, Long>)this.associationParams).put("customerId", this.customerID);
        if (this.resourceList != null) {
            ((Hashtable<String, List>)this.associationParams).put("resourceList", this.resourceList);
        }
        ((Hashtable<String, HashMap>)this.associationParams).put("profileCollectionMap", this.profileCollectionMap);
        if (userID != null) {
            ((Hashtable<String, Long>)this.associationParams).put("loggedOnUser", userID);
        }
        final SelectQuery batchProcessingQuery = this.getDeviceList(this.resourceList);
        final BaseQueryBatchProcessor baseQueryBatchProcessor = new BaseQueryBatchProcessor(this);
        baseQueryBatchProcessor.setSelectQuery(batchProcessingQuery, Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        (this.platformMap = new HashMap()).put("operation", 2);
        baseQueryBatchProcessor.performBatchProcessing(2, 500, this.platformMap);
        final List mergedList = new ArrayList();
        for (final int i : BlacklistAppHandler.platforms) {
            final List platformList = this.platformMap.get("ResourceList" + i);
            if (platformList != null) {
                mergedList.addAll(platformList);
            }
        }
        final List profileList = new ArrayList(this.profileCollectionMap.keySet());
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria colCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)mergedList.toArray(), 8);
        selectQuery.addJoin(new Join("ProfileToCollection", "BlacklistAppCollectionStatus", colCriteria.and(resCriteria), 2));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        final Criteria collnCriteria = new Criteria(Column.getColumn("ProfileToCollection", "PROFILE_ID"), (Object)profileList.toArray(), 8);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 11, 10, 3, 4, 5 }, 8);
        selectQuery.setCriteria(collnCriteria.and(resCriteria).and(statusCriteria));
        baseQueryBatchProcessor.setSelectQuery(selectQuery, Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        this.platformMap.put("operation", 1);
        baseQueryBatchProcessor.performBatchProcessing(2, 500, this.platformMap);
        ((Hashtable<String, Boolean>)this.associationParams).put("associateToDevice", true);
        ((Hashtable<String, String>)this.associationParams).put("commandName", "RemoveBlacklistAppInDevice");
        this.logger.log(Level.INFO, "Basic info added for Blacklist Profile disassociation profile collection Map : {0} Resource ids : {1}", new Object[] { this.profileCollectionMap, this.resourceList });
    }
    
    public static BlacklistAppInterface getBlacklistHandler(final int type) {
        BlacklistAppInterface blacklistAppInterface = null;
        if (type == 1) {
            blacklistAppInterface = new DeviceGroupBlackListHandler();
        }
        else if (type == 2) {
            blacklistAppInterface = new DeviceBlacklistHandler();
        }
        else if (type == 3) {
            blacklistAppInterface = new UserGroupBlackListHandler();
        }
        else if (type == 4) {
            blacklistAppInterface = new UserBlackListHandler();
        }
        else if (type == 5) {
            blacklistAppInterface = new NetworkBlacklistHandler();
        }
        return blacklistAppInterface;
    }
    
    public List updateResourcetoBlacklistAppStatus(final List resourceList, final List collectionList, final int status, final int scope) throws DataAccessException {
        final List updatedList = new ArrayList(resourceList);
        final List<Long> appliedColln = new ArrayList<Long>();
        final List<Long> removedColln = new ArrayList<Long>();
        final List<List> resSplitList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, this.resSize);
        final List<List> collnSplitList = MDMUtil.getInstance().splitListIntoSubLists(collectionList, this.collnSize);
        final HashSet<Long> blacklistRemovedColln = new HashSet<Long>();
        for (final List curSplitList : resSplitList) {
            Iterator resItertor = curSplitList.iterator();
            for (final List curCollnList : collnSplitList) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
                final Criteria resourceCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)curSplitList.toArray(), 8);
                final Criteria collectionCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), (Object)curCollnList.toArray(), 8);
                final Criteria scopeCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), (Object)scope, 0);
                selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "*"));
                selectQuery.setCriteria(resourceCriteria.and(collectionCriteria).and(scopeCriteria));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                resItertor = curSplitList.iterator();
                while (resItertor.hasNext()) {
                    final Long resId = resItertor.next();
                    for (final Long collnId : curCollnList) {
                        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resId, 0);
                        final Criteria collCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), (Object)collnId, 0);
                        Row row = dataObject.getRow("BlacklistAppCollectionStatus", resCriteria.and(collCriteria));
                        if (row == null) {
                            row = new Row("BlacklistAppCollectionStatus");
                            row.set("COLLECTION_ID", (Object)collnId);
                            row.set("RESOURCE_ID", (Object)resId);
                            row.set("STATUS", (Object)status);
                            row.set("LAST_NOTIFIED_TIME", (Object)(-1L));
                            row.set("SCOPE", (Object)scope);
                            if (status != 6 && status != 8) {
                                dataObject.addRow(row);
                            }
                            else {
                                blacklistRemovedColln.add(collnId);
                            }
                        }
                        else {
                            final Integer curStatus = (Integer)row.get("STATUS");
                            if ((curStatus != 8 || status != 6) && (curStatus != 4 || status != 1) && ((curStatus != 3 && curStatus != 7) || (curStatus == 3 && status == 6) || (curStatus == 7 && status == 1) || status == 4 || status == 9 || status == 8 || status == 10)) {
                                row.set("STATUS", (Object)status);
                            }
                            if ((curStatus == 1 || curStatus == 2 || curStatus == 5) && status == 6) {
                                dataObject.deleteRow(row);
                                blacklistRemovedColln.add(collnId);
                            }
                            if (curStatus == 1 || curStatus == 2 || curStatus == 3 || curStatus == 4) {
                                updatedList.remove(resId);
                            }
                            if (status == 8) {
                                dataObject.deleteRow(row);
                            }
                            dataObject.updateRow(row);
                        }
                    }
                }
                MDMUtil.getPersistenceLite().update(dataObject);
            }
        }
        if (collectionList.size() > 0 && resourceList.size() > 0) {
            try {
                if (status == 5 || status == 9 || status == 8 || blacklistRemovedColln.size() > 0) {
                    Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
                    if (blacklistRemovedColln.size() > 0) {
                        collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)blacklistRemovedColln.toArray(), 8);
                    }
                    collnCriteria = collnCriteria.and(new Criteria(Column.getColumn("BlacklistAppToCollection", "APPLIED_STATUS"), (Object)Boolean.TRUE, 0));
                    final List cllnlist = DBUtil.getDistinctColumnValue("BlacklistAppToCollection", "COLLECTION_ID", collnCriteria);
                    final Iterator appColln = cllnlist.iterator();
                    final SelectQuery collnQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppCollectionStatus"));
                    collnQuery.setRange(new Range(0, 1));
                    collnQuery.addSortColumn(new SortColumn(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), (boolean)Boolean.TRUE));
                    final Column collnIDColumn = Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID");
                    final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 4, 3, 1, 2, 6, 7, 11, 10 }, 8);
                    collnQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "*"));
                    while (appColln.hasNext()) {
                        final Long collnId2 = Long.valueOf(appColln.next().toString());
                        collnQuery.setCriteria(statusCriteria.and(new Criteria(collnIDColumn, (Object)collnId2, 0)));
                        final DataObject dobj = MDMUtil.getPersistence().get(collnQuery);
                        if (dobj.size("BlacklistAppCollectionStatus") <= 0) {
                            removedColln.add(collnId2);
                        }
                    }
                }
                if (status != 5 && status != 9 && status != 8) {
                    final List collnList = MDMUtil.getInstance().deepCloneList(collectionList);
                    collnList.removeAll(blacklistRemovedColln);
                    Criteria collnCriteria2 = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collnList.toArray(), 8);
                    collnCriteria2 = collnCriteria2.and(new Criteria(Column.getColumn("BlacklistAppToCollection", "APPLIED_STATUS"), (Object)Boolean.FALSE, 0));
                    final List cllnlist2 = DBUtil.getDistinctColumnValue("BlacklistAppToCollection", "COLLECTION_ID", collnCriteria2);
                    appliedColln.addAll(cllnlist2);
                }
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BlacklistAppToCollection");
                if (removedColln.size() > 0) {
                    updateQuery.setUpdateColumn("APPLIED_STATUS", (Object)Boolean.FALSE);
                    updateQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)removedColln.toArray(), 8));
                    MDMUtil.getPersistence().update(updateQuery);
                }
                if (appliedColln.size() > 0) {
                    updateQuery.setUpdateColumn("APPLIED_STATUS", (Object)Boolean.TRUE);
                    updateQuery.setCriteria(new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)appliedColln.toArray(), 8));
                    MDMUtil.getPersistence().update(updateQuery);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception occurred while updating app status", e);
            }
        }
        this.logger.log(Level.INFO, "Updated blacklist app status resource List : {0}, CollectionList : {1}, status : {2}", new Object[] { resourceList, collectionList, status });
        return updatedList;
    }
    
    protected Boolean sendBlacklistMailToUserIfApplicable() throws Exception {
        Boolean uninstall = false;
        final Long customerID = ((Hashtable<K, Long>)this.associationParams).get("customerId");
        final JSONObject jsonObject = new BlacklistAppHandler().getBlackListAppSettings(customerID);
        final Long notificationType = Long.valueOf(jsonObject.get("BLACKLIST_ACTION_TYPE").toString());
        if (notificationType != 0L) {
            final Properties properties = new Properties();
            ((Hashtable<String, List>)properties).put("resourceList", this.resourceList);
            ((Hashtable<String, Object>)properties).put("profileCollectionMap", ((Hashtable<K, Object>)this.associationParams).get("profileCollectionMap"));
            new BlacklistMailUtils().initiateBlacklistingMailOnAction(customerID);
            this.logger.log(Level.INFO, "black list mail notification initiaed for the following : resourceList {0}", this.resourceList);
        }
        if (((long)notificationType & 0x2L) != 0x0L) {
            uninstall = true;
        }
        return uninstall;
    }
    
    @Override
    public void processDOData(final DataObject dataObject, final HashMap params) throws Exception {
    }
    
    @Override
    public int processDSData(final DMDataSetWrapper dataSet, final HashMap params) throws Exception {
        this.logger.log(Level.INFO, "batch procesing in BaseBlacklistapphandler");
        int recordsRead = 0;
        while (dataSet.next()) {
            ++recordsRead;
            final Integer operation = params.get("operation");
            if (operation == 1) {
                final int platform = (int)dataSet.getValue("PLATFORM_TYPE");
                final Long collectionID = (Long)dataSet.getValue("COLLECTION_ID");
                final Long profileID = (Long)dataSet.getValue("PROFILE_ID");
                HashMap platformProfileCollectionMap = params.get("ProfileCollection" + platform);
                if (platformProfileCollectionMap == null) {
                    platformProfileCollectionMap = new HashMap();
                }
                platformProfileCollectionMap.put(profileID, collectionID);
                params.put("ProfileCollection" + platform, platformProfileCollectionMap);
            }
            if (operation == 2) {
                final Long resID = (Long)dataSet.getValue("RESOURCE_ID");
                final Integer platform2 = (Integer)dataSet.getValue("PLATFORM_TYPE");
                List platformResList = params.get("ResourceList" + platform2);
                if (platformResList == null) {
                    platformResList = new ArrayList();
                }
                if (!platformResList.contains(resID)) {
                    platformResList.add(resID);
                }
                params.put("ResourceList" + platform2, platformResList);
            }
        }
        return recordsRead;
    }
    
    public static class APIConstants
    {
        public static final String Inventory_App_Path_Param = "app_id";
        public static final String Inventory_App_ID_Authorizer = "com.me.mdm.server.apps.blocklist.validators.InventoryAppIDAuthorizer";
    }
}
