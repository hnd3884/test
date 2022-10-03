package com.me.ems.onpremise.summaryserver.summary.probedistribution;

import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.net.ssl.SSLHandshakeException;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.io.OutputStreamWriter;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class PushToProbeProcessor extends DCQueueDataProcessor
{
    Logger logger;
    Logger probeOneLineLogger;
    private static int[] retryMin;
    private static HashMap<Long, Integer> incrementRetryIndexMap;
    
    public PushToProbeProcessor() {
        this.logger = Logger.getLogger("probeActionsLogger");
        this.probeOneLineLogger = Logger.getLogger("probeOneLineLogger");
    }
    
    private Integer getRetryIndexForProbe(final Long probeID) {
        if (!PushToProbeProcessor.incrementRetryIndexMap.containsKey(probeID)) {
            PushToProbeProcessor.incrementRetryIndexMap.put(probeID, 0);
        }
        return PushToProbeProcessor.incrementRetryIndexMap.get(probeID);
    }
    
    private void setRetryIndexForProbe(final Long probeID, final Integer retryIndex) {
        PushToProbeProcessor.incrementRetryIndexMap.put(probeID, retryIndex);
    }
    
    public boolean preProcessQueueData(final DCQueueData qData) {
        try {
            final Long probeID = (Long)DBUtil.getValueFromDB("ProbeDetailsExtn", "QUEUE_NAME", (Object)this.queueName, "PROBE_ID");
            final int probeStatus = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getProbeLiveStatus(probeID);
            if (probeStatus == 1) {
                this.setRetryIndexForProbe(probeID, 0);
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while preProcessQueueData", e);
        }
        return false;
    }
    
    public boolean initiateQRetry() {
        try {
            final Long probeID = (Long)DBUtil.getValueFromDB("ProbeDetailsExtn", "QUEUE_NAME", (Object)this.queueName, "PROBE_ID");
            Integer incrementRetryIndex = this.getRetryIndexForProbe(probeID);
            final Long retryMillis = (Long)(PushToProbeProcessor.retryMin[incrementRetryIndex % PushToProbeProcessor.retryMin.length] * 60 * 1000);
            final DCQueue queue = DCQueueHandler.getQueue(this.queueName);
            queue.suspendQExecution();
            Thread.sleep(retryMillis);
            queue.resumeQExecution();
            final Long probeID2 = probeID;
            final Integer retryIndex = incrementRetryIndex;
            ++incrementRetryIndex;
            this.setRetryIndexForProbe(probeID2, retryIndex);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in retryHandling", e);
        }
        return true;
    }
    
    public void processData(final DCQueueData qData) {
        this.processData(qData, false);
    }
    
    public void processData(final DCQueueData qData, final boolean isRetry) {
        final long start = System.currentTimeMillis();
        String url = "";
        Long probeId = null;
        Long summaryEventId = null;
        int HttpResult = -1;
        try {
            final String inputString = (String)qData.queueData;
            final JSONObject json = this.convertStringToJSONObject(inputString);
            String authorization = null;
            String contentType = null;
            String accept = null;
            String domainName = null;
            String userName = null;
            final JSONArray jsonArray = json.getJSONArray("requestProperty");
            for (int index = 0; index < jsonArray.length(); ++index) {
                final JSONObject jsonObj = (JSONObject)jsonArray.get(index);
                if (jsonObj.has("ProbeAuthorization")) {
                    authorization = jsonObj.getString("ProbeAuthorization");
                }
                if (jsonObj.has("probeId")) {
                    probeId = (Long)jsonObj.get("probeId");
                }
                if (jsonObj.has("content-type")) {
                    contentType = jsonObj.getString("content-type");
                }
                if (jsonObj.has("accept")) {
                    accept = jsonObj.getString("accept");
                }
                if (jsonObj.has("domainName")) {
                    domainName = jsonObj.getString("domainName");
                }
                if (jsonObj.has("userName")) {
                    userName = jsonObj.getString("userName");
                }
            }
            final String baseUrl = ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().getProbeServerBaseURL(probeId);
            url = baseUrl + json.getString("apiUrl");
            final String requestMethod = json.getString("requestMethod");
            String requestBody = "";
            if (json.has("summaryEventID")) {
                summaryEventId = json.getLong("summaryEventID");
            }
            if (json.has("probeHandlerObject")) {
                final JSONObject probeHandlerObject = json.getJSONObject("probeHandlerObject");
                JSONObject requestBodyInjectObj = new JSONObject();
                if (json.has("requestBody")) {
                    if (json.get("requestBody") instanceof String) {
                        requestBodyInjectObj = new JSONObject((String)json.get("requestBody"));
                    }
                    else if (json.get("requestBody") instanceof JSONObject) {
                        requestBodyInjectObj = json.getJSONObject("requestBody");
                    }
                }
                requestBodyInjectObj.put("probeHandlerObject", (Object)probeHandlerObject);
                this.logger.log(Level.INFO, "Request body has additional handling injected " + probeHandlerObject.toString());
                requestBody = requestBodyInjectObj.toString();
                this.logger.log(Level.INFO, "Request body after additional handling injected " + requestBodyInjectObj.toString());
            }
            else if (json.has("requestBody")) {
                if (json.get("requestBody") instanceof String) {
                    requestBody = (String)json.get("requestBody");
                }
                else if (json.get("requestBody") instanceof JSONArray) {
                    requestBody = json.getJSONArray("requestBody").toString();
                }
                else if (json.get("requestBody") instanceof JSONObject) {
                    requestBody = json.getJSONObject("requestBody").toString();
                }
            }
            final URL object = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection)object.openConnection();
            final String encryptedStr = ApiFactoryProvider.getCryptoAPI().encrypt(userName + "::" + domainName, authorization, (String)null);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("ProbeAuthorization", authorization);
            conn.setRequestProperty("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
            conn.setRequestProperty("userDomain", encryptedStr);
            conn.setRequestProperty("probeDistributionRequest", "true");
            if (contentType != null) {
                conn.setRequestProperty("content-type", contentType);
            }
            conn.setRequestProperty("accept", accept);
            conn.setRequestMethod(requestMethod);
            if (url.contains("https")) {}
            final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            if (inputString != null && requestBody != null) {
                wr.write(requestBody);
            }
            wr.flush();
            wr.close();
            HttpResult = conn.getResponseCode();
            if (HttpResult >= 200 && HttpResult <= 210) {
                this.logger.log(Level.INFO, "Request successful. Response code: " + HttpResult);
                this.probeOneLineLogger.log(Level.INFO, "Request Successful for " + url + " " + requestBody);
            }
            else {
                this.logger.log(Level.INFO, "Request may be unsuccessful. Response code: " + HttpResult);
                this.probeOneLineLogger.log(Level.WARNING, "Request Unsuccessful for " + url + " " + requestBody);
            }
        }
        catch (final SSLHandshakeException ex) {
            if (!isRetry) {
                this.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes , rechecking certificate", ex);
                HttpsHandlerUtil.processCertificateFromServer(url, probeId);
                this.processData(qData, true);
            }
            else {
                this.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes FOR the url " + url, ex);
            }
        }
        catch (final MalformedURLException ex2) {
            this.logger.log(Level.SEVERE, "Malformed exception while pushing to probes", ex2);
        }
        catch (final IOException ex3) {
            this.logger.log(Level.SEVERE, "IO exception while pushing to probes", ex3);
            this.initiateQRetry();
        }
        catch (final Exception ex4) {
            this.logger.log(Level.SEVERE, "Exception while pushing to probes", ex4);
        }
        finally {
            if (summaryEventId != null) {
                this.addPushToProbesEventLog(summaryEventId, probeId, HttpResult);
            }
            final long end = System.currentTimeMillis();
            this.logger.log(Level.INFO, "Time taken for processing: " + (end - start) + " ms");
        }
    }
    
    private void addPushToProbesEventLog(final Long summaryEventID, final Long probeId, final int httpResult) {
        final Long currentTimeMillis = System.currentTimeMillis();
        try {
            final Criteria summaryEventIDCri = new Criteria(new Column("PushToProbesEventLog", "SUMMARY_EVENT_ID"), (Object)summaryEventID, 0);
            final Criteria probeIDCri = new Criteria(new Column("PushToProbesEventLog", "PROBE_ID"), (Object)probeId, 0);
            final Criteria criteria = summaryEventIDCri.and(probeIDCri);
            final DataObject dataObject = DataAccess.get("PushToProbesEventLog", criteria);
            if (dataObject.isEmpty()) {
                final Row eventRow = new Row("PushToProbesEventLog");
                eventRow.set("SUMMARY_EVENT_ID", (Object)summaryEventID);
                eventRow.set("PROBE_ID", (Object)probeId);
                eventRow.set("PUSH_EVENT_TIME", (Object)currentTimeMillis);
                eventRow.set("PUSH_EVENT_STATUS", (Object)httpResult);
                eventRow.set("RETRY_COUNT", (Object)0);
                dataObject.addRow(eventRow);
            }
            else {
                final Row eventRow = dataObject.getFirstRow("PushToProbesEventLog");
                eventRow.set("PUSH_EVENT_TIME", (Object)currentTimeMillis);
                eventRow.set("PUSH_EVENT_STATUS", (Object)httpResult);
                int retryCount = eventRow.getInt("RETRY_COUNT");
                ++retryCount;
                eventRow.set("RETRY_COUNT", (Object)retryCount);
                dataObject.updateRow(eventRow);
            }
            DataAccess.update(dataObject);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception in addPushToProbesEventLog", (Throwable)e);
        }
    }
    
    private JSONObject convertStringToJSONObject(final String str) throws JSONException {
        JSONObject jsonData = null;
        if (str != null) {
            jsonData = new JSONObject(str);
        }
        return jsonData;
    }
    
    static {
        PushToProbeProcessor.retryMin = new int[] { 2, 5, 10, 30, 60, 180, 360, 720 };
        PushToProbeProcessor.incrementRetryIndexMap = new HashMap<Long, Integer>();
    }
}
