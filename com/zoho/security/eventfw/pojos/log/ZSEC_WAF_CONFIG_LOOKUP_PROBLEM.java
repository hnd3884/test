package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_WAF_CONFIG_LOOKUP_PROBLEM
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "WAF_CONFIG_LOOKUP_PROBLEM";
    private static final EventProcessor RQ_AND_SS_WC_MISMATCH_EVENT;
    private static final EventProcessor RQ_WC_NOT_FOUND_EVENT;
    private static final EventProcessor SS_WC_NOT_FOUND_EVENT;
    
    public static void pushRqAndSsWcMismatch(final String RQ_URI, final String WC_URI, final String SS_URI, final String WC_SS_URI, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(7);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("SS_URI", SS_URI);
        dataMap.put("WC_SS_URI", WC_SS_URI);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        EventDataProcessor.pushData(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.RQ_AND_SS_WC_MISMATCH_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.RQ_AND_SS_WC_MISMATCH_EVENT, "WAF_CONFIG_LOOKUP_PROBLEM", "pushRqAndSsWcMismatch"), timer);
    }
    
    public static void pushRqWcNotFound(final String RQ_URI, final String SS_URI, final String WC_URI, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("SS_URI", SS_URI);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        EventDataProcessor.pushData(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.RQ_WC_NOT_FOUND_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.RQ_WC_NOT_FOUND_EVENT, "WAF_CONFIG_LOOKUP_PROBLEM", "pushRqWcNotFound"), timer);
    }
    
    public static void pushSsWcNotFound(final String RQ_URI, final String WC_URI, final String SS_URI, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("SS_URI", SS_URI);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        EventDataProcessor.pushData(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.SS_WC_NOT_FOUND_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.SS_WC_NOT_FOUND_EVENT, "WAF_CONFIG_LOOKUP_PROBLEM", "pushSsWcNotFound"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        RQ_AND_SS_WC_MISMATCH_EVENT = EventDataProcessor.getEventProcessor(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.TYPE, "WAF_CONFIG_LOOKUP_PROBLEM", "RQ_AND_SS_WC_MISMATCH");
        RQ_WC_NOT_FOUND_EVENT = EventDataProcessor.getEventProcessor(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.TYPE, "WAF_CONFIG_LOOKUP_PROBLEM", "RQ_WC_NOT_FOUND");
        SS_WC_NOT_FOUND_EVENT = EventDataProcessor.getEventProcessor(ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.TYPE, "WAF_CONFIG_LOOKUP_PROBLEM", "SS_WC_NOT_FOUND");
    }
}
