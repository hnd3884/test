package org.apache.tika.utils;

import org.xml.sax.SAXParseException;
import org.slf4j.LoggerFactory;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.xml.sax.helpers.DefaultHandler;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.io.IOException;
import org.w3c.dom.Document;
import org.apache.tika.parser.ParseContext;
import java.io.InputStream;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.xml.sax.XMLReader;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.stream.XMLResolver;
import org.xml.sax.EntityResolver;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.xml.sax.ErrorHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.slf4j.Logger;
import java.io.Serializable;

public class XMLReaderUtils implements Serializable
{
    public static final int DEFAULT_POOL_SIZE = 10;
    public static final int DEFAULT_MAX_ENTITY_EXPANSIONS = 20;
    private static final long serialVersionUID = 6110455808615143122L;
    private static final Logger LOG;
    private static final String XERCES_SECURITY_MANAGER = "org.apache.xerces.util.SecurityManager";
    private static final String XERCES_SECURITY_MANAGER_PROPERTY = "http://apache.org/xml/properties/security-manager";
    private static final ContentHandler IGNORING_CONTENT_HANDLER;
    private static final DTDHandler IGNORING_DTD_HANDLER;
    private static final ErrorHandler IGNORING_ERROR_HANDLER;
    private static final String JAXP_ENTITY_EXPANSION_LIMIT_KEY = "jdk.xml.entityExpansionLimit";
    private static final ReentrantReadWriteLock SAX_READ_WRITE_LOCK;
    private static final ReentrantReadWriteLock DOM_READ_WRITE_LOCK;
    private static final AtomicInteger POOL_GENERATION;
    private static final EntityResolver IGNORING_SAX_ENTITY_RESOLVER;
    private static final XMLResolver IGNORING_STAX_ENTITY_RESOLVER;
    private static int POOL_SIZE;
    private static long LAST_LOG;
    private static volatile int MAX_ENTITY_EXPANSIONS;
    private static ArrayBlockingQueue<PoolSAXParser> SAX_PARSERS;
    private static ArrayBlockingQueue<PoolDOMBuilder> DOM_BUILDERS;
    
    private static int determineMaxEntityExpansions() {
        final String expansionLimit = System.getProperty("jdk.xml.entityExpansionLimit");
        if (expansionLimit != null) {
            try {
                return Integer.parseInt(expansionLimit);
            }
            catch (final NumberFormatException e) {
                XMLReaderUtils.LOG.warn("Couldn't parse an integer for the entity expansion limit: {}; backing off to default: {}", (Object)expansionLimit, (Object)20);
            }
        }
        return 20;
    }
    
    public static XMLReader getXMLReader() throws TikaException {
        XMLReader reader;
        try {
            reader = getSAXParser().getXMLReader();
        }
        catch (final SAXException e) {
            throw new TikaException("Unable to create an XMLReader", e);
        }
        reader.setEntityResolver(XMLReaderUtils.IGNORING_SAX_ENTITY_RESOLVER);
        return reader;
    }
    
    public static SAXParser getSAXParser() throws TikaException {
        try {
            final SAXParser parser = getSAXParserFactory().newSAXParser();
            trySetXercesSecurityManager(parser);
            return parser;
        }
        catch (final ParserConfigurationException e) {
            throw new TikaException("Unable to configure a SAX parser", e);
        }
        catch (final SAXException e2) {
            throw new TikaException("Unable to create a SAX parser", e2);
        }
    }
    
    public static SAXParserFactory getSAXParserFactory() {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        return factory;
    }
    
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        trySetXercesSecurityManager(factory);
        return factory;
    }
    
    public static DocumentBuilder getDocumentBuilder() throws TikaException {
        try {
            final DocumentBuilderFactory documentBuilderFactory = getDocumentBuilderFactory();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(XMLReaderUtils.IGNORING_SAX_ENTITY_RESOLVER);
            documentBuilder.setErrorHandler(null);
            return documentBuilder;
        }
        catch (final ParserConfigurationException e) {
            throw new TikaException("XML parser not available", e);
        }
    }
    
    public static XMLInputFactory getXMLInputFactory() {
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        tryToSetStaxProperty(factory, "javax.xml.stream.isNamespaceAware", true);
        tryToSetStaxProperty(factory, "javax.xml.stream.isValidating", false);
        factory.setXMLResolver(XMLReaderUtils.IGNORING_STAX_ENTITY_RESOLVER);
        trySetStaxSecurityManager(factory);
        return factory;
    }
    
    private static void trySetTransformerAttribute(final TransformerFactory transformerFactory, final String attribute, final String value) {
        try {
            transformerFactory.setAttribute(attribute, value);
        }
        catch (final SecurityException e) {
            throw e;
        }
        catch (final Exception e2) {
            XMLReaderUtils.LOG.warn("Transformer Attribute unsupported: {}", (Object)attribute, (Object)e2);
        }
        catch (final AbstractMethodError ame) {
            XMLReaderUtils.LOG.warn("Cannot set Transformer attribute because outdated XML parser in classpath: {}", (Object)attribute, (Object)ame);
        }
    }
    
    private static void trySetSAXFeature(final SAXParserFactory saxParserFactory, final String feature, final boolean enabled) {
        try {
            saxParserFactory.setFeature(feature, enabled);
        }
        catch (final SecurityException e) {
            throw e;
        }
        catch (final Exception e2) {
            XMLReaderUtils.LOG.warn("SAX Feature unsupported: {}", (Object)feature, (Object)e2);
        }
        catch (final AbstractMethodError ame) {
            XMLReaderUtils.LOG.warn("Cannot set SAX feature because outdated XML parser in classpath: {}", (Object)feature, (Object)ame);
        }
    }
    
    private static void trySetSAXFeature(final DocumentBuilderFactory documentBuilderFactory, final String feature, final boolean enabled) {
        try {
            documentBuilderFactory.setFeature(feature, enabled);
        }
        catch (final Exception e) {
            XMLReaderUtils.LOG.warn("SAX Feature unsupported: {}", (Object)feature, (Object)e);
        }
        catch (final AbstractMethodError ame) {
            XMLReaderUtils.LOG.warn("Cannot set SAX feature because outdated XML parser in classpath: {}", (Object)feature, (Object)ame);
        }
    }
    
    private static void tryToSetStaxProperty(final XMLInputFactory factory, final String key, final boolean value) {
        try {
            factory.setProperty(key, value);
        }
        catch (final IllegalArgumentException e) {
            XMLReaderUtils.LOG.warn("StAX Feature unsupported: {}", (Object)key, (Object)e);
        }
    }
    
    public static Transformer getTransformer() throws TikaException {
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            trySetTransformerAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            trySetTransformerAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
            return transformerFactory.newTransformer();
        }
        catch (final TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new TikaException("Transformer not available", e);
        }
    }
    
    public static Document buildDOM(final InputStream is, final ParseContext context) throws TikaException, IOException, SAXException {
        DocumentBuilder builder = context.get(DocumentBuilder.class);
        PoolDOMBuilder poolBuilder = null;
        if (builder == null) {
            poolBuilder = acquireDOMBuilder();
            builder = poolBuilder.getDocumentBuilder();
        }
        try {
            return builder.parse(is);
        }
        finally {
            if (poolBuilder != null) {
                releaseDOMBuilder(poolBuilder);
            }
        }
    }
    
    public static Document buildDOM(final Path path) throws TikaException, IOException, SAXException {
        try (final InputStream is = Files.newInputStream(path, new OpenOption[0])) {
            return buildDOM(is);
        }
    }
    
    public static Document buildDOM(final String uriString) throws TikaException, IOException, SAXException {
        final PoolDOMBuilder builder = acquireDOMBuilder();
        try {
            return builder.getDocumentBuilder().parse(uriString);
        }
        finally {
            releaseDOMBuilder(builder);
        }
    }
    
    public static Document buildDOM(final InputStream is) throws TikaException, IOException, SAXException {
        final PoolDOMBuilder builder = acquireDOMBuilder();
        try {
            return builder.getDocumentBuilder().parse(is);
        }
        finally {
            releaseDOMBuilder(builder);
        }
    }
    
    public static void parseSAX(final InputStream is, final DefaultHandler contentHandler, final ParseContext context) throws TikaException, IOException, SAXException {
        SAXParser saxParser = context.get(SAXParser.class);
        PoolSAXParser poolSAXParser = null;
        if (saxParser == null) {
            poolSAXParser = acquireSAXParser();
            saxParser = poolSAXParser.getSAXParser();
        }
        try {
            saxParser.parse(is, contentHandler);
        }
        finally {
            if (poolSAXParser != null) {
                releaseParser(poolSAXParser);
            }
        }
    }
    
    private static PoolDOMBuilder acquireDOMBuilder() throws TikaException {
        int waiting = 0;
        long lastWarn = -1L;
        while (true) {
            PoolDOMBuilder builder = null;
            XMLReaderUtils.DOM_READ_WRITE_LOCK.readLock().lock();
            try {
                builder = XMLReaderUtils.DOM_BUILDERS.poll(100L, TimeUnit.MILLISECONDS);
            }
            catch (final InterruptedException e) {
                throw new TikaException("interrupted while waiting for DOMBuilder", e);
            }
            finally {
                XMLReaderUtils.DOM_READ_WRITE_LOCK.readLock().unlock();
            }
            if (builder != null) {
                return builder;
            }
            if (lastWarn < 0L || System.currentTimeMillis() - lastWarn > 1000L) {
                XMLReaderUtils.LOG.warn("Contention waiting for a DOMParser. Consider increasing the XMLReaderUtils.POOL_SIZE");
                lastWarn = System.currentTimeMillis();
            }
            if (++waiting > 3000) {
                setPoolSize(XMLReaderUtils.POOL_SIZE);
                throw new TikaException("Waited more than 5 minutes for a DocumentBuilder; This could indicate that a parser has not correctly released its DocumentBuilder. Please report this to the Tika team: dev@tika.apache.org");
            }
        }
    }
    
    private static void releaseDOMBuilder(final PoolDOMBuilder builder) {
        if (builder.getPoolGeneration() != XMLReaderUtils.POOL_GENERATION.get()) {
            return;
        }
        try {
            builder.reset();
        }
        catch (final UnsupportedOperationException ex) {}
        XMLReaderUtils.DOM_READ_WRITE_LOCK.readLock().lock();
        try {
            final boolean success = XMLReaderUtils.DOM_BUILDERS.offer(builder);
            if (!success) {
                XMLReaderUtils.LOG.warn("DocumentBuilder not taken back into pool.  If you haven't resized the pool, this could be a sign that there are more calls to 'acquire' than to 'release'");
            }
        }
        finally {
            XMLReaderUtils.DOM_READ_WRITE_LOCK.readLock().unlock();
        }
    }
    
    private static PoolSAXParser acquireSAXParser() throws TikaException {
        int waiting = 0;
        long lastWarn = -1L;
        while (true) {
            PoolSAXParser parser = null;
            XMLReaderUtils.SAX_READ_WRITE_LOCK.readLock().lock();
            try {
                parser = XMLReaderUtils.SAX_PARSERS.poll(100L, TimeUnit.MILLISECONDS);
            }
            catch (final InterruptedException e) {
                throw new TikaException("interrupted while waiting for SAXParser", e);
            }
            finally {
                XMLReaderUtils.SAX_READ_WRITE_LOCK.readLock().unlock();
            }
            if (parser != null) {
                return parser;
            }
            if (lastWarn < 0L || System.currentTimeMillis() - lastWarn > 1000L) {
                XMLReaderUtils.LOG.warn("Contention waiting for a SAXParser. Consider increasing the XMLReaderUtils.POOL_SIZE");
                lastWarn = System.currentTimeMillis();
            }
            if (++waiting > 3000) {
                setPoolSize(XMLReaderUtils.POOL_SIZE);
                throw new TikaException("Waited more than 5 minutes for a SAXParser; This could indicate that a parser has not correctly released its SAXParser. Please report this to the Tika team: dev@tika.apache.org");
            }
        }
    }
    
    private static void releaseParser(final PoolSAXParser parser) {
        try {
            parser.reset();
        }
        catch (final UnsupportedOperationException ex) {}
        if (parser.getGeneration() != XMLReaderUtils.POOL_GENERATION.get()) {
            return;
        }
        XMLReaderUtils.SAX_READ_WRITE_LOCK.readLock().lock();
        try {
            final boolean success = XMLReaderUtils.SAX_PARSERS.offer(parser);
            if (!success) {
                XMLReaderUtils.LOG.warn("SAXParser not taken back into pool.  If you haven't resized the pool this could be a sign that there are more calls to 'acquire' than to 'release'");
            }
        }
        finally {
            XMLReaderUtils.SAX_READ_WRITE_LOCK.readLock().unlock();
        }
    }
    
    private static void trySetXercesSecurityManager(final DocumentBuilderFactory factory) {
        for (final String securityManagerClassName : new String[] { "org.apache.xerces.util.SecurityManager" }) {
            try {
                final Object mgr = Class.forName(securityManagerClassName).newInstance();
                final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
                factory.setAttribute("http://apache.org/xml/properties/security-manager", mgr);
                return;
            }
            catch (final ClassNotFoundException ex) {}
            catch (final Throwable e) {
                if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                    XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
                }
            }
        }
        try {
            factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
        }
        catch (final IllegalArgumentException e2) {
            if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", (Throwable)e2);
                XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
            }
        }
    }
    
    private static void trySetXercesSecurityManager(final SAXParser parser) {
        for (final String securityManagerClassName : new String[] { "org.apache.xerces.util.SecurityManager" }) {
            try {
                final Object mgr = Class.forName(securityManagerClassName).newInstance();
                final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
                parser.setProperty("http://apache.org/xml/properties/security-manager", mgr);
                return;
            }
            catch (final ClassNotFoundException ex) {}
            catch (final Throwable e) {
                if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                    XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
                }
            }
        }
        try {
            parser.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
        }
        catch (final SAXException e2) {
            if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", (Throwable)e2);
                XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
            }
        }
    }
    
    private static void trySetStaxSecurityManager(final XMLInputFactory inputFactory) {
        try {
            inputFactory.setProperty("com.ctc.wstx.maxEntityCount", XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
        }
        catch (final IllegalArgumentException e) {
            if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", (Throwable)e);
                XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
            }
        }
    }
    
    public static int getPoolSize() {
        return XMLReaderUtils.POOL_SIZE;
    }
    
    public static void setPoolSize(final int poolSize) throws TikaException {
        XMLReaderUtils.SAX_READ_WRITE_LOCK.writeLock().lock();
        try {
            for (final PoolSAXParser parser : XMLReaderUtils.SAX_PARSERS) {
                parser.reset();
            }
            XMLReaderUtils.SAX_PARSERS.clear();
            XMLReaderUtils.SAX_PARSERS = new ArrayBlockingQueue<PoolSAXParser>(poolSize);
            final int generation = XMLReaderUtils.POOL_GENERATION.incrementAndGet();
            for (int i = 0; i < poolSize; ++i) {
                try {
                    XMLReaderUtils.SAX_PARSERS.offer(buildPoolParser(generation, getSAXParserFactory().newSAXParser()));
                }
                catch (final SAXException | ParserConfigurationException e) {
                    throw new TikaException("problem creating sax parser", e);
                }
            }
        }
        finally {
            XMLReaderUtils.SAX_READ_WRITE_LOCK.writeLock().unlock();
        }
        XMLReaderUtils.DOM_READ_WRITE_LOCK.writeLock().lock();
        try {
            XMLReaderUtils.DOM_BUILDERS.clear();
            XMLReaderUtils.DOM_BUILDERS = new ArrayBlockingQueue<PoolDOMBuilder>(poolSize);
            for (int j = 0; j < poolSize; ++j) {
                XMLReaderUtils.DOM_BUILDERS.offer(new PoolDOMBuilder(XMLReaderUtils.POOL_GENERATION.get(), getDocumentBuilder()));
            }
        }
        finally {
            XMLReaderUtils.DOM_READ_WRITE_LOCK.writeLock().unlock();
        }
        XMLReaderUtils.POOL_SIZE = poolSize;
    }
    
    public static int getMaxEntityExpansions() {
        return XMLReaderUtils.MAX_ENTITY_EXPANSIONS;
    }
    
    public static void setMaxEntityExpansions(final int maxEntityExpansions) {
        XMLReaderUtils.MAX_ENTITY_EXPANSIONS = maxEntityExpansions;
    }
    
    public static String getAttrValue(final String localName, final Attributes atts) {
        for (int i = 0; i < atts.getLength(); ++i) {
            if (localName.equals(atts.getLocalName(i))) {
                return atts.getValue(i);
            }
        }
        return null;
    }
    
    private static PoolSAXParser buildPoolParser(final int generation, final SAXParser parser) {
        boolean canReset = false;
        try {
            parser.reset();
            canReset = true;
        }
        catch (final UnsupportedOperationException e) {
            canReset = false;
        }
        boolean hasSecurityManager = false;
        try {
            final Object mgr = Class.forName("org.apache.xerces.util.SecurityManager").newInstance();
            final Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
            setLimit.invoke(mgr, XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
            parser.setProperty("http://apache.org/xml/properties/security-manager", mgr);
            hasSecurityManager = true;
        }
        catch (final SecurityException e2) {
            throw e2;
        }
        catch (final ClassNotFoundException ex) {}
        catch (final Throwable e3) {
            if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", e3);
                XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
            }
        }
        boolean canSetJaxPEntity = false;
        if (!hasSecurityManager) {
            try {
                parser.setProperty("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", XMLReaderUtils.MAX_ENTITY_EXPANSIONS);
                canSetJaxPEntity = true;
            }
            catch (final SAXException e4) {
                if (System.currentTimeMillis() > XMLReaderUtils.LAST_LOG + TimeUnit.MINUTES.toMillis(5L)) {
                    XMLReaderUtils.LOG.warn("SAX Security Manager could not be setup [log suppressed for 5 minutes]", (Throwable)e4);
                    XMLReaderUtils.LAST_LOG = System.currentTimeMillis();
                }
            }
        }
        if (!canReset && hasSecurityManager) {
            return new XercesPoolSAXParser(generation, parser);
        }
        if (canReset && hasSecurityManager) {
            return new Xerces2PoolSAXParser(generation, parser);
        }
        if (canReset && !hasSecurityManager && canSetJaxPEntity) {
            return new BuiltInPoolSAXParser(generation, parser);
        }
        return new UnrecognizedPoolSAXParser(generation, parser);
    }
    
    private static void clearReader(final XMLReader reader) {
        if (reader == null) {
            return;
        }
        reader.setContentHandler(XMLReaderUtils.IGNORING_CONTENT_HANDLER);
        reader.setDTDHandler(XMLReaderUtils.IGNORING_DTD_HANDLER);
        reader.setEntityResolver(XMLReaderUtils.IGNORING_SAX_ENTITY_RESOLVER);
        reader.setErrorHandler(XMLReaderUtils.IGNORING_ERROR_HANDLER);
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)XMLReaderUtils.class);
        IGNORING_CONTENT_HANDLER = new DefaultHandler();
        IGNORING_DTD_HANDLER = new DTDHandler() {
            @Override
            public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
            }
            
            @Override
            public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
            }
        };
        IGNORING_ERROR_HANDLER = new ErrorHandler() {
            @Override
            public void warning(final SAXParseException exception) throws SAXException {
            }
            
            @Override
            public void error(final SAXParseException exception) throws SAXException {
            }
            
            @Override
            public void fatalError(final SAXParseException exception) throws SAXException {
            }
        };
        SAX_READ_WRITE_LOCK = new ReentrantReadWriteLock();
        DOM_READ_WRITE_LOCK = new ReentrantReadWriteLock();
        POOL_GENERATION = new AtomicInteger();
        IGNORING_SAX_ENTITY_RESOLVER = ((publicId, systemId) -> {
            new InputSource(new StringReader(""));
            return;
        });
        IGNORING_STAX_ENTITY_RESOLVER = ((publicID, systemID, baseURI, namespace) -> "");
        XMLReaderUtils.POOL_SIZE = 10;
        XMLReaderUtils.LAST_LOG = -1L;
        XMLReaderUtils.MAX_ENTITY_EXPANSIONS = determineMaxEntityExpansions();
        XMLReaderUtils.SAX_PARSERS = new ArrayBlockingQueue<PoolSAXParser>(XMLReaderUtils.POOL_SIZE);
        XMLReaderUtils.DOM_BUILDERS = new ArrayBlockingQueue<PoolDOMBuilder>(XMLReaderUtils.POOL_SIZE);
        try {
            setPoolSize(XMLReaderUtils.POOL_SIZE);
        }
        catch (final TikaException e) {
            throw new RuntimeException("problem initializing SAXParser and DOMBuilder pools", e);
        }
    }
    
    private static class PoolDOMBuilder
    {
        private final int poolGeneration;
        private final DocumentBuilder documentBuilder;
        
        PoolDOMBuilder(final int poolGeneration, final DocumentBuilder documentBuilder) {
            this.poolGeneration = poolGeneration;
            this.documentBuilder = documentBuilder;
        }
        
        public int getPoolGeneration() {
            return this.poolGeneration;
        }
        
        public DocumentBuilder getDocumentBuilder() {
            return this.documentBuilder;
        }
        
        public void reset() {
            this.documentBuilder.reset();
            this.documentBuilder.setEntityResolver(XMLReaderUtils.IGNORING_SAX_ENTITY_RESOLVER);
            this.documentBuilder.setErrorHandler(null);
        }
    }
    
    private abstract static class PoolSAXParser
    {
        final int poolGeneration;
        final SAXParser saxParser;
        
        PoolSAXParser(final int poolGeneration, final SAXParser saxParser) {
            this.poolGeneration = poolGeneration;
            this.saxParser = saxParser;
        }
        
        abstract void reset();
        
        public int getGeneration() {
            return this.poolGeneration;
        }
        
        public SAXParser getSAXParser() {
            return this.saxParser;
        }
    }
    
    private static class XercesPoolSAXParser extends PoolSAXParser
    {
        public XercesPoolSAXParser(final int generation, final SAXParser parser) {
            super(generation, parser);
        }
        
        public void reset() {
            try {
                final XMLReader reader = this.saxParser.getXMLReader();
                clearReader(reader);
            }
            catch (final SAXException ex) {}
        }
    }
    
    private static class Xerces2PoolSAXParser extends PoolSAXParser
    {
        public Xerces2PoolSAXParser(final int generation, final SAXParser parser) {
            super(generation, parser);
        }
        
        @Override
        void reset() {
            try {
                final Object object = this.saxParser.getProperty("http://apache.org/xml/properties/security-manager");
                this.saxParser.reset();
                this.saxParser.setProperty("http://apache.org/xml/properties/security-manager", object);
            }
            catch (final SAXException e) {
                XMLReaderUtils.LOG.warn("problem resetting sax parser", (Throwable)e);
            }
            try {
                final XMLReader reader = this.saxParser.getXMLReader();
                clearReader(reader);
            }
            catch (final SAXException ex) {}
        }
    }
    
    private static class BuiltInPoolSAXParser extends PoolSAXParser
    {
        public BuiltInPoolSAXParser(final int generation, final SAXParser parser) {
            super(generation, parser);
        }
        
        @Override
        void reset() {
            this.saxParser.reset();
            try {
                final XMLReader reader = this.saxParser.getXMLReader();
                clearReader(reader);
            }
            catch (final SAXException ex) {}
        }
    }
    
    private static class UnrecognizedPoolSAXParser extends PoolSAXParser
    {
        public UnrecognizedPoolSAXParser(final int generation, final SAXParser parser) {
            super(generation, parser);
        }
        
        @Override
        void reset() {
            try {
                this.saxParser.reset();
            }
            catch (final UnsupportedOperationException ex) {}
            try {
                final XMLReader reader = this.saxParser.getXMLReader();
                clearReader(reader);
            }
            catch (final SAXException ex2) {}
            trySetXercesSecurityManager(this.saxParser);
        }
    }
}
