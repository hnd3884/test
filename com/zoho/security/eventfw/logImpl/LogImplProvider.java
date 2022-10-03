package com.zoho.security.eventfw.logImpl;

import java.util.logging.Level;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public abstract class LogImplProvider
{
    public static final Logger LOGGER;
    String name;
    public static boolean debug;
    
    public void init(final Element ele) {
        this.name = ele.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
    }
    
    public abstract void doLog(final Map<String, Object> p0, final CalleeInfo p1);
    
    public void debug(final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        if (LogImplProvider.debug) {
            LogImplProvider.LOGGER.log(Level.SEVERE, "{0}", eventObject);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(LogImplProvider.class.getName());
        LogImplProvider.debug = false;
    }
}
