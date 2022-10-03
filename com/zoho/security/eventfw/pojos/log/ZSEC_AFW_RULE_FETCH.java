package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_AFW_RULE_FETCH
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "AFW_RULE_FETCH";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor RULE_FETCH_SUCCESS_EVENT;
    private static final EventProcessor RULE_FETCH_ERROR_EVENT;
    
    public static void pushException(final String MESSAGE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_FETCH.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_FETCH.EXCEPTION_EVENT, "AFW_RULE_FETCH", "pushException"), timer);
    }
    
    public static void pushRuleFetchSuccess(final int NO_OF_RULES, final String RULEFETCH_TYPE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("NO_OF_RULES", NO_OF_RULES);
        dataMap.put("RULEFETCH_TYPE", RULEFETCH_TYPE);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_FETCH.RULE_FETCH_SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_FETCH.RULE_FETCH_SUCCESS_EVENT, "AFW_RULE_FETCH", "pushRuleFetchSuccess"), timer);
    }
    
    public static void pushRuleFetchError(final int STATUS_CODE, final String CAUSE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("STATUS_CODE", STATUS_CODE);
        dataMap.put("CAUSE", CAUSE);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_FETCH.RULE_FETCH_ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_FETCH.RULE_FETCH_ERROR_EVENT, "AFW_RULE_FETCH", "pushRuleFetchError"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_FETCH.TYPE, "AFW_RULE_FETCH", "EXCEPTION");
        RULE_FETCH_SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_FETCH.TYPE, "AFW_RULE_FETCH", "RULE_FETCH_SUCCESS");
        RULE_FETCH_ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_FETCH.TYPE, "AFW_RULE_FETCH", "RULE_FETCH_ERROR");
    }
}
