package com.zoho.security.eventfw.logImpl;

import java.util.logging.Level;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JavaLogsImplProvider extends LogImplProvider
{
    private static final String DEFAULT_LEVEL = "INFO";
    public static final Logger LOGGER;
    private Map<String, String> eventVsLevelMap;
    
    public JavaLogsImplProvider() {
        this.eventVsLevelMap = new LinkedHashMap<String, String>();
    }
    
    @Override
    public void init(final Element ele) {
        this.name = ele.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
        final List<Element> eleList = EventConfigUtil.getChildNodesByTagName(ele, EventFWConstants.TAG.TYPE.value());
        for (final Element typeEle : eleList) {
            final String eventtype = typeEle.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value()).toUpperCase();
            final String level = typeEle.getAttribute(EventFWConstants.ATTRIBUTES.VALUE.value()).toUpperCase();
            if (!this.eventVsLevelMap.containsKey(eventtype)) {
                this.eventVsLevelMap.put(eventtype, level);
            }
        }
    }
    
    @Override
    public void doLog(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        super.debug(eventObject, calleeInfo);
        String level = "INFO";
        final String key = EventFWConstants.KEY.TYPE.name();
        if (eventObject.containsKey(key)) {
            final String eventType = eventObject.get(key);
            if (this.eventVsLevelMap.containsKey(eventType)) {
                level = this.eventVsLevelMap.get(eventType);
            }
            else if (this.eventVsLevelMap.containsKey("DEFAULT")) {
                level = this.eventVsLevelMap.get("DEFAULT");
            }
        }
        JavaLogsImplProvider.LOGGER.logp(this.getLoggerLevel(level), calleeInfo.getMonitoringClassName(), calleeInfo.getMonitoringMethodName(), eventObject.toString());
    }
    
    public Level getLoggerLevel(final String loglevel) {
        switch (loglevel) {
            case "WARNING": {
                return Level.WARNING;
            }
            case "INFO": {
                return Level.INFO;
            }
            case "CONFIG": {
                return Level.CONFIG;
            }
            case "FINER": {
                return Level.FINER;
            }
            case "FINEST": {
                return Level.FINEST;
            }
            case "FINE": {
                return Level.FINE;
            }
            default: {
                return Level.SEVERE;
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(JavaLogsImplProvider.class.getName());
    }
}
