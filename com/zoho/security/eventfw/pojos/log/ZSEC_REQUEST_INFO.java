package com.zoho.security.eventfw.pojos.log;

import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import java.util.Map;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_REQUEST_INFO
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "REQUEST_INFO";
    private static final EventProcessor EVENT;
    
    public static void pushInfo(final Map<String, Object> REQINFO, final Map<String, Object> RESINFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(2);
        dataMap.put("REQINFO", REQINFO);
        dataMap.put("RESINFO", RESINFO);
        EventDataProcessor.pushData(ZSEC_REQUEST_INFO.EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_REQUEST_INFO.EVENT, "REQUEST_INFO", "pushInfo"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EVENT = EventDataProcessor.getEventProcessor(ZSEC_REQUEST_INFO.TYPE, "REQUEST_INFO");
    }
}
