package com.me.idps.core.sync.synch;

import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.Properties;
import java.util.Arrays;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.me.idps.core.util.DirectoryUtil;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.IdpsUtil;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.idps.core.util.DirQueue;

public class DirectorySyncConductor extends DirQueue
{
    public long getPartitionFeedId(final DCQueueData qData) {
        long objID = 0L;
        try {
            final JSONObject qNode = (JSONObject)new JSONParser().parse((String)qData.queueData);
            final String taskType = (String)qNode.getOrDefault((Object)"TASK_TYPE", (Object)null);
            if (!IdpsUtil.isStringEmpty(taskType) && taskType.contains("SCHEDULER_ADJUST")) {
                return 0L;
            }
            if (qNode.containsKey((Object)"CUSTOMER_ID")) {
                final Long customerID = Long.valueOf(String.valueOf(qNode.get((Object)"CUSTOMER_ID")));
                if (customerID != null) {
                    objID = customerID;
                    return objID;
                }
            }
            if (qNode.containsKey((Object)"DOMAIN_ID")) {
                final Object domainIDobj = qNode.getOrDefault((Object)"DOMAIN_ID", (Object)null);
                objID = Long.valueOf(String.valueOf(domainIDobj));
                return objID;
            }
        }
        catch (final Exception ex) {
            objID = 0L;
            IDPSlogger.ERR.log(Level.WARNING, "could not get ID", ex);
        }
        return objID;
    }
    
    public boolean isParallelProcessingQueue() {
        return false;
    }
    
    private void clearSyncToken(final JSONObject taskDetails) throws Exception {
        IDPSlogger.SYNC.log(Level.INFO, "clearing sync tokens...");
        final JSONObject tokenDelTaskDetails = DirectoryUtil.getInstance().getNewTaskDetails(taskDetails);
        if (taskDetails.containsKey((Object)"COLLATION_ID")) {
            tokenDelTaskDetails.put((Object)"COLLATION_ID", taskDetails.get((Object)"COLLATION_ID"));
        }
        if (taskDetails.containsKey((Object)"ALL_DONE")) {
            tokenDelTaskDetails.put((Object)"ALL_DONE", taskDetails.get((Object)"ALL_DONE"));
        }
        if (taskDetails.containsKey((Object)"DirectoryMetrics")) {
            tokenDelTaskDetails.put((Object)"DirectoryMetrics", taskDetails.get((Object)"DirectoryMetrics"));
        }
        if (taskDetails.containsKey((Object)"SOURCE")) {
            tokenDelTaskDetails.put((Object)"SOURCE", taskDetails.get((Object)"SOURCE"));
        }
        DirectorySequenceAsynchImpl.getInstance().clearSuspendedSyncTokens(tokenDelTaskDetails);
    }
    
    private void handleBasedOnTokenStatus(final JSONObject qData) throws Exception {
        final Long[] totalSucceededSyncTokens = DirectorySequnceSynchImpl.getInstance().getSyncTokens(921);
        if (totalSucceededSyncTokens != null && totalSucceededSyncTokens.length > 0) {
            IDPSlogger.SYNC.log(Level.INFO, "more sync tokens left to process : {0} in SUCCEEDED state", new Object[] { Arrays.toString(totalSucceededSyncTokens) });
            qData.put((Object)"TASK_TYPE", (Object)"processData");
            DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", null, qData);
        }
        final Long[] inProgressSyncTokens = DirectorySequnceSynchImpl.getInstance().getSyncTokens(941);
        if (inProgressSyncTokens != null && inProgressSyncTokens.length > 0) {
            IDPSlogger.SYNC.log(Level.INFO, "more sync tokens left to process : {0} in IN_PROGRESS state", new Object[] { Arrays.toString(inProgressSyncTokens) });
        }
    }
    
    public void processDirTask(final String taskType, final String dmDomainName, final Long customerID, final Long dmDomainID, final Integer dmDomainClient, final JSONObject qData) throws Exception {
        switch (taskType) {
            case "SYNC_NOW": {
                if (DirectorySequenceAsynchImpl.getInstance().checkDomainSyncReady(dmDomainID, dmDomainName, false)) {
                    Boolean doFullSync = Boolean.valueOf(String.valueOf(qData.get((Object)"doFullSync")));
                    doFullSync = DirectoryHealthChecker.getInstance().checkHealthAndQualifyIfFullSyncRequired(dmDomainID, dmDomainName, customerID, dmDomainClient, doFullSync);
                    IDPSlogger.AUDIT.log(Level.INFO, "AD domain :{0},{1},{2},{3} is Full Sync {4}", new Object[] { dmDomainName, String.valueOf(customerID), dmDomainClient, String.valueOf(dmDomainID), doFullSync });
                    qData.put((Object)"doFullSync", (Object)(boolean)doFullSync);
                    final Integer syncType = doFullSync ? 1 : 2;
                    this.clearSyncToken(qData);
                    final Long syncTokenID = DirectorySequnceSynchImpl.getInstance().getNewSyncToken(dmDomainID, dmDomainName, syncType, (String)qData.get((Object)"POST_SYNC_DETAILS"));
                    qData.put((Object)"SYNC_TOKEN_ID", (Object)syncTokenID);
                    DirectoryUtil.getInstance().addTaskToQueue("adRetreiver-task", null, qData);
                    break;
                }
                IDPSlogger.SYNC.log(Level.INFO, "AD domain :{0}  has a sync already ongoing", new Object[] { dmDomainName });
                break;
            }
            case "updateTally": {
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "FETCH_STATUS", 941);
                DirectorySequnceSynchImpl.getInstance().updateReceivedCount();
                final Long syncTokenID2 = Long.valueOf(String.valueOf(qData.get((Object)"SYNC_TOKEN_ID")));
                Long previousAllocationEndedAt = (Long)DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "ALLOCATION_ENDED_AT");
                if (previousAllocationEndedAt == null || previousAllocationEndedAt == 0L) {
                    previousAllocationEndedAt = System.currentTimeMillis();
                }
                else {
                    previousAllocationEndedAt = Math.max(previousAllocationEndedAt, System.currentTimeMillis());
                }
                ++previousAllocationEndedAt;
                final Long batchTimeStampRange = Long.valueOf(String.valueOf(qData.get((Object)"PREVIOUS_BATCH_COMPLETED_AT")));
                final Long batchExpectedToCompleteAt = previousAllocationEndedAt + batchTimeStampRange;
                DMDomainSyncDetailsDataHandler.getInstance().addOrUpdateADDomainSyncDetails(dmDomainID, "ALLOCATION_ENDED_AT", batchExpectedToCompleteAt);
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put((Object)"CURRENT_BATCH_POSTED_AT", qData.get((Object)"CURRENT_BATCH_POSTED_AT"));
                jsonObject.put((Object)"PRE_PROCESSED_COUNT", (Object)Integer.valueOf(String.valueOf(qData.get((Object)"PRE_PROCESSED_COUNT"))));
                DirectorySequnceSynchImpl.getInstance().setValue(dmDomainID, syncTokenID2, jsonObject);
                DirectoryUtil.getInstance().updateFileIOstats(dmDomainID, syncTokenID2, qData);
                final JSONObject taskDetails = DirectoryUtil.getInstance().getNewTaskDetails(qData);
                taskDetails.put((Object)"SYNC_TOKEN_ID", (Object)syncTokenID2);
                taskDetails.put((Object)"time_stamp_end", (Object)String.valueOf(batchExpectedToCompleteAt));
                taskDetails.put((Object)"time_stamp_start", (Object)String.valueOf(previousAllocationEndedAt));
                taskDetails.put((Object)"validatorAllocationPath", qData.get((Object)"validatorAllocationPath"));
                taskDetails.put((Object)"TASK_TYPE", (Object)"tempInject");
                taskDetails.put((Object)"FILE_PATHS", qData.get((Object)"FILE_PATHS"));
                taskDetails.put((Object)"OBJECT_TYPE", (Object)Integer.valueOf(String.valueOf(qData.get((Object)"OBJECT_TYPE"))));
                taskDetails.put((Object)"PRE_PROCESSED_COUNT", (Object)Integer.valueOf(String.valueOf(qData.get((Object)"PRE_PROCESSED_COUNT"))));
                DirectoryUtil.getInstance().addTaskToQueue("adTemp-task", null, taskDetails);
                break;
            }
            case "tempInjected": {
                final JSONObject syncDetails = new JSONObject();
                syncDetails.put((Object)"LATEST_BATCH_PROCESSED_AT", qData.get((Object)"LATEST_BATCH_PROCESSED_AT"));
                final Long syncTokenID3 = Long.valueOf(String.valueOf(qData.get((Object)"SYNC_TOKEN_ID")));
                DirectorySequnceSynchImpl.getInstance().setValue(dmDomainID, syncTokenID3, syncDetails);
                break;
            }
            case "collate": {
                final Long collateRequestID = Long.valueOf(String.valueOf(qData.get((Object)"COLLATE_REQUEST_ID")));
                DirectoryCollationHandler.getInstance().collate(qData, dmDomainID, collateRequestID);
                break;
            }
            case "coreSyncEngineCompleted": {
                final JSONObject respQdata = DirSuccessHandler.getInstance().handleSuccess(dmDomainName, customerID, dmDomainID, dmDomainClient, qData);
                this.clearSyncToken(respQdata);
                break;
            }
            case "ALL_DONE": {
                DirectoryUtil.getInstance().addTaskToQueue("adEvent-task", null, qData);
                this.handleBasedOnTokenStatus(qData);
                break;
            }
            case "INCREMENT_METRICS": {
                DirectoryMetricsDataHandler.getInstance().increment(qData);
                break;
            }
            case "UPDATE_METRICS": {
                DirectoryMetricsDataHandler.getInstance().updateDirTrackingDetails(customerID);
                break;
            }
            case "SCHEDULER_ADJUST": {
                final String schedulerName = (String)qData.get((Object)"NAME");
                final String loggerName = (String)qData.get((Object)"AD_DOMAIN_NAME");
                final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                dirProdImplRequest.eventType = IdpEventConstants.SCHEDULER_SPREAD_ADJUST;
                dirProdImplRequest.args = new Object[] { customerID, schedulerName, loggerName, false, -1L, false };
                DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
            }
            case "END_STATE": {
                JSONObject opsDetails = null;
                if (qData.containsKey((Object)"DirectoryMetrics")) {
                    opsDetails = (JSONObject)qData.get((Object)"DirectoryMetrics");
                }
                if (opsDetails == null) {
                    opsDetails = new JSONObject();
                }
                opsDetails.put((Object)"NAME", (Object)dmDomainName);
                opsDetails.put((Object)"DOMAIN_ID", (Object)dmDomainID);
                DirectoryUtil.getInstance().addTaskToQueue("adCoreDB-task", null, qData);
                this.handleBasedOnTokenStatus(qData);
                final String source = String.valueOf(qData.get((Object)"SOURCE"));
                DirectoryEndStatehandler.getInstance().handleEndstate(customerID, dmDomainName + "_" + source, opsDetails);
                break;
            }
        }
        DirectorySequnceSynchImpl.getInstance().printDirectorySyncDetails();
    }
}
