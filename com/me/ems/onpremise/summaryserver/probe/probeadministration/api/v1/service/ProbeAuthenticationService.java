package com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.service;

import com.adventnet.ds.query.Criteria;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.summaryserver.probe.probeadministration.SummaryServerReachabilityChecker;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeAuthenticationService
{
    public static Logger logger;
    
    public HashMap regenerateProbeAuthKey() {
        final HashMap apiKeyMap = new HashMap();
        try {
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("ProbeApiKeyDetails");
            final String apiKey = ProbeAuthUtil.getInstance().generateAPIKey();
            uq.setUpdateColumn("PROBE_API_KEY", (Object)apiKey);
            uq.setUpdateColumn("GENERATED_BY", (Object)userId);
            final Long currentTime = System.currentTimeMillis();
            uq.setUpdateColumn("GENERATED_TIME", (Object)currentTime);
            SyMUtil.getPersistence().update(uq);
            apiKeyMap.put("probeAuthKey", apiKey);
            ProbeDetailsUtil.setProbeAuthKey(apiKey);
            apiKeyMap.put("generatedBy", DMUserHandler.getUserNameFromUserID(userId));
            apiKeyMap.put("generatedOn", DateTimeUtil.longdateToString((long)currentTime, timeFormat));
        }
        catch (final DataAccessException e) {
            ProbeAuthenticationService.logger.log(Level.SEVERE, "Exception while regenerating probe auth key in probe", (Throwable)e);
        }
        catch (final Exception e2) {
            e2.printStackTrace();
        }
        return apiKeyMap;
    }
    
    public HashMap updateSummaryServerAuthKey(final String apiKey) throws Exception {
        HashMap resp = SummaryServerReachabilityChecker.checkAndUpdateLiveStatus(apiKey, false);
        try {
            final String serverStatus = resp.get("serverStatus");
            if (serverStatus != null && serverStatus.equals("REACHABLE") && apiKey != null) {
                final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
                ProbeAuthenticationService.logger.log(Level.INFO, "SUMMARY API KEY IS VALID, SO UPDATING SUMMARY API KEY IN PROBE");
                final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("SummaryServerApiKeyDetails");
                uq.setUpdateColumn("SUMMARY_API_KEY", (Object)apiKey);
                uq.setUpdateColumn("GENERATED_BY", resp.get("summaryServerAuthKeyGeneratedBy"));
                uq.setUpdateColumn("GENERATED_TIME", resp.get("summaryServerAuthKeyGeneratedOn"));
                resp.put("summaryServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(resp.get("summaryServerAuthKeyGeneratedBy"))));
                resp.put("summaryServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)resp.get("summaryServerAuthKeyGeneratedOn"), timeFormat));
                SyMUtil.getPersistence().update(uq);
            }
            else if (resp.get("errorMsg") != null) {
                final String errorMsg = resp.get("errorMsg");
                ProbeAuthenticationService.logger.log(Level.INFO, "SUMMARY API  KEY  UPDATION FAILED DUE TO " + errorMsg);
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
            ProbeAuthenticationService.logger.log(Level.SEVERE, "DataAccessException while updating summary auth key in probe", (Throwable)e);
            resp = new HashMap();
            resp.put("errorMsg", "DataAccessException");
            return resp;
        }
    }
    
    public HashMap checkSummaryServerConnectivity() throws Exception {
        final HashMap resp = SummaryServerReachabilityChecker.checkAndUpdateLiveStatus();
        if (resp.get("errorMsg") != null) {
            String errorMsg = resp.get("errorMsg");
            errorMsg = I18N.getMsg(errorMsg, new Object[0]);
            resp.put("errorMsg", errorMsg);
        }
        return resp;
    }
    
    public HashMap getApiKeyDetails() {
        final HashMap apiDetails = ProbeDetailsUtil.getApiKeyDetails(null);
        try {
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            apiDetails.put("probeServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(apiDetails.get("probeServerAuthKeyGeneratedBy"))));
            apiDetails.put("summaryServerAuthKeyGeneratedBy", DMUserHandler.getUserNameFromUserID(Long.valueOf(apiDetails.get("summaryServerAuthKeyGeneratedBy"))));
            apiDetails.put("probeServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)apiDetails.get("probeServerAuthKeyGeneratedOn"), timeFormat));
            apiDetails.put("summaryServerAuthKeyGeneratedOn", DateTimeUtil.longdateToString((long)apiDetails.get("summaryServerAuthKeyGeneratedOn"), timeFormat));
        }
        catch (final Exception e) {
            ProbeAuthenticationService.logger.log(Level.SEVERE, "Exception while getting api key details", e);
        }
        return apiDetails;
    }
    
    static {
        ProbeAuthenticationService.logger = Logger.getLogger("probeActionsLogger");
    }
}
