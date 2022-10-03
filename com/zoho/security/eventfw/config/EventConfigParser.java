package com.zoho.security.eventfw.config;

import java.util.ArrayList;
import java.util.LinkedList;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsImplProvider;
import com.zoho.security.eventfw.type.Event;
import com.zoho.security.eventfw.type.Log;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import com.zoho.security.eventfw.dispatcher.DistinctDispatcher;
import com.zoho.security.eventfw.dispatcher.TimerDispatcher;
import com.zoho.security.eventfw.dispatcher.BatchDispatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.IOException;
import com.zoho.security.eventfw.builtinfieldsImpl.BuiltInFieldsProvider;
import java.io.FileNotFoundException;
import com.zoho.security.eventfw.exceptions.EventConfigurationException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import com.zoho.security.eventfw.EventDataProcessor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import com.zoho.security.eventfw.dispatcher.Dispatcher;
import com.zoho.security.eventfw.logImpl.LogImplProvider;
import com.zoho.security.eventfw.type.EventProcessor;
import java.util.Map;
import java.util.logging.Logger;

public class EventConfigParser
{
    public static final Logger LOGGER;
    private static final String EVENTS_FILENAME = "events.xml";
    private Map<String, EventProcessor> logs;
    private Map<String, EventProcessor> events;
    private Map<String, LogImplProvider> logAPI;
    private Map<String, DataFields> fieldsMap;
    private Map<String, DataTemplateConfig> dataTemplateConfigMap;
    private Map<String, DataTemplateConfig> builtInDataTemplateConfig;
    private Map<String, Dispatcher> dispatcherTemplate;
    private String packageName;
    private Pattern calleeInfoExcludePattern;
    private static Class<?>[] calleeInfoInheritClassExcludes;
    
    public EventConfigParser() {
        this(true);
    }
    
    public EventConfigParser(final boolean isInit) {
        this.logs = new LinkedHashMap<String, EventProcessor>();
        this.events = new LinkedHashMap<String, EventProcessor>();
        this.logAPI = new HashMap<String, LogImplProvider>();
        this.fieldsMap = new HashMap<String, DataFields>();
        this.dataTemplateConfigMap = new HashMap<String, DataTemplateConfig>();
        this.builtInDataTemplateConfig = new HashMap<String, DataTemplateConfig>();
        this.dispatcherTemplate = new HashMap<String, Dispatcher>();
        this.packageName = "com.zoho.security.eventfw.pojos";
        this.calleeInfoExcludePattern = Pattern.compile("com\\.adventnet\\.iam\\.security\\..*|com\\.zoho\\.security\\..*|org\\.apache\\.jasper\\.runtime\\.(?:JspRuntimeLibrary|PageContextImpl)");
        final InputStream inputStream = EventDataProcessor.class.getResourceAsStream("/events.xml");
        if (inputStream == null) {
            throw new IllegalStateException(String.format("Failed to locate the \"%s\" file", "events.xml"));
        }
        this.parse(inputStream, "events.xml", isInit);
        EventConfigParser.LOGGER.log(Level.INFO, "{0} Successfully loaded from ClassLoader", new Object[] { "events.xml" });
    }
    
    public void parse(final File file) {
        this.parse(file, true);
    }
    
    public void parse(final InputStream inputstream, final String filename) {
        this.parse(inputstream, filename, true);
    }
    
    public void parseFromPojoConverter(final File file) {
        this.parse(file, false);
    }
    
    public void parse(final File file, final boolean isInit) {
        if (file == null) {
            return;
        }
        try {
            EventConfigParser.LOGGER.log(Level.INFO, " {0} File loaded from directory :: {1}", new Object[] { file.getName(), file.getPath() });
            this.parse(new FileInputStream(file), file.getName(), isInit);
        }
        catch (final FileNotFoundException e) {
            EventConfigParser.LOGGER.log(Level.SEVERE, "Exception occurred while Initiating Event Logger MetaData : exception {0}", new Object[] { e });
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
    }
    
    private void parse(final InputStream stream, final String filename, final boolean isInit) {
        try {
            if (stream == null) {
                throw new NullPointerException(String.format("Inputstream of event xml \"%s\" is null", filename));
            }
            final Document doc = EventConfigUtil.createDocumentBuilder().parse(stream);
            if (doc == null) {
                EventConfigParser.LOGGER.log(Level.SEVERE, " Document object is null while parsing event XML  : {0} ", filename);
                throw new EventConfigurationException("DOCUMENT_NULL");
            }
            final Element root = doc.getDocumentElement();
            final Element dataTemplatesEle = EventConfigUtil.getFirstChildNodeByTagName(root, EventFWConstants.TAG.DATA_TEMPLATES.value());
            if (isInit) {
                this.initLogAPIImpl(EventConfigUtil.getFirstChildNodeByTagName(root, EventFWConstants.TAG.TRANSFERAPIS.value()));
            }
            this.initFields(dataTemplatesEle);
            this.initBuiltInFieldsDataTemplate(this.builtInDataTemplateConfig, dataTemplatesEle, isInit);
            this.initDataTemplates(this.dataTemplateConfigMap, dataTemplatesEle, null);
            final String packageName = root.getAttribute(EventFWConstants.ATTRIBUTES.PACKAGE.value());
            if (EventConfigUtil.isValid(packageName)) {
                this.packageName = packageName.trim();
            }
            this.initDispatcherTemplate(EventConfigUtil.getFirstChildNodeByTagName(root, EventFWConstants.TAG.DISPATCHER_TEMPLATES.value()));
            this.initConfiguration(this.logs, EventConfigUtil.getChildNodesByTagName(root, EventFWConstants.TAG.LOGS.value()), EventFWConstants.TAG.LOG);
            this.initConfiguration(this.events, EventConfigUtil.getChildNodesByTagName(root, EventFWConstants.TAG.EVENTS.value()), EventFWConstants.TAG.EVENT);
        }
        catch (final EventConfigurationException e) {
            throw e;
        }
        catch (final Exception e2) {
            EventConfigParser.LOGGER.log(Level.SEVERE, "Exception occurred while Initiating Event Logger MetaData : exception {0}", new Object[] { e2 });
            throw new EventConfigurationException("PARSER_NOT_INITIALISED");
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final IOException e3) {
                EventConfigParser.LOGGER.log(Level.SEVERE, " Exception Occured while closing Stream: exception  {0}", new Object[] { e3 });
                throw new EventConfigurationException("PARSER_NOT_INITIALISED");
            }
        }
    }
    
    private void initDispatcherTemplate(final Element dispatcherTemplatesEle) {
        if (dispatcherTemplatesEle != null) {
            final List<Element> eleList = EventConfigUtil.getChildNodesByTagName(dispatcherTemplatesEle, EventFWConstants.TAG.DISPATCHER_TEMPLATE.value());
            for (final Element ele : eleList) {
                final String type = ele.getAttribute(EventFWConstants.ATTRIBUTES.TYPE.value());
                final EventFWConstants.DISPATCHER_TYPE dispatcherType = EventFWConstants.DISPATCHER_TYPE.getType(type);
                if (dispatcherType != null) {
                    Dispatcher dispatcher = null;
                    switch (dispatcherType) {
                        case BATCH: {
                            dispatcher = new BatchDispatcher(ele, dispatcherType);
                            break;
                        }
                        case TIMER: {
                            dispatcher = new TimerDispatcher(ele, dispatcherType);
                            break;
                        }
                        case DISTINCT: {
                            dispatcher = new DistinctDispatcher(ele, dispatcherType);
                            break;
                        }
                        case CUSTOM: {
                            final String impl = ele.getAttribute(EventFWConstants.ATTRIBUTES.IMPL.value());
                            try {
                                if (EventConfigUtil.isValid(impl)) {
                                    final Class<?> clazz = Class.forName(impl);
                                    final Constructor<?> ctor = clazz.getConstructor(Element.class, EventFWConstants.DISPATCHER_TYPE.class);
                                    dispatcher = (Dispatcher)ctor.newInstance(ele, dispatcherType);
                                }
                            }
                            catch (final Exception e) {
                                EventConfigParser.LOGGER.log(Level.SEVERE, " Exception occurred while loading dispatcher implementation Provider class  : {0} , Exception {1} ", new Object[] { impl, e.getMessage() });
                                throw new EventConfigurationException("DISPATCHER_TEMPLATE_CUSTOM_IMPL_ISSUE");
                            }
                            break;
                        }
                    }
                    if (this.dispatcherTemplate.containsKey(dispatcher.getName())) {
                        EventConfigParser.LOGGER.log(Level.SEVERE, "The dispatcher template  : {0}  already defined ", new Object[] { dispatcher.getName() });
                        throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
                    }
                    this.dispatcherTemplate.put(dispatcher.getName(), dispatcher);
                }
            }
        }
    }
    
    private void initLogAPIImpl(final Element logapisElement) {
        if (logapisElement != null) {
            final List<Element> eleList = EventConfigUtil.getChildNodesByTagName(logapisElement, EventFWConstants.TAG.TRANSFERAPI.value());
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            for (final Element ele : eleList) {
                final String name = ele.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
                final String impl = ele.getAttribute(EventFWConstants.ATTRIBUTES.IMPL.value());
                try {
                    if (!EventConfigUtil.isValid(impl)) {
                        continue;
                    }
                    if (this.logAPI.containsKey(name)) {
                        EventConfigParser.LOGGER.log(Level.SEVERE, "The logAPIImpl provider  : {0}  already defined ", new Object[] { name });
                        throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
                    }
                    final LogImplProvider implProvider = (LogImplProvider)cl.loadClass(impl).newInstance();
                    implProvider.init(ele);
                    this.logAPI.put(name, implProvider);
                }
                catch (final EventConfigurationException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    EventConfigParser.LOGGER.log(Level.SEVERE, " Exception occurred while loading log implementation Provider class  : {0} , Exception {1} ", new Object[] { impl, e2.getMessage() });
                    throw new EventConfigurationException("LOGAPI_IMPL_EXCEPTION");
                }
            }
        }
    }
    
    public void initConfiguration(final Map<String, EventProcessor> configurationMap, final List<Element> list, final EventFWConstants.TAG tagName) {
        if (list.size() > 0) {
            for (final Element element : list) {
                final String type = tagName.value();
                final List<Element> eleList = EventConfigUtil.getChildNodesByTagName(element, type);
                for (final Element ele : eleList) {
                    EventProcessor config = null;
                    switch (tagName) {
                        case LOG: {
                            config = new Log(element, ele, this, type);
                            break;
                        }
                        case EVENT: {
                            config = new Event(element, ele, this, type);
                            break;
                        }
                    }
                    if (configurationMap.containsKey(config.getName())) {
                        EventConfigParser.LOGGER.log(Level.SEVERE, "The event name : {0}  already defined ", new Object[] { config.getName() });
                        throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
                    }
                    if (config.getLogAPIList().size() == 0) {
                        config.getLogAPIList().add(this.getLogAPIImplProviderConfigMap().get("ZohoLogs"));
                    }
                    configurationMap.put(config.getName(), config);
                }
            }
        }
    }
    
    private void initFields(final Element dtsElement) {
        if (dtsElement != null) {
            final List<Element> fieldElements = EventConfigUtil.getChildNodesByTagName(dtsElement, EventFWConstants.TAG.FIELDS.value());
            for (final Element fieldEle : fieldElements) {
                for (final Element field : EventConfigUtil.getChildNodesByTagName(fieldEle, EventFWConstants.TAG.FIELD.value())) {
                    final DataFields dataFields = new DataFields(field, null);
                    if (this.fieldsMap.containsKey(dataFields.getName().toLowerCase())) {
                        EventConfigParser.LOGGER.log(Level.SEVERE, " Data Field Config  : {0} already defined ", new Object[] { dataFields.getName().toLowerCase() });
                        throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
                    }
                    this.fieldsMap.put(dataFields.getName().toLowerCase(), dataFields);
                }
            }
        }
    }
    
    private void initBuiltInFieldsDataTemplate(final Map<String, DataTemplateConfig> builtInDataTemplate, final Element dtsElement, final boolean isTemplateInit) {
        if (dtsElement != null) {
            final List<Element> builtInFieldsEle = EventConfigUtil.getChildNodesByTagName(dtsElement, EventFWConstants.TAG.BUILTINFIELDS.value());
            for (final Element builtInEle : builtInFieldsEle) {
                final String impl = builtInEle.getAttribute(EventFWConstants.ATTRIBUTES.IMPL.value());
                BuiltInFieldsProvider implProvider = null;
                Label_0151: {
                    if (isTemplateInit && EventConfigUtil.isValid(impl)) {
                        try {
                            implProvider = (BuiltInFieldsProvider)Thread.currentThread().getContextClassLoader().loadClass(impl).newInstance();
                            break Label_0151;
                        }
                        catch (final Exception e) {
                            EventConfigParser.LOGGER.log(Level.SEVERE, " Exception occurred while loading builtin implementation Provider class  : {0} , Exception {1} ", new Object[] { impl, e.getMessage() });
                            throw new EventConfigurationException("BUILTIN_FIELD_IMPL_EXCEPTION");
                        }
                    }
                    implProvider = new BuiltInFieldsImplProvider();
                }
                this.initDataTemplates(builtInDataTemplate, builtInEle, implProvider);
            }
        }
    }
    
    private void initDataTemplates(final Map<String, DataTemplateConfig> dataTemplateConfigMap, final Element dtParentEle, final BuiltInFieldsProvider implProvider) {
        if (dtParentEle != null) {
            final Map<String, List<DataFields>> dataFieldsMap = new LinkedHashMap<String, List<DataFields>>();
            for (final Element data : EventConfigUtil.getChildNodesByTagName(dtParentEle, EventFWConstants.TAG.DATA_TEMPLATE.value())) {
                final String templateName = data.getAttribute(EventFWConstants.ATTRIBUTES.NAME.value());
                if (dataFieldsMap.containsKey(templateName)) {
                    EventConfigParser.LOGGER.log(Level.SEVERE, " Data Template Config  : {0} already defined ", new Object[] { templateName });
                    throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
                }
                final LinkedList<DataFields> df = new LinkedList<DataFields>();
                dataFieldsMap.put(templateName, df);
                for (final Element field : EventConfigUtil.getChildNodesByTagName(data, EventFWConstants.TAG.FIELD.value())) {
                    if (!dataFieldsMap.containsKey(templateName)) {
                        df.add(new DataFields(field, implProvider));
                    }
                    else {
                        dataFieldsMap.get(templateName).add(new DataFields(field, implProvider));
                    }
                }
            }
            this.resolveDataFieldReferences(dataTemplateConfigMap, dataFieldsMap, implProvider);
        }
    }
    
    private void resolveDataFieldReferences(final Map<String, DataTemplateConfig> dataTemplateConfigMap, final Map<String, List<DataFields>> dataFieldsMap, final BuiltInFieldsProvider implProvider) {
        for (final String keyName : dataFieldsMap.keySet()) {
            final DataTemplateConfig config = this.getFields(keyName, dataFieldsMap, implProvider);
            if (dataTemplateConfigMap.containsKey(keyName)) {
                EventConfigParser.LOGGER.log(Level.SEVERE, " Data Template Config  : {0} already defined ", new Object[] { keyName });
                throw new EventConfigurationException("DUPLICATE_CONFIGURATION");
            }
            dataTemplateConfigMap.put(keyName, config);
        }
    }
    
    private DataTemplateConfig getFields(final String fieldName, final Map<String, List<DataFields>> dataFieldsMap, final BuiltInFieldsProvider implProvider) {
        final DataTemplateConfig dftc = new DataTemplateConfig();
        if (this.fieldsMap.containsKey(fieldName)) {
            dftc.addField(this.fieldsMap.get(fieldName));
        }
        else if (dataFieldsMap.containsKey(fieldName)) {
            for (final DataFields field : dataFieldsMap.get(fieldName)) {
                if (EventConfigUtil.isValid(field.getName())) {
                    dftc.addField(field);
                }
                else {
                    if (!EventConfigUtil.isValid(field.getRef())) {
                        continue;
                    }
                    final String[] split;
                    final String[] references = split = field.getRef().split(",");
                    for (final String reference : split) {
                        final DataTemplateConfig innerdftc = this.dataTemplateConfigMap.containsKey(reference) ? this.dataTemplateConfigMap.get(reference) : this.getFields(reference, dataFieldsMap, implProvider);
                        dftc.addAllFields(innerdftc.getDataFields());
                        dftc.addAllBuiltInFields(innerdftc.getBuiltInFields());
                    }
                }
            }
        }
        else {
            if (implProvider != null || !this.builtInDataTemplateConfig.containsKey(fieldName)) {
                EventConfigParser.LOGGER.log(Level.SEVERE, "Reference \"{0}\" undefined", fieldName);
                throw new EventConfigurationException("DATATEMPLATE_REF_UNDEFINED");
            }
            for (final DataFields field : this.builtInDataTemplateConfig.get(fieldName).getDataFields()) {
                if (EventConfigUtil.isValid(field.getName())) {
                    dftc.addBuiltInField(field);
                }
            }
        }
        return dftc;
    }
    
    public Map<String, EventProcessor> getEvents() {
        return this.events;
    }
    
    public Map<String, EventProcessor> getLogs() {
        return this.logs;
    }
    
    public Map<String, Dispatcher> getDispatcherTemplate() {
        return this.dispatcherTemplate;
    }
    
    public Map<String, DataTemplateConfig> getDataTemplate() {
        return this.dataTemplateConfigMap;
    }
    
    public Map<String, LogImplProvider> getLogAPIImplProviderConfigMap() {
        return this.logAPI;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public Pattern getCalleeInfoExcludePattern() {
        return this.calleeInfoExcludePattern;
    }
    
    public Class<?>[] getCalleeInfoInheritClassExcludes() {
        return EventConfigParser.calleeInfoInheritClassExcludes;
    }
    
    public void printConf() {
        EventConfigParser.LOGGER.log(Level.SEVERE, "\n******* logs ********");
        EventConfigParser.LOGGER.log(Level.SEVERE, this.getLogs().toString());
        EventConfigParser.LOGGER.log(Level.SEVERE, "******* events ********");
        EventConfigParser.LOGGER.log(Level.SEVERE, this.getEvents().toString());
    }
    
    static {
        LOGGER = Logger.getLogger(EventConfigParser.class.getName());
        final List<Class<?>> calleeInfoInheritClassExcludesList = new ArrayList<Class<?>>();
        for (final String inheritClass : new String[] { "javax.servlet.ServletRequest" }) {
            try {
                calleeInfoInheritClassExcludesList.add(Class.forName(inheritClass, false, EventConfigParser.class.getClassLoader()));
            }
            catch (final ClassNotFoundException e) {
                EventConfigParser.LOGGER.log(Level.WARNING, "callee info exclude class \"{0} lookup failed", inheritClass);
            }
        }
        calleeInfoInheritClassExcludesList.toArray(EventConfigParser.calleeInfoInheritClassExcludes = new Class[calleeInfoInheritClassExcludesList.size()]);
    }
}
