package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_AFW_RULE_CONVERSION
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "AFW_RULE_CONVERSION";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor ERROR_EVENT;
    
    public static void pushException(final String SOURCE, final String RULE_KEY_VALUE, final String MESSAGE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(5);
        dataMap.put("SOURCE", SOURCE);
        dataMap.put("RULE_KEY_VALUE", RULE_KEY_VALUE);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_CONVERSION.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_CONVERSION.EXCEPTION_EVENT, "AFW_RULE_CONVERSION", "pushException"), timer);
    }
    
    public static void pushError(final String SOURCE, final String RULE_KEY_VALUE, final String ERROR, final String MESSAGE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(5);
        dataMap.put("SOURCE", SOURCE);
        dataMap.put("RULE_KEY_VALUE", RULE_KEY_VALUE);
        dataMap.put("ERROR", ERROR);
        dataMap.put("MESSAGE", MESSAGE);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_CONVERSION.ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_CONVERSION.ERROR_EVENT, "AFW_RULE_CONVERSION", "pushError"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_CONVERSION.TYPE, "AFW_RULE_CONVERSION", "EXCEPTION");
        ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_CONVERSION.TYPE, "AFW_RULE_CONVERSION", "ERROR");
    }
}
