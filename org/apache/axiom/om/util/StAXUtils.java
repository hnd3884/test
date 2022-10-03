package org.apache.axiom.om.util;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLOutputFactory;
import java.util.Collections;
import java.util.WeakHashMap;
import java.security.AccessController;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.apache.axiom.util.stax.wrapper.ImmutableXMLInputFactory;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.io.Writer;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLInputFactory;
import java.util.Map;
import org.apache.commons.logging.Log;

public class StAXUtils
{
    private static final Log log;
    private static boolean isFactoryPerClassLoader;
    private static final Map<StAXParserConfiguration, XMLInputFactory> inputFactoryMap;
    private static final Map<StAXWriterConfiguration, XMLOutputFactory> outputFactoryMap;
    private static final Map<StAXParserConfiguration, Map<ClassLoader, XMLInputFactory>> inputFactoryPerCLMap;
    private static final Map<StAXWriterConfiguration, Map<ClassLoader, XMLOutputFactory>> outputFactoryPerCLMap;
    
    public static XMLInputFactory getXMLInputFactory() {
        return getXMLInputFactory(null, StAXUtils.isFactoryPerClassLoader);
    }
    
    public static XMLInputFactory getXMLInputFactory(final StAXParserConfiguration configuration) {
        return getXMLInputFactory(configuration, StAXUtils.isFactoryPerClassLoader);
    }
    
    public static XMLInputFactory getXMLInputFactory(final boolean factoryPerClassLoaderPolicy) {
        return getXMLInputFactory(null, factoryPerClassLoaderPolicy);
    }
    
    public static XMLInputFactory getXMLInputFactory(final StAXParserConfiguration configuration, final boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return getXMLInputFactory_perClassLoader(configuration);
        }
        return getXMLInputFactory_singleton(configuration);
    }
    
    @Deprecated
    public static void releaseXMLInputFactory(final XMLInputFactory factory) {
    }
    
    public static XMLStreamReader createXMLStreamReader(final InputStream in, final String encoding) throws XMLStreamException {
        return createXMLStreamReader(null, in, encoding);
    }
    
    public static XMLStreamReader createXMLStreamReader(final StAXParserConfiguration configuration, final InputStream in, final String encoding) throws XMLStreamException {
        final XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(in, encoding);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
        }
        return reader;
    }
    
    public static XMLStreamReader createXMLStreamReader(final InputStream in) throws XMLStreamException {
        return createXMLStreamReader(null, in);
    }
    
    public static XMLStreamReader createXMLStreamReader(final StAXParserConfiguration configuration, final InputStream in) throws XMLStreamException {
        final XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(in);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
        }
        return reader;
    }
    
    public static XMLStreamReader createXMLStreamReader(final StAXParserConfiguration configuration, final String systemId, final InputStream in) throws XMLStreamException {
        final XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(systemId, in);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
        }
        return reader;
    }
    
    public static XMLStreamReader createXMLStreamReader(final Reader in) throws XMLStreamException {
        return createXMLStreamReader(null, in);
    }
    
    public static XMLStreamReader createXMLStreamReader(final StAXParserConfiguration configuration, final Reader in) throws XMLStreamException {
        final XMLStreamReader reader = getXMLInputFactory(configuration).createXMLStreamReader(in);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamReader is " + reader.getClass().getName()));
        }
        return reader;
    }
    
    public static XMLOutputFactory getXMLOutputFactory() {
        return getXMLOutputFactory(null, StAXUtils.isFactoryPerClassLoader);
    }
    
    public static XMLOutputFactory getXMLOutputFactory(final StAXWriterConfiguration configuration) {
        return getXMLOutputFactory(configuration, StAXUtils.isFactoryPerClassLoader);
    }
    
    public static XMLOutputFactory getXMLOutputFactory(final boolean factoryPerClassLoaderPolicy) {
        return getXMLOutputFactory(null, factoryPerClassLoaderPolicy);
    }
    
    public static XMLOutputFactory getXMLOutputFactory(final StAXWriterConfiguration configuration, final boolean factoryPerClassLoaderPolicy) {
        if (factoryPerClassLoaderPolicy) {
            return getXMLOutputFactory_perClassLoader(configuration);
        }
        return getXMLOutputFactory_singleton(configuration);
    }
    
    public static void setFactoryPerClassLoader(final boolean value) {
        StAXUtils.isFactoryPerClassLoader = value;
    }
    
    @Deprecated
    public static void releaseXMLOutputFactory(final XMLOutputFactory factory) {
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out) throws XMLStreamException {
        return createXMLStreamWriter(null, out);
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final StAXWriterConfiguration configuration, final OutputStream out) throws XMLStreamException {
        final XMLStreamWriter writer = getXMLOutputFactory(configuration).createXMLStreamWriter(out, "utf-8");
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
        }
        return writer;
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out, final String encoding) throws XMLStreamException {
        return createXMLStreamWriter(null, out, encoding);
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final StAXWriterConfiguration configuration, final OutputStream out, final String encoding) throws XMLStreamException {
        final XMLStreamWriter writer = getXMLOutputFactory(configuration).createXMLStreamWriter(out, encoding);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
        }
        return writer;
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final Writer out) throws XMLStreamException {
        return createXMLStreamWriter(null, out);
    }
    
    public static XMLStreamWriter createXMLStreamWriter(final StAXWriterConfiguration configuration, final Writer out) throws XMLStreamException {
        final XMLStreamWriter writer = getXMLOutputFactory(configuration).createXMLStreamWriter(out);
        if (StAXUtils.log.isDebugEnabled()) {
            StAXUtils.log.debug((Object)("XMLStreamWriter is " + writer.getClass().getName()));
        }
        return writer;
    }
    
    @Deprecated
    public static void reset() {
    }
    
    static Map<String, Object> loadFactoryProperties(final String name) {
        final ClassLoader cl = getContextClassLoader();
        final InputStream in = cl.getResourceAsStream(name);
        if (in == null) {
            return null;
        }
        try {
            final Properties rawProps = new Properties();
            final Map<String, Object> props = new HashMap<String, Object>();
            rawProps.load(in);
            for (final Map.Entry<Object, Object> entry : rawProps.entrySet()) {
                final String strValue = entry.getValue();
                Object value;
                if (strValue.equals("true")) {
                    value = Boolean.TRUE;
                }
                else if (strValue.equals("false")) {
                    value = Boolean.FALSE;
                }
                else {
                    try {
                        value = Integer.valueOf(strValue);
                    }
                    catch (final NumberFormatException ex) {
                        value = strValue;
                    }
                }
                props.put(entry.getKey(), value);
            }
            if (StAXUtils.log.isDebugEnabled()) {
                StAXUtils.log.debug((Object)("Loaded factory properties from " + name + ": " + props));
            }
            return props;
        }
        catch (final IOException ex2) {
            StAXUtils.log.error((Object)("Failed to read " + name), (Throwable)ex2);
            return null;
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex3) {}
        }
    }
    
    private static XMLInputFactory newXMLInputFactory(final ClassLoader classLoader, final StAXParserConfiguration configuration) {
        return AccessController.doPrivileged((PrivilegedAction<XMLInputFactory>)new PrivilegedAction<XMLInputFactory>() {
            public XMLInputFactory run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                }
                else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    factory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
                    final Map<String, Object> props = StAXUtils.loadFactoryProperties("XMLInputFactory.properties");
                    if (props != null) {
                        for (final Map.Entry<String, Object> entry : props.entrySet()) {
                            factory.setProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    final StAXDialect dialect = StAXDialectDetector.getDialect(factory);
                    if (configuration != null) {
                        factory = configuration.configure(factory, dialect);
                    }
                    return new ImmutableXMLInputFactory(dialect.normalize(dialect.makeThreadSafe(factory)));
                }
                finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }
    
    private static XMLInputFactory getXMLInputFactory_perClassLoader(StAXParserConfiguration configuration) {
        final ClassLoader cl = getContextClassLoader();
        XMLInputFactory factory;
        if (cl == null) {
            factory = getXMLInputFactory_singleton(configuration);
        }
        else {
            if (configuration == null) {
                configuration = StAXParserConfiguration.DEFAULT;
            }
            Map<ClassLoader, XMLInputFactory> map = StAXUtils.inputFactoryPerCLMap.get(configuration);
            if (map == null) {
                map = Collections.synchronizedMap(new WeakHashMap<ClassLoader, XMLInputFactory>());
                StAXUtils.inputFactoryPerCLMap.put(configuration, map);
                factory = null;
            }
            else {
                factory = map.get(cl);
            }
            if (factory == null) {
                if (StAXUtils.log.isDebugEnabled()) {
                    StAXUtils.log.debug((Object)("About to create XMLInputFactory implementation with classloader=" + cl));
                    StAXUtils.log.debug((Object)("The classloader for javax.xml.stream.XMLInputFactory is: " + XMLInputFactory.class.getClassLoader()));
                }
                try {
                    factory = newXMLInputFactory(null, configuration);
                }
                catch (final ClassCastException cce) {
                    if (StAXUtils.log.isDebugEnabled()) {
                        StAXUtils.log.debug((Object)("Failed creation of XMLInputFactory implementation with classloader=" + cl));
                        StAXUtils.log.debug((Object)("Exception is=" + cce));
                        StAXUtils.log.debug((Object)("Attempting with classloader: " + XMLInputFactory.class.getClassLoader()));
                    }
                    factory = newXMLInputFactory(XMLInputFactory.class.getClassLoader(), configuration);
                }
                if (factory != null) {
                    map.put(cl, factory);
                    if (StAXUtils.log.isDebugEnabled()) {
                        StAXUtils.log.debug((Object)("Created XMLInputFactory = " + factory.getClass() + " with classloader=" + cl));
                        StAXUtils.log.debug((Object)("Configuration = " + configuration));
                        StAXUtils.log.debug((Object)("Size of XMLInputFactory map for this configuration = " + map.size()));
                        StAXUtils.log.debug((Object)("Configurations for which factories have been cached = " + StAXUtils.inputFactoryPerCLMap.keySet()));
                    }
                }
                else {
                    factory = getXMLInputFactory_singleton(configuration);
                }
            }
        }
        return factory;
    }
    
    private static XMLInputFactory getXMLInputFactory_singleton(StAXParserConfiguration configuration) {
        if (configuration == null) {
            configuration = StAXParserConfiguration.DEFAULT;
        }
        XMLInputFactory f = StAXUtils.inputFactoryMap.get(configuration);
        if (f == null) {
            f = newXMLInputFactory(StAXUtils.class.getClassLoader(), configuration);
            StAXUtils.inputFactoryMap.put(configuration, f);
            if (StAXUtils.log.isDebugEnabled() && f != null) {
                StAXUtils.log.debug((Object)("Created singleton XMLInputFactory " + f.getClass() + " with configuration " + configuration));
            }
        }
        return f;
    }
    
    private static XMLOutputFactory newXMLOutputFactory(final ClassLoader classLoader, final StAXWriterConfiguration configuration) {
        return AccessController.doPrivileged((PrivilegedAction<XMLOutputFactory>)new PrivilegedAction<XMLOutputFactory>() {
            public XMLOutputFactory run() {
                ClassLoader savedClassLoader;
                if (classLoader == null) {
                    savedClassLoader = null;
                }
                else {
                    savedClassLoader = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(classLoader);
                }
                try {
                    XMLOutputFactory factory = XMLOutputFactory.newInstance();
                    factory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
                    final Map<String, Object> props = StAXUtils.loadFactoryProperties("XMLOutputFactory.properties");
                    if (props != null) {
                        for (final Map.Entry<String, Object> entry : props.entrySet()) {
                            factory.setProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    final StAXDialect dialect = StAXDialectDetector.getDialect(factory);
                    if (configuration != null) {
                        factory = configuration.configure(factory, dialect);
                    }
                    return new ImmutableXMLOutputFactory(dialect.normalize(dialect.makeThreadSafe(factory)));
                }
                finally {
                    if (savedClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(savedClassLoader);
                    }
                }
            }
        });
    }
    
    private static XMLOutputFactory getXMLOutputFactory_perClassLoader(StAXWriterConfiguration configuration) {
        final ClassLoader cl = getContextClassLoader();
        XMLOutputFactory factory;
        if (cl == null) {
            factory = getXMLOutputFactory_singleton(configuration);
        }
        else {
            if (configuration == null) {
                configuration = StAXWriterConfiguration.DEFAULT;
            }
            Map<ClassLoader, XMLOutputFactory> map = StAXUtils.outputFactoryPerCLMap.get(configuration);
            if (map == null) {
                map = Collections.synchronizedMap(new WeakHashMap<ClassLoader, XMLOutputFactory>());
                StAXUtils.outputFactoryPerCLMap.put(configuration, map);
                factory = null;
            }
            else {
                factory = map.get(cl);
            }
            if (factory == null) {
                if (StAXUtils.log.isDebugEnabled()) {
                    StAXUtils.log.debug((Object)("About to create XMLOutputFactory implementation with classloader=" + cl));
                    StAXUtils.log.debug((Object)("The classloader for javax.xml.stream.XMLOutputFactory is: " + XMLOutputFactory.class.getClassLoader()));
                }
                try {
                    factory = newXMLOutputFactory(null, configuration);
                }
                catch (final ClassCastException cce) {
                    if (StAXUtils.log.isDebugEnabled()) {
                        StAXUtils.log.debug((Object)("Failed creation of XMLOutputFactory implementation with classloader=" + cl));
                        StAXUtils.log.debug((Object)("Exception is=" + cce));
                        StAXUtils.log.debug((Object)("Attempting with classloader: " + XMLOutputFactory.class.getClassLoader()));
                    }
                    factory = newXMLOutputFactory(XMLOutputFactory.class.getClassLoader(), configuration);
                }
                if (factory != null) {
                    map.put(cl, factory);
                    if (StAXUtils.log.isDebugEnabled()) {
                        StAXUtils.log.debug((Object)("Created XMLOutputFactory = " + factory.getClass() + " for classloader=" + cl));
                        StAXUtils.log.debug((Object)("Configuration = " + configuration));
                        StAXUtils.log.debug((Object)("Size of XMLOutFactory map for this configuration = " + map.size()));
                        StAXUtils.log.debug((Object)("Configurations for which factories have been cached = " + StAXUtils.outputFactoryPerCLMap.keySet()));
                    }
                }
                else {
                    factory = getXMLOutputFactory_singleton(configuration);
                }
            }
        }
        return factory;
    }
    
    private static XMLOutputFactory getXMLOutputFactory_singleton(StAXWriterConfiguration configuration) {
        if (configuration == null) {
            configuration = StAXWriterConfiguration.DEFAULT;
        }
        XMLOutputFactory f = StAXUtils.outputFactoryMap.get(configuration);
        if (f == null) {
            f = newXMLOutputFactory(StAXUtils.class.getClassLoader(), configuration);
            StAXUtils.outputFactoryMap.put(configuration, f);
            if (StAXUtils.log.isDebugEnabled() && f != null) {
                StAXUtils.log.debug((Object)("Created singleton XMLOutputFactory " + f.getClass() + " with configuration " + configuration));
            }
        }
        return f;
    }
    
    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    @Deprecated
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final InputStream in, final String encoding) throws XMLStreamException {
        return createXMLStreamReader(StAXParserConfiguration.STANDALONE, in, encoding);
    }
    
    @Deprecated
    public static XMLInputFactory getNetworkDetachedXMLInputFactory() {
        return getXMLInputFactory(StAXParserConfiguration.STANDALONE);
    }
    
    @Deprecated
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final InputStream in) throws XMLStreamException {
        return createXMLStreamReader(StAXParserConfiguration.STANDALONE, in);
    }
    
    @Deprecated
    public static XMLStreamReader createNetworkDetachedXMLStreamReader(final Reader in) throws XMLStreamException {
        return createXMLStreamReader(StAXParserConfiguration.STANDALONE, in);
    }
    
    @Deprecated
    public static String getEventTypeString(final int event) {
        return XMLEventUtils.getEventTypeString(event);
    }
    
    static {
        log = LogFactory.getLog((Class)StAXUtils.class);
        StAXUtils.isFactoryPerClassLoader = true;
        inputFactoryMap = Collections.synchronizedMap(new WeakHashMap<StAXParserConfiguration, XMLInputFactory>());
        outputFactoryMap = Collections.synchronizedMap(new WeakHashMap<StAXWriterConfiguration, XMLOutputFactory>());
        inputFactoryPerCLMap = Collections.synchronizedMap(new WeakHashMap<StAXParserConfiguration, Map<ClassLoader, XMLInputFactory>>());
        outputFactoryPerCLMap = Collections.synchronizedMap(new WeakHashMap<StAXWriterConfiguration, Map<ClassLoader, XMLOutputFactory>>());
    }
}
