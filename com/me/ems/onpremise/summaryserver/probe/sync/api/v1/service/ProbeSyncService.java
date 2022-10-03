package com.me.ems.onpremise.summaryserver.probe.sync.api.v1.service;

import com.me.ems.summaryserver.probe.sync.utils.ProbeSyncStatusUpdater;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Map;
import java.util.logging.Logger;

public class ProbeSyncService
{
    private static final Logger logger;
    private static final String sourceClass = "ProbeSyncService";
    
    public Map<String, Object> updateSyncStatusFromSummaryServer(final Map properties) {
        final String sourceMethod = "updateSyncStatusFromSummaryServer";
        SyMLogger.info(ProbeSyncService.logger, "ProbeSyncService", sourceMethod, "Module Sync status received from Summary Server");
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        final ProbeSyncStatusUpdater probeSyncStatusUpdater = new ProbeSyncStatusUpdater();
        final boolean updateStatus = probeSyncStatusUpdater.updateSyncStatusFromSummaryServer(properties);
        resultMap.put("StatusUpdated", updateStatus);
        return resultMap;
    }
    
    static {
        logger = Logger.getLogger("ProbeSyncLogger");
    }
}
