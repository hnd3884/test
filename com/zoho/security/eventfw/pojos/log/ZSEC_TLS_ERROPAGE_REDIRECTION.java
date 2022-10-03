package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_TLS_ERROPAGE_REDIRECTION
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "TLS_ERROPAGE_REDIRECTION";
    private static final EventProcessor REDIRECTION_SUCCESS_EVENT;
    
    public static void pushRedirectionSuccess(final String DOMAIN, final String TLS_VERSION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("DOMAIN", DOMAIN);
        dataMap.put("TLS_VERSION", TLS_VERSION);
        EventDataProcessor.pushData(ZSEC_TLS_ERROPAGE_REDIRECTION.REDIRECTION_SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_TLS_ERROPAGE_REDIRECTION.REDIRECTION_SUCCESS_EVENT, "TLS_ERROPAGE_REDIRECTION", "pushRedirectionSuccess"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        REDIRECTION_SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_TLS_ERROPAGE_REDIRECTION.TYPE, "TLS_ERROPAGE_REDIRECTION", "REDIRECTION_SUCCESS");
    }
}
