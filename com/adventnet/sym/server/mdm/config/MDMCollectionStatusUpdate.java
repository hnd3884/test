package com.adventnet.sym.server.mdm.config;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.uem.queue.ModernMgmtOperationData;
import com.me.mdm.uem.queue.ModernMgmtQueueOperation;
import com.me.mdm.uem.queue.ModernMgmtCollectionStatusUpdateData;
import com.me.mdm.uem.ModernCollectionUtil;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class MDMCollectionStatusUpdate
{
    public Logger logger;
    private static MDMCollectionStatusUpdate mdmCollectionStatusUpdate;
    
    public MDMCollectionStatusUpdate() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static MDMCollectionStatusUpdate getInstance() {
        if (MDMCollectionStatusUpdate.mdmCollectionStatusUpdate == null) {
            MDMCollectionStatusUpdate.mdmCollectionStatusUpdate = new MDMCollectionStatusUpdate();
        }
        return MDMCollectionStatusUpdate.mdmCollectionStatusUpdate;
    }
    
    public DataObject getCollnToResourceDO(final Long collectionId, final Long resourceId) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("CollnToResources"));
        query.addSelectColumn(new Column("CollnToResources", "*"));
        final Criteria criteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0, false);
        final Criteria criteria2 = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0, false);
        query.setCriteria(criteria.and(criteria2));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public void updateMdmConfigStatusForAppIds(final JSONArray appDetailsList, final Long resourceID) throws QueryConstructionException, DataAccessException, JSONException {
        DataObject collectionDO = null;
        Integer errorCode = null;
        try {
            this.logger.log(Level.INFO, "Inside: updateMdmConfigStatusForAppIds resID = {0}, appDetailsList ={1}", new Object[] { resourceID, appDetailsList });
            final HashMap appDetailsMap = this.getAppDetailsMap(appDetailsList);
            final List appIDList = new ArrayList();
            appIDList.addAll(appDetailsMap.keySet());
            final SelectQuery collnQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceID, 0);
            final int chunkSize = 250;
            final List<List> appIDSplitList = MDMUtil.getInstance().splitListIntoSubLists(appIDList, chunkSize);
            for (final List tempAppIDList : appIDSplitList) {
                final Criteria appsCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)tempAppIDList.toArray(), 8);
                Criteria collnToResJoinCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("CollnToResources", "COLLECTION_ID"), 0);
                collnToResJoinCriteria = collnToResJoinCriteria.and(appsCriteria).and(resourceCriteria);
                collnQuery.addJoin(new Join("MdAppToCollection", "CollnToResources", collnToResJoinCriteria, 2));
                collnQuery.addJoin(new Join("CollnToResources", "MDMCollnToResErrorCode", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 1));
                collnQuery.addSelectColumn(Column.getColumn("CollnToResources", "*"));
                collnQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
                collnQuery.addSelectColumn(Column.getColumn("MDMCollnToResErrorCode", "*"));
                collnQuery.setCriteria(resourceCriteria.and(appsCriteria));
                collectionDO = MDMUtil.getPersistence().get(collnQuery);
                for (final Long appId : tempAppIDList) {
                    final HashMap appMap = appDetailsMap.get(appId);
                    final int status = appMap.get("STATUS");
                    final String remarks = appMap.get("REMARKS");
                    errorCode = appMap.get("ERROR_CODE");
                    final Criteria appIdCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)appId, 0);
                    final Row collnToResRow = collectionDO.getRow("CollnToResources", appIdCriteria, new Join("CollnToResources", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
                    if (collnToResRow != null) {
                        collnToResRow.set("STATUS", (Object)status);
                        collnToResRow.set("REMARKS", (Object)remarks);
                        collnToResRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                        collnToResRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                        collectionDO.updateRow(collnToResRow);
                        this.logger.log(Level.INFO, "CollnToResDetails is updated for app {0} to collection {1} of resource {2}", new Object[] { appId, collnToResRow.get("COLLECTION_ID"), resourceID });
                        this.updateCollnToResErrorCodeDo((Long)collnToResRow.get("RESOURCE_ID"), (Long)collnToResRow.get("COLLECTION_ID"), errorCode, collectionDO);
                    }
                }
            }
            if (!collectionDO.isEmpty()) {
                MDMUtil.getPersistence().update(collectionDO);
            }
        }
        catch (final Exception excep) {
            this.logger.log(Level.INFO, "Exception in updateMdmConfigStatusForAppIds", excep);
        }
    }
    
    private HashMap getAppDetailsMap(final JSONArray appDetailsArray) {
        final HashMap allAppDetailsMap = new HashMap();
        try {
            for (int i = 0; i < appDetailsArray.length(); ++i) {
                final JSONObject appDetailsJson = (JSONObject)appDetailsArray.get(i);
                final Long appID = (Long)appDetailsJson.get("APP_ID");
                final HashMap appDetail = new HashMap();
                appDetail.put("STATUS", appDetailsJson.get("STATUS"));
                appDetail.put("REMARKS", appDetailsJson.get("REMARKS").toString());
                appDetail.put("ERROR_CODE", appDetailsJson.opt("ERROR_CODE"));
                allAppDetailsMap.put(appID, appDetail);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAppDetailsMap", e);
        }
        return allAppDetailsMap;
    }
    
    public void updateMdmConfigStatus(final Long resourceId, final String collnId, final int status, final String remarks) throws SyMException {
        this.logger.log(Level.INFO, "updateMdmConfigStatus: resourceId-> {0} collnId-> {1} status-> {2}", new Object[] { resourceId, collnId, status });
        try {
            Long collectionID = null;
            if (resourceId != null && collnId != null) {
                try {
                    collectionID = Long.parseLong(collnId);
                }
                catch (final Exception ex) {
                    this.logger.log(Level.INFO, "Unable to update updateMdmConfigStatus() for collectionID {0}", collnId);
                }
                final DataObject collnToResDO = this.getCollnToResourceDO(collectionID, resourceId);
                if (collnToResDO.isEmpty()) {
                    if (this.isCollectionPresent(collectionID)) {
                        this.logger.log(Level.INFO, "Inserting resource {0} and collection {1} in CollnToResources", new Object[] { resourceId, collectionID });
                        final Row collnToResRow = new Row("CollnToResources");
                        collnToResRow.set("COLLECTION_ID", (Object)collectionID);
                        collnToResRow.set("RESOURCE_ID", (Object)resourceId);
                        collnToResRow.set("STATUS", (Object)new Integer(status));
                        collnToResRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                        collnToResRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                        if (remarks != null && remarks.trim().length() > 0) {
                            collnToResRow.set("REMARKS", (Object)remarks);
                            collnToResRow.set("REMARKS_EN", (Object)remarks);
                        }
                        else {
                            collnToResRow.set("REMARKS", (Object)"--");
                            collnToResRow.set("REMARKS_EN", (Object)"--");
                        }
                        collnToResDO.addRow(collnToResRow);
                        try {
                            MDMUtil.getPersistence().add(collnToResDO);
                        }
                        catch (final DataAccessException e) {
                            this.logger.log(Level.WARNING, "Issue may be due to overlapping thread. Please check if the collection/resource is mapped already", (Throwable)e);
                        }
                    }
                }
                else {
                    final Row collnToResRow = collnToResDO.getFirstRow("CollnToResources");
                    collnToResRow.set("COLLECTION_ID", (Object)collectionID);
                    collnToResRow.set("RESOURCE_ID", (Object)resourceId);
                    collnToResRow.set("STATUS", (Object)new Integer(status));
                    collnToResRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                    collnToResRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                    if (remarks != null && remarks.trim().length() > 0) {
                        collnToResRow.set("REMARKS", (Object)remarks);
                        collnToResRow.set("REMARKS_EN", (Object)remarks);
                    }
                    else {
                        collnToResRow.set("REMARKS", (Object)"--");
                        collnToResRow.set("REMARKS_EN", (Object)"--");
                    }
                    collnToResDO.updateRow(collnToResRow);
                    MDMUtil.getPersistence().update(collnToResDO);
                    if (ModernCollectionUtil.isModernCollection(collectionID)) {
                        new ModernMgmtQueueOperation(2, new ModernMgmtCollectionStatusUpdateData(collectionID, resourceId, remarks, status)).addToModernMgmtOperationQueue();
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "Unable to update updateMdmConfigStatus() as collectionID or resourceID is null , collnID : {0} resourceID{1}", new Object[] { collnId, resourceId });
            }
        }
        catch (final Exception excep) {
            throw new SyMException(1001, (Throwable)excep);
        }
    }
    
    public void clearCollnErrorCode(final Long resourceId, final Long collectionId) throws DataAccessException {
        this.logger.log(Level.INFO, "clearing error code : resourceId {0} and collectionId {1} ", new Object[] { resourceId, collectionId });
        final DeleteQuery dQuery = (DeleteQuery)new DeleteQueryImpl("MDMCollnToResErrorCode");
        final Criteria collnCriteria = new Criteria(Column.getColumn("MDMCollnToResErrorCode", "COLLECTION_ID"), (Object)collectionId, 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceId, 0);
        dQuery.setCriteria(collnCriteria.and(resourceCriteria));
        MDMUtil.getPersistence().delete(dQuery);
    }
    
    public void updateGroupProfileCollectionStatus(final Long groupId, final Long collectionId, final int status, final String remarks) throws SyMException {
        this.logger.log(Level.INFO, "updateGroupProfileCollectionStatus: groupId {0} and collectionId {1} ", new Object[] { groupId, collectionId });
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("GroupToProfileHistory"));
            query.addSelectColumn(new Column("GroupToProfileHistory", "*"));
            final Criteria criteria = new Criteria(new Column("GroupToProfileHistory", "GROUP_ID"), (Object)groupId, 0);
            final Criteria criteria2 = new Criteria(new Column("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria cri = criteria.and(criteria2);
            query.setCriteria(cri);
            final DataObject groupProfileHistoryDO = MDMUtil.getPersistence().get((SelectQuery)query);
            Row groupProfileHistoryRow = null;
            if (!groupProfileHistoryDO.isEmpty()) {
                groupProfileHistoryRow = groupProfileHistoryDO.getRow("GroupToProfileHistory");
                groupProfileHistoryRow.set("GROUP_ID", (Object)groupId);
                groupProfileHistoryRow.set("COLLECTION_ID", (Object)collectionId);
                groupProfileHistoryRow.set("COLLECTION_STATUS", (Object)status);
                groupProfileHistoryRow.set("REMARKS", (Object)remarks);
                groupProfileHistoryDO.updateRow(groupProfileHistoryRow);
                MDMUtil.getPersistence().update(groupProfileHistoryDO);
            }
        }
        catch (final Exception ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    private void updateCollnToResErrorCodeDo(final Long resourceId, final Long collectionId, final Integer errorCode, final DataObject collectionDO) {
        try {
            final Criteria cRes = new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria collnCri = new Criteria(new Column("MDMCollnToResErrorCode", "COLLECTION_ID"), (Object)collectionId, 0);
            Row collnToResErrorCodeRow = null;
            if (collectionDO.containsTable("MDMCollnToResErrorCode")) {
                collnToResErrorCodeRow = collectionDO.getRow("MDMCollnToResErrorCode", cRes.and(collnCri));
            }
            if (collnToResErrorCodeRow == null && errorCode != null) {
                final Row errorRow = new Row("MDMCollnToResErrorCode");
                errorRow.set("RESOURCE_ID", (Object)resourceId);
                errorRow.set("COLLECTION_ID", (Object)collectionId);
                errorRow.set("ERROR_CODE", (Object)errorCode);
                collectionDO.addRow(errorRow);
            }
            else if (errorCode != null) {
                collnToResErrorCodeRow.set("ERROR_CODE", (Object)errorCode);
                collectionDO.updateRow(collnToResErrorCodeRow);
            }
            else if (collnToResErrorCodeRow != null) {
                collectionDO.deleteRow(collnToResErrorCodeRow);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updateCollnToResErrorCodeDo {0}", ex);
        }
    }
    
    public void updateCollnToResErrorCode(final Long resourceId, final Long collectionId, final Integer errorCode) {
        final List resourceIdList = new ArrayList();
        resourceIdList.add(resourceId);
        this.updateCollnToResListErrorCode(resourceIdList, collectionId, errorCode);
    }
    
    @Deprecated
    public void updateCollnToResListErrorCode(final List resourceIdList, final Long collectionId, final Integer errorCode) {
        final Criteria cResList = new Criteria(new Column("MDMCollnToResErrorCode", "RESOURCE_ID"), (Object)resourceIdList.toArray(), 8);
        final Criteria cColl = new Criteria(new Column("MDMCollnToResErrorCode", "COLLECTION_ID"), (Object)collectionId, 0);
        try {
            final DataObject collectionDO = MDMUtil.getPersistence().get("MDMCollnToResErrorCode", cResList.and(cColl));
            for (final Object resourceIdObj : resourceIdList) {
                final Long resourceId = (Long)resourceIdObj;
                this.updateCollnToResErrorCodeDo(resourceId, collectionId, errorCode, collectionDO);
            }
            MDMUtil.getPersistence().update(collectionDO);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public void removeAllCollectionRelForResource(final Long resourceID) {
        try {
            this.logger.log(Level.INFO, "Inside removeAllCollectionRelForResource {0}", resourceID);
            final Column resCol = Column.getColumn("CollnToResources", "RESOURCE_ID");
            final Criteria resCri = new Criteria(resCol, (Object)resourceID, 0);
            DataAccess.delete("CollnToResources", resCri);
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception in removeAllCollectionRelForResource {0}", (Throwable)ex);
        }
    }
    
    public void updateStatusForCollntoRes(final List resourceList, final Long collnId, final int status) throws DataAccessException {
        this.updateCollnToResourcesRow(resourceList, collnId, status, null);
    }
    
    public void updateCollnToResourcesRow(final List resourceList, final Long collnId, final int status, final String remarks) throws DataAccessException {
        if (!resourceList.isEmpty()) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
            final Criteria resourceCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria collectionCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collnId, 0);
            updateQuery.setCriteria(resourceCriteria.and(collectionCriteria));
            updateQuery.setUpdateColumn("STATUS", (Object)status);
            updateQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
            updateQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
            if (remarks != null) {
                updateQuery.setUpdateColumn("REMARKS", (Object)remarks);
                updateQuery.setUpdateColumn("REMARKS_EN", (Object)remarks);
            }
            MDMUtil.getPersistence().update(updateQuery);
            this.updateCollnToResListErrorCode(resourceList, collnId, null);
        }
    }
    
    public void updateStatusForCollntoRes(final List resourceList, final Long collnId, final int status, final String remarks) throws DataAccessException {
        if (!resourceList.isEmpty()) {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
            final Criteria cRes = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria cColln = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collnId, 0);
            final Criteria isStatusChangedCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)status, 1);
            uQuery.setCriteria(cRes.and(cColln).and(isStatusChangedCriteria));
            uQuery.setUpdateColumn("STATUS", (Object)status);
            uQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
            uQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
            if (remarks != null) {
                uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            }
            MDMUtil.getPersistence().update(uQuery);
            this.updateCollnToResListErrorCode(resourceList, collnId, null);
        }
    }
    
    public void updateStatusForCollntoRes(final List resList, final List collnList, final int status, final String remarks) throws DataAccessException {
        if (resList != null && !resList.isEmpty() && collnList != null && !collnList.isEmpty()) {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("CollnToResources");
            final Criteria cRes = new Criteria(new Column("CollnToResources", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            final Criteria cColln = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collnList.toArray(), 8);
            final Criteria isStatusChangedCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)status, 1);
            uQuery.setCriteria(cRes.and(cColln).and(isStatusChangedCriteria));
            uQuery.setUpdateColumn("STATUS", (Object)status);
            uQuery.setUpdateColumn("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
            uQuery.setUpdateColumn("APPLIED_TIME", (Object)System.currentTimeMillis());
            if (remarks != null) {
                uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            }
            MDMUtil.getPersistence().update(uQuery);
        }
    }
    
    public Boolean isCollectionPresent(final Long collectionId) {
        Row row = null;
        try {
            row = DBUtil.getRowFromDB("Collection", "COLLECTION_ID", (Object)collectionId);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isCollectionPresent method", ex);
        }
        Boolean retVal = Boolean.TRUE;
        if (row == null) {
            retVal = Boolean.FALSE;
        }
        return retVal;
    }
    
    public void updateManagedUserProfileCollectionStatus(final Long mUserId, final Long collectionId, final int status, final String remarks) throws SyMException {
        this.logger.log(Level.INFO, "updateGroupProfileCollectionStatus: managedUserId {0} and collectionId {1} ", new Object[] { mUserId, collectionId });
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("MDMResourceToProfileHistory"));
            query.addSelectColumn(new Column("MDMResourceToProfileHistory", "*"));
            final Criteria criteria = new Criteria(new Column("MDMResourceToProfileHistory", "RESOURCE_ID"), (Object)mUserId, 0);
            final Criteria criteria2 = new Criteria(new Column("MDMResourceToProfileHistory", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria cri = criteria.and(criteria2);
            query.setCriteria(cri);
            final DataObject groupProfileHistoryDO = MDMUtil.getPersistence().get((SelectQuery)query);
            Row groupProfileHistoryRow = null;
            if (!groupProfileHistoryDO.isEmpty()) {
                groupProfileHistoryRow = groupProfileHistoryDO.getRow("MDMResourceToProfileHistory");
                groupProfileHistoryRow.set("RESOURCE_ID", (Object)mUserId);
                groupProfileHistoryRow.set("COLLECTION_ID", (Object)collectionId);
                groupProfileHistoryRow.set("COLLECTION_STATUS", (Object)status);
                groupProfileHistoryRow.set("REMARKS", (Object)remarks);
                groupProfileHistoryDO.updateRow(groupProfileHistoryRow);
                MDMUtil.getPersistence().update(groupProfileHistoryDO);
            }
        }
        catch (final Exception ex) {
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public void updateAppStatusToCollnToResources(final HashMap<String, List> remarksToResMap, final Long collectionID, final int collnResStatus) throws DataAccessException {
        final List<String> remarksList = new ArrayList<String>(remarksToResMap.keySet());
        List resourceIdList = new ArrayList();
        final List resourceCriList = new ArrayList();
        String remarks = null;
        for (int i = 0; i < remarksList.size(); ++i) {
            remarks = remarksList.get(i);
            resourceIdList = remarksToResMap.get(remarks);
            resourceCriList.addAll(resourceIdList);
        }
        if (!resourceCriList.isEmpty() && collectionID != null) {
            final Criteria collIdCri = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionID, 0);
            final Criteria resCriForCollnTable = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceCriList.toArray(), 8);
            final Criteria criteria = collIdCri.and(resCriForCollnTable);
            final DataObject collnToResDO = MDMUtil.getPersistence().get("CollnToResources", criteria);
            for (int j = 0; j < remarksList.size(); ++j) {
                remarks = remarksList.get(j);
                resourceIdList = remarksToResMap.get(remarks);
                if (!resourceIdList.isEmpty()) {
                    try {
                        Row collnToResRow = null;
                        for (int k = 0; k < resourceIdList.size(); ++k) {
                            final Long resId = resourceIdList.get(k);
                            final Criteria resCri = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resId, 0);
                            collnToResRow = collnToResDO.getRow("CollnToResources", resCri);
                            if (collnToResRow != null) {
                                collnToResRow.set("STATUS", (Object)collnResStatus);
                                collnToResRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                                collnToResRow.set("AGENT_APPLIED_TIME", (Object)System.currentTimeMillis());
                                if (remarks != null) {
                                    collnToResRow.set("REMARKS", (Object)remarks);
                                    collnToResRow.set("REMARKS_EN", (Object)remarks);
                                }
                                collnToResDO.updateRow(collnToResRow);
                            }
                            else {
                                this.logger.log(Level.WARNING, "Row for COLLNTORESOURCES table for resource id {0} is {1}", new Object[] { resId, null });
                            }
                        }
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.SEVERE, "Exception in updateAppStatusToCollnToResources...", ex);
                    }
                }
            }
            MDMUtil.getPersistence().update(collnToResDO);
            this.updateCollnToResListErrorCode(resourceCriList, collectionID, null);
        }
    }
    
    public void updateStatusForCollntoResDO(final List resList, final List collnList, final int status, final String remarks, final Boolean forceUpdateRemarks) throws DataAccessException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokeinterface java/util/List.isEmpty:()Z
        //     6: ifne            341
        //     9: aload_2         /* collnList */
        //    10: invokeinterface java/util/List.isEmpty:()Z
        //    15: ifne            341
        //    18: invokestatic    java/lang/System.currentTimeMillis:()J
        //    21: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //    24: astore          currentTime
        //    26: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getInstance:()Lcom/adventnet/sym/server/mdm/util/MDMUtil;
        //    29: aload_1         /* resList */
        //    30: sipush          500
        //    33: invokevirtual   com/adventnet/sym/server/mdm/util/MDMUtil.splitListIntoSubLists:(Ljava/util/List;I)Ljava/util/List;
        //    36: astore          resourceSubList
        //    38: aload           resourceSubList
        //    40: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //    45: astore          8
        //    47: aload           8
        //    49: invokeinterface java/util/Iterator.hasNext:()Z
        //    54: ifeq            341
        //    57: aload           8
        //    59: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    64: checkcast       Ljava/util/List;
        //    67: astore          resources
        //    69: new             Lcom/adventnet/ds/query/SelectQueryImpl;
        //    72: dup            
        //    73: ldc             "CollnToResources"
        //    75: invokestatic    com/adventnet/ds/query/Table.getTable:(Ljava/lang/String;)Lcom/adventnet/ds/query/Table;
        //    78: invokespecial   com/adventnet/ds/query/SelectQueryImpl.<init>:(Lcom/adventnet/ds/query/Table;)V
        //    81: astore          selectQuery
        //    83: new             Lcom/adventnet/ds/query/Criteria;
        //    86: dup            
        //    87: new             Lcom/adventnet/ds/query/Column;
        //    90: dup            
        //    91: ldc             "CollnToResources"
        //    93: ldc             "RESOURCE_ID"
        //    95: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //    98: aload           resources
        //   100: invokeinterface java/util/List.toArray:()[Ljava/lang/Object;
        //   105: bipush          8
        //   107: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   110: astore          cRes
        //   112: new             Lcom/adventnet/ds/query/Criteria;
        //   115: dup            
        //   116: new             Lcom/adventnet/ds/query/Column;
        //   119: dup            
        //   120: ldc             "CollnToResources"
        //   122: ldc             "COLLECTION_ID"
        //   124: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   127: aload_2         /* collnList */
        //   128: invokeinterface java/util/List.toArray:()[Ljava/lang/Object;
        //   133: bipush          8
        //   135: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   138: astore          cColln
        //   140: aload           cRes
        //   142: aload           cColln
        //   144: invokevirtual   com/adventnet/ds/query/Criteria.and:(Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/ds/query/Criteria;
        //   147: astore          effectiveCriteria
        //   149: aload           forceUpdateRemarks
        //   151: invokevirtual   java/lang/Boolean.booleanValue:()Z
        //   154: ifne            191
        //   157: new             Lcom/adventnet/ds/query/Criteria;
        //   160: dup            
        //   161: new             Lcom/adventnet/ds/query/Column;
        //   164: dup            
        //   165: ldc             "CollnToResources"
        //   167: ldc             "STATUS"
        //   169: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   172: iload_3         /* status */
        //   173: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   176: iconst_1       
        //   177: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   180: astore          isStatusChangedCriteria
        //   182: aload           effectiveCriteria
        //   184: aload           isStatusChangedCriteria
        //   186: invokevirtual   com/adventnet/ds/query/Criteria.and:(Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/ds/query/Criteria;
        //   189: astore          effectiveCriteria
        //   191: aload           selectQuery
        //   193: aload           effectiveCriteria
        //   195: invokeinterface com/adventnet/ds/query/SelectQuery.setCriteria:(Lcom/adventnet/ds/query/Criteria;)V
        //   200: aload           selectQuery
        //   202: new             Lcom/adventnet/ds/query/Column;
        //   205: dup            
        //   206: aconst_null    
        //   207: ldc             "*"
        //   209: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   212: invokeinterface com/adventnet/ds/query/SelectQuery.addSelectColumn:(Lcom/adventnet/ds/query/Column;)V
        //   217: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //   220: aload           selectQuery
        //   222: invokeinterface com/adventnet/persistence/Persistence.get:(Lcom/adventnet/ds/query/SelectQuery;)Lcom/adventnet/persistence/DataObject;
        //   227: astore          dataObject
        //   229: aload           dataObject
        //   231: invokeinterface com/adventnet/persistence/DataObject.isEmpty:()Z
        //   236: ifne            338
        //   239: aload           dataObject
        //   241: ldc             "CollnToResources"
        //   243: invokeinterface com/adventnet/persistence/DataObject.getRows:(Ljava/lang/String;)Ljava/util/Iterator;
        //   248: astore          iterator
        //   250: aload           iterator
        //   252: invokeinterface java/util/Iterator.hasNext:()Z
        //   257: ifeq            327
        //   260: aload           iterator
        //   262: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   267: checkcast       Lcom/adventnet/persistence/Row;
        //   270: astore          row
        //   272: aload           row
        //   274: ldc             "STATUS"
        //   276: iload_3         /* status */
        //   277: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   280: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   283: aload           row
        //   285: ldc             "AGENT_APPLIED_TIME"
        //   287: aload           currentTime
        //   289: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   292: aload           row
        //   294: ldc             "APPLIED_TIME"
        //   296: aload           currentTime
        //   298: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   301: aload           remarks
        //   303: ifnull          315
        //   306: aload           row
        //   308: ldc             "REMARKS"
        //   310: aload           remarks
        //   312: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   315: aload           dataObject
        //   317: aload           row
        //   319: invokeinterface com/adventnet/persistence/DataObject.updateRow:(Lcom/adventnet/persistence/Row;)V
        //   324: goto            250
        //   327: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //   330: aload           dataObject
        //   332: invokeinterface com/adventnet/persistence/Persistence.update:(Lcom/adventnet/persistence/DataObject;)Lcom/adventnet/persistence/DataObject;
        //   337: pop            
        //   338: goto            47
        //   341: return         
        //    Exceptions:
        //  throws com.adventnet.persistence.DataAccessException
        //    StackMapTable: 00 07 FE 00 2F 07 00 E6 07 00 EA 07 00 ED FF 00 8F 00 0E 07 00 E4 07 00 EA 07 00 EA 01 07 00 EE 07 01 3C 07 00 E6 07 00 EA 07 00 ED 07 00 EA 07 00 EB 07 00 EC 07 00 EC 07 00 EC 00 00 FD 00 3A 07 00 E7 07 00 ED FC 00 40 07 01 03 FA 00 0B FF 00 0A 00 09 07 00 E4 07 00 EA 07 00 EA 01 07 00 EE 07 01 3C 07 00 E6 07 00 EA 07 00 ED 00 00 F8 00 02
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static {
        MDMCollectionStatusUpdate.mdmCollectionStatusUpdate = null;
    }
}
