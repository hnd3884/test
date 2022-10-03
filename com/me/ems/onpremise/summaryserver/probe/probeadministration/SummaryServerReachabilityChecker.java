package com.me.ems.onpremise.summaryserver.probe.probeadministration;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileWriter;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import java.net.HttpURLConnection;
import javax.net.ssl.SSLHandshakeException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import org.json.JSONObject;
import com.me.ems.onpremise.summaryserver.common.probeadministration.LiveStatusUpdateUtil;
import java.util.logging.Level;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class SummaryServerReachabilityChecker
{
    static Logger logger;
    
    public static HashMap checkAndUpdateLiveStatus() {
        return checkAndUpdateLiveStatus(null, false);
    }
    
    public static HashMap checkAndUpdateLiveStatus(String apikey, final boolean isRetry) {
        final HashMap resp = new HashMap();
        boolean isUpdateApiKeyRequest = true;
        final String url = getSummaryServerLiveStatusUpdateUrl();
        String serverStatus = "NOT_REACHABLE";
        String apiKeyStatus = "UNKNOWN";
        String errorMsg = "";
        int statusCode = 9507;
        try {
            final HttpURLConnection conn = HttpsHandlerUtil.getServerUrlConnection(url);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final Map<String, Object> params = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerAPIKeyDetails();
            if (apikey == null) {
                isUpdateApiKeyRequest = false;
                apikey = params.get("summaryServerAuthKey");
            }
            conn.setRequestProperty("summaryServerAuthKey", apikey);
            conn.setRequestProperty("probeId", params.get("probeId"));
            conn.setRequestProperty("isUpdateApiKeyRequest", String.valueOf(isUpdateApiKeyRequest));
            conn.setRequestProperty("buildnumber", ProductUrlLoader.getInstance().getValue("buildnumber"));
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            if (url.contains("https")) {}
            final long start = System.currentTimeMillis();
            try {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                final String response = rd.readLine();
                rd.close();
                final int HttpResult = conn.getResponseCode();
                if (HttpResult == 200) {
                    SummaryServerReachabilityChecker.logger.log(Level.INFO, "Request for Probe Live Status Update Successful: " + HttpResult);
                    LiveStatusUpdateUtil.updateSummaryServerLiveStatus(1, 9500, null);
                    serverStatus = "REACHABLE";
                    apiKeyStatus = "VALID";
                    statusCode = 9500;
                    final JSONObject jsonObject = new JSONObject(response);
                    if (isUpdateApiKeyRequest) {
                        if (jsonObject.has("summaryServerAuthKeyGeneratedBy")) {
                            resp.put("summaryServerAuthKeyGeneratedBy", jsonObject.get("summaryServerAuthKeyGeneratedBy"));
                        }
                        if (jsonObject.has("summaryServerAuthKeyGeneratedOn")) {
                            resp.put("summaryServerAuthKeyGeneratedOn", jsonObject.get("summaryServerAuthKeyGeneratedOn"));
                        }
                    }
                    else if (jsonObject.has("buildnumber")) {
                        final String buildNumber = jsonObject.getString("buildnumber");
                        if (buildNumber.equals(ProductUrlLoader.getInstance().getValue("buildnumber"))) {
                            SummaryServerReachabilityChecker.logger.log(Level.INFO, "Request for summary server Live Status Update Successful = " + HttpResult);
                            LiveStatusUpdateUtil.updateSummaryServerLiveStatus(1, 9500, null);
                        }
                        else {
                            errorMsg = "ems.ss.probemgmt.version_mismatch";
                            statusCode = 9505;
                            serverStatus = "NOT_REACHABLE";
                            SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "version in Summary Server mismatched with probe, so summary server is changed to down ");
                            LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, 9505, "ems.ss.probemgmt.version_mismatch");
                        }
                        storeSummaryServerVersionInJson(buildNumber);
                        updateSummaryServeVersionInDB(buildNumber);
                    }
                }
                final long end = System.currentTimeMillis();
                SummaryServerReachabilityChecker.logger.info("Time taken for updating live status" + (end - start));
            }
            catch (final ConnectException ex) {
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                final String ipAddr = ProbeDetailsUtil.getSummaryServerIp();
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
                catch (final UnknownHostException e) {
                    statusCode = 9502;
                }
                catch (final IOException e2) {
                    statusCode = 9507;
                }
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, errorMsg);
                }
                SummaryServerReachabilityChecker.logger.log(Level.SEVERE, " connect exception while connecting to summary for live status update", ex);
                ex.printStackTrace();
            }
            catch (final SocketTimeoutException socketTimeoutException) {
                errorMsg = "Socket Timed out";
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                statusCode = 9508;
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, errorMsg);
                }
            }
            catch (final SSLHandshakeException ex2) {
                errorMsg = "No Proper Certificate Found";
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                statusCode = 9501;
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, errorMsg);
                }
                if (!isRetry) {
                    SummaryServerReachabilityChecker.logger.log(Level.INFO, "Request for SS Live Status Update UnSuccessful due to proper certification unavailablity  and trying to fetch the certificate from server");
                    HttpsHandlerUtil.processCertificateFromServer(url, null);
                    checkAndUpdateLiveStatus(apikey, true);
                }
                else {
                    SummaryServerReachabilityChecker.logger.log(Level.INFO, "Request for SS Live Status Update UnSuccessful due to proper certification unavailablity for the second time");
                }
            }
            catch (final UnknownHostException e3) {
                errorMsg = "Unknownhost";
                serverStatus = "NOT_REACHABLE";
                apiKeyStatus = "UNKNOWN";
                statusCode = 9502;
                if (!isUpdateApiKeyRequest) {
                    LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, errorMsg);
                }
                SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "Request for summary server live status is down due to UNKNOWN HOST exception", e3);
            }
            catch (final Exception e4) {
                final int httpResponse = conn.getResponseCode();
                if (httpResponse == 401) {
                    errorMsg = "ems.ss.probemgmt.api_mismatch";
                    apiKeyStatus = "INVALID";
                    serverStatus = "NOT_REACHABLE";
                    statusCode = 9506;
                    SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "api key mismatch may occured in Probe Live Status Update task , , so summary server status is changed to down", e4);
                    if (!isUpdateApiKeyRequest) {
                        LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, "ems.ss.probemgmt.api_mismatch");
                    }
                }
                else {
                    errorMsg = "ems.ss.probemgmt.unknown_issue";
                    apiKeyStatus = "UNKNOWN";
                    serverStatus = "NOT_REACHABLE";
                    statusCode = 9507;
                    if (!isUpdateApiKeyRequest) {
                        LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, statusCode, errorMsg);
                    }
                }
                SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in summary server reachable check , so summary status is changed to down ", e4);
                SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "exception while connecting to summary for live status update  ", e4);
                e4.printStackTrace();
            }
        }
        catch (final Exception e5) {
            SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in summary server reachable check", e5);
            LiveStatusUpdateUtil.updateSummaryServerLiveStatus(2, 9507, "ems.ss.probemgmt.unknown_issue");
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
    
    private static String getSummaryServerLiveStatusUpdateUrl() {
        String baseURL = "";
        try {
            final HashMap summaryProps = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerDetails();
            final String serverName = summaryProps.get("host");
            final String portNumber = summaryProps.get("port");
            final String protocol = summaryProps.get("protocol");
            baseURL = protocol + "://" + serverName + ":" + portNumber + "/servlets/summaryServerLiveStatusUpdate";
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return baseURL;
    }
    
    private static void storeSummaryServerVersionInJson(final String buildnumber) throws IOException {
        FileWriter file = null;
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("buildnumber", (Object)buildnumber);
            jsonObject.put("updatedTime", System.currentTimeMillis());
            file = new FileWriter(SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "summaryServerVersion.json");
            file.write(jsonObject.toString());
        }
        catch (final Exception e) {
            SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in storeSummaryServerVersionInJson", e);
        }
        finally {
            if (file != null) {
                file.close();
            }
        }
    }
    
    private static void updateSummaryServeVersionInDB(final String buildnumber) throws IOException {
        try {
            final String buildNumberFromCache = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("summaryServerVersion", 2);
            if (buildNumberFromCache == null || !buildnumber.equals(buildNumberFromCache)) {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("SummaryServerInfo");
                updateQuery.setUpdateColumn("BUILD_NUMBER", (Object)buildnumber);
                DataAccess.update(updateQuery);
                ApiFactoryProvider.getCacheAccessAPI().putCache("summaryServerVersion", (Object)buildnumber, 2);
            }
        }
        catch (final Exception e) {
            SummaryServerReachabilityChecker.logger.log(Level.SEVERE, "exception occurred in updateSummaryServeVersionInDB", e);
        }
    }
    
    static {
        SummaryServerReachabilityChecker.logger = Logger.getLogger("probeActionsLogger");
    }
}
