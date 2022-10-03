package com.zoho.security.eventfw.type;

import com.zoho.security.eventfw.dispatcher.TimerDispatcher;
import com.zoho.security.eventfw.CalleeInfo;
import java.util.Collections;
import com.zoho.security.eventfw.dispatcher.BatchDispatcher;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsImplProvider;
import com.zoho.security.eventfw.config.DataTemplateConfig;
import com.zoho.security.eventfw.exceptions.EventConfigurationException;
import java.util.logging.Level;
import com.zoho.security.eventfw.config.EventConfigUtil;
import com.zoho.security.eventfw.config.EventFWConstants;
import java.util.ArrayList;
import com.zoho.security.eventfw.config.EventConfigParser;
import org.w3c.dom.Element;
import com.zoho.security.eventfw.EventCallerInferrer;
import java.util.HashMap;
import com.zoho.security.eventfw.dispatcher.Dispatcher;
import com.zoho.security.eventfw.logImpl.LogImplProvider;
import com.zoho.security.eventfw.config.DataFields;
import java.util.List;
import java.util.logging.Logger;

public abstract class EventProcessor
{
    public static final Logger LOGGER;
    protected String name;
    private int severity;
    private List<DataFields> dataFields;
    private List<DataFields> builtInFields;
    private List<LogImplProvider> logImplProvider;
    protected String mapStr;
    String type;
    Dispatcher dispatcher;
    public long lastDispatchedTime;
    public boolean isPushed;
    private List<HashMap<String, Object>> batchEvents;
    private List<LogImplProvider> defaultLogAPI;
    private Dispatcher defaultDispatcher;
    private EventCallerInferrer.CallerInferrerMode callerInferrerMode;
    
    public EventProcessor(final Element parentElement, final Element element, final EventConfigParser config, final String type) {
        this.name = null;
        this.severity = -1;
        this.dataFields = null;
        this.builtInFields = null;
        this.logImplProvider = new ArrayList<LogImplProvider>();
        this.type = null;
        this.lastDispatchedTime = -1L;
        this.isPushed = false;
        this.batchEvents = null;
        this.defaultLogAPI = null;
        this.defaultDispatcher = null;
        this.callerInferrerMode = EventCallerInferrer.CallerInferrerMode.DISABLE;
        this.name = element.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value()).toUpperCase();
        final String severityAttr = this.getAttribute(EventFWConstants.ATTRIBUTES.SEVERITY.value(), parentElement, element);
        if (EventConfigUtil.isValid(severityAttr)) {
            this.severity = Integer.parseInt(severityAttr);
        }
        this.type = type.toUpperCase();
        final String templateName = this.getAttribute(EventFWConstants.ATTRIBUTES.DATATEMPLATE.value(), parentElement, element);
        final Map<String, DataTemplateConfig> dataTemplate = config.getDataTemplate();
        if (EventConfigUtil.isValid(templateName)) {
            if (!dataTemplate.containsKey(templateName)) {
                EventProcessor.LOGGER.log(Level.SEVERE, " Data template   : {0}  not found for the event {1}", new Object[] { templateName, this.name });
                throw new EventConfigurationException("DATATEMPLATE_NOT_FOUND");
            }
            final DataTemplateConfig dftc = dataTemplate.get(templateName);
            this.dataFields = dftc.getDataFields();
            this.builtInFields = dftc.getBuiltInFields();
            if (this.callerInferrerMode == EventCallerInferrer.CallerInferrerMode.DISABLE) {
                if (this.getBuiltInFields() != null && this.getBuiltInFields().stream().anyMatch(datafield -> datafield.getImplProvider() instanceof BuiltInFieldsImplProvider && datafield.getName().equals("CALLEE_CLASS_AND_METHOD_NAME"))) {
                    this.callerInferrerMode = EventCallerInferrer.CallerInferrerMode.ENABLE;
                }
                else {
                    this.callerInferrerMode = EventCallerInferrer.CallerInferrerMode.DISABLE;
                }
            }
        }
        final String calleeInferrerAttr = this.getAttribute(EventFWConstants.ATTRIBUTES.CALLER_INFERRER.value(), parentElement, element);
        if (EventConfigUtil.isValid(calleeInferrerAttr)) {
            this.callerInferrerMode = EventCallerInferrer.CallerInferrerMode.valueOf(calleeInferrerAttr.toUpperCase().replace('-', '_'));
        }
        final String logAPI = this.getAttribute(EventFWConstants.ATTRIBUTES.TRANSFERAPI.value(), parentElement, element);
        if (EventConfigUtil.isValid(logAPI)) {
            final List<String> logAPIs = EventConfigUtil.getStringAsList(logAPI, ",");
            for (final String logapi : logAPIs) {
                if (config.getLogAPIImplProviderConfigMap().containsKey(logapi)) {
                    this.logImplProvider.add(config.getLogAPIImplProviderConfigMap().get(logapi));
                }
            }
        }
        else {
            (this.defaultLogAPI = new ArrayList<LogImplProvider>()).add(config.getLogAPIImplProviderConfigMap().get("JavaLogs"));
        }
        final String dispatcherTemplateName = this.getAttribute(EventFWConstants.ATTRIBUTES.DISPATCHER_TEMPLATE.value(), parentElement, element);
        if (EventConfigUtil.isValid(dispatcherTemplateName)) {
            final Map<String, Dispatcher> dispatcherTemplate = config.getDispatcherTemplate();
            if (!dispatcherTemplate.containsKey(dispatcherTemplateName)) {
                EventProcessor.LOGGER.log(Level.SEVERE, " EVENTCONFIG : dispatcher template \" {0} \" is not defined   ", new Object[] { dispatcherTemplateName });
                throw new EventConfigurationException("DISPATCHER_TEMPLATE_NOT_FOUND");
            }
            this.setDispatcher(dispatcherTemplate.get(dispatcherTemplateName));
        }
        else {
            this.defaultDispatcher = config.getDispatcherTemplate().get(EventFWConstants.DEFAULT_DISPATCHER);
        }
    }
    
    private String getAttribute(final String attribute, final Element parentElement, final Element element) {
        if (element.hasAttribute(attribute)) {
            return element.getAttribute(attribute);
        }
        if (parentElement.hasAttribute(attribute)) {
            return parentElement.getAttribute(attribute);
        }
        return null;
    }
    
    public Map<String, Object> getEventObject(final Map<String, Object> map) {
        return this.getEventObject(Arrays.asList(map));
    }
    
    public Map<String, Object> getEventObject(final List<? extends Map<String, Object>> map) {
        final Map<String, Object> eventMap = new LinkedHashMap<String, Object>();
        eventMap.put(EventFWConstants.KEY.NAME.name(), this.name);
        if (this.severity != -1) {
            eventMap.put(EventFWConstants.KEY.SEVERITY.name(), this.severity);
        }
        eventMap.put(EventFWConstants.KEY.TYPE.name(), this.type);
        eventMap.put(EventFWConstants.KEY.DATA.name(), map);
        return eventMap;
    }
    
    public List<DataFields> getBuiltInFields() {
        return this.builtInFields;
    }
    
    public void setDispatcher(final Dispatcher dispatcherObj) {
        this.dispatcher = dispatcherObj;
        if (this.dispatcher instanceof BatchDispatcher) {
            this.lastDispatchedTime = System.currentTimeMillis();
            this.batchEvents = Collections.synchronizedList(new ArrayList<HashMap<String, Object>>());
        }
    }
    
    public void pushData(final Map<String, Object> params, final CalleeInfo calleeInfo) {
        final Dispatcher dispatcherObj = (this.dispatcher != null) ? this.dispatcher : this.defaultDispatcher;
        switch (dispatcherObj.getType()) {
            case BATCH: {
                if (this.batchEvents != null) {
                    this.batchEvents.add((HashMap)params);
                    ((BatchDispatcher)dispatcherObj).dispatchBatch(this, this.batchEvents, calleeInfo);
                    break;
                }
                break;
            }
            case TIMER: {
                dispatcherObj.dispatch(this.getLogAPIListForDispatch(), this.getEventObject(params), calleeInfo);
                break;
            }
            default: {
                dispatcherObj.dispatch(this.getLogAPIListForDispatch(), this.getEventObject(params), calleeInfo);
                break;
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getSeverity() {
        return this.severity;
    }
    
    public String getType() {
        return this.type;
    }
    
    public List<DataFields> getDataFields() {
        return this.dataFields;
    }
    
    public List<LogImplProvider> getLogAPIList() {
        return this.logImplProvider;
    }
    
    public List<LogImplProvider> getLogAPIListForDispatch() {
        return (this.logImplProvider.size() > 0) ? this.logImplProvider : this.defaultLogAPI;
    }
    
    public Dispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    public boolean isTimerDispatcher() {
        return this.dispatcher instanceof TimerDispatcher;
    }
    
    public void setLogAPIList(final List<LogImplProvider> list) {
        this.logImplProvider = list;
    }
    
    public EventCallerInferrer.CallerInferrerMode getCallerInferrerMode() {
        return this.callerInferrerMode;
    }
    
    @Override
    public String toString() {
        if (this.mapStr != null) {
            return this.mapStr;
        }
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("datafields", this.dataFields);
        map.put("builtindatafields", this.builtInFields);
        map.put("type", this.type);
        map.put("dispatcher", (this.dispatcher != null) ? this.dispatcher.getType() : null);
        if (this.defaultDispatcher != null) {
            map.put("defaultdispatcher", this.defaultDispatcher.getType());
        }
        return this.mapStr = map.toString();
    }
    
    static {
        LOGGER = Logger.getLogger(EventProcessor.class.getName());
    }
}
