package com.me.idps.core.sync.asynch;

import java.util.Hashtable;
import com.me.idps.core.util.DirectoryUtil;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DataPoster;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.DirectorySequenceUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.api.ADSyncAPI;

public class DirectoryDataReceiver implements ADSyncAPI
{
    public void proccessFetchedADData(final Long syncTokenID, final ArrayList<Properties> directoryData, final String netBIOSName, final Long customerID, final int nResType, final int nObjectCount, final int nStartIndex, final int nEndIndex, final boolean isFirstList, final boolean isLastList) throws Exception {
        this.proccessFetchedADData(directoryData, netBIOSName, customerID, nResType, nObjectCount, nStartIndex, nEndIndex, isFirstList, isLastList);
    }
    
    public void proccessFetchedADData(final ArrayList<Properties> directoryData, String domainName, Long customerID, final int resType, final int nObjectCount, final int nStartIndex, final int nEndIndex, final boolean isFirstList, final boolean isLastList) throws Exception {
        final Long currentBatchPostedAt = System.currentTimeMillis();
        Long dmDomainID = DirectorySyncThreadLocal.getDomainID();
        final Long syncTokenID = DirectorySyncThreadLocal.getSyncToken();
        Integer dmDomainClientID = DirectorySyncThreadLocal.getClientID();
        if (!SyMUtil.isStringEmpty(domainName) && customerID != null && (dmDomainID == null || dmDomainClientID == null)) {
            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, dmDomainClientID);
            dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
        }
        else if (dmDomainID != null && (customerID == null || domainName == null || dmDomainClientID == null)) {
            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
            domainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
            customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
            dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
        }
        boolean isSyncTokenValid = false;
        if (syncTokenID == null) {
            IDPSlogger.ERR.log(Level.INFO, "dropping data because {0} sync token for domain {1} of cust {2} is invalid", new Object[] { String.valueOf(syncTokenID), domainName, String.valueOf(customerID) });
            throw new Exception("|InvalidSyncToken|");
        }
        if (dmDomainID == null) {
            final Criteria criteria = DirectorySequenceUtil.getValidSyncTokenForDataPostingCri().and(new Criteria(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"), (Object)syncTokenID, 0));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectorySyncDetails"));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DirectorySyncDetails", "SYNC_TOKEN_ID"));
            final DataObject dobj = SyMUtil.getPersistenceLite().get(selectQuery);
            if (dobj != null && !dobj.isEmpty()) {
                final Row row = dobj.getFirstRow("DirectorySyncDetails");
                dmDomainID = (Long)row.get("DM_DOMAIN_ID");
                if (dmDomainID != null) {
                    final Properties dmDomainProps2 = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
                    domainName = ((Hashtable<K, String>)dmDomainProps2).get("NAME");
                    customerID = ((Hashtable<K, Long>)dmDomainProps2).get("CUSTOMER_ID");
                    dmDomainClientID = ((Hashtable<K, Integer>)dmDomainProps2).get("CLIENT_ID");
                    isSyncTokenValid = true;
                }
            }
        }
        if (dmDomainID != null && !isSyncTokenValid) {
            isSyncTokenValid = DirectorySequenceAsynchImpl.getInstance().isSyncTokenValid(dmDomainID, syncTokenID);
        }
        if (!isSyncTokenValid) {
            IDPSlogger.ERR.log(Level.INFO, "dropping data because {0} sync token for domain {1} of cust {2} with domainID {3} is invalid", new Object[] { String.valueOf(syncTokenID), domainName, String.valueOf(customerID), String.valueOf(dmDomainID) });
            throw new Exception("|InvalidSyncToken|");
        }
        if (directoryData == null) {
            IDPSlogger.ERR.log(Level.INFO, "dropping null data of {0} for domain {1} of cust {2}", new Object[] { syncTokenID, domainName, customerID });
            throw new Exception("|NULLData|");
        }
        DirectorySequenceAsynchImpl.getInstance().incrementPostedCount(syncTokenID, directoryData.size());
        final JSONObject data = new JSONObject();
        data.put((Object)"DirResRel", (Object)directoryData);
        final JSONObject taskDetails = new JSONObject();
        taskDetails.put((Object)"DirResRel", (Object)data);
        taskDetails.put((Object)"NAME", (Object)domainName);
        taskDetails.put((Object)"DOMAIN_ID", (Object)dmDomainID);
        taskDetails.put((Object)"CUSTOMER_ID", (Object)customerID);
        taskDetails.put((Object)"CLIENT_ID", (Object)dmDomainClientID);
        taskDetails.put((Object)"SYNC_TOKEN_ID", (Object)syncTokenID);
        taskDetails.put((Object)"DIR_RESOURCE_TYPE", (Object)resType);
        taskDetails.put((Object)"LAST_COUNT", (Object)isLastList);
        taskDetails.put((Object)"TASK_TYPE", (Object)"PRE_SYNC");
        taskDetails.put((Object)"FIRST_COUNT", (Object)isFirstList);
        taskDetails.put((Object)"CURRENT_BATCH_POSTED_AT", (Object)currentBatchPostedAt);
        new DataPosterImpl(taskDetails).run();
    }
    
    private class DataPosterImpl extends DataPoster
    {
        protected DataPosterImpl(final JSONObject taskDetails) {
            super(taskDetails);
        }
        
        @Override
        protected byte[] handlePre() {
            final JSONObject data = (JSONObject)this.taskDetails.get((Object)"DirResRel");
            this.taskDetails.remove((Object)"DirResRel");
            return data.toString().getBytes();
        }
        
        @Override
        protected void handlePost(final String dataFilePath, final int timeTaken, final int bytes) throws Exception {
            final JSONArray writeSize = new JSONArray();
            writeSize.add((Object)bytes);
            final JSONArray writeTimeTaken = new JSONArray();
            writeTimeTaken.add((Object)timeTaken);
            this.taskDetails.put((Object)"DirResRel", (Object)dataFilePath);
            this.taskDetails.put((Object)"FILE_WRITE_SIZE", (Object)writeSize);
            this.taskDetails.put((Object)"FILE_WRITE_TIME_TAKEN", (Object)writeTimeTaken);
            DirectoryUtil.getInstance().addTaskToQueue("adAsync-task", null, this.taskDetails);
        }
    }
}
