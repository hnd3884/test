package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "ACCESS_DECRYPTPARAM_VIA_GETPARAMETER";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_PARAM_NAME, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(9);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_PARAM_NAME", RQ_PARAM_NAME);
        EventDataProcessor.pushData(ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER.EVENT, "ACCESS_DECRYPTPARAM_VIA_GETPARAMETER", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER.TYPE, "ACCESS_DECRYPTPARAM_VIA_GETPARAMETER");
    }
}
