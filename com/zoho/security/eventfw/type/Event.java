package com.zoho.security.eventfw.type;

import com.zoho.security.eventfw.config.EventConfigParser;
import org.w3c.dom.Element;

public class Event extends EventProcessor
{
    public Event(final Element parentElement, final Element element, final EventConfigParser config, final String type) {
        super(parentElement, element, config, type);
    }
}
