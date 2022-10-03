package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_CONFIG_PUSH
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "CONFIG_PUSH";
    private static final EventProcessor CONFIG_PUSH_ERROR_EVENT;
    
    public static void pushConfigPushError(final String ERROR, final String MESSAGE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("ERROR", ERROR);
        dataMap.put("MESSAGE", MESSAGE);
        EventDataProcessor.pushData(ZSEC_CONFIG_PUSH.CONFIG_PUSH_ERROR_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_CONFIG_PUSH.CONFIG_PUSH_ERROR_EVENT, "CONFIG_PUSH", "pushConfigPushError"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        CONFIG_PUSH_ERROR_EVENT = EventDataProcessor.getEventProcessor(ZSEC_CONFIG_PUSH.TYPE, "CONFIG_PUSH", "CONFIG_PUSH_ERROR");
    }
}
