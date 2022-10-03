package com.zoho.security.eventfw.dispatcher;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.HashMap;
import com.zoho.security.eventfw.type.EventProcessor;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;

public class BatchDispatcher extends Dispatcher
{
    private int countThreshold;
    private long timeThresholdInMillis;
    
    public BatchDispatcher(final Element element, final EventFWConstants.DISPATCHER_TYPE dispatcherType) {
        super(element, dispatcherType);
        this.countThreshold = 1000;
        this.timeThresholdInMillis = -1L;
        final List<Element> thresholdEleList = EventConfigUtil.getChildNodesByTagName(element, EventFWConstants.TAG.THRESHOLD.value());
        for (final Element thresholdEle : thresholdEleList) {
            final String type = thresholdEle.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value());
            final EventFWConstants.THRESHOLD_TYPE thrsholdType = EventFWConstants.THRESHOLD_TYPE.getType(type.toUpperCase());
            if (thrsholdType != null) {
                final String value = thresholdEle.getAttribute(EventFWConstants.ATTRIBUTES.VALUE.value());
                if (!EventConfigUtil.isValid(value)) {
                    continue;
                }
                switch (thrsholdType) {
                    case COUNT: {
                        this.countThreshold = Integer.parseInt(value);
                        continue;
                    }
                    case TIME: {
                        this.timeThresholdInMillis = EventConfigUtil.getTimeInMilliSeconds(value);
                        continue;
                    }
                }
            }
        }
    }
    
    public long getTimeThresholdInMillis() {
        return this.timeThresholdInMillis;
    }
    
    public int getCountThreshold() {
        return this.countThreshold;
    }
    
    public void dispatchBatch(final EventProcessor eventProcessor, final List<HashMap<String, Object>> batchEvents, final CalleeInfo calleeInfo) {
        if (!eventProcessor.isPushed && (batchEvents.size() >= this.getCountThreshold() || System.currentTimeMillis() - eventProcessor.lastDispatchedTime >= this.getTimeThresholdInMillis())) {
            eventProcessor.lastDispatchedTime = System.currentTimeMillis();
            this.dispatchData(eventProcessor, batchEvents, calleeInfo);
            eventProcessor.isPushed = false;
        }
    }
    
    public void dispatchBatch(final EventProcessor eventProcessor, final Map<String, Object> map, final CalleeInfo calleeInfo) {
        if (!eventProcessor.isPushed && (map.size() >= this.getCountThreshold() || System.currentTimeMillis() - eventProcessor.lastDispatchedTime >= this.getTimeThresholdInMillis())) {
            eventProcessor.lastDispatchedTime = System.currentTimeMillis();
            this.dispatchData(eventProcessor, map, calleeInfo);
            eventProcessor.isPushed = false;
        }
    }
    
    private synchronized void dispatchData(final EventProcessor eventProcessor, final List<HashMap<String, Object>> batchEvents, final CalleeInfo calleeInfo) {
        if (!eventProcessor.isPushed && batchEvents.size() > 0) {
            eventProcessor.isPushed = true;
            final List<HashMap<String, Object>> localbatchEvents = new ArrayList<HashMap<String, Object>>();
            localbatchEvents.addAll(batchEvents);
            batchEvents.clear();
            EventDispatcherExecutor.executeRunnableTask(new EventDispatcherTask(eventProcessor, localbatchEvents, calleeInfo));
        }
    }
    
    private synchronized void dispatchData(final EventProcessor eventProcessor, final Map<String, Object> map, final CalleeInfo calleeInfo) {
        if (!eventProcessor.isPushed && map.size() > 0) {
            eventProcessor.isPushed = true;
            final Map<String, Object> localmap = new HashMap<String, Object>();
            localmap.putAll(map);
            map.clear();
            EventDispatcherExecutor.executeRunnableTask(new EventDispatcherTask(eventProcessor, localmap, calleeInfo));
        }
    }
}
