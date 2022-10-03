package com.zoho.security.eventfw.pojos.event;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import java.util.Set;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_SECRET_PARAM_IN_QUERYSTRING
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "SECRET_PARAM_IN_QUERYSTRING";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_TYPE, final String RQ_METHOD, final String RQ_OVERRIDE_METHOD, final String RQ_ORIGIN, final String RQ_USER_AGENT, final Set<String> RQ_PARAM_NAMES, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(13);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_TYPE", RQ_TYPE);
        dataMap.put("RQ_METHOD", RQ_METHOD);
        dataMap.put("RQ_OVERRIDE_METHOD", RQ_OVERRIDE_METHOD);
        dataMap.put("RQ_ORIGIN", RQ_ORIGIN);
        dataMap.put("RQ_USER_AGENT", RQ_USER_AGENT);
        dataMap.put("RQ_PARAM_NAMES", RQ_PARAM_NAMES);
        EventDataProcessor.pushData(ZSEC_SECRET_PARAM_IN_QUERYSTRING.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_SECRET_PARAM_IN_QUERYSTRING.EVENT, "SECRET_PARAM_IN_QUERYSTRING", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("EVENT");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_SECRET_PARAM_IN_QUERYSTRING.TYPE, "SECRET_PARAM_IN_QUERYSTRING");
    }
}
