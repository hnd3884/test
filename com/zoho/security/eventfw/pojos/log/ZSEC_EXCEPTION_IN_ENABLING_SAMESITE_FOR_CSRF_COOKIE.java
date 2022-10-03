package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_METHOD, final String RQ_REFERRER, final String RQ_USER_AGENT, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_METHOD", RQ_METHOD);
        dataMap.put("RQ_REFERRER", RQ_REFERRER);
        dataMap.put("RQ_USER_AGENT", RQ_USER_AGENT);
        EventDataProcessor.pushData(ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE.EVENT, "EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE.TYPE, "EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE");
    }
}
