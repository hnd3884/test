package com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service;

import java.util.List;
import java.util.Iterator;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import com.me.ems.summaryserver.summary.sync.utils.SummarySyncModuleDataDAOUtil;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.ProbeReachabilityChecker;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeService
{
    public static Logger logger;
    
    public HashMap getProbeSpecificDetails(final Long probeId) {
        final HashMap detailMap = new HashMap();
        try {
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
            final Criteria criteria = new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0);
            sq.setCriteria(criteria);
            final Join join = new Join("ProbeDetails", "ProbeServerInfo", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join);
            final Join join_extn = new Join("ProbeDetails", "ProbeDetailsExtn", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join_extn);
            final Join status_join = new Join("ProbeDetails", "ProbeLiveStatus", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(status_join);
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
            sq.addSelectColumn(new Column("ProbeDetailsExtn", "*"));
            sq.addSelectColumn(new Column("ProbeLiveStatus", "*"));
            sq.setCriteria(criteria);
            final DataObject dobj1 = SyMUtil.getPersistence().get(sq);
            if (!dobj1.isEmpty()) {
                final Row probeDetailsRow = dobj1.getRow("ProbeDetails");
                final Row probeServerInfo = dobj1.getRow("ProbeServerInfo", criteria);
                final Row probeLiveStatusRow = dobj1.getRow("ProbeLiveStatus", criteria);
                final Row probeExtnRow = dobj1.getRow("ProbeDetailsExtn", criteria);
                if (probeDetailsRow != null && probeServerInfo != null && probeLiveStatusRow != null && probeExtnRow != null) {
                    detailMap.put("probeID", probeDetailsRow.get("PROBE_ID"));
                    detailMap.put("probeName", probeDetailsRow.get("PROBE_NAME"));
                    detailMap.put("probeDescription", probeDetailsRow.get("PROBE_DESCRIPTION"));
                    detailMap.put("probeIPAddress", probeServerInfo.get("IPADDRESS"));
                    detailMap.put("status", probeLiveStatusRow.get("STATUS"));
                    if ((long)probeLiveStatusRow.get("LAST_CONTACTED_TIME") != -1L) {
                        detailMap.put("lastContactTime", DateTimeUtil.longdateToString((long)probeLiveStatusRow.get("LAST_CONTACTED_TIME"), timeFormat));
                    }
                    else {
                        detailMap.put("lastContactTime", "-");
                    }
                    if ((long)probeServerInfo.get("INSTALLED_TIME") != -1L) {
                        detailMap.put("installedTime", DateTimeUtil.longdateToString((long)probeServerInfo.get("INSTALLED_TIME"), timeFormat));
                    }
                    else {
                        detailMap.put("installedTime", "-");
                    }
                    detailMap.put("remarks", probeLiveStatusRow.get("REMARKS"));
                    detailMap.put("isProbeAgentInstalled", true);
                    detailMap.put("probeAgentName", probeServerInfo.get("HOST"));
                    detailMap.put("probeAgentPort", probeServerInfo.get("PORT"));
                    detailMap.put("probeAgentProtocol", probeServerInfo.get("PROTOCOL"));
                    detailMap.put("version", probeServerInfo.get("BUILD_NUMBER"));
                    detailMap.put("timeZone", SyMUtil.getFormattedTimeZone((String)probeServerInfo.get("TIMEZONE")));
                    detailMap.put("freeSpaceInGB", probeExtnRow.get("FREE_SPACE"));
                    detailMap.put("totalSpaceInGB", probeExtnRow.get("TOTAL_SPACE"));
                    detailMap.put("usedSpacePercentage", this.getUsedSpacePercentage((float)probeExtnRow.get("FREE_SPACE"), (float)probeExtnRow.get("TOTAL_SPACE")));
                    detailMap.put("computersManaged", ProbeMgmtFactoryProvider.getProbeResourceAPI().getProbeWiseManagedComputersCount(probeId));
                    detailMap.put("mobileDevicesManaged", ProbeMgmtFactoryProvider.getProbeResourceAPI().getProbeWiseManagedMobileDevicesCount(probeId));
                    detailMap.put("techniciansAdded", ProbeUsersUtil.getTechniciansCountForProbe(probeId));
                }
            }
        }
        catch (final Exception ex) {
            ProbeService.logger.log(Level.SEVERE, "Exception while GETTING Probe SPECIFIC Details", ex);
        }
        return detailMap;
    }
    
    public float getUsedSpacePercentage(final Float freeSpace, final Float totalSpace) {
        if (totalSpace != 0.0f && totalSpace != null && freeSpace != null) {
            final float usedPercentage = (totalSpace - freeSpace) * 100.0f / totalSpace;
            return usedPercentage;
        }
        return 0.0f;
    }
    
    public HashMap regenerateSummaryServerAuthKey(final Long probeId) {
        final HashMap apiKeyMap = new HashMap();
        try {
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Long currentTime = System.currentTimeMillis();
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("SummaryServerApiKeyDetails");
            uq.setCriteria(new Criteria(new Column("SummaryServerApiKeyDetails", "PROBE_ID"), (Object)probeId, 0));
            final String apiKey = ProbeAuthUtil.getInstance().generateAPIKey();
            uq.setUpdateColumn("SUMMARY_API_KEY", (Object)apiKey);
            uq.setUpdateColumn("GENERATED_TIME", (Object)currentTime);
            uq.setUpdateColumn("GENERATED_BY", (Object)userId);
            SyMUtil.getPersistence().update(uq);
            apiKeyMap.put("summaryServerAuthKey", apiKey);
            apiKeyMap.put("generatedBy", DMUserHandler.getUserNameFromUserID(userId));
            apiKeyMap.put("generatedOn", DateTimeUtil.longdateToString((long)currentTime, timeFormat));
        }
        catch (final DataAccessException e) {
            ProbeService.logger.log(Level.SEVERE, "DataAccessException while regenerating probe auth key in ss", (Throwable)e);
        }
        catch (final Exception e2) {
            ProbeService.logger.log(Level.SEVERE, "Exception while regenerating probe auth key in ss", e2);
        }
        return apiKeyMap;
    }
    
    public HashMap updateProbeAuthKey(final Long probeId, final String apiKey) throws Exception {
        HashMap resp = ProbeReachabilityChecker.checkAndUpdateLiveStatus(probeId, apiKey);
        try {
            if (resp.get("serverStatus") != null && resp.get("serverStatus").equals("REACHABLE") && apiKey != null) {
                final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
                ProbeService.logger.log(Level.INFO, "PROBE API KEY IS VALID, SO UPDATING PROBE API KEY IN SS for probeId->" + probeId);
                final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("ProbeApiKeyDetails");
                uq.setCriteria(new Criteria(new Column("ProbeApiKeyDetails", "PROBE_ID"), (Object)probeId, 0));
                uq.setUpdateColumn("PROBE_API_KEY", (Object)apiKey);
                ProbeAuthUtil.updateProbeKeysCache(probeId, apiKey);
                uq.setUpdateColumn("GENERATED_BY", resp.get("probeServerAuthKeyGeneratedBy"));
                uq.setUpdateColumn("GENERATED_TIME", resp.get("probeServerAuthKeyGeneratedOn"));
                resp.put("probeServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(resp.get("probeServerAuthKeyGeneratedBy"))));
                resp.put("probeServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)resp.get("probeServerAuthKeyGeneratedOn"), timeFormat));
                SyMUtil.getPersistence().update(uq);
            }
            else if (resp.get("errorMsg") != null) {
                final String errorMsg = resp.get("errorMsg");
                ProbeService.logger.log(Level.INFO, "PROBE API KEY  UPDATION FAILED DUE TO " + errorMsg);
                if (errorMsg.equals("ems.ss.probemgmt.api_mismatch")) {
                    throw new APIException("PRBE9500305", errorMsg, new String[0]);
                }
                if (errorMsg.equals("ems.ss.probemgmt.server_down")) {
                    throw new APIException("PRBE9500306", errorMsg, new String[0]);
                }
                throw new APIException("PRBE9500301", errorMsg, new String[0]);
            }
            return resp;
        }
        catch (final DataAccessException e) {
            ProbeService.logger.log(Level.SEVERE, "DataAccessException while updating probe auth key in probe", (Throwable)e);
            resp = new HashMap();
            resp.put("errorMsg", "DataAccessException");
            return resp;
        }
    }
    
    public HashMap checkPSConnectivity(final Long probeId) {
        HashMap resp = new HashMap();
        try {
            resp = ProbeReachabilityChecker.checkAndUpdateLiveStatus(probeId, null, true);
            if (resp.containsKey("STATUS")) {
                if (resp.get("STATUS") == 1) {
                    resp = new HashMap();
                    resp.put("serverStatus", "REACHABLE");
                    resp.put("apiKeyStatus", "VALID");
                }
                else {
                    String errorMsg = resp.get("REMARKS");
                    resp = new HashMap();
                    if (errorMsg.equals("ems.ss.probemgmt.api_mismatch")) {
                        resp.put("apiKeyStatus", "INVALID");
                    }
                    else {
                        resp.put("apiKeyStatus", "UNKNOWN");
                    }
                    errorMsg = I18N.getMsg(errorMsg, new Object[0]);
                    resp.put("errorMsg", errorMsg);
                    resp.put("serverStatus", "NOT_REACHABLE");
                }
            }
            else if (resp.get("errorMsg") != null) {
                String errorMsg = resp.get("errorMsg");
                errorMsg = I18N.getMsg(errorMsg, new Object[0]);
                resp.put("errorMsg", errorMsg);
            }
        }
        catch (final Exception e) {
            ProbeService.logger.log(Level.SEVERE, "Exception while getting probe server Connectivity", e);
        }
        return resp;
    }
    
    public HashMap<String, String> getProbeLastSyncTime(final Long probeId, final Long moduleId) throws Exception {
        Long allModulesLastSyncTime = null;
        final HashMap<String, String> probeSyncTimeMap = new HashMap<String, String>();
        probeSyncTimeMap.put("syncInterval", "5");
        final SummarySyncModuleDataDAOUtil summarySyncModuleDataDAOUtil = new SummarySyncModuleDataDAOUtil();
        final HashMap probeDetails = ProbeUtil.getInstance().getProbeDetail(probeId);
        if (!probeDetails.isEmpty()) {
            if (moduleId != -1L) {
                final long lastSuccessfulSyncTime = summarySyncModuleDataDAOUtil.getLastSuccessfulSyncTime((long)probeId, (long)moduleId);
                if (lastSuccessfulSyncTime == -1L) {
                    throw new Exception("Invalid module ID");
                }
                probeSyncTimeMap.put(moduleId.toString(), Utils.getTime(Long.valueOf(lastSuccessfulSyncTime)));
            }
            else {
                final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
                for (int numberOfModules = syncModuleMetaDAOUtil.getAllModuleIDs().size(), index = 1; index <= numberOfModules; ++index) {
                    final long lastSuccessfulSyncTime2 = summarySyncModuleDataDAOUtil.getLastSuccessfulSyncTime((long)probeId, (long)index);
                    probeSyncTimeMap.put(String.valueOf(index), Utils.getTime(Long.valueOf(lastSuccessfulSyncTime2)));
                    if (allModulesLastSyncTime == null || lastSuccessfulSyncTime2 > allModulesLastSyncTime) {
                        allModulesLastSyncTime = lastSuccessfulSyncTime2;
                    }
                }
                if (allModulesLastSyncTime != null) {
                    probeSyncTimeMap.put("allModules", Utils.getTime(allModulesLastSyncTime));
                }
            }
        }
        return probeSyncTimeMap;
    }
    
    public HashMap<String, String> getAllProbesLastSyncTime(final Long moduleId) throws Exception {
        Long allProbesLastSyncTime = null;
        Long allProbesModuleLastSyncTime = null;
        final HashMap<String, String> probeSyncTimeMap = new HashMap<String, String>();
        probeSyncTimeMap.put("syncInterval", "5");
        final SummarySyncModuleDataDAOUtil summarySyncModuleDataDAOUtil = new SummarySyncModuleDataDAOUtil();
        final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
        if (!probeDetails.isEmpty()) {
            if (moduleId != -1L) {
                for (final Long probeId : probeDetails.keySet()) {
                    final long lastSuccessfulSyncTime = summarySyncModuleDataDAOUtil.getLastSuccessfulSyncTime((long)probeId, (long)moduleId);
                    if (lastSuccessfulSyncTime == -1L) {
                        throw new Exception("Invalid module ID");
                    }
                    probeSyncTimeMap.put(probeId.toString(), Utils.getTime(Long.valueOf(lastSuccessfulSyncTime)));
                    if (allProbesModuleLastSyncTime != null && lastSuccessfulSyncTime >= allProbesModuleLastSyncTime) {
                        continue;
                    }
                    allProbesModuleLastSyncTime = lastSuccessfulSyncTime;
                }
                if (allProbesModuleLastSyncTime != null) {
                    probeSyncTimeMap.put("allProbesModule", Utils.getTime(allProbesModuleLastSyncTime));
                }
            }
            else {
                final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
                final List<Long> moduleIDs = syncModuleMetaDAOUtil.getAllModuleIDs();
                for (final Long probeId2 : probeDetails.keySet()) {
                    Long allModulesLastSyncTime = null;
                    for (final Long moduleID : moduleIDs) {
                        final long lastSuccessfulSyncTime2 = summarySyncModuleDataDAOUtil.getLastSuccessfulSyncTime((long)probeId2, (long)moduleID);
                        if (lastSuccessfulSyncTime2 != -1L && (allModulesLastSyncTime == null || lastSuccessfulSyncTime2 < allModulesLastSyncTime)) {
                            allModulesLastSyncTime = lastSuccessfulSyncTime2;
                        }
                    }
                    probeSyncTimeMap.put(probeId2.toString(), Utils.getTime(allModulesLastSyncTime));
                    if (allModulesLastSyncTime != null && (allProbesLastSyncTime == null || allModulesLastSyncTime < allProbesLastSyncTime)) {
                        allProbesLastSyncTime = allModulesLastSyncTime;
                    }
                }
                if (allProbesLastSyncTime != null) {
                    probeSyncTimeMap.put("allProbes", Utils.getTime(allProbesLastSyncTime));
                }
            }
        }
        return probeSyncTimeMap;
    }
    
    static {
        ProbeService.logger = Logger.getLogger("probeActionsLogger");
    }
}
