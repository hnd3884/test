package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_UNVALIDATED_PARAM
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "UNVALIDATED_PARAM";
    private static final EventProcessor NORMAL_RQ_EVENT;
    private static final EventProcessor ERR_RQ_EVENT;
    
    public static void pushNormalRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_PARAM_NAME, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(9);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_PARAM_NAME", RQ_PARAM_NAME);
        EventDataProcessor.pushData(ZSEC_UNVALIDATED_PARAM.NORMAL_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_UNVALIDATED_PARAM.NORMAL_RQ_EVENT, "UNVALIDATED_PARAM", "pushNormalRq"), timer);
    }
    
    public static void pushErrRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_PARAM_NAME, final String ER_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(10);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_PARAM_NAME", RQ_PARAM_NAME);
        dataMap.put("ER_URI", ER_URI);
        EventDataProcessor.pushData(ZSEC_UNVALIDATED_PARAM.ERR_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_UNVALIDATED_PARAM.ERR_RQ_EVENT, "UNVALIDATED_PARAM", "pushErrRq"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        NORMAL_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_UNVALIDATED_PARAM.TYPE, "UNVALIDATED_PARAM", "NORMAL_RQ");
        ERR_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_UNVALIDATED_PARAM.TYPE, "UNVALIDATED_PARAM", "ERR_RQ");
    }
}
