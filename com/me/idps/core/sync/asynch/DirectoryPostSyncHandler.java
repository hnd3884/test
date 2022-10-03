package com.me.idps.core.sync.asynch;

import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.util.IdpsJSONutil;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.Properties;
import com.me.idps.core.util.DirectoryUtil;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;

class DirectoryPostSyncHandler
{
    private static DirectoryPostSyncHandler directoryPostSyncHandler;
    
    static DirectoryPostSyncHandler getInstance() {
        if (DirectoryPostSyncHandler.directoryPostSyncHandler == null) {
            DirectoryPostSyncHandler.directoryPostSyncHandler = new DirectoryPostSyncHandler();
        }
        return DirectoryPostSyncHandler.directoryPostSyncHandler;
    }
    
    private List<String> getPostSyncOpDetails(final Long dmDomainID, final Long collationID) throws Exception {
        final List<String> postSyncOPdetailsFilePaths = new ArrayList<String>();
        final Column syncTokenAddedAtcol = Column.getColumn("DirectorySyncDetails", "ADDED_AT");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0).and(new Criteria(Column.getColumn("DirectorySyncDetails", "COLLATION_ID"), (Object)collationID, 0)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "POST_SYNC_DETAILS"), (Object)null, 1)).and(new Criteria(Column.getColumn("DirectorySyncDetails", "STATUS_ID"), (Object)911, 0)));
        selectQuery.addSelectColumn(syncTokenAddedAtcol);
        selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "POST_SYNC_DETAILS"));
        selectQuery.addSortColumn(new SortColumn(syncTokenAddedAtcol, true));
        final DataObject dobj = IdpsUtil.getPersistenceLite().get(selectQuery);
        if (dobj != null && !dobj.isEmpty() && dobj.containsTable("DirectorySyncDetails")) {
            final Iterator itr = dobj.getRows("DirectorySyncDetails");
            while (itr != null && itr.hasNext()) {
                final Row row = itr.next();
                final String filePathVal = (String)row.get("POST_SYNC_DETAILS");
                if (!SyMUtil.isStringEmpty(filePathVal)) {
                    postSyncOPdetailsFilePaths.add(filePathVal);
                }
            }
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectorySyncDetails");
        updateQuery.setCriteria(selectQuery.getCriteria());
        updateQuery.setUpdateColumn("POST_SYNC_DETAILS", (Object)null);
        SyMUtil.getPersistenceLite().update(updateQuery);
        return postSyncOPdetailsFilePaths;
    }
    
    void handlePostSyncOps(final JSONObject taskDetails) throws Exception {
        final Long dmDomainID = Long.valueOf(String.valueOf(taskDetails.get((Object)"DOMAIN_ID")));
        final Long collationID = Long.valueOf(String.valueOf(taskDetails.get((Object)"COLLATION_ID")));
        final List<String> postSyncOPdetails = this.getPostSyncOpDetails(dmDomainID, collationID);
        taskDetails.put((Object)"TASK_TYPE", (Object)"coreSyncEngineCompleted");
        DirectoryUtil.getInstance().addTaskToQueue("adProc-task", null, taskDetails);
        final Integer dmDomainClientID = Integer.valueOf(String.valueOf(taskDetails.get((Object)"CLIENT_ID")));
        final Integer syncType = Integer.valueOf(String.valueOf(taskDetails.get((Object)"SYNC_TYPE")));
        final Boolean isFullSync = syncType == 1;
        IDPSlogger.ASYNCH.log(Level.INFO, "initiating call back for post sync processes to directory service providers");
        if (postSyncOPdetails != null && postSyncOPdetails.size() > 0) {
            for (int i = 0; i < postSyncOPdetails.size(); ++i) {
                try {
                    final String postSyncOPdetailFilePath = postSyncOPdetails.get(i);
                    final JSONObject postSyncOPdetail = (JSONObject)DirectoryUtil.getInstance().readAndDeleteFile(postSyncOPdetailFilePath);
                    IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).postSyncOperations(taskDetails, isFullSync, postSyncOPdetail);
                }
                catch (final Exception ex) {
                    IDPSlogger.ERR.log(Level.SEVERE, "post sync call back failed : ", ex);
                }
            }
        }
        else {
            IdpsFactoryProvider.getIdpsAccessAPI(dmDomainClientID).postSyncOperations(taskDetails, isFullSync, null);
        }
        IDPSlogger.ASYNCH.log(Level.INFO, "directory service impl post sync call completed");
        IDPSlogger.ASYNCH.log(Level.INFO, "initiating product specific handling for post sync");
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.eventType = IdpEventConstants.POST_SYNC_OPS;
        dirProdImplRequest.args = new Object[] { taskDetails, isFullSync, postSyncOPdetails };
        dirProdImplRequest.dmDomainProps = IdpsJSONutil.convertJSONObjectToProperties(taskDetails);
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    static {
        DirectoryPostSyncHandler.directoryPostSyncHandler = null;
    }
}
