package com.zoho.security.eventfw.dispatcher;

import com.zoho.security.eventfw.CalleeInfo;
import java.util.Map;
import com.zoho.security.eventfw.logImpl.LogImplProvider;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import org.w3c.dom.Element;

public class TimerDispatcher extends Dispatcher
{
    protected static final String TIME_TAKEN = "TIME_TAKEN";
    protected long timeThresholdInMillis;
    
    public TimerDispatcher(final Element element, final EventFWConstants.DISPATCHER_TYPE dispatcherType) {
        super(element, dispatcherType);
        this.timeThresholdInMillis = -1L;
        final List<Element> thresholdEleList = EventConfigUtil.getChildNodesByTagName(element, EventFWConstants.TAG.THRESHOLD.value());
        for (final Element thresholdEle : thresholdEleList) {
            final String type = thresholdEle.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value());
            final EventFWConstants.THRESHOLD_TYPE thrsholdType = EventFWConstants.THRESHOLD_TYPE.getType(type.toUpperCase());
            if (thrsholdType != null && thrsholdType == EventFWConstants.THRESHOLD_TYPE.EXECUTION_TIME) {
                final String value = thresholdEle.getAttribute(EventFWConstants.ATTRIBUTES.VALUE.value());
                if (!EventConfigUtil.isValid(value)) {
                    continue;
                }
                this.timeThresholdInMillis = EventConfigUtil.getTimeInMilliSeconds(value);
            }
        }
    }
    
    public long getTimeThresholdInMillis() {
        return this.timeThresholdInMillis;
    }
    
    @Override
    public void dispatch(final List<LogImplProvider> logAPIList, final Map<String, Object> eventObject, final CalleeInfo calleeInfo) {
        if (eventObject.containsKey(EventFWConstants.KEY.DATA.name())) {
            final Map<String, Object> map = eventObject.get(EventFWConstants.KEY.DATA.name()).get(0);
            if (map.containsKey("TIME_TAKEN")) {
                final Long timetaken = map.get("TIME_TAKEN");
                if (this.timeThresholdInMillis != -1L && timetaken > this.timeThresholdInMillis) {
                    super.dispatch(logAPIList, eventObject, calleeInfo);
                }
            }
        }
    }
}
