package com.zoho.security.eventfw.pojos.log;

import java.util.List;
import java.util.Map;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;

public final class ZSEC_PERFORMANCE_ANOMALY
{
    private static final EventFWConstants.TYPE TYPE;
    private static final String NAME = "PERFORMANCE_ANOMALY";
    private static final EventProcessor XSS_VALIDATION_EVENT;
    private static final EventProcessor AV_SCAN_EVENT;
    private static final EventProcessor URL_VALIDATION_EVENT;
    private static final EventProcessor AUTHENTICATION_EVENT;
    private static final EventProcessor URLRULE_VALIDATION_EVENT;
    private static final EventProcessor REGEX_MATCHES_EVENT;
    private static final EventProcessor LIVE_THROTTLE_EVENT;
    private static final EventProcessor BWAF_RULEINFO_EVENT;
    private static final EventProcessor MIME_DETECTION_EVENT;
    private static final EventProcessor CONTROL_DOS_EVENT;
    private static final EventProcessor BWAF_SCANINFO_EVENT;
    private static final EventProcessor REQ_HEADER_VALIDATION_EVENT;
    
    public static void pushXssValidation(final String RQ_URI, final String PARAM, final String FILTER, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("PARAM", PARAM);
        dataMap.put("FILTER", FILTER);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.XSS_VALIDATION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.XSS_VALIDATION_EVENT, "PERFORMANCE_ANOMALY", "pushXssValidation"), timer);
    }
    
    public static void pushAvScan(final String RQ_URI, final String FILENAME, final String DETECTEDVIRUSNAME, final String DTMIMETYPE, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(7);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("FILENAME", FILENAME);
        dataMap.put("DETECTEDVIRUSNAME", DETECTEDVIRUSNAME);
        dataMap.put("DTMIMETYPE", DTMIMETYPE);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.AV_SCAN_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.AV_SCAN_EVENT, "PERFORMANCE_ANOMALY", "pushAvScan"), timer);
    }
    
    public static void pushUrlValidation(final String RQ_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("RQ_URI", RQ_URI);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.URL_VALIDATION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.URL_VALIDATION_EVENT, "PERFORMANCE_ANOMALY", "pushUrlValidation"), timer);
    }
    
    public static void pushAuthentication(final String RQ_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("RQ_URI", RQ_URI);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.AUTHENTICATION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.AUTHENTICATION_EVENT, "PERFORMANCE_ANOMALY", "pushAuthentication"), timer);
    }
    
    public static void pushUrlruleValidation(final String RQ_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("RQ_URI", RQ_URI);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.URLRULE_VALIDATION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.URLRULE_VALIDATION_EVENT, "PERFORMANCE_ANOMALY", "pushUrlruleValidation"), timer);
    }
    
    public static void pushRegexMatches(final String RQ_URI, final String PARAM, final String REGEX, final String EXCEPTION, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(6);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("PARAM", PARAM);
        dataMap.put("REGEX", REGEX);
        dataMap.put("EXCEPTION", EXCEPTION);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.REGEX_MATCHES_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.REGEX_MATCHES_EVENT, "PERFORMANCE_ANOMALY", "pushRegexMatches"), timer);
    }
    
    public static void pushLiveThrottle(final String RQ_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("RQ_URI", RQ_URI);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.LIVE_THROTTLE_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.LIVE_THROTTLE_EVENT, "PERFORMANCE_ANOMALY", "pushLiveThrottle"), timer);
    }
    
    public static void pushBWAFRuleinfo(final String RQ_URI, final String RULEID, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(4);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("RULEID", RULEID);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.BWAF_RULEINFO_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.BWAF_RULEINFO_EVENT, "PERFORMANCE_ANOMALY", "pushBWAFRuleinfo"), timer);
    }
    
    public static void pushMimeDetection(final String RQ_URI, final String FILENAME, final String DTMIMETYPE, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(5);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("FILENAME", FILENAME);
        dataMap.put("DTMIMETYPE", DTMIMETYPE);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.MIME_DETECTION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.MIME_DETECTION_EVENT, "PERFORMANCE_ANOMALY", "pushMimeDetection"), timer);
    }
    
    public static void pushControlDos(final String RQ_URI, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(3);
        dataMap.put("RQ_URI", RQ_URI);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.CONTROL_DOS_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.CONTROL_DOS_EVENT, "PERFORMANCE_ANOMALY", "pushControlDos"), timer);
    }
    
    public static void pushBWAFScaninfo(final String RQ_URI, final List<HashMap<String, Object>> RULEINFO, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(4);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("RULEINFO", RULEINFO);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.BWAF_SCANINFO_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.BWAF_SCANINFO_EVENT, "PERFORMANCE_ANOMALY", "pushBWAFScaninfo"), timer);
    }
    
    public static void pushReqHeaderValidation(final String RQ_URI, final String HEADERS, final String COOKIES, final ExecutionTimer timer) {
        EventDataProcessor.stopRunningTimer(timer);
        final Map<String, Object> dataMap = new HashMap<String, Object>(5);
        dataMap.put("RQ_URI", RQ_URI);
        dataMap.put("HEADERS", HEADERS);
        dataMap.put("COOKIES", COOKIES);
        EventDataProcessor.pushData(ZSEC_PERFORMANCE_ANOMALY.REQ_HEADER_VALIDATION_EVENT, (Map)dataMap, EventCallerInferrer.inferClass(ZSEC_PERFORMANCE_ANOMALY.REQ_HEADER_VALIDATION_EVENT, "PERFORMANCE_ANOMALY", "pushReqHeaderValidation"), timer);
    }
    
    static {
        TYPE = EventFWConstants.TYPE.valueOf("LOG");
        XSS_VALIDATION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "XSS_VALIDATION");
        AV_SCAN_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "AV_SCAN");
        URL_VALIDATION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "URL_VALIDATION");
        AUTHENTICATION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "AUTHENTICATION");
        URLRULE_VALIDATION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "URLRULE_VALIDATION");
        REGEX_MATCHES_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "REGEX_MATCHES");
        LIVE_THROTTLE_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "LIVE_THROTTLE");
        BWAF_RULEINFO_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "BWAF_RULEINFO");
        MIME_DETECTION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "MIME_DETECTION");
        CONTROL_DOS_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "CONTROL_DOS");
        BWAF_SCANINFO_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "BWAF_SCANINFO");
        REQ_HEADER_VALIDATION_EVENT = EventDataProcessor.getEventProcessor(ZSEC_PERFORMANCE_ANOMALY.TYPE, "PERFORMANCE_ANOMALY", "REQ_HEADER_VALIDATION");
    }
}
