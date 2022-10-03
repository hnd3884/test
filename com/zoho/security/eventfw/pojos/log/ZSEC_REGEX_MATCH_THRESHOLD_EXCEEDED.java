package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "REGEX_MATCH_THRESHOLD_EXCEEDED";
    private static final EventProcessor TIME_EXCEEDED_EVENT;
    private static final EventProcessor ITER_EXCEEDED_EVENT;
    
    public static void pushTimeExceeded(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String WC_REGEX_PATTERN, final CharSequence VALUE_USED_IN_MATCH, final long WC_TIMEOUT_MAX, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("WC_REGEX_PATTERN", WC_REGEX_PATTERN);
        dataMap.put("VALUE_USED_IN_MATCH", VALUE_USED_IN_MATCH);
        dataMap.put("WC_TIMEOUT_MAX", WC_TIMEOUT_MAX);
        EventDataProcessor.pushData(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.TIME_EXCEEDED_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.TIME_EXCEEDED_EVENT, "REGEX_MATCH_THRESHOLD_EXCEEDED", "pushTimeExceeded"), timer);
    }
    
    public static void pushIterExceeded(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String WC_REGEX_PATTERN, final CharSequence VALUE_USED_IN_MATCH, final long WC_ITER_MAX, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("WC_REGEX_PATTERN", WC_REGEX_PATTERN);
        dataMap.put("VALUE_USED_IN_MATCH", VALUE_USED_IN_MATCH);
        dataMap.put("WC_ITER_MAX", WC_ITER_MAX);
        EventDataProcessor.pushData(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.ITER_EXCEEDED_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.ITER_EXCEEDED_EVENT, "REGEX_MATCH_THRESHOLD_EXCEEDED", "pushIterExceeded"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        TIME_EXCEEDED_EVENT = EventDataProcessor.getEventProcessor(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.TYPE, "REGEX_MATCH_THRESHOLD_EXCEEDED", "TIME_EXCEEDED");
        ITER_EXCEEDED_EVENT = EventDataProcessor.getEventProcessor(ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.TYPE, "REGEX_MATCH_THRESHOLD_EXCEEDED", "ITER_EXCEEDED");
    }
}
