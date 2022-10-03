package com.zoho.security.eventfw;

import com.zoho.security.eventfw.exceptions.EventConfigurationException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.config.DataFields;
import com.zoho.security.eventfw.type.Log;
import java.util.logging.Level;
import com.zoho.security.eventfw.type.EventProcessor;
import java.util.Map;
import com.zoho.security.eventfw.config.EventFWConstants;
import com.zoho.security.eventfw.config.EventConfigParser;
import java.util.logging.Logger;

public class EventDataProcessor
{
    public static final Logger LOGGER;
    private static final String WAF_EVENTS_FILENAME = "waf-events.xml";
    static EventConfigParser parser;
    private static boolean defaultConfigLoaded;
    
    public static void pushData(final EventFWConstants.TYPE type, final String name, final Map<String, Object> params, final CalleeInfo calleeInfo, final ExecutionTimer timer) {
        pushData(getEventProcessor(type, name), params, calleeInfo, timer);
    }
    
    public static void pushData(final EventFWConstants.TYPE type, final String name, final String subLogName, final Map<String, Object> params, final CalleeInfo calleeInfo, final ExecutionTimer timer) {
        pushData(getEventProcessor(type, name, subLogName), params, calleeInfo, timer);
    }
    
    public static void pushData(final EventProcessor eventProcessor, final Map<String, Object> params, final CalleeInfo calleeInfo, final ExecutionTimer timer) {
        if (eventProcessor == null) {
            return;
        }
        try {
            fillBuiltInFields(params, eventProcessor, timer, calleeInfo);
            eventProcessor.pushData(params, calleeInfo);
        }
        catch (final Exception e) {
            EventDataProcessor.LOGGER.log(Level.SEVERE, String.format("Exception occurred while pushing event : %s  , ex : ", eventProcessor.getName()), e);
        }
        catch (final Throwable t) {
            EventDataProcessor.LOGGER.log(Level.SEVERE, String.format("Error occurred while pushing event : %s  , ex : ", eventProcessor.getName()), t);
        }
    }
    
    public static EventProcessor getEventProcessor(final EventFWConstants.TYPE type, final String name) {
        return getEventProcessor(type, name, null);
    }
    
    public static EventProcessor getEventProcessor(final EventFWConstants.TYPE type, final String name, final String subLogName) {
        if (EventDataProcessor.parser != null) {
            EventProcessor eventProcessor = null;
            switch (type) {
                case LOG: {
                    final Log logEventProcessor = EventDataProcessor.parser.getLogs().get(name);
                    if (subLogName == null) {
                        eventProcessor = logEventProcessor;
                        break;
                    }
                    if (logEventProcessor != null) {
                        eventProcessor = logEventProcessor.getSubLog(subLogName);
                        break;
                    }
                    break;
                }
                case EVENT: {
                    eventProcessor = EventDataProcessor.parser.getEvents().get(name);
                    break;
                }
            }
            if (eventProcessor == null) {
                EventDataProcessor.LOGGER.log(Level.WARNING, "Instance for type \"{0}\", name \"{1}\" and sub-log name \"{2}\" not found ", new Object[] { type, name, subLogName });
            }
            return eventProcessor;
        }
        EventDataProcessor.LOGGER.log(Level.SEVERE, " EventConfig Parser is not initialized : Check exception thrown in initialization");
        return null;
    }
    
    private static void fillBuiltInFields(final Map<String, Object> params, final EventProcessor config, final ExecutionTimer timer, final CalleeInfo calleeInfo) {
        final List<DataFields> builtInFields = config.getBuiltInFields();
        if (builtInFields != null) {
            for (final DataFields builtInField : builtInFields) {
                if (builtInField.getImplProvider() != null) {
                    builtInField.getImplProvider().fillData(params, builtInField, timer, calleeInfo);
                }
            }
        }
    }
    
    public static void init(final File eventxml) {
        try {
            if (eventxml == null) {
                throw new NullPointerException("Init eventxml file is null");
            }
            init(new FileInputStream(eventxml), eventxml.getName());
        }
        catch (final Exception e) {
            EventDataProcessor.LOGGER.log(Level.SEVERE, " Event Config Parser is not initialized : {0} ", e.getMessage());
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
    }
    
    public static void init(final InputStream eventxmlStream, final String eventXmlFileName) {
        if (EventDataProcessor.parser == null) {
            EventDataProcessor.parser = new EventConfigParser();
        }
        try {
            EventDataProcessor.parser.parse(eventxmlStream, eventXmlFileName);
        }
        catch (final EventConfigurationException e) {
            throw e;
        }
        catch (final Exception e2) {
            EventDataProcessor.LOGGER.log(Level.SEVERE, " Event Config Parser is not initialized : {0} ", e2.getMessage());
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
    }
    
    public static EventConfigParser getParser() {
        if (EventDataProcessor.parser == null) {
            EventDataProcessor.LOGGER.log(Level.SEVERE, " Event Config Parser is not initialized , getParser() is called before init");
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
        return EventDataProcessor.parser;
    }
    
    public static void initDefaultConfig() {
        if (EventDataProcessor.defaultConfigLoaded) {
            return;
        }
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("waf-events.xml");
        if (inputStream != null) {
            init(inputStream, "waf-events.xml");
            EventDataProcessor.LOGGER.log(Level.INFO, "{0} Successfully loaded from ClassLoader", new Object[] { "waf-events.xml" });
        }
        EventDataProcessor.defaultConfigLoaded = true;
    }
    
    public static void stopRunningTimer(final ExecutionTimer timer) {
        if (timer != null) {
            timer.stop();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(EventDataProcessor.class.getName());
        EventDataProcessor.parser = null;
        EventDataProcessor.defaultConfigLoaded = false;
        initDefaultConfig();
    }
}
