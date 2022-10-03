package com.zoho.security.eventfw.pojos.log;

import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "SERVLET_STD_ATTRIBUTE_MISUSE";
    private static final EventProcessor FWD_ATTR_SET_IN_RQ_EVENT;
    private static final EventProcessor ASYNC_ATTR_SET_IN_RQ_EVENT;
    private static final EventProcessor ERR_ATTR_NOTSET_IN_ERR_RQ_EVENT;
    private static final EventProcessor INC_ATTR_SET_IN_RQ_EVENT;
    
    public static void pushFwdAttrSetInRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final String SS_ATTR_NAME, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        dataMap.put("SS_ATTR_NAME", SS_ATTR_NAME);
        EventDataProcessor.pushData(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.FWD_ATTR_SET_IN_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.FWD_ATTR_SET_IN_RQ_EVENT, "SERVLET_STD_ATTRIBUTE_MISUSE", "pushFwdAttrSetInRq"), timer);
    }
    
    public static void pushAsyncAttrSetInRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final String SS_ATTR_NAME, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        dataMap.put("SS_ATTR_NAME", SS_ATTR_NAME);
        EventDataProcessor.pushData(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.ASYNC_ATTR_SET_IN_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.ASYNC_ATTR_SET_IN_RQ_EVENT, "SERVLET_STD_ATTRIBUTE_MISUSE", "pushAsyncAttrSetInRq"), timer);
    }
    
    public static void pushErrAttrNotsetInErrRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String RQ_TYPE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(7);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("RQ_TYPE", RQ_TYPE);
        EventDataProcessor.pushData(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.ERR_ATTR_NOTSET_IN_ERR_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.ERR_ATTR_NOTSET_IN_ERR_RQ_EVENT, "SERVLET_STD_ATTRIBUTE_MISUSE", "pushErrAttrNotsetInErrRq"), timer);
    }
    
    public static void pushIncAttrSetInRq(final String RQ_URI, final String WC_URI_PREFIX, final String WC_URI, final String WC_METHOD, final String WC_OPERATION, final String SS_SERVLET_PATH, final String SS_PATH_INFO, final String SS_ATTR_NAME, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(11);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("WC_URI_PREFIX", WC_URI_PREFIX);
        dataMap.put("WC_URI", WC_URI);
        dataMap.put("WC_METHOD", WC_METHOD);
        dataMap.put("WC_OPERATION", WC_OPERATION);
        dataMap.put("SS_SERVLET_PATH", SS_SERVLET_PATH);
        dataMap.put("SS_PATH_INFO", SS_PATH_INFO);
        dataMap.put("SS_ATTR_NAME", SS_ATTR_NAME);
        EventDataProcessor.pushData(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.INC_ATTR_SET_IN_RQ_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.INC_ATTR_SET_IN_RQ_EVENT, "SERVLET_STD_ATTRIBUTE_MISUSE", "pushIncAttrSetInRq"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        FWD_ATTR_SET_IN_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.TYPE, "SERVLET_STD_ATTRIBUTE_MISUSE", "FWD_ATTR_SET_IN_RQ");
        ASYNC_ATTR_SET_IN_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.TYPE, "SERVLET_STD_ATTRIBUTE_MISUSE", "ASYNC_ATTR_SET_IN_RQ");
        ERR_ATTR_NOTSET_IN_ERR_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.TYPE, "SERVLET_STD_ATTRIBUTE_MISUSE", "ERR_ATTR_NOTSET_IN_ERR_RQ");
        INC_ATTR_SET_IN_RQ_EVENT = EventDataProcessor.getEventProcessor(ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.TYPE, "SERVLET_STD_ATTRIBUTE_MISUSE", "INC_ATTR_SET_IN_RQ");
    }
}
