package com.zoho.security.eventfw.dispatcher;

import java.util.Iterator;
import java.util.HashMap;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import com.zoho.security.eventfw.logImpl.LogImplProvider;
import java.util.List;
import org.w3c.dom.Element;
import com.zoho.security.eventfw.config.EventFWConstants;

public abstract class Dispatcher
{
    private String name;
    private EventFWConstants.DISPATCHER_TYPE type;
    
    public Dispatcher(final Element element, final EventFWConstants.DISPATCHER_TYPE dispatcherType) {
        this.name = element.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
        this.type = dispatcherType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public EventFWConstants.DISPATCHER_TYPE getType() {
        return this.type;
    }
    
    public void dispatch(final List<LogImplProvider> logAPIList, final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        for (final LogImplProvider logImpl : logAPIList) {
            final Map<String, Object> localObject = new HashMap<String, Object>();
            localObject.putAll(eventObject);
            logImpl.doLog(localObject, calleeInfo);
        }
    }
}
