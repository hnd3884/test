package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_INVALID_CLICKJACKING_PREVENTION
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "INVALID_CLICKJACKING_PREVENTION";
    private static final EventProcessor CROSS_DOMAIN_ACCESS_EVENT;
    private static final EventProcessor SAME_DOMAIN_ACCESS_EVENT;
    
    public static void pushCrossDomainAccess(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String MATCHED_DOMAIN, final String RQ_REFERRER, final String RQ_USER_AGENT, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("MATCHED_DOMAIN", MATCHED_DOMAIN);
        dataMap.put("RQ_REFERRER", RQ_REFERRER);
        dataMap.put("RQ_USER_AGENT", RQ_USER_AGENT);
        EventDataProcessor.pushData(ZSEC_INVALID_CLICKJACKING_PREVENTION.CROSS_DOMAIN_ACCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_INVALID_CLICKJACKING_PREVENTION.CROSS_DOMAIN_ACCESS_EVENT, "INVALID_CLICKJACKING_PREVENTION", "pushCrossDomainAccess"), timer);
    }
    
    public static void pushSameDomainAccess(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String MATCHED_DOMAIN, final String RQ_REFERRER, final String RQ_USER_AGENT, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("MATCHED_DOMAIN", MATCHED_DOMAIN);
        dataMap.put("RQ_REFERRER", RQ_REFERRER);
        dataMap.put("RQ_USER_AGENT", RQ_USER_AGENT);
        EventDataProcessor.pushData(ZSEC_INVALID_CLICKJACKING_PREVENTION.SAME_DOMAIN_ACCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_INVALID_CLICKJACKING_PREVENTION.SAME_DOMAIN_ACCESS_EVENT, "INVALID_CLICKJACKING_PREVENTION", "pushSameDomainAccess"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        CROSS_DOMAIN_ACCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_INVALID_CLICKJACKING_PREVENTION.TYPE, "INVALID_CLICKJACKING_PREVENTION", "CROSS_DOMAIN_ACCESS");
        SAME_DOMAIN_ACCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_INVALID_CLICKJACKING_PREVENTION.TYPE, "INVALID_CLICKJACKING_PREVENTION", "SAME_DOMAIN_ACCESS");
    }
}
