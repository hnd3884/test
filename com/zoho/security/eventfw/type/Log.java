package com.zoho.security.eventfw.type;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import com.zoho.security.eventfw.config.EventConfigParser;
import org.w3c.dom.Element;
import java.util.Map;

public class Log extends EventProcessor
{
    private final Map<String, SubLog> subLogs;
    
    public Log(final Element parentElement, final Element element, final EventConfigParser config, final String type) {
        super(parentElement, element, config, type);
        this.inheritAttribute(EventFWConstants.ATTRIBUTES.SEVERITY.value(), parentElement, element);
        this.inheritAttribute(EventFWConstants.ATTRIBUTES.CALLER_INFERRER.value(), parentElement, element);
        this.inheritAttribute(EventFWConstants.ATTRIBUTES.DATATEMPLATE.value(), parentElement, element);
        this.inheritAttribute(EventFWConstants.ATTRIBUTES.DISPATCHER_TEMPLATE.value(), parentElement, element);
        this.inheritAttribute(EventFWConstants.ATTRIBUTES.TRANSFERAPI.value(), parentElement, element);
        this.subLogs = this.getSubLogs(element, config, type);
    }
    
    private void inheritAttribute(final String attribute, final Element parentElement, final Element element) {
        if (!element.hasAttribute(attribute) && parentElement.hasAttribute(attribute)) {
            element.setAttribute(attribute, parentElement.getAttribute(attribute));
        }
    }
    
    public Map<String, SubLog> getSubLogs() {
        return this.subLogs;
    }
    
    public EventProcessor getSubLog(final String subLogName) {
        if (subLogName == null) {
            return null;
        }
        return this.subLogs.get(subLogName);
    }
    
    private Map<String, SubLog> getSubLogs(final Element element, final EventConfigParser config, final String type) {
        final List<Element> subLogElements = EventConfigUtil.getChildNodesByTagName(element, EventFWConstants.TAG.SUB_TYPE.value());
        if (subLogElements.isEmpty()) {
            return null;
        }
        final Map<String, SubLog> subLogEventProcessors = new HashMap<String, SubLog>(subLogElements.size());
        for (final Element subLogEle : subLogElements) {
            final SubLog subLog = new SubLog(element, subLogEle, config, type);
            subLogEventProcessors.put(subLog.getName(), subLog);
        }
        return subLogEventProcessors.isEmpty() ? null : subLogEventProcessors;
    }
    
    public class SubLog extends EventProcessor
    {
        private final String actualName;
        
        public SubLog(final Element parentElement, final Element element, final EventConfigParser config, final String type) {
            super(parentElement, element, config, type);
            this.actualName = element.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
        }
        
        @Override
        public Map<String, Object> getEventObject(final List<? extends Map<String, Object>> map) {
            final Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
            eventMap.put(EventFWConstants.KEY.NAME.name(), Log.this.getName());
            if (this.getSeverity() != -1) {
                eventMap.put(EventFWConstants.KEY.SEVERITY.name(), this.getSeverity());
            }
            eventMap.put(EventFWConstants.KEY.SUB_TYPE.name(), this.name);
            eventMap.put(EventFWConstants.KEY.TYPE.name(), this.type);
            eventMap.put(EventFWConstants.KEY.DATA.name(), map);
            return eventMap;
        }
        
        public String getCapitalizedName() {
            final char[] chars = this.actualName.toCharArray();
            int size = 0;
            for (int ci = 0; ci < chars.length; ++ci, ++size) {
                if (chars[ci] == '_' && ci + 1 < chars.length) {
                    chars[size] = Character.toUpperCase(chars[++ci]);
                }
                else {
                    chars[size] = ((ci == 0) ? Character.toUpperCase(chars[ci]) : chars[ci]);
                }
            }
            return String.valueOf(chars, 0, size);
        }
    }
}
