package com.me.ems.summaryserver.summary.sync.utils;

import com.me.ems.summaryserver.summary.util.SummaryAPIRedirectHandler;
import java.util.Properties;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.ems.summaryserver.common.sync.utils.SyncFileAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class SummarySyncStatusUpdater
{
    private static Logger syncStatusLogger;
    private static String sourceClass;
    
    public void postModuleSyncStatus(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "postModuleSyncStatus";
        boolean isStatusUpdated = false;
        try {
            final JSONObject syncStatusJSON = this.getSyncStatusJSON(probeID, moduleID, syncTime);
            isStatusUpdated = this.postSyncStatusToProbeServer(probeID, syncStatusJSON);
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "Exception while postModuleSyncStatus", e);
        }
        SyMLogger.info(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "SyncStatusJSON posted to Probe {0} : {1} ", new Object[] { probeID, isStatusUpdated });
    }
    
    public JSONObject getSyncStatusJSON(final long probeID, final long moduleID, final long syncTime) {
        final String sourceMethod = "getSyncStatusJSON";
        final JSONObject syncStatusJSON = new JSONObject();
        try {
            syncStatusJSON.put("probe_id", probeID);
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String moduleName = syncModuleMetaDAOUtil.getModuleName(moduleID);
            syncStatusJSON.put("sync_module_name", (Object)moduleName);
            syncStatusJSON.put("sync_time", syncTime);
            final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
            final Row moduleAuditRow = syncModuleAuditDAOUtil.getSyncModuleAuditRow(probeID, moduleID, syncTime);
            if (moduleAuditRow != null) {
                final Long moduleAuditID = (Long)moduleAuditRow.get("MODULE_AUDIT_ID");
                final int filesSentFromProbe = (int)moduleAuditRow.get("FILES_POSTED_FROM_PROBE");
                final int filesProcessedInSummary = (int)moduleAuditRow.get("FILES_PROCESSED_IN_SUMMARY");
                final int moduleSyncStatusDB = (int)moduleAuditRow.get("MODULE_SYNC_STATUS");
                syncStatusJSON.put("files_posted_from_probe_count", filesSentFromProbe);
                int totalFileCount = 0;
                int successFileCount = 0;
                int inProgressFileCount = 0;
                int failedFileCount = 0;
                final JSONObject fileDetailsJSON = new JSONObject();
                final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
                final DataObject fileAuditDO = syncFileAuditDAOUtil.getSyncFileAuditDO(moduleAuditID);
                if (fileAuditDO != null && !fileAuditDO.isEmpty()) {
                    final Iterator fileAuditRows = fileAuditDO.getRows("SyncFileAudit");
                    while (fileAuditRows.hasNext()) {
                        final Row fileAuditRow = fileAuditRows.next();
                        final String fileName = (String)fileAuditRow.get("FILE_NAME");
                        final JSONObject fileStatusJSON = new JSONObject();
                        final int fileStatus = (int)fileAuditRow.get("FILE_STATUS");
                        final int retryCount = (int)fileAuditRow.get("RETRY_COUNT");
                        if (fileStatus == 950100) {
                            ++successFileCount;
                        }
                        else if (fileStatus == 950101) {
                            ++inProgressFileCount;
                        }
                        else {
                            ++failedFileCount;
                        }
                        fileStatusJSON.put("file_status", fileStatus);
                        fileStatusJSON.put("retry_count", retryCount);
                        ++totalFileCount;
                        fileDetailsJSON.put(fileName, (Object)fileStatusJSON);
                    }
                }
                int moduleSyncStatus;
                if (inProgressFileCount > 0) {
                    moduleSyncStatus = 2;
                }
                else if (failedFileCount > 0) {
                    moduleSyncStatus = 4;
                }
                else {
                    moduleSyncStatus = 1;
                }
                if (moduleSyncStatusDB != moduleSyncStatus) {
                    syncModuleAuditDAOUtil.updateModuleSyncStatus(moduleAuditID, moduleSyncStatus);
                }
                if (filesProcessedInSummary != totalFileCount) {
                    syncModuleAuditDAOUtil.updateFilesProcessedInSummary(moduleAuditID, totalFileCount);
                }
                syncStatusJSON.put("file_details", (Object)fileDetailsJSON);
                syncStatusJSON.put("files_processed_in_summary_count", totalFileCount);
                syncStatusJSON.put("module_sync_status", moduleSyncStatus);
                SyMLogger.info(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "SyncStatusJSON : {0}", syncStatusJSON);
            }
            else {
                SyMLogger.info(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "Audit Data not found! for Probe ID : {0}, moduleID : {1}, syncTime : {2}", new Object[] { probeID, moduleID, syncTime });
            }
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "Caught exception while getSyncStatusJSON", e);
        }
        return syncStatusJSON;
    }
    
    private boolean postSyncStatusToProbeServer(final long probeID, final JSONObject syncStatusJSON) {
        final String sourceMethod = "postSyncStatusToProbeServer";
        boolean isPosted = true;
        try {
            final String actionURL = "emsapi/probe/sync/updateSyncStatus";
            final String contentType = "application/probeUpdateSyncStatus.v1+json";
            final String accept = "application/probeUpdateSyncStatusResult.v1+json";
            final String methodType = "POST";
            final Properties apiProperties = new Properties();
            apiProperties.setProperty("url", actionURL);
            apiProperties.setProperty("content-type", contentType);
            apiProperties.setProperty("requestMethod", methodType);
            apiProperties.setProperty("accept", accept);
            final SummaryAPIRedirectHandler summaryAPIRedirectHandler = new SummaryAPIRedirectHandler();
            summaryAPIRedirectHandler.doAPICall(probeID, apiProperties, syncStatusJSON);
        }
        catch (final Exception e) {
            SyMLogger.error(SummarySyncStatusUpdater.syncStatusLogger, SummarySyncStatusUpdater.sourceClass, sourceMethod, "Caught exception while postSyncStatusToProbeServer", e);
            isPosted = false;
        }
        return isPosted;
    }
    
    static {
        SummarySyncStatusUpdater.syncStatusLogger = Logger.getLogger("ProbeSyncStatusLogger");
        SummarySyncStatusUpdater.sourceClass = "SummarySyncStatusUpdater";
    }
}
