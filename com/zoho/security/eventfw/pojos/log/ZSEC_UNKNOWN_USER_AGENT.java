package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_UNKNOWN_USER_AGENT
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "UNKNOWN_USER_AGENT";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_USER_AGENT, final String FAILED_UA_INFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(8);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_USER_AGENT", RQ_USER_AGENT);
        dataMap.put("FAILED_UA_INFO", FAILED_UA_INFO);
        EventDataProcessor.pushData(ZSEC_UNKNOWN_USER_AGENT.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_UNKNOWN_USER_AGENT.EVENT, "UNKNOWN_USER_AGENT", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_UNKNOWN_USER_AGENT.TYPE, "UNKNOWN_USER_AGENT");
    }
}
