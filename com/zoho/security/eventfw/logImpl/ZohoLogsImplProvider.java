package com.zoho.security.eventfw.logImpl;

import com.zoho.logs.manager.agent.AgentConf;
import java.util.logging.Level;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;
import java.util.LinkedHashMap;
import com.zoho.logs.logclient.logger.LogAPI;
import java.util.Map;
import java.util.logging.Logger;

public class ZohoLogsImplProvider extends LogImplProvider
{
    public static final Logger LOGGER;
    private static final String DEFAULT_TYPE = "zsecevents";
    private Map<String, String> eventVsLogTypeMap;
    private static String logService;
    static LogAPI logAPI;
    
    public ZohoLogsImplProvider() {
        this.eventVsLogTypeMap = new LinkedHashMap<String, String>();
    }
    
    @Override
    public void init(final Element ele) {
        this.name = ele.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
        final List<Element> eleList = EventConfigUtil.getChildNodesByTagName(ele, EventFWConstants.TAG.TYPE.value());
        for (final Element typeEle : eleList) {
            final String eventtype = typeEle.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value()).toUpperCase();
            final String logtype = typeEle.getAttribute(EventFWConstants.ATTRIBUTES.VALUE.value());
            if (!this.eventVsLogTypeMap.containsKey(eventtype)) {
                this.eventVsLogTypeMap.put(eventtype, logtype);
            }
        }
    }
    
    @Override
    public void doLog(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        try {
            super.debug(eventObject, calleeInfo);
            if (ZohoLogsImplProvider.logAPI != null) {
                String type = "zsecevents";
                final String key = EventFWConstants.KEY.TYPE.name();
                if (eventObject.containsKey(key)) {
                    final String eventType = eventObject.get(key);
                    if (this.eventVsLogTypeMap.containsKey(eventType)) {
                        type = this.eventVsLogTypeMap.get(eventType);
                    }
                    else if (this.eventVsLogTypeMap.containsKey("DEFAULT")) {
                        type = this.eventVsLogTypeMap.get("DEFAULT");
                    }
                }
                final ExecutionTimer timer = ExecutionTimer.startInstance();
                ZohoLogsImplProvider.logAPI.log(type, ZohoLogsImplProvider.logService, (Map)eventObject);
                if (timer.getExecutionTime() > 1000L) {
                    ZohoLogsImplProvider.LOGGER.log(Level.SEVERE, "ZSEC:DBUG: {0} ms", timer.getExecutionTime());
                }
            }
        }
        catch (final Exception e) {
            ZohoLogsImplProvider.LOGGER.log(Level.SEVERE, "Unable to push event: {0} ", e);
        }
    }
    
    public String getLogServiceName() {
        return ZohoLogsImplProvider.logService;
    }
    
    static {
        LOGGER = Logger.getLogger(ZohoLogsImplProvider.class.getName());
        ZohoLogsImplProvider.logAPI = null;
        try {
            ZohoLogsImplProvider.logService = AgentConf.globalConf.getServiceName();
            ZohoLogsImplProvider.logAPI = new LogAPI();
        }
        catch (final Exception e) {
            ZohoLogsImplProvider.LOGGER.log(Level.SEVERE, "Exception occurred while initializing the LogAPI instance : {0}", e.getMessage());
        }
        catch (final Throwable e2) {
            ZohoLogsImplProvider.LOGGER.log(Level.SEVERE, "Exception occurred while initializing the servicename/LogAPI instance : {0}", e2.getMessage());
        }
    }
}
