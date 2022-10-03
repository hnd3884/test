package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_INVALID_DISPATCHER_URI
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "INVALID_DISPATCHER_URI";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final String RQ_DISPATCHER_URI, final String WC_DISPATCHER_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(14);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        dataMap.put("RQ_DISPATCHER_URI", RQ_DISPATCHER_URI);
        dataMap.put("WC_DISPATCHER_URI", WC_DISPATCHER_URI);
        EventDataProcessor.pushData(ZSEC_INVALID_DISPATCHER_URI.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_INVALID_DISPATCHER_URI.EVENT, "INVALID_DISPATCHER_URI", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_INVALID_DISPATCHER_URI.TYPE, "INVALID_DISPATCHER_URI");
    }
}
