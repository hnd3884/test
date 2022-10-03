package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_APPSENSE_LOCALWRITE
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "APPSENSE_LOCALWRITE";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor EXCEPTION_WITH_MSG_EVENT;
    private static final EventProcessor RULE_FETCH_ERROR_EVENT;
    
    public static void pushException(final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(1);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APPSENSE_LOCALWRITE.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_LOCALWRITE.EXCEPTION_EVENT, "APPSENSE_LOCALWRITE", "pushException"), timer);
    }
    
    public static void pushExceptionWithMsg(final String MESSAGE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APPSENSE_LOCALWRITE.EXCEPTION_WITH_MSG_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_LOCALWRITE.EXCEPTION_WITH_MSG_EVENT, "APPSENSE_LOCALWRITE", "pushExceptionWithMsg"), timer);
    }
    
    public static void pushRuleFetchError(final String ERROR, final String MESSAGE, final String FETCH_TYPE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(4);
        dataMap.put("ERROR", ERROR);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("FETCH_TYPE", FETCH_TYPE);
        EventDataProcessor.pushData(ZSEC_APPSENSE_LOCALWRITE.RULE_FETCH_ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_LOCALWRITE.RULE_FETCH_ERROR_EVENT, "APPSENSE_LOCALWRITE", "pushRuleFetchError"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_LOCALWRITE.TYPE, "APPSENSE_LOCALWRITE", "EXCEPTION");
        EXCEPTION_WITH_MSG_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_LOCALWRITE.TYPE, "APPSENSE_LOCALWRITE", "EXCEPTION_WITH_MSG");
        RULE_FETCH_ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_LOCALWRITE.TYPE, "APPSENSE_LOCALWRITE", "RULE_FETCH_ERROR");
    }
}
