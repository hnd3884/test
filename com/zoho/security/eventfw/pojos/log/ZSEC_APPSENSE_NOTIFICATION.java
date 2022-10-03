package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_APPSENSE_NOTIFICATION
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "APPSENSE_NOTIFICATION";
    private static final EventProcessor EXCEPTION_EVENT;
    private static final EventProcessor SERVICENAME_MISTMATCH_EVENT;
    private static final EventProcessor SUCCESS_EVENT;
    private static final EventProcessor EXCEPTION_WITH_COMPONENT_EVENT;
    private static final EventProcessor EXCEPTION_WITH_COMPONENTS_EVENT;
    
    public static void pushException(final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(1);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_EVENT, "APPSENSE_NOTIFICATION", "pushException"), timer);
    }
    
    public static void pushServicenameMistmatch(final String EXCEPTION, final String ACTUAL_SERVICE, final String RECEIVED_SERVICE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("EXCEPTION", EXCEPTION);
        dataMap.put("ACTUAL_SERVICE", ACTUAL_SERVICE);
        dataMap.put("RECEIVED_SERVICE", RECEIVED_SERVICE);
        EventDataProcessor.pushData(ZSEC_APPSENSE_NOTIFICATION.SERVICENAME_MISTMATCH_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_NOTIFICATION.SERVICENAME_MISTMATCH_EVENT, "APPSENSE_NOTIFICATION", "pushServicenameMistmatch"), timer);
    }
    
    public static void pushSuccess(final String COMPONENT, final String COMP_KEY, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("COMPONENT", COMPONENT);
        dataMap.put("COMP_KEY", COMP_KEY);
        EventDataProcessor.pushData(ZSEC_APPSENSE_NOTIFICATION.SUCCESS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_NOTIFICATION.SUCCESS_EVENT, "APPSENSE_NOTIFICATION", "pushSuccess"), timer);
    }
    
    public static void pushExceptionWithComponent(final String COMPONENT, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(2);
        dataMap.put("COMPONENT", COMPONENT);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_WITH_COMPONENT_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_WITH_COMPONENT_EVENT, "APPSENSE_NOTIFICATION", "pushExceptionWithComponent"), timer);
    }
    
    public static void pushExceptionWithComponents(final String SUBCOMPONENT, final String COMPONENT, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("SUBCOMPONENT", SUBCOMPONENT);
        dataMap.put("COMPONENT", COMPONENT);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_WITH_COMPONENTS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_APPSENSE_NOTIFICATION.EXCEPTION_WITH_COMPONENTS_EVENT, "APPSENSE_NOTIFICATION", "pushExceptionWithComponents"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        EXCEPTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_NOTIFICATION.TYPE, "APPSENSE_NOTIFICATION", "EXCEPTION");
        SERVICENAME_MISTMATCH_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_NOTIFICATION.TYPE, "APPSENSE_NOTIFICATION", "SERVICENAME_MISTMATCH");
        SUCCESS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_NOTIFICATION.TYPE, "APPSENSE_NOTIFICATION", "SUCCESS");
        EXCEPTION_WITH_COMPONENT_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_NOTIFICATION.TYPE, "APPSENSE_NOTIFICATION", "EXCEPTION_WITH_COMPONENT");
        EXCEPTION_WITH_COMPONENTS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_APPSENSE_NOTIFICATION.TYPE, "APPSENSE_NOTIFICATION", "EXCEPTION_WITH_COMPONENTS");
    }
}
