package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import java.util.List;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_AFW_RULE_EXPIRY
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "AFW_RULE_EXPIRY";
    private static final EventProcessor SUCCESS_EVENT;
    
    public static void pushSuccess(final String MESSAGE, final List<String> EXPIRED_RULEIDS, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("MESSAGE", MESSAGE);
        dataMap.put("EXPIRED_RULEIDS", EXPIRED_RULEIDS);
        EventDataProcessor.pushData(ZSEC_AFW_RULE_EXPIRY.SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_AFW_RULE_EXPIRY.SUCCESS_EVENT, "AFW_RULE_EXPIRY", "pushSuccess"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_AFW_RULE_EXPIRY.TYPE, "AFW_RULE_EXPIRY", "SUCCESS");
    }
}
