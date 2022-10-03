package com.zoho.security.eventfw.dispatcher;

import java.util.HashMap;
import java.util.List;
import com.zoho.security.eventfw.CalleeInfo;
import com.zoho.security.eventfw.type.EventProcessor;
import java.util.Map;

public class EventDispatcherTask implements Runnable
{
    private Map<String, Object> eventObject;
    private EventProcessor config;
    private CalleeInfo calleeInfo;
    
    public EventDispatcherTask(final EventProcessor eventProcessor, final List<HashMap<String, Object>> batchEvents, final CalleeInfo calleeInfo) {
        this.eventObject = eventProcessor.getEventObject(batchEvents);
        this.config = eventProcessor;
        this.calleeInfo = calleeInfo;
    }
    
    public EventDispatcherTask(final EventProcessor eventConfiguration, final Map<String, Object> map, final CalleeInfo calleeInfo) {
        this.eventObject = eventConfiguration.getEventObject(map);
        this.config = eventConfiguration;
        this.calleeInfo = calleeInfo;
    }
    
    @Override
    public void run() {
        this.config.getDispatcher().dispatch(this.config.getLogAPIListForDispatch(), this.eventObject, this.calleeInfo);
    }
}
