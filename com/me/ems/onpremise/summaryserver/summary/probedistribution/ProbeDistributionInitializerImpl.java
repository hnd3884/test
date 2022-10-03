package com.me.ems.onpremise.summaryserver.summary.probedistribution;

import org.json.JSONException;
import java.util.Enumeration;
import com.me.ems.summaryserver.summary.util.SummaryAPIRedirectHandler;
import java.util.Iterator;
import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.ems.summaryserver.summary.probedistribution.SummaryEventDataHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import javax.ws.rs.container.ContainerRequestContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.summaryserver.summary.probedistribution.ProbeDistributionInitializer;

public class ProbeDistributionInitializerImpl implements ProbeDistributionInitializer
{
    public static Logger logger;
    private String sourceClass;
    private static ProbeDistributionInitializerImpl instance;
    
    public ProbeDistributionInitializerImpl() {
        this.sourceClass = "ProbeDistributionInitializerImpl";
    }
    
    public static ProbeDistributionInitializerImpl getInstance() {
        if (ProbeDistributionInitializerImpl.instance == null) {
            ProbeDistributionInitializerImpl.instance = new ProbeDistributionInitializerImpl();
        }
        return ProbeDistributionInitializerImpl.instance;
    }
    
    public void addToProbeQueue(final HttpServletRequest servletRequest, final int statusCode, final ContainerRequestContext containerRequestContext, final int apiType) {
        final String requestMethod = servletRequest.getMethod();
        if ((requestMethod.equalsIgnoreCase("POST") || requestMethod.equalsIgnoreCase("DELETE") || requestMethod.equalsIgnoreCase("PUT")) && statusCode >= 200 && statusCode <= 210) {
            try {
                final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
                List targetProbes = new ArrayList();
                boolean isProbeRequest = Boolean.FALSE;
                String apiUrl = "";
                final String requestQuery = servletRequest.getQueryString();
                JSONObject probeHandlerObject = new JSONObject();
                if (servletRequest.getAttribute("targetProbes") != null) {
                    targetProbes = (List)servletRequest.getAttribute("targetProbes");
                }
                if (servletRequest.getAttribute("isProbeRequest") != null) {
                    isProbeRequest = Boolean.parseBoolean(servletRequest.getAttribute("isProbeRequest").toString());
                }
                if (servletRequest.getAttribute("probeHandlerObject") != null) {
                    probeHandlerObject = (JSONObject)servletRequest.getAttribute("probeHandlerObject");
                }
                if (isProbeRequest) {
                    final JSONObject jsonObject = this.storeHeaders(servletRequest);
                    final Object requestBody = servletRequest.getParameter("zoho-inputstream");
                    String api = "emsapi";
                    if (apiType == 1) {
                        api = "dcapi";
                    }
                    else if (apiType == 2) {
                        api = "emsapi";
                    }
                    else {
                        ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Unknown API Call.Hence, returning ...");
                    }
                    apiUrl = api + "/" + containerRequestContext.getUriInfo().getPath();
                    if (requestQuery != null) {
                        apiUrl = apiUrl + "?" + requestQuery;
                    }
                    final JSONObject reqObj = new JSONObject();
                    reqObj.put("requestMethod", (Object)requestMethod);
                    reqObj.put("requestBody", requestBody);
                    final Map props = new HashMap();
                    if (jsonObject.has("content-type")) {
                        props.put("content-type", jsonObject.get("content-type"));
                    }
                    if (jsonObject.has("accept")) {
                        props.put("accept", jsonObject.get("accept"));
                    }
                    props.put("domainName", ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName());
                    props.put("userName", ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName());
                    if (isProbeRequest && probeHandlerObject.length() > 0) {
                        reqObj.put("probeHandlerObject", (Object)probeHandlerObject);
                    }
                    final JSONArray jsonArray = new JSONArray();
                    jsonArray.put(props);
                    reqObj.put("apiUrl", (Object)apiUrl);
                    reqObj.put("requestProperty", (Object)jsonArray);
                    reqObj.put("requestQuery", (Object)requestQuery);
                    if (servletRequest.getAttribute("eventID") != null) {
                        final int eventCode = (int)servletRequest.getAttribute("eventID");
                        long eventUniqueID = -1L;
                        boolean isReqdForNewProbe = false;
                        boolean isApplicableForAllProbes = false;
                        if (targetProbes.size() == 0) {
                            isApplicableForAllProbes = true;
                        }
                        isReqdForNewProbe = (boolean)((servletRequest.getAttribute("isReqdForNewProbe") != null) ? servletRequest.getAttribute("isReqdForNewProbe") : isApplicableForAllProbes);
                        if (servletRequest.getAttribute("eventUniqueID") != null) {
                            eventUniqueID = Long.parseLong(String.valueOf(servletRequest.getAttribute("eventUniqueID")));
                        }
                        final long summaryEventID = SummaryEventDataHandler.getInstance().storeEventData(eventCode, isApplicableForAllProbes, reqObj, eventUniqueID, isReqdForNewProbe);
                        if (summaryEventID != -1L) {
                            reqObj.put("summaryEventID", summaryEventID);
                        }
                    }
                    for (final Long probeId : probeDetails.keySet()) {
                        final JSONArray propArray = new JSONArray();
                        final String authToken = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
                        props.put("ProbeAuthorization", authToken);
                        props.put("probeId", probeId);
                        propArray.put(props);
                        reqObj.put("requestProperty", (Object)propArray);
                        ProbeDistributionInitializerImpl.logger.log(Level.INFO, "JSON Output String from Common Request: " + reqObj.toString());
                        final DCQueueData dcQueueData = new DCQueueData();
                        dcQueueData.queueData = reqObj.toString();
                        dcQueueData.priority = true;
                        SyMUtil.getInstance();
                        final long postTime = SyMUtil.getCurrentTimeInMillis();
                        dcQueueData.fileName = postTime + ".json";
                        final List runningQueues = (ArrayList)ApiFactoryProvider.getCacheAccessAPI().getCache("PUSH_TO_PROBES_QUEUES");
                        if (runningQueues != null) {
                            if (targetProbes.size() == 0) {
                                ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Target is all running probe queues...");
                                final HashMap currProbeRow = probeDetails.get(probeId);
                                final String currentQ = currProbeRow.get("QUEUE_NAME").toString();
                                DCQueueHandler.addToQueue(currentQ, dcQueueData, reqObj.toString());
                                ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Added sync data from request to " + currentQ + " queue : " + dcQueueData.fileName);
                            }
                            else {
                                ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Target is specific running probe queues: " + targetProbes);
                                final HashMap currProbeRow = probeDetails.get(probeId);
                                final String currentQ = currProbeRow.get("QUEUE_NAME").toString();
                                if (!targetProbes.contains(probeId)) {
                                    continue;
                                }
                                DCQueueHandler.addToQueue(currentQ, dcQueueData, reqObj.toString());
                                ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Added sync data to " + currentQ + " queue : " + dcQueueData.fileName);
                            }
                        }
                    }
                }
            }
            catch (final Exception e) {
                ProbeDistributionInitializerImpl.logger.log(Level.SEVERE, "Exception while trying to add push to probe data to the queue", e);
            }
        }
    }
    
    public void addToProbeQueue(final JSONObject jsonObject, final List targetProbes) {
        try {
            final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
            String apiUrl = null;
            String requestMethod = null;
            String requestQuery = null;
            JSONArray requestPropertyArray = new JSONArray();
            String requestBodyType = null;
            Object requestBody = null;
            Map requestAuthProperties = null;
            String userDomainName = null;
            String userName = null;
            Long summaryEventId = -1L;
            Long eventUniqueId = -1L;
            Integer eventCode = -1;
            JSONObject probeHandlerObject = null;
            if (jsonObject.has("apiUrl")) {
                apiUrl = jsonObject.opt("apiUrl").toString();
            }
            if (jsonObject.has("requestMethod")) {
                requestMethod = jsonObject.opt("requestMethod").toString();
            }
            if (jsonObject.has("requestQuery")) {
                requestQuery = jsonObject.opt("requestQuery").toString();
            }
            if (jsonObject.has("requestProperty")) {
                requestPropertyArray = jsonObject.getJSONArray("requestProperty");
            }
            if (jsonObject.has("requestBodyType")) {
                requestBodyType = jsonObject.opt("requestBodyType").toString();
            }
            if (jsonObject.has("requestBody")) {
                requestBody = jsonObject.get("requestBody");
            }
            if (jsonObject.has("probeHandlerObject")) {
                probeHandlerObject = jsonObject.getJSONObject("probeHandlerObject");
            }
            if (jsonObject.has("requestAuthProperties")) {
                requestAuthProperties = SummaryAPIRedirectHandler.getInstance().toMap((JSONObject)jsonObject.get("requestAuthProperties"));
                userDomainName = requestAuthProperties.get("domainName").toString();
                userName = requestAuthProperties.get("userName").toString();
            }
            boolean createNewSummaryEventData = false;
            if (jsonObject.has("summaryEventID")) {
                summaryEventId = jsonObject.optLong("summaryEventID");
                if (summaryEventId == null || summaryEventId == -1L) {
                    createNewSummaryEventData = true;
                }
            }
            else {
                createNewSummaryEventData = true;
            }
            if (createNewSummaryEventData && jsonObject.has("eventID")) {
                eventCode = jsonObject.optInt("eventID");
                if (jsonObject.has("eventUniqueID")) {
                    eventUniqueId = jsonObject.optLong("eventUniqueID");
                }
                boolean isReqdForNewProbe = false;
                boolean isApplicableForAllProbes = false;
                if (targetProbes.size() == 0) {
                    isApplicableForAllProbes = true;
                }
                isReqdForNewProbe = (jsonObject.has("isReqdForNewProbe") ? jsonObject.getBoolean("isReqdForNewProbe") : isApplicableForAllProbes);
                summaryEventId = SummaryEventDataHandler.getInstance().storeEventData((int)eventCode, isApplicableForAllProbes, jsonObject, (long)eventUniqueId, isReqdForNewProbe);
                if (summaryEventId != -1L) {
                    jsonObject.put("summaryEventID", (Object)summaryEventId);
                }
            }
            if (!probeDetails.isEmpty() && requestBodyType != null && requestBodyType.equalsIgnoreCase("json")) {
                for (final Long probeId : probeDetails.keySet()) {
                    final HashMap currProbeRow = probeDetails.get(probeId);
                    if (targetProbes != null && targetProbes.size() > 0 && !targetProbes.contains(probeId)) {
                        continue;
                    }
                    final JSONObject reqObj = new JSONObject();
                    final String authToken = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
                    requestPropertyArray.getJSONObject(0).put("ProbeAuthorization", (Object)authToken);
                    requestPropertyArray.getJSONObject(0).put("probeId", (Object)probeId);
                    requestPropertyArray.getJSONObject(0).put("domainName", (Object)((userDomainName == null) ? ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName() : userDomainName));
                    requestPropertyArray.getJSONObject(0).put("userName", (Object)((userName == null) ? ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName() : userName));
                    if (requestQuery != null) {
                        apiUrl = apiUrl + "?" + requestQuery;
                    }
                    reqObj.put("apiUrl", (Object)apiUrl);
                    reqObj.put("requestMethod", (Object)requestMethod);
                    reqObj.put("requestProperty", (Object)requestPropertyArray);
                    if (requestBody != null) {
                        reqObj.put("requestBody", requestBody);
                    }
                    if (probeHandlerObject != null) {
                        reqObj.put("probeHandlerObject", (Object)probeHandlerObject);
                    }
                    ProbeDistributionInitializerImpl.logger.log(Level.INFO, "JSON Output String from Constructed Request: " + reqObj.toString());
                    final DCQueueData dcQueueData = new DCQueueData();
                    dcQueueData.queueData = reqObj.toString();
                    dcQueueData.priority = true;
                    SyMUtil.getInstance();
                    final long postTime = SyMUtil.getCurrentTimeInMillis();
                    dcQueueData.fileName = postTime + ".json";
                    List runningQueues = (ArrayList)ApiFactoryProvider.getCacheAccessAPI().getCache("PUSH_TO_PROBES_QUEUES");
                    if (runningQueues == null) {
                        runningQueues = ProbeUtil.getInstance().getQueuesFromDB();
                        ApiFactoryProvider.getCacheAccessAPI().putCache("PUSH_TO_PROBES_QUEUES", (Object)runningQueues, 2);
                    }
                    if (runningQueues == null) {
                        continue;
                    }
                    if (targetProbes != null) {
                        ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Target is specific running probe queues: " + targetProbes);
                    }
                    final String currentQ = currProbeRow.get("QUEUE_NAME").toString();
                    DCQueueHandler.addToQueue(currentQ, dcQueueData, reqObj.toString());
                    ProbeDistributionInitializerImpl.logger.log(Level.INFO, "Added sync data from JSON to " + currentQ + " queue : " + dcQueueData.fileName);
                }
            }
        }
        catch (final Exception e) {
            ProbeDistributionInitializerImpl.logger.log(Level.SEVERE, "Exception while trying to add push to probe data to the queue", e);
        }
    }
    
    public JSONObject constructRequestJson(final String uri, final String method, final String queryParam, final JSONObject requestProperty, final Object body) {
        final JSONObject requestObject = new JSONObject();
        requestObject.put("apiUrl", (Object)uri);
        requestObject.put("requestMethod", (Object)method);
        if (queryParam != null && !queryParam.isEmpty()) {
            requestObject.put("requestQuery", (Object)queryParam);
        }
        final JSONArray requestPropertyArray = new JSONArray();
        requestPropertyArray.put(0, (Object)requestProperty);
        requestObject.put("requestProperty", (Object)requestPropertyArray);
        requestObject.put("requestBodyType", (Object)"json");
        if (body != null) {
            requestObject.put("requestBody", body);
        }
        return requestObject;
    }
    
    public JSONObject storeHeaders(final HttpServletRequest request) throws JSONException {
        final JSONObject headerValueMap = new JSONObject();
        final Enumeration enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            final String headerName = enumeration.nextElement();
            headerValueMap.put(headerName.toLowerCase(), (Object)request.getHeader(headerName));
        }
        return headerValueMap;
    }
    
    public void addRequestAuthProperties(final Map props, final JSONObject requestProperty) {
        if (props.containsKey("domainName") && props.containsKey("userName")) {
            requestProperty.put("requestAuthProperties", props);
        }
    }
    
    static {
        ProbeDistributionInitializerImpl.logger = Logger.getLogger("probeActionsLogger");
        ProbeDistributionInitializerImpl.instance = null;
    }
}
