package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_CSP_STATUS_FETCH
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "CSP_STATUS_FETCH";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor STATUS_FETCH_ERROR_EVENT;
    private static final EventProcessor SUCCESS_EVENT;
    
    public static void pushException(final String MESSAGE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_CSP_STATUS_FETCH.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_CSP_STATUS_FETCH.EXCEPTION_EVENT, "CSP_STATUS_FETCH", "pushException"), timer);
    }
    
    public static void pushStatusFetchError(final int STATUS_CODE, final String CAUSE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("STATUS_CODE", STATUS_CODE);
        dataMap.put("CAUSE", CAUSE);
        EventDataProcessor.pushData(ZSEC_CSP_STATUS_FETCH.STATUS_FETCH_ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_CSP_STATUS_FETCH.STATUS_FETCH_ERROR_EVENT, "CSP_STATUS_FETCH", "pushStatusFetchError"), timer);
    }
    
    public static void pushSuccess(final String TYPE, final String STATUS, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("TYPE", TYPE);
        dataMap.put("STATUS", STATUS);
        EventDataProcessor.pushData(ZSEC_CSP_STATUS_FETCH.SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_CSP_STATUS_FETCH.SUCCESS_EVENT, "CSP_STATUS_FETCH", "pushSuccess"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_CSP_STATUS_FETCH.TYPE, "CSP_STATUS_FETCH", "EXCEPTION");
        STATUS_FETCH_ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_CSP_STATUS_FETCH.TYPE, "CSP_STATUS_FETCH", "STATUS_FETCH_ERROR");
        SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_CSP_STATUS_FETCH.TYPE, "CSP_STATUS_FETCH", "SUCCESS");
    }
}
