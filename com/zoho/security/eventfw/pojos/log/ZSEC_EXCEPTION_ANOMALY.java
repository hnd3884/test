package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_EXCEPTION_ANOMALY
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "EXCEPTION_ANOMALY";
    private static final EventProcessor ANOMALOUS_EXCEPTION_EVENT;
    
    public static void pushAnomalousException(final String ERROR_CODE, final String CAUSE, final String REMOTE_IP, final int STATUS, final String USER_AGENT, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("ERROR_CODE", ERROR_CODE);
        dataMap.put("CAUSE", CAUSE);
        dataMap.put("REMOTE_IP", REMOTE_IP);
        dataMap.put("STATUS", STATUS);
        dataMap.put("USER_AGENT", USER_AGENT);
        EventDataProcessor.pushData(ZSEC_EXCEPTION_ANOMALY.ANOMALOUS_EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_EXCEPTION_ANOMALY.ANOMALOUS_EXCEPTION_EVENT, "EXCEPTION_ANOMALY", "pushAnomalousException"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        ANOMALOUS_EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_EXCEPTION_ANOMALY.TYPE, "EXCEPTION_ANOMALY", "ANOMALOUS_EXCEPTION");
    }
}
