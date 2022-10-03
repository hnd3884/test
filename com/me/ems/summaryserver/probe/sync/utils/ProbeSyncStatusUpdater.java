package com.me.ems.summaryserver.probe.sync.utils;

import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.me.ems.summaryserver.common.sync.utils.SyncFileAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.Map;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Collection;
import org.json.JSONArray;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class ProbeSyncStatusUpdater
{
    protected static Logger syncStatusLogger;
    private static String sourceClass;
    
    public boolean updateSyncStatusToSummaryServer(final long probeID, final long moduleID, final long syncTime, final int totalFile, final int postedFile, final List<String> fileNames) {
        final String sourceMethod = "updateSyncStatusToSummaryServer";
        boolean isStatusUpdated = false;
        final JSONObject syncStatusJSON = new JSONObject();
        try {
            syncStatusJSON.put("probe_id", probeID);
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final String moduleName = syncModuleMetaDAOUtil.getModuleName(moduleID);
            syncStatusJSON.put("sync_module_name", (Object)moduleName);
            syncStatusJSON.put("sync_time", syncTime);
            syncStatusJSON.put("total_file_count", totalFile);
            syncStatusJSON.put("files_posted_from_probe_count", postedFile);
            syncStatusJSON.put("file_names", (Object)new JSONArray((Collection)fileNames));
            final SyncPost syncPost = new SyncPost();
            isStatusUpdated = syncPost.postSyncStatusToSummaryServer(syncStatusJSON);
        }
        catch (final Exception e) {
            SyMLogger.error(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Exception while updateSyncStatusToSummaryServer", e);
        }
        SyMLogger.info(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "SyncStatusJSON from Probe posted : {0} - {1}", new Object[] { isStatusUpdated, syncStatusJSON });
        return isStatusUpdated;
    }
    
    public boolean updateSyncStatusFromSummaryServer(final Map properties) {
        final String sourceMethod = "updateSyncStatusFromSummaryServer";
        boolean isUpdated = false;
        long moduleID = -1L;
        long syncTime = -1L;
        int moduleSyncStatus = 6;
        try {
            final long probeID = Long.parseLong(properties.get("probe_id").toString());
            final String moduleName = properties.get("sync_module_name").toString();
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            moduleID = syncModuleMetaDAOUtil.getModuleID(moduleName);
            syncTime = Long.parseLong(properties.get("sync_time").toString());
            final int filesPostedFromProbe = Integer.parseInt(properties.getOrDefault("files_posted_from_probe_count", "-1").toString());
            final int filesProcessedInSummary = Integer.parseInt(properties.getOrDefault("files_processed_in_summary_count", "-1").toString());
            final Map fileStatusProps = properties.getOrDefault("file_details", null);
            moduleSyncStatus = Integer.parseInt(properties.getOrDefault("module_sync_status", 6).toString());
            final long currentProbeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
            isUpdated = false;
            if (probeID == currentProbeID) {
                final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
                final Row moduleAuditRow = syncModuleAuditDAOUtil.getSyncModuleAuditRow(probeID, moduleID, syncTime);
                if (moduleAuditRow != null) {
                    final Long moduleAuditID = (Long)moduleAuditRow.get("MODULE_AUDIT_ID");
                    final int filesPostedFromProbeDB = (int)moduleAuditRow.get("FILES_POSTED_FROM_PROBE");
                    if (filesPostedFromProbe != filesPostedFromProbeDB) {
                        SyMLogger.warning(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "FILES_POSTED_FROM_PROBE mismatch!!! In DB - {0}, Received - {1}", new Object[] { filesPostedFromProbeDB, filesPostedFromProbe });
                    }
                    if (fileStatusProps != null) {
                        final JSONObject fileDetailsJSON = new JSONObject(fileStatusProps);
                        final Iterator<String> keys = fileDetailsJSON.keys();
                        final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
                        while (keys.hasNext()) {
                            final String fileName = keys.next();
                            final JSONObject fileStatusJSON = fileDetailsJSON.getJSONObject(fileName);
                            final int fileStatus = fileStatusJSON.getInt("file_status");
                            final int retryCount = fileStatusJSON.getInt("retry_count");
                            syncFileAuditDAOUtil.addOrUpdateSyncFileStatus(moduleAuditID, fileName, fileStatus, retryCount);
                        }
                    }
                    syncModuleAuditDAOUtil.updateFilesProcessedInSummary(moduleAuditID, filesProcessedInSummary);
                    isUpdated = syncModuleAuditDAOUtil.updateModuleSyncStatus(moduleAuditID, moduleSyncStatus);
                    final ProbeSyncModuleDataDAOUtil probeSyncModuleDataDAOUtil = new ProbeSyncModuleDataDAOUtil();
                    probeSyncModuleDataDAOUtil.updateLastSummaryUpdateTime(moduleID, syncTime);
                    if (moduleSyncStatus == 1) {
                        probeSyncModuleDataDAOUtil.updateLastSuccessfulSyncTime(moduleID, syncTime);
                        SyncUtil.getInstance().cleanUpSyncFolderData(moduleID, syncTime);
                        SyncUtil.getInstance().cleanUpDeletionAuditTable(moduleID, syncTime);
                    }
                    SyncUtil.getInstance().checkAndEnableSyncScheduler(moduleID, "ENABLE_ON_SS_STATUS");
                }
                else {
                    SyMLogger.warning(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Audit Data not found for Probe ID : {0}, moduleID : {1}, syncTime : {2}" + syncTime, new Object[] { probeID, moduleID, syncTime });
                }
            }
            else {
                SyMLogger.warning(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "ProbeID mismatch!!! ProbeID from Summary server : {0}, current ProbeID : {1}", new Object[] { probeID, currentProbeID });
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Exception while updateSyncStatusFromSummaryServer", e);
        }
        SyMLogger.info(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "SyncStatusJSON from ss updated : {0} : {1}", new Object[] { isUpdated, properties });
        SyMLogger.info(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Sync status : {0} for {1}, sync time : {2}, Status received from ss", new Object[] { moduleSyncStatus, moduleID, syncTime });
        return isUpdated;
    }
    
    public boolean getAndUpdateSyncStatusFromSummaryServer(final long moduleID, final long syncTime) {
        final String sourceMethod = "getAndUpdateSyncStatusFromSummaryServer";
        boolean isUpdated = false;
        try {
            SyMLogger.info(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Inside getAndUpdateSyncStatusFromSummaryServer for module : {0}", moduleID);
            final long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
            final SyncPost syncPost = new SyncPost();
            final Map syncStatusMap = syncPost.fetchSyncStatusFromSummaryServer(probeID, moduleID, syncTime);
            if (syncStatusMap != null) {
                isUpdated = this.updateSyncStatusFromSummaryServer(syncStatusMap);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ProbeSyncStatusUpdater.syncStatusLogger, ProbeSyncStatusUpdater.sourceClass, sourceMethod, "Exception while getAndUpdateSyncStatusFromSummaryServer", e);
        }
        return isUpdated;
    }
    
    static {
        ProbeSyncStatusUpdater.syncStatusLogger = Logger.getLogger("ProbeSyncStatusLogger");
        ProbeSyncStatusUpdater.sourceClass = "ProbeSyncStatusUpdater";
    }
}
