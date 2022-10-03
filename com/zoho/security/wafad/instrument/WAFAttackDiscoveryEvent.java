package com.zoho.security.wafad.instrument;

import com.zoho.security.eventfw.type.EventProcessor;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.zoho.security.eventfw.config.EventConfigParser;
import java.util.Collection;
import com.zoho.security.eventfw.EventDataProcessor;
import java.util.ArrayList;
import com.zoho.security.eventfw.config.DataFields;
import java.util.List;
import com.zoho.security.eventfw.type.Event;

public class WAFAttackDiscoveryEvent
{
    private final String eventName;
    private final Event event;
    private final List<DataFields> fields;
    
    public WAFAttackDiscoveryEvent(final String eventName) {
        this.fields = new ArrayList<DataFields>();
        this.eventName = eventName;
        final EventConfigParser parser = EventDataProcessor.getParser();
        this.event = parser.getEvents().get(eventName);
        if (this.event == null) {
            throw new IllegalArgumentException("Event \"" + eventName + "\" not found");
        }
        if (this.event.getDataFields() != null) {
            this.fields.addAll(this.event.getDataFields());
        }
        if (this.event.getBuiltInFields() != null) {
            this.fields.addAll(this.event.getBuiltInFields());
        }
    }
    
    protected String dataFieldToParam(final String name) {
        return name;
    }
    
    public String getEventName() {
        return this.eventName;
    }
    
    public Map<String, Object> toEventData(final Map<String, Object> params) {
        final Map<String, Object> eventData = new HashMap<String, Object>();
        for (final DataFields dataField : this.fields) {
            if (params.containsKey(dataField.getName())) {
                eventData.put(dataField.getName(), params.get(dataField.getName()));
            }
        }
        return eventData;
    }
    
    public EventProcessor getEvent() {
        return (EventProcessor)this.event;
    }
}
