package com.zoho.security.eventfw.pojos.log;

import java.util.List;
import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_AFW_RULE_MATCH
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "AFW_RULE_MATCH";
    private static final EventProcessor JSON_EXCEPTION_EVENT;
    private static final EventProcessor BLOCKED_REQUEST_STATUS_EVENT;
    
    public static void pushJsonException(final String MESSAGE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_MATCH.JSON_EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_MATCH.JSON_EXCEPTION_EVENT, "AFW_RULE_MATCH", "pushJsonException"), timer);
    }
    
    public static void pushBlockedRequestStatus(final String STATUS, final String RQ_URI, final String REMOTE_ADDRESS, final List<HashMap<String, Object>> RULE_INFO, final Map<String, Object> RULE_MATCH_INFO, final String RULE_ID, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(7);
        dataMap.put("STATUS", STATUS);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("REMOTE_ADDRESS", REMOTE_ADDRESS);
        dataMap.put("RULE_INFO", RULE_INFO);
        dataMap.put("RULE_MATCH_INFO", RULE_MATCH_INFO);
        dataMap.put("RULE_ID", RULE_ID);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_MATCH.BLOCKED_REQUEST_STATUS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_MATCH.BLOCKED_REQUEST_STATUS_EVENT, "AFW_RULE_MATCH", "pushBlockedRequestStatus"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        JSON_EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_MATCH.TYPE, "AFW_RULE_MATCH", "JSON_EXCEPTION");
        BLOCKED_REQUEST_STATUS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_MATCH.TYPE, "AFW_RULE_MATCH", "BLOCKED_REQUEST_STATUS");
    }
}
