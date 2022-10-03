package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        EventDataProcessor.pushData(ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM.EVENT, "GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM.TYPE, "GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM");
    }
}
