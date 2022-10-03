package com.me.idps.core.sync.asynch;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.Properties;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.ds.query.GroupByClause;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.IdpsUtil;
import org.json.simple.JSONObject;
import java.sql.Connection;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import com.me.idps.core.util.DirectorySequenceUtil;

public class DirectorySequenceAsynchImpl extends DirectorySequenceUtil
{
    private static DirectorySequenceAsynchImpl directorySequenceAsynchImpl;
    
    public static DirectorySequenceAsynchImpl getInstance() {
        if (DirectorySequenceAsynchImpl.directorySequenceAsynchImpl == null) {
            DirectorySequenceAsynchImpl.directorySequenceAsynchImpl = new DirectorySequenceAsynchImpl();
        }
        return DirectorySequenceAsynchImpl.directorySequenceAsynchImpl;
    }
    
    public boolean checkDomainSyncReady(final Long dmDomainID, final String dmDomainName, final boolean alsoNotQueued) throws Exception {
        return this.checkDomainFetchStatus(dmDomainID, dmDomainName, alsoNotQueued);
    }
    
    private void updateSyncInitatedAt(final Long dmDomainID) {
        try {
            final int curFetchStatus = (int)DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "FETCH_STATUS");
            if (curFetchStatus != 951 && curFetchStatus != 941) {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "LAST_SYNC_INITIATED", System.currentTimeMillis());
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    public void getNewSyncToken(final Long customerID, final Long dmDomainID, final String domainName, final Integer syncType, final String postSyncOPdetails, final String sourceHint) throws Exception {
        IDPSlogger.ASYNCH.log(Level.INFO, "new sync token being generated from {0}", new Object[] { sourceHint });
        DirectoryMetricsDataHandler.getInstance().enQueueIncrementTask(customerID, "WEBHOOK_SYNC", 1);
        this.updateSyncInitatedAt(dmDomainID);
        final Long syncTokenID = this.getNewSyncToken(dmDomainID, domainName, syncType, postSyncOPdetails, true);
        if (!SyMUtil.isStringEmpty(sourceHint)) {
            ApiFactoryProvider.getCacheAccessAPI().putCache("DirectorySyncDetails_SOURCE_" + dmDomainID + "_" + syncTokenID, (Object)sourceHint, 2);
        }
        DirectorySyncThreadLocal.setSyncToken(syncTokenID);
    }
    
    public void suspendSyncTokens(final Connection connection, final JSONObject taskDetails, final Long curDomainID, final Long curCollationID) throws Exception {
        Criteria dirSyncDetailsCri = null;
        if (taskDetails != null) {
            IDPSlogger.DBO.log(Level.INFO, "suspend token details {0}", new Object[] { IdpsUtil.getPrettyJSON(taskDetails) });
            if (taskDetails.containsKey((Object)"COLLATION_ID")) {
                final Long collationID = (Long)taskDetails.get((Object)"COLLATION_ID");
                dirSyncDetailsCri = new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationID, 0);
            }
            Long dmDomainID = null;
            if (taskDetails.containsKey((Object)"DM_DOMAIN_ID")) {
                dmDomainID = (Long)taskDetails.get((Object)"DM_DOMAIN_ID");
            }
            if (taskDetails.containsKey((Object)"DOMAIN_ID")) {
                dmDomainID = (Long)taskDetails.get((Object)"DOMAIN_ID");
            }
            if (dmDomainID != null) {
                final Criteria domainCri = new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
                if (dirSyncDetailsCri != null) {
                    dirSyncDetailsCri = dirSyncDetailsCri.and(domainCri);
                }
                else {
                    dirSyncDetailsCri = domainCri;
                }
            }
        }
        Criteria notSuspendedCri = new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 1);
        notSuspendedCri = ((dirSyncDetailsCri != null) ? dirSyncDetailsCri.and(notSuspendedCri) : notSuspendedCri);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
        updateQuery.setCriteria(notSuspendedCri);
        updateQuery.setUpdateColumn("STATUS_ID", (Object)911);
        if (curDomainID != null && curCollationID != null) {
            DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        }
        else {
            DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
        }
        final Column collIDcol = Column.getColumn("DirectorySyncDetails", "COLLATION_ID");
        final Column dmDomainIDcol = Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        Criteria collRevokeCri = new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)null, 1);
        collRevokeCri = ((dirSyncDetailsCri != null) ? dirSyncDetailsCri.and(collRevokeCri) : collRevokeCri);
        selectQuery.setCriteria(collRevokeCri);
        selectQuery.addSelectColumns((List)new ArrayList(Arrays.asList(dmDomainIDcol, collIDcol)));
        selectQuery.setGroupByClause(new GroupByClause(selectQuery.getSelectColumns()));
        final JSONArray toBeRevokedCollDetails = IdpsUtil.executeSelectQuery(connection, selectQuery);
        if (toBeRevokedCollDetails != null && !toBeRevokedCollDetails.isEmpty()) {
            for (int i = 0; i < toBeRevokedCollDetails.size(); ++i) {
                final JSONObject collIDdetails = (JSONObject)toBeRevokedCollDetails.get(i);
                if (collIDdetails.containsKey((Object)"COLLATION_ID") && collIDdetails.containsKey((Object)"DM_DOMAIN_ID")) {
                    final String collIDstr = String.valueOf(collIDdetails.get((Object)"COLLATION_ID"));
                    final Long dmDomainID2 = Long.valueOf(String.valueOf(collIDdetails.get((Object)"DM_DOMAIN_ID")));
                    if (!SyMUtil.isStringEmpty(collIDstr)) {
                        IDPSlogger.DBO.log(Level.INFO, "{0} {1} collID cur value is {2}", new Object[] { String.valueOf(dmDomainID2), collIDstr, DirectoryUtil.getInstance().canExecQuery(dmDomainID2, Long.valueOf(collIDstr)) });
                        DirectoryUtil.getInstance().updateGlobalCollStatus(dmDomainID2, collIDstr, false);
                        IDPSlogger.DBO.log(Level.INFO, "revoked {0} collID for domain ID {1} ", new Object[] { collIDstr, String.valueOf(dmDomainID2) });
                    }
                }
            }
        }
    }
    
    public void clearSuspendedSyncTokens(JSONObject taskDetails) throws Exception {
        if (taskDetails == null) {
            taskDetails = new JSONObject();
        }
        taskDetails.put((Object)"TASK_TYPE", (Object)"CLEAR_TOKEN");
        DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", null, taskDetails);
    }
    
    public Boolean isSyncTokenValid(final Long dmDomainID, final Long syncTokenID) throws Exception {
        final Properties taskDetails = new Properties();
        ((Hashtable<String, Long>)taskDetails).put("DM_DOMAIN_ID", dmDomainID);
        ((Hashtable<String, Long>)taskDetails).put("SYNC_TOKEN_ID", syncTokenID);
        ((Hashtable<String, String>)taskDetails).put("TASK_TYPE", "validateToken");
        return (Boolean)this.performDirectorySequenceTask("com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl", taskDetails);
    }
    
    public boolean isSyncTokenValid(final Long dmDomainID, final JSONObject qNode) throws Exception {
        Long syncTokenID = null;
        boolean syncTokenValid = true;
        if (qNode.containsKey((Object)"SYNC_TOKEN_ID")) {
            syncTokenID = Long.valueOf(String.valueOf(qNode.get((Object)"SYNC_TOKEN_ID")));
        }
        if (syncTokenID != null && dmDomainID != null) {
            syncTokenValid = getInstance().isSyncTokenValid(dmDomainID, syncTokenID);
        }
        return syncTokenValid;
    }
    
    private void setValue(final Long syncTokenID, final int tokenAttrID, int val) throws Exception {
        final Criteria criteria = new Criteria(Column.getColumn("DirectorySyncAsynchPostDetails", "SYNC_TOKEN_ID"), (Object)syncTokenID, 0).and(new Criteria(Column.getColumn("DirectorySyncAsynchPostDetails", "TOKEN_ATTRIBUTE_ID"), (Object)tokenAttrID, 0));
        DataObject dataObject = SyMUtil.getPersistenceLite().get("DirectorySyncAsynchPostDetails", criteria);
        if (dataObject != null && dataObject.containsTable("DirectorySyncAsynchPostDetails")) {
            final Row row = dataObject.getRow("DirectorySyncAsynchPostDetails");
            final int existingValue = (int)DirectoryUtil.getInstance().extractValue(row, "VALUE", 0);
            if ((tokenAttrID == 5 || tokenAttrID == 6) && existingValue > 0 && val > 0) {
                throw new Exception(tokenAttrID + " is already " + existingValue + " and still again being incremented by !?" + val);
            }
            val += existingValue;
            row.set("VALUE", (Object)val);
            dataObject.updateRow(row);
            SyMUtil.getPersistenceLite().update(dataObject);
        }
        else {
            if ((tokenAttrID == 5 || tokenAttrID == 6) && val > 1) {
                throw new Exception(tokenAttrID + " should take values only 0 or 1, but is being set to " + val);
            }
            final Row row = new Row("DirectorySyncAsynchPostDetails");
            row.set("SYNC_TOKEN_ID", (Object)syncTokenID);
            row.set("TOKEN_ATTRIBUTE_ID", (Object)tokenAttrID);
            row.set("VALUE", (Object)val);
            dataObject = SyMUtil.getPersistenceLite().constructDataObject();
            dataObject.addRow(row);
            SyMUtil.getPersistenceLite().add(dataObject);
        }
    }
    
    void incrementPostedCount(final Long syncTokenID, final int postedCount) throws Exception {
        this.setValue(syncTokenID, 7, postedCount);
    }
    
    void incrementFirstCount(final Long syncTokenID) throws Exception {
        this.setValue(syncTokenID, 5, 1);
    }
    
    void incrementLastCount(final Long syncTokenID) throws Exception {
        this.setValue(syncTokenID, 6, 1);
    }
    
    static {
        DirectorySequenceAsynchImpl.directorySequenceAsynchImpl = null;
    }
}
