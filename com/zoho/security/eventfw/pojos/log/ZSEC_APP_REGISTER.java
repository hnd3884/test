package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_APP_REGISTER
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "APP_REGISTER";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor ERROR_EVENT;
    private static final EventProcessor SUCCESS_EVENT;
    
    public static void pushException(final String URL, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(2);
        dataMap.put("URL", URL);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APP_REGISTER.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APP_REGISTER.EXCEPTION_EVENT, "APP_REGISTER", "pushException"), timer);
    }
    
    public static void pushError(final int STATUS_CODE, final String CAUSE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("STATUS_CODE", STATUS_CODE);
        dataMap.put("CAUSE", CAUSE);
        EventDataProcessor.pushData(ZSEC_APP_REGISTER.ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APP_REGISTER.ERROR_EVENT, "APP_REGISTER", "pushError"), timer);
    }
    
    public static void pushSuccess(final String URL, final String MESSAGE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("URL", URL);
        dataMap.put("MESSAGE", MESSAGE);
        EventDataProcessor.pushData(ZSEC_APP_REGISTER.SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APP_REGISTER.SUCCESS_EVENT, "APP_REGISTER", "pushSuccess"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APP_REGISTER.TYPE, "APP_REGISTER", "EXCEPTION");
        ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APP_REGISTER.TYPE, "APP_REGISTER", "ERROR");
        SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APP_REGISTER.TYPE, "APP_REGISTER", "SUCCESS");
    }
}
