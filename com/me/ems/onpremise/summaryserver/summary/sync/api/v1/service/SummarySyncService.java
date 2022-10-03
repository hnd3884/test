package com.me.ems.onpremise.summaryserver.summary.sync.api.v1.service;

import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.ems.summaryserver.summary.util.SummaryAPIRedirectHandler;
import com.me.ems.summaryserver.summary.sync.utils.SummarySyncStatusUpdater;
import java.util.Map;
import java.util.logging.Logger;

public class SummarySyncService
{
    private static String sourceClass;
    private static Logger logger;
    
    public Map<String, Object> fetchSummarySyncStatus(final long probeID, final long moduleID, final long syncTime) {
        final SummarySyncStatusUpdater summarySyncStatusUpdater = new SummarySyncStatusUpdater();
        final JSONObject syncStatusJSON = summarySyncStatusUpdater.getSyncStatusJSON(probeID, moduleID, syncTime);
        return new SummaryAPIRedirectHandler().toMap(syncStatusJSON);
    }
    
    public void updateProbeSyncStatus(final Map properties) throws Exception {
        final String sourceMethod = "updateProbeSyncStatus";
        final String probeIDStr = properties.get("probe_id");
        final String moduleName = properties.get("sync_module_name");
        final String syncTimeStr = properties.get("sync_time");
        final String filesSentFromProbeStr = properties.get("files_posted_from_probe_count");
        final ArrayList fileNames = properties.get("file_names");
        SyMLogger.info(SummarySyncService.logger, SummarySyncService.sourceClass, sourceMethod, "Module Sync status received from Probe Server for module {0} : {1}", new Object[] { moduleName, syncTimeStr });
        final long probeID = Long.parseLong(probeIDStr);
        final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
        final long moduleID = syncModuleMetaDAOUtil.getModuleID(moduleName);
        final long syncTime = Long.parseLong(syncTimeStr);
        final int filesSentFromProbe = Integer.parseInt(filesSentFromProbeStr);
        final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
        syncModuleAuditDAOUtil.addOrUpdateSyncModuleAudit(probeID, moduleID, syncTime, filesSentFromProbe);
    }
    
    static {
        SummarySyncService.sourceClass = "SummarySyncService";
        SummarySyncService.logger = Logger.getLogger("SummarySyncLogger");
    }
}
