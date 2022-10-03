package com.me.ems.onpremise.summaryserver.summary.probeadministration;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.net.HttpURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import org.json.JSONObject;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import com.me.ems.onpremise.summaryserver.common.probeadministration.LiveStatusUpdateUtil;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeReachabilityChecker
{
    static Logger logger;
    
    public static HashMap checkAndUpdateLiveStatus(final Long probeId) {
        return checkAndUpdateLiveStatus(probeId, null, false);
    }
    
    public static HashMap checkAndUpdateLiveStatus(final Long probeId, final String apikey) {
        return checkAndUpdateLiveStatus(probeId, apikey, false);
    }
    
    public static HashMap checkAndUpdateLiveStatus(final Long probeId, String apikey, final boolean isForcefulOnDemandCall) {
        HashMap resp = new HashMap();
        try {
            boolean isUpdateKeyRequest = true;
            final long currentTimeMillis = System.currentTimeMillis();
            if (isForcefulOnDemandCall) {
                HashMap<Long, Long> forcefulOnDemandTimeMap = (HashMap<Long, Long>)ApiFactoryProvider.getCacheAccessAPI().getCache("forcefulOnDemandTimeMap_SS", 2);
                if (forcefulOnDemandTimeMap != null && !forcefulOnDemandTimeMap.isEmpty() && forcefulOnDemandTimeMap.get(probeId) != null) {
                    final Long lastForcefulUpdatedTime = forcefulOnDemandTimeMap.get(probeId);
                    if (currentTimeMillis - lastForcefulUpdatedTime <= 60000L) {
                        return ProbeMgmtFactoryProvider.getProbeDetailsAPI().getProbeLiveStatusDetails(probeId);
                    }
                    forcefulOnDemandTimeMap.put(probeId, currentTimeMillis);
                    ApiFactoryProvider.getCacheAccessAPI().putCache("forcefulOnDemandTimeMap_SS", (Object)forcefulOnDemandTimeMap, 2);
                    ProbeReachabilityChecker.logger.log(Level.INFO, "probe server reachable check proceeding as last forcefully updated time > 1 min");
                }
                else {
                    if (forcefulOnDemandTimeMap == null) {
                        forcefulOnDemandTimeMap = new HashMap<Long, Long>();
                    }
                    forcefulOnDemandTimeMap.put(probeId, currentTimeMillis);
                    ApiFactoryProvider.getCacheAccessAPI().putCache("forcefulOnDemandTimeMap_SS", (Object)forcefulOnDemandTimeMap, 2);
                    ProbeReachabilityChecker.logger.log(Level.INFO, "probe server reachable check proceeding as it is the first forceful ondemand update");
                }
            }
            final HashMap probeRow = ProbeUtil.getInstance().getProbeDetail(probeId);
            if (apikey == null) {
                apikey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
                isUpdateKeyRequest = false;
            }
            resp = sendProbeReachableRequest(probeId, probeRow, apikey, isUpdateKeyRequest, false);
        }
        catch (final Exception e) {
            ProbeReachabilityChecker.logger.log(Level.SEVERE, "Exception occured in Probe Reachable check", e);
            LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, 9507, "ems.ss.probemgmt.unknown_issue");
            resp.put("serverStatus", "NOT_REACHABLE");
            resp.put("apiKeyStatus", "UNKNOWN");
        }
        return resp;
    }
    
    private static HashMap sendProbeReachableRequest(final Long probeId, final Map probeRow, final String authToken, final boolean isUpdateApiKeyRequest, final boolean isRetry) {
        final HashMap resp = new HashMap();
        String serverStatus = "NOT_REACHABLE";
        String apiKeyStatus = "UNKNOWN";
        String errorMsg = "";
        int statusCode = 9507;
        try {
            final String serverName = probeRow.get("HOST");
            final int portNumber = probeRow.get("PORT");
            final String protocol = probeRow.get("PROTOCOL");
            final String ipAddr = probeRow.get("IPADDRESS");
            final String baseURL = protocol + "://" + serverName + ":" + portNumber + "/servlets/probeLiveStatusUpdate";
            final HttpURLConnection conn = HttpsHandlerUtil.getServerUrlConnection(baseURL);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("probeAuthKey", authToken);
            conn.setRequestProperty("isUpdateApiKeyRequest", String.valueOf(isUpdateApiKeyRequest));
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            try {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                final String response = rd.readLine();
                rd.close();
                final int HttpResult = conn.getResponseCode();
                if (HttpResult == 200 || HttpResult == 201) {
                    apiKeyStatus = "VALID";
                    serverStatus = "REACHABLE";
                    statusCode = 9500;
                    LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 1, statusCode, null);
                    final JSONObject jsonObject = new JSONObject(response);
                    if (isUpdateApiKeyRequest) {
                        if (jsonObject.has("probeServerAuthKeyGeneratedBy")) {
                            resp.put("probeServerAuthKeyGeneratedBy", jsonObject.get("probeServerAuthKeyGeneratedBy"));
                        }
                        if (jsonObject.has("probeServerAuthKeyGeneratedOn")) {
                            resp.put("probeServerAuthKeyGeneratedOn", jsonObject.get("probeServerAuthKeyGeneratedOn"));
                        }
                    }
                    else if (jsonObject.has("buildnumber")) {
                        final String buildNumber = jsonObject.getString("buildnumber");
                        if (buildNumber.equals(ProductUrlLoader.getInstance().getValue("buildnumber"))) {
                            ProbeReachabilityChecker.logger.log(Level.INFO, "Request for Probe Live Status Update Successful for the probe id->" + probeId + " and the result:" + HttpResult);
                            LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 1, statusCode, null);
                        }
                        else {
                            errorMsg = "ems.ss.probemgmt.version_mismatch";
                            serverStatus = "NOT_REACHABLE";
                            statusCode = 9505;
                            ProbeReachabilityChecker.logger.log(Level.SEVERE, "version in Summary Server mismatched with probe, so probe status is changed to down for probe id->" + probeId);
                            LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, "ems.ss.probemgmt.version_mismatch");
                        }
                        updateProbeVersionInDB(probeId, buildNumber);
                    }
                }
            }
            catch (final UnknownHostException e) {
                errorMsg = "Unknownhost";
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                statusCode = 9502;
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, errorMsg);
                }
                ProbeReachabilityChecker.logger.log(Level.SEVERE, "Request for Probe Live Status Update UnSuccessful due to UNKNOWN HOST for the probe->" + probeId, e);
            }
            catch (final ConnectException e2) {
                try {
                    if (InetAddress.getByName(ipAddr).isReachable(1000)) {
                        errorMsg = "ems.ss.probemgmt.server_down";
                        statusCode = 9504;
                    }
                    else {
                        errorMsg = "ems.ss.probemgmt.machine_not_reachable";
                        statusCode = 9503;
                    }
                }
                catch (final UnknownHostException ex) {
                    statusCode = 9502;
                    e2.printStackTrace();
                }
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, errorMsg);
                }
                ProbeReachabilityChecker.logger.log(Level.SEVERE, "host not reachable in Summary Live Status Update task , so probe status is changed to down for probe id->" + probeId, e2);
            }
            catch (final SocketTimeoutException socketTimeoutException) {
                errorMsg = "Socket Timed out";
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                statusCode = 9508;
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, errorMsg);
                }
            }
            catch (final SSLHandshakeException ex2) {
                if (!isRetry) {
                    ProbeReachabilityChecker.logger.log(Level.INFO, "Request for Probe Live Status Update UnSuccessful due to proper certification unavailablity for the probe->" + probeId + " and trying to fetch the certificate from server", ex2);
                    HttpsHandlerUtil.processCertificateFromServer(baseURL, probeId);
                    sendProbeReachableRequest(probeId, probeRow, authToken, isUpdateApiKeyRequest, true);
                }
                else {
                    errorMsg = "No Proper Certificate Found";
                    serverStatus = "NOT_REACHABLE";
                    apiKeyStatus = "UNKNOWN";
                    statusCode = 9501;
                    if (!isUpdateApiKeyRequest) {
                        LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, errorMsg);
                    }
                    ProbeReachabilityChecker.logger.log(Level.INFO, "Request for Probe Live Status Update UnSuccessful due to proper certification unavailablity for the probe->" + probeId + " second time");
                }
            }
            catch (final Exception e3) {
                final int httpResponse = conn.getResponseCode();
                if (httpResponse == 401) {
                    errorMsg = "ems.ss.probemgmt.api_mismatch";
                    apiKeyStatus = "INVALID";
                    serverStatus = "NOT_REACHABLE";
                    statusCode = 9506;
                    ProbeReachabilityChecker.logger.log(Level.SEVERE, "api key mismatch may occured in Summary Live Status Update task , so probe status is changed to down for probe id->" + probeId, e3);
                }
                else {
                    errorMsg = "ems.ss.probemgmt.unknown_issue";
                    apiKeyStatus = "UNKNOWN";
                    serverStatus = "NOT_REACHABLE";
                    statusCode = 9507;
                    ProbeReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in probe reachable check , so probe status is changed to down for probeId ->" + probeId, e3);
                }
                LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, statusCode, errorMsg);
            }
        }
        catch (final Exception e4) {
            ProbeReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in probe reachable check", e4);
            LiveStatusUpdateUtil.updateProbeLiveStatus(probeId, 2, 9507, "ems.ss.probemgmt.unknown_issue");
        }
        finally {
            if (errorMsg != null && !errorMsg.equals("")) {
                resp.put("errorMsg", errorMsg);
                resp.put("statusCode", statusCode);
            }
            resp.put("serverStatus", serverStatus);
            resp.put("apiKeyStatus", apiKeyStatus);
        }
        return resp;
    }
    
    private static void updateProbeVersionInDB(final Long probeID, final String buildnumber) {
        try {
            final HashMap allProbeDetails = ProbeUtil.getInstance().getAllProbeDetails();
            final HashMap probeDetails = allProbeDetails.get(probeID);
            final String buildNumberFromCache = probeDetails.get("BUILD_NUMBER");
            if (!buildnumber.equals(buildNumberFromCache)) {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ProbeServerInfo");
                updateQuery.setUpdateColumn("BUILD_NUMBER", (Object)buildnumber);
                updateQuery.setCriteria(new Criteria(new Column("ProbeServerInfo", "PROBE_ID"), (Object)probeID, 0));
                DataAccess.update(updateQuery);
                probeDetails.put("BUILD_NUMBER", buildnumber);
                ProbeUtil.getAllProbeDetailsCache().put(probeID, probeDetails);
            }
        }
        catch (final Exception e) {
            ProbeReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in updateProbeVersionInDB", e);
        }
    }
    
    static {
        ProbeReachabilityChecker.logger = Logger.getLogger("probeActionsLogger");
    }
}
