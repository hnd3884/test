package com.me.ems.onpremise.summaryserver.common.probeadministration;

import com.me.ems.summaryserver.probe.sync.utils.SyncUtil;
import com.me.ems.onpremise.summaryserver.probe.probeadministration.SummaryServerReachabilityChecker;
import com.adventnet.ds.query.UpdateQuery;
import com.me.ems.onpremise.summaryserver.common.probeadministration.util.ProbeSMSHandler;
import com.me.ems.onpremise.summaryserver.common.probeadministration.util.ProbeMailHandler;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util.ProbeNotificationUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.ems.summaryserver.listener.ProbeMgmtListenerUtil;
import com.me.ems.summaryserver.listener.ProbeMgmtEvent;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.logging.Logger;

public class LiveStatusUpdateUtil
{
    public static Logger logger;
    public static final String API_KEY_MISMATCH = "ems.ss.probemgmt.api_mismatch";
    public static final String SERVER_DOWN = "ems.ss.probemgmt.server_down";
    public static final String VERSION_MISMATCH = "ems.ss.probemgmt.version_mismatch";
    public static final String MACHINE_NOT_REACHABLE = "ems.ss.probemgmt.machine_not_reachable";
    public static final String UNKNOWN_ISSUE = "ems.ss.probemgmt.unknown_issue";
    public static final String REACHABLE = "REACHABLE";
    public static final String NOT_REACHABLE = "NOT_REACHABLE";
    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String UNKNOWN = "UNKNOWN";
    public static final int REACHABLE_CODE = 9500;
    public static final int CERTIFICATE_VALIDATION_FAILED_CODE = 9501;
    public static final int UNKNOWN_HOST_CODE = 9502;
    public static final int MACHINE_UNREACHABLE_CODE = 9503;
    public static final int SERVER_DOWN_CODE = 9504;
    public static final int VERSION_MISMATCH_CODE = 9505;
    public static final int API_KEY_MISMATCH_CODE = 9506;
    public static final int UNKNOWN_ISSUE_CODE = 9507;
    public static final int SOCKET_TIME_OUT_CODE = 9508;
    private static HashMap<Long, HashMap> probeLiveStatusCache;
    private static HashMap summaryServerLiveStatusCache;
    
    private static HashMap populateProbeLiveStatusDetails(final Criteria criteria) {
        try {
            if (LiveStatusUpdateUtil.probeLiveStatusCache.isEmpty() || criteria != null) {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ProbeLiveStatus"));
                sq.addSelectColumn(new Column("ProbeLiveStatus", "*"));
                if (criteria != null) {
                    sq.setCriteria(criteria);
                }
                final DataObject dObj = SyMUtil.getPersistence().get(sq);
                if (!dObj.isEmpty()) {
                    final Iterator iter = dObj.getRows("ProbeLiveStatus");
                    while (iter.hasNext()) {
                        final Row probeLiveStatusRow = iter.next();
                        final HashMap liveStatusParams = new HashMap();
                        liveStatusParams.put("STATUS", probeLiveStatusRow.get("STATUS"));
                        liveStatusParams.put("REMARKS", probeLiveStatusRow.get("REMARKS"));
                        liveStatusParams.put("STATUS_CODE", probeLiveStatusRow.get("STATUS_CODE"));
                        liveStatusParams.put("LAST_CONTACTED_TIME", probeLiveStatusRow.get("LAST_CONTACTED_TIME"));
                        LiveStatusUpdateUtil.probeLiveStatusCache.put((Long)probeLiveStatusRow.get("PROBE_ID"), liveStatusParams);
                    }
                }
            }
        }
        catch (final Exception e) {
            LiveStatusUpdateUtil.logger.log(Level.SEVERE, "Exception occurred while getting probe live status ", e);
        }
        return LiveStatusUpdateUtil.probeLiveStatusCache;
    }
    
    public static HashMap getProbeLiveStatusDetails(final Long probeId) {
        if (LiveStatusUpdateUtil.probeLiveStatusCache.isEmpty()) {
            populateProbeLiveStatusDetails(null);
        }
        else if (LiveStatusUpdateUtil.probeLiveStatusCache.get(probeId) == null) {
            populateProbeLiveStatusDetails(new Criteria(new Column("ProbeLiveStatus", "PROBE_ID"), (Object)probeId, 0));
        }
        return LiveStatusUpdateUtil.probeLiveStatusCache.get(probeId);
    }
    
    public static void updateProbeLiveStatus(final Long probeId, final int liveStatus, final int statusCode, final String remarks) {
        final HashMap liveStatusParams = new HashMap();
        try {
            final HashMap probeLiveStatusDetails = getProbeLiveStatusDetails(probeId);
            if (liveStatus == 1 || liveStatus != probeLiveStatusDetails.get("STATUS") || probeLiveStatusDetails.get("REMARKS") == null || !probeLiveStatusDetails.get("REMARKS").equals(remarks)) {
                if (liveStatus == 1 && probeLiveStatusDetails.get("LAST_CONTACTED_TIME") == -1L) {
                    final ProbeMgmtEvent probeMgmtEvent = new ProbeMgmtEvent(probeId);
                    ProbeMgmtListenerUtil.getInstance().invokeProbeMgmtListeners(probeMgmtEvent, 2);
                    LiveStatusUpdateUtil.logger.log(Level.INFO, "Probe Mgmt - New Probe Installed Event Listeners - Invoked for {0}", new String[] { String.valueOf(probeId) });
                }
                else if (liveStatus == 1 && probeLiveStatusDetails.get("STATUS") == 2) {
                    final ProbeMgmtEvent probeMgmtEvent = new ProbeMgmtEvent(probeId);
                    ProbeMgmtListenerUtil.getInstance().invokeProbeMgmtListeners(probeMgmtEvent, 5);
                    LiveStatusUpdateUtil.logger.log(Level.INFO, "Probe Mgmt - New Probe Back to Live Event Listeners - Invoked for {0}", new String[] { String.valueOf(probeId) });
                }
                LiveStatusUpdateUtil.logger.log(Level.INFO, " Entered Live Status Update method for updating status {0} for probe {1}", new String[] { String.valueOf(liveStatus), String.valueOf(probeId) });
                final Criteria idCriteria = new Criteria(new Column("ProbeLiveStatus", "PROBE_ID"), (Object)probeId, 0);
                final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("ProbeLiveStatus");
                uq.setCriteria(idCriteria);
                Long lastContactedTime = null;
                if (liveStatus == 1) {
                    lastContactedTime = System.currentTimeMillis();
                    uq.setUpdateColumn("LAST_CONTACTED_TIME", (Object)lastContactedTime);
                }
                else {
                    final boolean ismailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
                    if (ismailServerConfigured && ProbeNotificationUtil.isMailEnabledForProbe(probeId)) {
                        LiveStatusUpdateUtil.logger.log(Level.INFO, "EMAIL TRIGGERING STARTED FOR PROBE DOWN -> " + probeId);
                        ProbeMailHandler.sendMailForProbeEvent(probeId, "probedown");
                    }
                    ProbeSMSHandler.triggerSMSForProbeEvent(probeId, "probedown");
                }
                uq.setUpdateColumn("REMARKS", (Object)remarks);
                uq.setUpdateColumn("STATUS", (Object)liveStatus);
                uq.setUpdateColumn("STATUS_CODE", (Object)statusCode);
                SyMUtil.getPersistence().update(uq);
                if (LiveStatusUpdateUtil.probeLiveStatusCache.get(probeId) != null) {
                    liveStatusParams.put("STATUS", liveStatus);
                    liveStatusParams.put("REMARKS", remarks);
                    liveStatusParams.put("STATUS_CODE", statusCode);
                    if (lastContactedTime != null) {
                        liveStatusParams.put("LAST_CONTACTED_TIME", lastContactedTime);
                    }
                    else {
                        liveStatusParams.put("LAST_CONTACTED_TIME", probeLiveStatusDetails.get("LAST_CONTACTED_TIME"));
                    }
                    LiveStatusUpdateUtil.probeLiveStatusCache.put(probeId, liveStatusParams);
                }
            }
        }
        catch (final Exception e) {
            LiveStatusUpdateUtil.logger.log(Level.SEVERE, "Exception occurred while updating probe live status ", e);
        }
    }
    
    public static synchronized HashMap getSummaryServerLiveStatusDetails() {
        if (LiveStatusUpdateUtil.summaryServerLiveStatusCache.isEmpty()) {
            SummaryServerReachabilityChecker.checkAndUpdateLiveStatus();
        }
        return LiveStatusUpdateUtil.summaryServerLiveStatusCache;
    }
    
    public static void updateSummaryServerLiveStatus(final int liveStatus, final int statusCode, final String remarks) {
        try {
            if (!LiveStatusUpdateUtil.summaryServerLiveStatusCache.isEmpty()) {
                final int previousLiveStatus = LiveStatusUpdateUtil.summaryServerLiveStatusCache.get("STATUS");
                if (liveStatus == 1 && previousLiveStatus == 2) {
                    SyncUtil.getInstance().checkAndEnableSyncScheduler("ENABLE_ON_SS_LIVE");
                }
            }
            LiveStatusUpdateUtil.logger.log(Level.INFO, "Entered Live Status Update method for updating status " + liveStatus);
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("SummaryServerLiveStatus");
            Long lastContactedTime = null;
            if (liveStatus == 1) {
                lastContactedTime = System.currentTimeMillis();
                uq.setUpdateColumn("LAST_CONTACTED_TIME", (Object)lastContactedTime);
            }
            uq.setUpdateColumn("STATUS", (Object)liveStatus);
            uq.setUpdateColumn("REMARKS", (Object)remarks);
            uq.setUpdateColumn("STATUS_CODE", (Object)statusCode);
            SyMUtil.getPersistence().update(uq);
            if (LiveStatusUpdateUtil.summaryServerLiveStatusCache != null) {
                LiveStatusUpdateUtil.summaryServerLiveStatusCache.put("STATUS", liveStatus);
                LiveStatusUpdateUtil.summaryServerLiveStatusCache.put("REMARKS", remarks);
                LiveStatusUpdateUtil.summaryServerLiveStatusCache.put("STATUS_CODE", statusCode);
                if (lastContactedTime != null) {
                    LiveStatusUpdateUtil.summaryServerLiveStatusCache.put("LAST_CONTACTED_TIME", lastContactedTime);
                }
            }
        }
        catch (final Exception e) {
            LiveStatusUpdateUtil.logger.log(Level.SEVERE, "Exception occurred while updating summary live status ", e);
        }
    }
    
    static {
        LiveStatusUpdateUtil.logger = Logger.getLogger("probeActionsLogger");
        LiveStatusUpdateUtil.probeLiveStatusCache = new HashMap<Long, HashMap>();
        LiveStatusUpdateUtil.summaryServerLiveStatusCache = new HashMap();
    }
}
