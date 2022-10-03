package com.zoho.security.eventfwimpl;

import com.zoho.security.eventfw.EventDataProcessor;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import java.util.logging.Logger;
import com.zoho.security.eventfw.logImpl.ZohoLogsImplProvider;

public class ZSecSinglePointLoggerImplProvider extends ZohoLogsImplProvider
{
    public static final Logger LOGGER;
    public static final String TRANSFER_API_NAME = "SinglePointLogger";
    
    public void doLog(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        SecurityUtil.getEventObjectList().add(eventObject);
    }
    
    private void doPush() {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        if (request != null) {
            final List<Object> eventList = SecurityUtil.getEventObjectList();
            if (eventList.size() > 0) {
                for (final Object eventObj : eventList) {
                    if (eventObj instanceof Map) {
                        final Map<String, Object> eo = (Map<String, Object>)eventObj;
                        super.doLog((Map)eo, (CalleeInfo)null);
                    }
                }
            }
        }
    }
    
    public static void pushEvents() {
        final ZSecSinglePointLoggerImplProvider singlePointLogger = EventDataProcessor.getParser().getLogAPIImplProviderConfigMap().get("SinglePointLogger");
        if (singlePointLogger != null) {
            singlePointLogger.doPush();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ZSecSinglePointLoggerImplProvider.class.getName());
    }
}
