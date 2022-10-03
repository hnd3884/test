package com.zoho.security.eventfw.dispatcher;

import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;

public class DistinctDispatcher extends Dispatcher
{
    public DistinctDispatcher(final Element element, final EventFWConstants.DISPATCHER_TYPE dispatcherType) {
        super(element, dispatcherType);
    }
}
