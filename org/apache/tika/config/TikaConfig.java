package org.apache.tika.config;

import org.apache.tika.parser.AbstractEncodingDetectorParser;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.apache.tika.parser.AutoDetectParser;
import java.util.HashMap;
import org.apache.tika.utils.AnnotationUtils;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.detect.Detector;
import org.apache.tika.parser.Parser;
import java.util.Locale;
import org.apache.tika.exception.TikaConfigException;
import java.util.HashSet;
import org.apache.tika.mime.MediaType;
import java.util.Set;
import org.apache.tika.mime.MimeTypesFactory;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import org.apache.tika.concurrent.SimpleThreadPoolExecutor;
import org.apache.tika.concurrent.ConfigurableThreadPoolExecutor;
import org.apache.tika.language.translate.DefaultTranslator;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.CompositeEncodingDetector;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.metadata.filter.NoOpFilter;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.net.URL;
import java.io.File;
import org.apache.tika.utils.XMLReaderUtils;
import java.nio.file.Path;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import java.nio.file.Paths;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.detect.EncodingDetector;
import java.util.concurrent.ExecutorService;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.parser.CompositeParser;
import java.util.concurrent.atomic.AtomicInteger;

public class TikaConfig
{
    protected static AtomicInteger TIMES_INSTANTIATED;
    private final ServiceLoader serviceLoader;
    private final CompositeParser parser;
    private final CompositeDetector detector;
    private final Translator translator;
    private final MimeTypes mimeTypes;
    private final ExecutorService executorService;
    private final EncodingDetector encodingDetector;
    private final MetadataFilter metadataFilter;
    
    public TikaConfig(final String file) throws TikaException, IOException, SAXException {
        this(Paths.get(file, new String[0]));
    }
    
    public TikaConfig(final Path path) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(path));
    }
    
    public TikaConfig(final Path path, final ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(path), loader);
    }
    
    public TikaConfig(final File file) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(file.toPath()));
    }
    
    public TikaConfig(final File file, final ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(file.toPath()), loader);
    }
    
    public TikaConfig(final URL url) throws TikaException, IOException, SAXException {
        this(url, ServiceLoader.getContextClassLoader());
    }
    
    public TikaConfig(final URL url, final ClassLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(url.toString()).getDocumentElement(), loader);
    }
    
    public TikaConfig(final URL url, final ServiceLoader loader) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(url.toString()).getDocumentElement(), loader);
    }
    
    public TikaConfig(final InputStream stream) throws TikaException, IOException, SAXException {
        this(XMLReaderUtils.buildDOM(stream));
    }
    
    public TikaConfig(final Document document) throws TikaException, IOException {
        this(document.getDocumentElement());
    }
    
    public TikaConfig(final Document document, final ServiceLoader loader) throws TikaException, IOException {
        this(document.getDocumentElement(), loader);
    }
    
    public TikaConfig(final Element element) throws TikaException, IOException {
        this(element, serviceLoaderFromDomElement(element, null));
    }
    
    public TikaConfig(final Element element, final ClassLoader loader) throws TikaException, IOException {
        this(element, serviceLoaderFromDomElement(element, loader));
    }
    
    private TikaConfig(final Element element, final ServiceLoader loader) throws TikaException, IOException {
        final DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
        final TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
        final ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
        final EncodingDetectorXmlLoader encodingDetectorXmlLoader = new EncodingDetectorXmlLoader();
        this.updateXMLReaderUtils(element);
        this.mimeTypes = typesFromDomElement(element);
        this.detector = ((XmlLoader<CompositeDetector, T>)detectorLoader).loadOverall(element, this.mimeTypes, loader);
        this.encodingDetector = ((XmlLoader<EncodingDetector, T>)encodingDetectorXmlLoader).loadOverall(element, this.mimeTypes, loader);
        final ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
        this.parser = ((XmlLoader<CompositeParser, T>)parserLoader).loadOverall(element, this.mimeTypes, loader);
        this.translator = ((XmlLoader<Translator, T>)translatorLoader).loadOverall(element, this.mimeTypes, loader);
        this.executorService = ((XmlLoader<ExecutorService, T>)executorLoader).loadOverall(element, this.mimeTypes, loader);
        this.metadataFilter = MetadataFilter.load(element, true);
        this.serviceLoader = loader;
        TikaConfig.TIMES_INSTANTIATED.incrementAndGet();
    }
    
    public TikaConfig(final ClassLoader loader) throws MimeTypeException, IOException {
        this.serviceLoader = new ServiceLoader(loader);
        this.mimeTypes = getDefaultMimeTypes(loader);
        this.detector = getDefaultDetector(this.mimeTypes, this.serviceLoader);
        this.encodingDetector = getDefaultEncodingDetector(this.serviceLoader);
        this.parser = getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
        this.translator = getDefaultTranslator(this.serviceLoader);
        this.executorService = getDefaultExecutorService();
        this.metadataFilter = new NoOpFilter();
        TikaConfig.TIMES_INSTANTIATED.incrementAndGet();
    }
    
    public TikaConfig() throws TikaException, IOException {
        String config = System.getProperty("tika.config");
        if (config == null || config.trim().equals("")) {
            config = System.getenv("TIKA_CONFIG");
        }
        if (config == null || config.trim().equals("")) {
            this.serviceLoader = new ServiceLoader();
            this.mimeTypes = getDefaultMimeTypes(ServiceLoader.getContextClassLoader());
            this.encodingDetector = getDefaultEncodingDetector(this.serviceLoader);
            this.parser = getDefaultParser(this.mimeTypes, this.serviceLoader, this.encodingDetector);
            this.detector = getDefaultDetector(this.mimeTypes, this.serviceLoader);
            this.translator = getDefaultTranslator(this.serviceLoader);
            this.executorService = getDefaultExecutorService();
            this.metadataFilter = new NoOpFilter();
        }
        else {
            final ServiceLoader tmpServiceLoader = new ServiceLoader();
            try (final InputStream stream = getConfigInputStream(config, tmpServiceLoader)) {
                final Element element = XMLReaderUtils.buildDOM(stream).getDocumentElement();
                this.updateXMLReaderUtils(element);
                this.serviceLoader = serviceLoaderFromDomElement(element, tmpServiceLoader.getLoader());
                final DetectorXmlLoader detectorLoader = new DetectorXmlLoader();
                final EncodingDetectorXmlLoader encodingDetectorLoader = new EncodingDetectorXmlLoader();
                final TranslatorXmlLoader translatorLoader = new TranslatorXmlLoader();
                final ExecutorServiceXmlLoader executorLoader = new ExecutorServiceXmlLoader();
                this.mimeTypes = typesFromDomElement(element);
                this.encodingDetector = ((XmlLoader<EncodingDetector, T>)encodingDetectorLoader).loadOverall(element, this.mimeTypes, this.serviceLoader);
                final ParserXmlLoader parserLoader = new ParserXmlLoader(this.encodingDetector);
                this.parser = ((XmlLoader<CompositeParser, T>)parserLoader).loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.detector = ((XmlLoader<CompositeDetector, T>)detectorLoader).loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.translator = ((XmlLoader<Translator, T>)translatorLoader).loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.executorService = ((XmlLoader<ExecutorService, T>)executorLoader).loadOverall(element, this.mimeTypes, this.serviceLoader);
                this.metadataFilter = MetadataFilter.load(element, true);
            }
            catch (final SAXException e) {
                throw new TikaException("Specified Tika configuration has syntax errors: " + config, e);
            }
        }
        TikaConfig.TIMES_INSTANTIATED.incrementAndGet();
    }
    
    private static MimeTypes getDefaultMimeTypes(final ClassLoader loader) {
        return MimeTypes.getDefaultMimeTypes(loader);
    }
    
    protected static CompositeDetector getDefaultDetector(final MimeTypes types, final ServiceLoader loader) {
        return new DefaultDetector(types, loader);
    }
    
    protected static CompositeEncodingDetector getDefaultEncodingDetector(final ServiceLoader loader) {
        return new DefaultEncodingDetector(loader);
    }
    
    private static CompositeParser getDefaultParser(final MimeTypes types, final ServiceLoader loader, final EncodingDetector encodingDetector) {
        return new DefaultParser(types.getMediaTypeRegistry(), loader, encodingDetector);
    }
    
    private static Translator getDefaultTranslator(final ServiceLoader loader) {
        return new DefaultTranslator(loader);
    }
    
    private static ConfigurableThreadPoolExecutor getDefaultExecutorService() {
        return new SimpleThreadPoolExecutor();
    }
    
    private static InputStream getConfigInputStream(final String config, final ServiceLoader serviceLoader) throws TikaException, IOException {
        InputStream stream = null;
        try {
            stream = new URL(config).openStream();
        }
        catch (final IOException ex) {}
        if (stream == null) {
            stream = serviceLoader.getResourceAsStream(config);
        }
        if (stream == null) {
            final Path file = Paths.get(config, new String[0]);
            if (Files.isRegularFile(file, new LinkOption[0])) {
                stream = Files.newInputStream(file, new OpenOption[0]);
            }
        }
        if (stream == null) {
            throw new TikaException("Specified Tika configuration not found: " + config);
        }
        return stream;
    }
    
    private static String getText(final Node node) {
        if (node.getNodeType() == 3) {
            return node.getNodeValue();
        }
        if (node.getNodeType() == 1) {
            final StringBuilder builder = new StringBuilder();
            final NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); ++i) {
                builder.append(getText(list.item(i)));
            }
            return builder.toString();
        }
        return "";
    }
    
    public static TikaConfig getDefaultConfig() {
        try {
            return new TikaConfig();
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read default configuration", e);
        }
        catch (final TikaException e2) {
            throw new RuntimeException("Unable to access default configuration", e2);
        }
    }
    
    private static Element getChild(final Element element, final String name) {
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1 && name.equals(child.getNodeName())) {
                return (Element)child;
            }
        }
        return null;
    }
    
    private static List<Element> getTopLevelElementChildren(final Element element, final String parentName, final String childrenName) throws TikaException {
        Node parentNode = null;
        if (parentName != null) {
            final NodeList nodes = element.getElementsByTagName(parentName);
            if (nodes.getLength() > 1) {
                throw new TikaException("Properties may not contain multiple " + parentName + " entries");
            }
            if (nodes.getLength() == 1) {
                parentNode = nodes.item(0);
            }
        }
        else {
            parentNode = element;
        }
        if (parentNode != null) {
            final NodeList nodes = parentNode.getChildNodes();
            final List<Element> elements = new ArrayList<Element>();
            for (int i = 0; i < nodes.getLength(); ++i) {
                final Node node = nodes.item(i);
                if (node instanceof Element) {
                    final Element nodeE = (Element)node;
                    if (childrenName.equals(nodeE.getTagName())) {
                        elements.add(nodeE);
                    }
                }
            }
            return elements;
        }
        return Collections.emptyList();
    }
    
    private static MimeTypes typesFromDomElement(final Element element) throws TikaException, IOException {
        final Element mtr = getChild(element, "mimeTypeRepository");
        if (mtr != null && mtr.hasAttribute("resource")) {
            return MimeTypesFactory.create(mtr.getAttribute("resource"));
        }
        return getDefaultMimeTypes(null);
    }
    
    private static Set<MediaType> mediaTypesListFromDomElement(final Element node, final String tag) throws TikaException, IOException {
        Set<MediaType> types = null;
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node cNode = children.item(i);
            if (cNode instanceof Element) {
                final Element cElement = (Element)cNode;
                if (tag.equals(cElement.getTagName())) {
                    final String mime = getText(cElement);
                    final MediaType type = MediaType.parse(mime);
                    if (type == null) {
                        throw new TikaException("Invalid media type name: " + mime);
                    }
                    if (types == null) {
                        types = new HashSet<MediaType>();
                    }
                    types.add(type);
                }
            }
        }
        if (types != null) {
            return types;
        }
        return Collections.emptySet();
    }
    
    private static ServiceLoader serviceLoaderFromDomElement(final Element element, ClassLoader loader) throws TikaConfigException {
        final Element serviceLoaderElement = getChild(element, "service-loader");
        ServiceLoader serviceLoader;
        if (serviceLoaderElement != null) {
            final boolean dynamic = Boolean.parseBoolean(serviceLoaderElement.getAttribute("dynamic"));
            LoadErrorHandler loadErrorHandler = LoadErrorHandler.THROW;
            final String loadErrorHandleConfig = serviceLoaderElement.getAttribute("loadErrorHandler");
            if (LoadErrorHandler.WARN.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
                loadErrorHandler = LoadErrorHandler.WARN;
            }
            else if (LoadErrorHandler.THROW.toString().equalsIgnoreCase(loadErrorHandleConfig)) {
                loadErrorHandler = LoadErrorHandler.THROW;
            }
            final InitializableProblemHandler initializableProblemHandler = getInitializableProblemHandler(serviceLoaderElement.getAttribute("initializableProblemHandler"));
            if (loader == null) {
                loader = ServiceLoader.getContextClassLoader();
            }
            serviceLoader = new ServiceLoader(loader, loadErrorHandler, initializableProblemHandler, dynamic);
        }
        else if (loader != null) {
            serviceLoader = new ServiceLoader(loader);
        }
        else {
            serviceLoader = new ServiceLoader();
        }
        return serviceLoader;
    }
    
    private static InitializableProblemHandler getInitializableProblemHandler(final String initializableProblemHandler) throws TikaConfigException {
        if (initializableProblemHandler == null || initializableProblemHandler.length() == 0) {
            return InitializableProblemHandler.DEFAULT;
        }
        if (InitializableProblemHandler.IGNORE.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.IGNORE;
        }
        if (InitializableProblemHandler.INFO.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.INFO;
        }
        if (InitializableProblemHandler.WARN.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.WARN;
        }
        if (InitializableProblemHandler.THROW.toString().equalsIgnoreCase(initializableProblemHandler)) {
            return InitializableProblemHandler.THROW;
        }
        throw new TikaConfigException(String.format(Locale.US, "Couldn't parse non-null '%s'. Must be one of 'ignore', 'info', 'warn' or 'throw'", initializableProblemHandler));
    }
    
    public static void mustNotBeEmpty(final String paramName, final String paramValue) throws TikaConfigException {
        if (paramValue == null || paramValue.trim().equals("")) {
            throw new IllegalArgumentException("parameter '" + paramName + "' must be set in the config file");
        }
    }
    
    public static void mustNotBeEmpty(final String paramName, final Path paramValue) throws TikaConfigException {
        if (paramValue == null) {
            throw new IllegalArgumentException("parameter '" + paramName + "' must be set in the config file");
        }
    }
    
    private void updateXMLReaderUtils(final Element element) throws TikaException {
        final Element child = getChild(element, "xml-reader-utils");
        if (child == null) {
            return;
        }
        String attr = child.getAttribute("maxEntityExpansions");
        if (attr != null) {
            XMLReaderUtils.setMaxEntityExpansions(Integer.parseInt(attr));
        }
        attr = child.getAttribute("poolSize");
        if (attr != null) {
            XMLReaderUtils.setPoolSize(Integer.parseInt(attr));
        }
    }
    
    @Deprecated
    public Parser getParser(final MediaType mimeType) {
        return this.parser.getParsers().get(mimeType);
    }
    
    public Parser getParser() {
        return this.parser;
    }
    
    public Detector getDetector() {
        return this.detector;
    }
    
    public EncodingDetector getEncodingDetector() {
        return this.encodingDetector;
    }
    
    public Translator getTranslator() {
        return this.translator;
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    public MimeTypes getMimeRepository() {
        return this.mimeTypes;
    }
    
    public MediaTypeRegistry getMediaTypeRegistry() {
        return this.mimeTypes.getMediaTypeRegistry();
    }
    
    public ServiceLoader getServiceLoader() {
        return this.serviceLoader;
    }
    
    public MetadataFilter getMetadataFilter() {
        return this.metadataFilter;
    }
    
    static {
        TikaConfig.TIMES_INSTANTIATED = new AtomicInteger();
    }
    
    private abstract static class XmlLoader<CT, T>
    {
        protected static final String PARAMS_TAG_NAME = "params";
        
        abstract boolean supportsComposite();
        
        abstract String getParentTagName();
        
        abstract String getLoaderTagName();
        
        abstract Class<? extends T> getLoaderClass();
        
        abstract boolean isComposite(final T p0);
        
        abstract boolean isComposite(final Class<? extends T> p0);
        
        abstract T preLoadOne(final Class<? extends T> p0, final String p1, final MimeTypes p2) throws TikaException;
        
        abstract CT createDefault(final MimeTypes p0, final ServiceLoader p1);
        
        abstract CT createComposite(final List<T> p0, final MimeTypes p1, final ServiceLoader p2);
        
        abstract T createComposite(final Class<? extends T> p0, final List<T> p1, final Set<Class<? extends T>> p2, final Map<String, Param> p3, final MimeTypes p4, final ServiceLoader p5) throws InvocationTargetException, IllegalAccessException, InstantiationException;
        
        abstract T decorate(final T p0, final Element p1) throws IOException, TikaException;
        
        CT loadOverall(final Element element, final MimeTypes mimeTypes, final ServiceLoader loader) throws TikaException, IOException {
            final List<T> loaded = new ArrayList<T>();
            for (final Element le : getTopLevelElementChildren(element, this.getParentTagName(), this.getLoaderTagName())) {
                final T loadedChild = this.loadOne(le, mimeTypes, loader);
                if (loadedChild != null) {
                    loaded.add(loadedChild);
                }
            }
            if (loaded.isEmpty()) {
                return this.createDefault(mimeTypes, loader);
            }
            if (loaded.size() == 1) {
                final T single = loaded.get(0);
                if (this.isComposite(single)) {
                    return (CT)single;
                }
            }
            else if (!this.supportsComposite()) {
                if (loaded.size() == 1) {
                    return (CT)loaded.get(0);
                }
                if (loaded.size() > 1) {
                    throw new TikaConfigException("Composite not supported for " + this.getParentTagName() + ". Must specify only one child!");
                }
            }
            return this.createComposite(loaded, mimeTypes, loader);
        }
        
        T loadOne(final Element element, final MimeTypes mimeTypes, final ServiceLoader loader) throws TikaException, IOException {
            final String name = element.getAttribute("class");
            if (name == null) {
                throw new TikaConfigException("class attribute must not be null: " + element);
            }
            final String initProbHandler = element.getAttribute("initializableProblemHandler");
            InitializableProblemHandler initializableProblemHandler;
            if (initProbHandler == null || initProbHandler.length() == 0) {
                initializableProblemHandler = loader.getInitializableProblemHandler();
            }
            else {
                initializableProblemHandler = getInitializableProblemHandler(initProbHandler);
            }
            T loaded = null;
            try {
                final Class<? extends T> loadedClass = loader.getServiceClass(this.getLoaderClass(), name);
                loaded = this.preLoadOne(loadedClass, name, mimeTypes);
                if (loaded != null) {
                    return loaded;
                }
                Map<String, Param> params = null;
                try {
                    params = this.getParams(element);
                }
                catch (final Exception e) {
                    throw new TikaConfigException(e.getMessage(), e);
                }
                if (this.isComposite(loadedClass)) {
                    final List<T> children = new ArrayList<T>();
                    final NodeList childNodes = element.getElementsByTagName(this.getLoaderTagName());
                    if (childNodes.getLength() > 0) {
                        for (int i = 0; i < childNodes.getLength(); ++i) {
                            final T loadedChild = this.loadOne((Element)childNodes.item(i), mimeTypes, loader);
                            if (loadedChild != null) {
                                children.add(loadedChild);
                            }
                        }
                    }
                    final Set<Class<? extends T>> excludeChildren = new HashSet<Class<? extends T>>();
                    final NodeList excludeChildNodes = element.getElementsByTagName(this.getLoaderTagName() + "-exclude");
                    if (excludeChildNodes.getLength() > 0) {
                        for (int j = 0; j < excludeChildNodes.getLength(); ++j) {
                            final Element excl = (Element)excludeChildNodes.item(j);
                            final String exclName = excl.getAttribute("class");
                            try {
                                excludeChildren.add(loader.getServiceClass(this.getLoaderClass(), exclName));
                            }
                            catch (final ClassNotFoundException e2) {
                                throw new TikaConfigException("Class not found in -exclude list: " + exclName);
                            }
                        }
                    }
                    loaded = this.createComposite(loadedClass, children, excludeChildren, params, mimeTypes, loader);
                    if (loaded == null) {
                        loaded = this.newInstance(loadedClass);
                    }
                }
                else {
                    loaded = this.newInstance(loadedClass);
                }
                AnnotationUtils.assignFieldParams(loaded, params);
                if (loaded instanceof Initializable) {
                    ((Initializable)loaded).initialize(params);
                    ((Initializable)loaded).checkInitialization(initializableProblemHandler);
                }
                loaded = this.decorate(loaded, element);
                return loaded;
            }
            catch (final ClassNotFoundException e3) {
                if (loader.getLoadErrorHandler() == LoadErrorHandler.THROW) {
                    throw new TikaConfigException("Unable to find a " + this.getLoaderTagName() + " class: " + name, e3);
                }
                loader.getLoadErrorHandler().handleLoadError(name, e3);
                return null;
            }
            catch (final IllegalAccessException e4) {
                throw new TikaException("Unable to access a " + this.getLoaderTagName() + " class: " + name, e4);
            }
            catch (final InvocationTargetException e5) {
                throw new TikaException("Unable to create a " + this.getLoaderTagName() + " class: " + name, e5);
            }
            catch (final InstantiationException e6) {
                throw new TikaException("Unable to instantiate a " + this.getLoaderTagName() + " class: " + name, e6);
            }
            catch (final NoSuchMethodException e7) {
                throw new TikaException("Unable to find the right constructor for " + this.getLoaderTagName() + " class: " + name, e7);
            }
        }
        
        T newInstance(final Class<? extends T> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            return (T)loadedClass.newInstance();
        }
        
        Map<String, Param> getParams(final Element el) throws TikaException {
            final Map<String, Param> params = new HashMap<String, Param>();
            Node child = el.getFirstChild();
            while (child != null) {
                if ("params".equals(child.getNodeName())) {
                    if (child.hasChildNodes()) {
                        final NodeList childNodes = child.getChildNodes();
                        for (int i = 0; i < childNodes.getLength(); ++i) {
                            final Node item = childNodes.item(i);
                            if (item.getNodeType() == 1) {
                                final Param<?> param = Param.load(item);
                                params.put(param.getName(), param);
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    child = child.getNextSibling();
                }
            }
            return params;
        }
    }
    
    private static class ParserXmlLoader extends XmlLoader<CompositeParser, Parser>
    {
        private final EncodingDetector encodingDetector;
        
        private ParserXmlLoader(final EncodingDetector encodingDetector) {
            this.encodingDetector = encodingDetector;
        }
        
        @Override
        boolean supportsComposite() {
            return true;
        }
        
        @Override
        String getParentTagName() {
            return "parsers";
        }
        
        @Override
        String getLoaderTagName() {
            return "parser";
        }
        
        @Override
        Class<? extends Parser> getLoaderClass() {
            return Parser.class;
        }
        
        @Override
        Parser preLoadOne(final Class<? extends Parser> loadedClass, final String classname, final MimeTypes mimeTypes) throws TikaException {
            if (AutoDetectParser.class.isAssignableFrom(loadedClass)) {
                throw new TikaException("AutoDetectParser not supported in a <parser> configuration element: " + classname);
            }
            return null;
        }
        
        @Override
        boolean isComposite(final Parser loaded) {
            return loaded instanceof CompositeParser;
        }
        
        @Override
        boolean isComposite(final Class<? extends Parser> loadedClass) {
            return CompositeParser.class.isAssignableFrom(loadedClass) || AbstractMultipleParser.class.isAssignableFrom(loadedClass) || ParserDecorator.class.isAssignableFrom(loadedClass);
        }
        
        @Override
        CompositeParser createDefault(final MimeTypes mimeTypes, final ServiceLoader loader) {
            return getDefaultParser(mimeTypes, loader, this.encodingDetector);
        }
        
        @Override
        CompositeParser createComposite(final List<Parser> parsers, final MimeTypes mimeTypes, final ServiceLoader loader) {
            final MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            return new CompositeParser(registry, parsers);
        }
        
        @Override
        Parser createComposite(final Class<? extends Parser> parserClass, final List<Parser> childParsers, final Set<Class<? extends Parser>> excludeParsers, final Map<String, Param> params, final MimeTypes mimeTypes, final ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Parser parser = null;
            Constructor<? extends Parser> c = null;
            final MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class, EncodingDetector.class);
                    parser = (Parser)c.newInstance(registry, loader, excludeParsers, this.encodingDetector);
                }
                catch (final NoSuchMethodException ex) {}
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, ServiceLoader.class, Collection.class);
                    parser = (Parser)c.newInstance(registry, loader, excludeParsers);
                }
                catch (final NoSuchMethodException ex2) {}
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
                    parser = (Parser)c.newInstance(registry, childParsers, excludeParsers);
                }
                catch (final NoSuchMethodException ex3) {}
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, Collection.class, Map.class);
                    parser = (Parser)c.newInstance(registry, childParsers, params);
                }
                catch (final NoSuchMethodException ex4) {}
            }
            if (parser == null) {
                try {
                    c = parserClass.getConstructor(MediaTypeRegistry.class, List.class);
                    parser = (Parser)c.newInstance(registry, childParsers);
                }
                catch (final NoSuchMethodException ex5) {}
            }
            if (parser == null && ParserDecorator.class.isAssignableFrom(parserClass)) {
                try {
                    CompositeParser cp = null;
                    if (childParsers.size() == 1 && excludeParsers.size() == 0 && childParsers.get(0) instanceof CompositeParser) {
                        cp = childParsers.get(0);
                    }
                    else {
                        cp = new CompositeParser(registry, childParsers, excludeParsers);
                    }
                    c = parserClass.getConstructor(Parser.class);
                    parser = (Parser)c.newInstance(cp);
                }
                catch (final NoSuchMethodException ex6) {}
            }
            return parser;
        }
        
        @Override
        Parser newInstance(final Class<? extends Parser> loadedClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            if (AbstractEncodingDetectorParser.class.isAssignableFrom(loadedClass)) {
                final Constructor ctor = loadedClass.getConstructor(EncodingDetector.class);
                return ctor.newInstance(this.encodingDetector);
            }
            return (Parser)loadedClass.newInstance();
        }
        
        @Override
        Parser decorate(final Parser created, final Element element) throws IOException, TikaException {
            Parser parser = created;
            final Set<MediaType> parserTypes = mediaTypesListFromDomElement(element, "mime");
            if (!parserTypes.isEmpty()) {
                parser = ParserDecorator.withTypes(parser, parserTypes);
            }
            final Set<MediaType> parserExclTypes = mediaTypesListFromDomElement(element, "mime-exclude");
            if (!parserExclTypes.isEmpty()) {
                parser = ParserDecorator.withoutTypes(parser, parserExclTypes);
            }
            return parser;
        }
    }
    
    private static class DetectorXmlLoader extends XmlLoader<CompositeDetector, Detector>
    {
        @Override
        boolean supportsComposite() {
            return true;
        }
        
        @Override
        String getParentTagName() {
            return "detectors";
        }
        
        @Override
        String getLoaderTagName() {
            return "detector";
        }
        
        @Override
        Class<? extends Detector> getLoaderClass() {
            return Detector.class;
        }
        
        @Override
        Detector preLoadOne(final Class<? extends Detector> loadedClass, final String classname, final MimeTypes mimeTypes) throws TikaException {
            if (MimeTypes.class.equals(loadedClass)) {
                return mimeTypes;
            }
            return null;
        }
        
        @Override
        boolean isComposite(final Detector loaded) {
            return loaded instanceof CompositeDetector;
        }
        
        @Override
        boolean isComposite(final Class<? extends Detector> loadedClass) {
            return CompositeDetector.class.isAssignableFrom(loadedClass);
        }
        
        @Override
        CompositeDetector createDefault(final MimeTypes mimeTypes, final ServiceLoader loader) {
            return TikaConfig.getDefaultDetector(mimeTypes, loader);
        }
        
        @Override
        CompositeDetector createComposite(final List<Detector> detectors, final MimeTypes mimeTypes, final ServiceLoader loader) {
            final MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            return new CompositeDetector(registry, detectors);
        }
        
        @Override
        Detector createComposite(final Class<? extends Detector> detectorClass, final List<Detector> childDetectors, final Set<Class<? extends Detector>> excludeDetectors, final Map<String, Param> params, final MimeTypes mimeTypes, final ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            Detector detector = null;
            final MediaTypeRegistry registry = mimeTypes.getMediaTypeRegistry();
            if (detector == null) {
                try {
                    final Constructor<? extends Detector> c = detectorClass.getConstructor(MimeTypes.class, ServiceLoader.class, Collection.class);
                    detector = (Detector)c.newInstance(mimeTypes, loader, excludeDetectors);
                }
                catch (final NoSuchMethodException ex) {}
            }
            if (detector == null) {
                try {
                    final Constructor<? extends Detector> c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class, Collection.class);
                    detector = (Detector)c.newInstance(registry, childDetectors, excludeDetectors);
                }
                catch (final NoSuchMethodException ex2) {}
            }
            if (detector == null) {
                try {
                    final Constructor<? extends Detector> c = detectorClass.getConstructor(MediaTypeRegistry.class, List.class);
                    detector = (Detector)c.newInstance(registry, childDetectors);
                }
                catch (final NoSuchMethodException ex3) {}
            }
            if (detector == null) {
                try {
                    final Constructor<? extends Detector> c = detectorClass.getConstructor(List.class);
                    detector = (Detector)c.newInstance(childDetectors);
                }
                catch (final NoSuchMethodException ex4) {}
            }
            return detector;
        }
        
        @Override
        Detector decorate(final Detector created, final Element element) {
            return created;
        }
    }
    
    private static class TranslatorXmlLoader extends XmlLoader<Translator, Translator>
    {
        @Override
        boolean supportsComposite() {
            return false;
        }
        
        @Override
        String getParentTagName() {
            return null;
        }
        
        @Override
        String getLoaderTagName() {
            return "translator";
        }
        
        @Override
        Class<? extends Translator> getLoaderClass() {
            return Translator.class;
        }
        
        @Override
        Translator preLoadOne(final Class<? extends Translator> loadedClass, final String classname, final MimeTypes mimeTypes) throws TikaException {
            return null;
        }
        
        @Override
        boolean isComposite(final Translator loaded) {
            return false;
        }
        
        @Override
        boolean isComposite(final Class<? extends Translator> loadedClass) {
            return false;
        }
        
        @Override
        Translator createDefault(final MimeTypes mimeTypes, final ServiceLoader loader) {
            return getDefaultTranslator(loader);
        }
        
        @Override
        Translator createComposite(final List<Translator> loaded, final MimeTypes mimeTypes, final ServiceLoader loader) {
            return loaded.get(0);
        }
        
        @Override
        Translator createComposite(final Class<? extends Translator> compositeClass, final List<Translator> children, final Set<Class<? extends Translator>> excludeChildren, final Map<String, Param> params, final MimeTypes mimeTypes, final ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            throw new InstantiationException("Only one translator supported");
        }
        
        @Override
        Translator decorate(final Translator created, final Element element) {
            return created;
        }
    }
    
    private static class ExecutorServiceXmlLoader extends XmlLoader<ConfigurableThreadPoolExecutor, ConfigurableThreadPoolExecutor>
    {
        @Override
        ConfigurableThreadPoolExecutor createComposite(final Class<? extends ConfigurableThreadPoolExecutor> compositeClass, final List<ConfigurableThreadPoolExecutor> children, final Set<Class<? extends ConfigurableThreadPoolExecutor>> excludeChildren, final Map<String, Param> params, final MimeTypes mimeTypes, final ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            throw new InstantiationException("Only one executor service supported");
        }
        
        @Override
        ConfigurableThreadPoolExecutor createComposite(final List<ConfigurableThreadPoolExecutor> loaded, final MimeTypes mimeTypes, final ServiceLoader loader) {
            return loaded.get(0);
        }
        
        @Override
        ConfigurableThreadPoolExecutor createDefault(final MimeTypes mimeTypes, final ServiceLoader loader) {
            return getDefaultExecutorService();
        }
        
        @Override
        ConfigurableThreadPoolExecutor decorate(final ConfigurableThreadPoolExecutor created, final Element element) throws IOException, TikaException {
            final Element maxThreadElement = getChild(element, "max-threads");
            if (maxThreadElement != null) {
                created.setMaximumPoolSize(Integer.parseInt(getText(maxThreadElement)));
            }
            final Element coreThreadElement = getChild(element, "core-threads");
            if (coreThreadElement != null) {
                created.setCorePoolSize(Integer.parseInt(getText(coreThreadElement)));
            }
            return created;
        }
        
        @Override
        Class<? extends ConfigurableThreadPoolExecutor> getLoaderClass() {
            return ConfigurableThreadPoolExecutor.class;
        }
        
        @Override
        ConfigurableThreadPoolExecutor loadOne(final Element element, final MimeTypes mimeTypes, final ServiceLoader loader) throws TikaException, IOException {
            return super.loadOne(element, mimeTypes, loader);
        }
        
        @Override
        boolean supportsComposite() {
            return false;
        }
        
        @Override
        String getParentTagName() {
            return null;
        }
        
        @Override
        String getLoaderTagName() {
            return "executor-service";
        }
        
        @Override
        boolean isComposite(final ConfigurableThreadPoolExecutor loaded) {
            return false;
        }
        
        @Override
        boolean isComposite(final Class<? extends ConfigurableThreadPoolExecutor> loadedClass) {
            return false;
        }
        
        @Override
        ConfigurableThreadPoolExecutor preLoadOne(final Class<? extends ConfigurableThreadPoolExecutor> loadedClass, final String classname, final MimeTypes mimeTypes) throws TikaException {
            return null;
        }
    }
    
    private static class EncodingDetectorXmlLoader extends XmlLoader<EncodingDetector, EncodingDetector>
    {
        @Override
        boolean supportsComposite() {
            return true;
        }
        
        @Override
        String getParentTagName() {
            return "encodingDetectors";
        }
        
        @Override
        String getLoaderTagName() {
            return "encodingDetector";
        }
        
        @Override
        Class<? extends EncodingDetector> getLoaderClass() {
            return EncodingDetector.class;
        }
        
        @Override
        boolean isComposite(final EncodingDetector loaded) {
            return loaded instanceof CompositeEncodingDetector;
        }
        
        @Override
        boolean isComposite(final Class<? extends EncodingDetector> loadedClass) {
            return CompositeEncodingDetector.class.isAssignableFrom(loadedClass);
        }
        
        @Override
        EncodingDetector preLoadOne(final Class<? extends EncodingDetector> loadedClass, final String classname, final MimeTypes mimeTypes) throws TikaException {
            return null;
        }
        
        @Override
        EncodingDetector createDefault(final MimeTypes mimeTypes, final ServiceLoader loader) {
            return TikaConfig.getDefaultEncodingDetector(loader);
        }
        
        @Override
        CompositeEncodingDetector createComposite(final List<EncodingDetector> encodingDetectors, final MimeTypes mimeTypes, final ServiceLoader loader) {
            return new CompositeEncodingDetector(encodingDetectors);
        }
        
        @Override
        EncodingDetector createComposite(final Class<? extends EncodingDetector> encodingDetectorClass, final List<EncodingDetector> childEncodingDetectors, final Set<Class<? extends EncodingDetector>> excludeDetectors, final Map<String, Param> params, final MimeTypes mimeTypes, final ServiceLoader loader) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            EncodingDetector encodingDetector = null;
            if (encodingDetector == null) {
                try {
                    final Constructor<? extends EncodingDetector> c = encodingDetectorClass.getConstructor(ServiceLoader.class, Collection.class);
                    encodingDetector = (EncodingDetector)c.newInstance(loader, excludeDetectors);
                }
                catch (final NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            if (encodingDetector == null) {
                try {
                    final Constructor<? extends EncodingDetector> c = encodingDetectorClass.getConstructor(List.class);
                    encodingDetector = (EncodingDetector)c.newInstance(childEncodingDetectors);
                }
                catch (final NoSuchMethodException me) {
                    me.printStackTrace();
                }
            }
            return encodingDetector;
        }
        
        @Override
        EncodingDetector decorate(final EncodingDetector created, final Element element) {
            return created;
        }
    }
}
