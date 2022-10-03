package com.me.ems.summaryserver.probe.sync;

import com.me.ems.summaryserver.probe.sync.utils.ProbeSyncStatusUpdater;
import com.me.ems.summaryserver.common.sync.utils.SyncFileAuditDAOUtil;
import com.me.ems.summaryserver.probe.sync.utils.SyncPost;
import java.util.Iterator;
import java.util.LinkedHashSet;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import java.util.Collection;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.summaryserver.probe.sync.utils.SyncDataCreator;
import java.util.ArrayList;
import java.util.List;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.me.ems.summaryserver.probe.sync.utils.ProbeSyncModuleDataDAOUtil;
import java.util.Map;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.ems.summaryserver.probe.sync.utils.SyncUtil;
import java.util.logging.Logger;
import com.me.ems.summaryserver.probe.sync.factory.SyncAPI;

public abstract class DefaultProbeSyncImpl implements SyncAPI
{
    private static Logger logger;
    private static Logger syncStatusLogger;
    private static String sourceClass;
    
    @Override
    public boolean syncPreChecks() {
        final String sourceMethod = "syncPreChecks";
        final boolean status = SyncUtil.getInstance().isSummaryServerLiveForSync();
        SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Sync PreCheck status : ", status);
        return status;
    }
    
    @Override
    public Map<String, Object> getSyncParameters(final long moduleID) {
        final String sourceMethod = "getSyncParameters";
        Map<String, Object> syncParams = null;
        try {
            final ProbeSyncModuleDataDAOUtil probeSyncModuleDataDAOUtil = new ProbeSyncModuleDataDAOUtil();
            final DataObject probeSyncModuleDataDO = probeSyncModuleDataDAOUtil.getProbeSyncModuleDataDO(moduleID);
            if (probeSyncModuleDataDO != null) {
                final Row probeSyncModuleDataRow = probeSyncModuleDataDO.getFirstRow("ProbeSyncModuleData");
                long currSyncLockedTime = (long)probeSyncModuleDataRow.get("CURR_SYNC_LOCKED_TIME");
                long lastSuccessfulSyncTime = (long)probeSyncModuleDataRow.get("LAST_SUCCESSFUL_SYNC_TIME");
                long lastSyncTime = (long)probeSyncModuleDataRow.get("LAST_SYNC_TIME");
                final long lastUpdateFromSummaryTime = (long)probeSyncModuleDataRow.get("LAST_UPDATE_FROM_SUMMARY");
                currSyncLockedTime = ((currSyncLockedTime == -1L) ? 0L : currSyncLockedTime);
                lastSuccessfulSyncTime = ((lastSuccessfulSyncTime == -1L) ? 0L : lastSuccessfulSyncTime);
                lastSyncTime = ((lastSyncTime == -1L) ? 0L : lastSyncTime);
                final boolean status = SyncUtil.getInstance().IsPreviousSyncCompleted(moduleID, currSyncLockedTime);
                if (status) {
                    syncParams = new HashMap<String, Object>();
                    syncParams.put("LAST_SYNC_TIME", lastSyncTime);
                    syncParams.put("LAST_SUCCESSFUL_SYNC_TIME", lastSuccessfulSyncTime);
                    syncParams.put("LAST_UPDATE_FROM_SUMMARY", lastUpdateFromSummaryTime);
                    currSyncLockedTime = SyncUtil.getInstance().getCurrSyncLockTime(syncParams);
                    syncParams.put("CURR_SYNC_LOCKED_TIME", currSyncLockedTime);
                    probeSyncModuleDataDAOUtil.updateCurrSyncLockedTime(moduleID, currSyncLockedTime);
                }
                SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "SyncParameters for current sync :", syncParams);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Exception in getting sync parameter", e);
        }
        return syncParams;
    }
    
    @Override
    public int getLastSyncStatus(final long moduleID, final Map<String, Object> syncParams) {
        final String sourceMethod = "getLastSyncStatus";
        int lastSyncStatus = 6;
        try {
            final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
            final long lastSyncTime = syncParams.get("LAST_SYNC_TIME");
            final long lastSuccessfulSyncTime = syncParams.get("LAST_SUCCESSFUL_SYNC_TIME");
            final long lastSummaryUpdateTime = syncParams.get("LAST_UPDATE_FROM_SUMMARY");
            if (lastSyncTime == lastSuccessfulSyncTime) {
                lastSyncStatus = 1;
                final int schedulerIntervalMin = 5;
                final int threshold = 3;
                final long timeInMillis = schedulerIntervalMin * threshold * 60L * 1000L;
                if (currSyncLockedTime - lastSyncTime > timeInMillis) {
                    lastSyncStatus = 3;
                }
            }
            else if (lastSyncTime == lastSummaryUpdateTime) {
                final long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
                final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
                lastSyncStatus = syncModuleAuditDAOUtil.getModuleSyncStatus(probeID, moduleID, lastSyncTime);
            }
            else {
                lastSyncStatus = 5;
            }
            SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Last Sync Status :", lastSyncStatus);
        }
        catch (final Exception e) {
            SyMLogger.error(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Exception in checking isLastSyncSuccess", e);
        }
        return lastSyncStatus;
    }
    
    @Override
    public List<SyncData> getAndWriteAddOrUpdateData(final long moduleID, final Map<String, Object> syncParams) throws Exception {
        final String sourceMethod = "getAndWriteAddOrUpdateData";
        final List<SyncData> csvSyncData = new ArrayList<SyncData>();
        final long startTime = System.currentTimeMillis();
        try {
            final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
            final long lastSuccessfulSyncTime = syncParams.get("LAST_SUCCESSFUL_SYNC_TIME");
            SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Going to start insert/update data collection for module {0} with sync time {1} ", new Object[] { moduleID, currSyncLockedTime });
            final SyncDataCreator syncDataCreator = new SyncDataCreator();
            final String currSyncFolderPath = syncDataCreator.getFolderPathForCurrSync(moduleID, currSyncLockedTime);
            final boolean isDirExist = ApiFactoryProvider.getFileAccessAPI().isFileExists(currSyncFolderPath);
            if (isDirExist) {
                ApiFactoryProvider.getFileAccessAPI().deleteFilesInDirectory(currSyncFolderPath, true);
                SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Sync folder already exist for, {0} deleting old files!", currSyncLockedTime);
            }
            else {
                final boolean isDirCreated = ApiFactoryProvider.getFileAccessAPI().createDirectory(currSyncFolderPath);
                SyMLogger.debug(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "New folder created for {0} : {1}", new Object[] { currSyncLockedTime, isDirCreated });
                if (!isDirCreated) {
                    throw new Exception("Sync folder cannot be created for " + currSyncFolderPath);
                }
            }
            final LinkedHashSet<Long> sqlIds = SyncUtil.getInstance().getUpdatedDataSQLIds(moduleID, lastSuccessfulSyncTime);
            for (final long sqlId : sqlIds) {
                final List<SyncData> tableSyncData = syncDataCreator.getCSVWrittenSyncData(moduleID, sqlId, lastSuccessfulSyncTime, currSyncLockedTime);
                csvSyncData.addAll(tableSyncData);
            }
        }
        finally {
            final long endTime = System.currentTimeMillis();
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Total time taken for getAndWriteAddOrUpdateData for {0} = {1}", new Object[] { syncModuleMetaDAOUtil.getModuleName(moduleID), endTime - startTime });
        }
        return csvSyncData;
    }
    
    @Override
    public List<SyncData> getAndWriteDeleteData(final long moduleID, final Map<String, Object> syncParams) throws Exception {
        final String sourceMethod = "getAndWriteDeleteData";
        final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        final long lastSuccessfulSyncTime = syncParams.get("LAST_SUCCESSFUL_SYNC_TIME");
        final long startTime = System.currentTimeMillis();
        List<SyncData> deleteSyncData;
        try {
            SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Going to start deletion data collection for module {0} with sync time : {1} ", new Object[] { moduleID, currSyncLockedTime });
            final SyncDataCreator syncDataCreator = new SyncDataCreator();
            deleteSyncData = syncDataCreator.getJSONWrittenSyncDeleteData(moduleID, lastSuccessfulSyncTime, currSyncLockedTime);
        }
        finally {
            final long endTime = System.currentTimeMillis();
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Total time taken for getAndWriteDeleteData for {0} : {1}", new Object[] { syncModuleMetaDAOUtil.getModuleName(moduleID), endTime - startTime });
        }
        return deleteSyncData;
    }
    
    @Override
    public Map<String, Boolean> postSyncDataToSummaryServer(final long moduleID, final Map<String, Object> syncParams, final List<SyncData> syncDataObjects) throws Exception {
        final String sourceMethod = "postSyncDataToSummaryServer";
        SyMLogger.debug(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "SyncData objects : {0}", syncDataObjects);
        final Map<String, Boolean> syncFileStatus = new HashMap<String, Boolean>();
        try {
            final SyncPost syncPost = new SyncPost();
            boolean postStatus = true;
            for (int i = 0; i < syncDataObjects.size(); ++i) {
                final SyncData syncData = syncDataObjects.get(i);
                final boolean isLastFile = i == syncDataObjects.size() - 1;
                final String fileName = syncData.fileName;
                if (postStatus) {
                    postStatus = syncPost.postSyncDataToSummaryServer(moduleID, syncData, isLastFile);
                }
                syncFileStatus.put(fileName, postStatus);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Caught exception while postSyncData to summary server", e);
        }
        return syncFileStatus;
    }
    
    @Override
    public boolean addAuditEntryAndUpdateSyncStatus(final long moduleID, final Map<String, Object> syncParams, final Map<String, Boolean> syncFileStatus) {
        final String sourceMethod = "postSyncStatusToSummaryServer";
        final int totalFileCount = syncFileStatus.size();
        int postedFileCount = 0;
        final long probeID = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeID();
        final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
        int syncRetryCount = -1;
        if (syncParams.get("SYNC_RETRY_COUNT") != null) {
            syncRetryCount = syncParams.get("SYNC_RETRY_COUNT");
        }
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        final long moduleAuditID = syncModuleAuditDAOUtil.addOrUpdateSyncModuleAudit(probeID, moduleID, currSyncLockedTime, totalFileCount);
        final SyncFileAuditDAOUtil syncFileAuditDAOUtil = new SyncFileAuditDAOUtil();
        final List<String> fileNames = new ArrayList<String>();
        for (final Map.Entry<String, Boolean> entry : syncFileStatus.entrySet()) {
            final String fileName = entry.getKey();
            final boolean status = entry.getValue();
            int statusCode;
            if (status) {
                statusCode = 950001;
                ++postedFileCount;
            }
            else {
                statusCode = 950003;
            }
            fileNames.add(fileName);
            if (syncRetryCount != -1) {
                syncFileAuditDAOUtil.addOrUpdateSyncFileStatus(moduleAuditID, fileName, statusCode, syncRetryCount);
            }
            else {
                syncFileAuditDAOUtil.addOrUpdateSyncFileStatus(moduleAuditID, fileName, statusCode);
            }
        }
        final ProbeSyncStatusUpdater probeSyncStatusUpdater = new ProbeSyncStatusUpdater();
        final boolean postStatus = probeSyncStatusUpdater.updateSyncStatusToSummaryServer(probeID, moduleID, currSyncLockedTime, totalFileCount, postedFileCount, fileNames);
        SyMLogger.info(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Sync Status updated : {0}", postStatus);
        return postStatus;
    }
    
    @Override
    public boolean syncPostChecks(final long moduleID, final Map<String, Object> syncParams, final boolean isSyncSuccess) {
        final String sourceMethod = "syncPostChecks";
        boolean status = true;
        try {
            final long currSyncLockedTime = syncParams.get("CURR_SYNC_LOCKED_TIME");
            final ProbeSyncModuleDataDAOUtil probeSyncModuleDataDAOUtil = new ProbeSyncModuleDataDAOUtil();
            probeSyncModuleDataDAOUtil.updateCurrSyncLockedTime(moduleID, 0L);
            if (isSyncSuccess) {
                probeSyncModuleDataDAOUtil.updateLastSyncTime(moduleID, currSyncLockedTime);
                final int isEmptySync = syncParams.getOrDefault("IS_EMPTY_SYNC", 0);
                if (isEmptySync == 1) {
                    SyMLogger.info(DefaultProbeSyncImpl.syncStatusLogger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Sync status : {0} for module {1}, sync time {2}, {3} ", new Object[] { 1, moduleID, currSyncLockedTime, "No Data to Sync!" });
                    probeSyncModuleDataDAOUtil.updateLastSuccessfulSyncTime(moduleID, currSyncLockedTime);
                    final SyncDataCreator syncDataCreator = new SyncDataCreator();
                    final String currSyncFolderPath = syncDataCreator.getFolderPathForCurrSync(moduleID, currSyncLockedTime);
                    ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(currSyncFolderPath);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DefaultProbeSyncImpl.logger, DefaultProbeSyncImpl.sourceClass, sourceMethod, "Caught exception while syncpostchecks", e);
            status = false;
        }
        return status;
    }
    
    @Override
    public abstract boolean performPreChecks(final long p0) throws Exception;
    
    @Override
    public abstract boolean performPostChecks(final long p0, final boolean p1) throws Exception;
    
    static {
        DefaultProbeSyncImpl.logger = Logger.getLogger("ProbeSyncLogger");
        DefaultProbeSyncImpl.syncStatusLogger = Logger.getLogger("ProbeSyncStatusLogger");
        DefaultProbeSyncImpl.sourceClass = "DefaultSyncImpl";
    }
}
