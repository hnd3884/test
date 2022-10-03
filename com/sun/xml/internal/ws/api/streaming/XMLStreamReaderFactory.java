package com.sun.xml.internal.ws.api.streaming;

import java.lang.reflect.InvocationTargetException;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import com.sun.istack.internal.NotNull;
import java.io.Reader;
import java.io.InputStream;
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.resources.StreamingMessages;
import java.util.logging.Level;
import javax.xml.stream.XMLInputFactory;
import java.util.logging.Logger;

public abstract class XMLStreamReaderFactory
{
    private static final Logger LOGGER;
    private static final String CLASS_NAME_OF_WSTXINPUTFACTORY = "com.ctc.wstx.stax.WstxInputFactory";
    private static volatile ContextClassloaderLocal<XMLStreamReaderFactory> streamReader;
    
    private static XMLInputFactory getXMLInputFactory() {
        XMLInputFactory xif = null;
        if (getProperty(XMLStreamReaderFactory.class.getName() + ".woodstox")) {
            try {
                xif = (XMLInputFactory)Class.forName("com.ctc.wstx.stax.WstxInputFactory").newInstance();
            }
            catch (final Exception e) {
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.WARNING)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.WARNING, StreamingMessages.WOODSTOX_CANT_LOAD("com.ctc.wstx.stax.WstxInputFactory"), e);
                }
            }
        }
        if (xif == null) {
            xif = XmlUtil.newXMLInputFactory(true);
        }
        xif.setProperty("javax.xml.stream.isNamespaceAware", true);
        xif.setProperty("javax.xml.stream.supportDTD", false);
        xif.setProperty("javax.xml.stream.isCoalescing", true);
        return xif;
    }
    
    public static void set(final XMLStreamReaderFactory f) {
        if (f == null) {
            throw new IllegalArgumentException();
        }
        XMLStreamReaderFactory.streamReader.set(f);
    }
    
    public static XMLStreamReaderFactory get() {
        return XMLStreamReaderFactory.streamReader.get();
    }
    
    public static XMLStreamReader create(final InputSource source, final boolean rejectDTDs) {
        try {
            if (source.getCharacterStream() != null) {
                return get().doCreate(source.getSystemId(), source.getCharacterStream(), rejectDTDs);
            }
            if (source.getByteStream() != null) {
                return get().doCreate(source.getSystemId(), source.getByteStream(), rejectDTDs);
            }
            return get().doCreate(source.getSystemId(), new URL(source.getSystemId()).openStream(), rejectDTDs);
        }
        catch (final IOException e) {
            throw new XMLReaderException("stax.cantCreate", new Object[] { e });
        }
    }
    
    public static XMLStreamReader create(@Nullable final String systemId, final InputStream in, final boolean rejectDTDs) {
        return get().doCreate(systemId, in, rejectDTDs);
    }
    
    public static XMLStreamReader create(@Nullable final String systemId, final InputStream in, @Nullable final String encoding, final boolean rejectDTDs) {
        return (encoding == null) ? create(systemId, in, rejectDTDs) : get().doCreate(systemId, in, encoding, rejectDTDs);
    }
    
    public static XMLStreamReader create(@Nullable final String systemId, final Reader reader, final boolean rejectDTDs) {
        return get().doCreate(systemId, reader, rejectDTDs);
    }
    
    public static void recycle(final XMLStreamReader r) {
        get().doRecycle(r);
        if (r instanceof RecycleAware) {
            ((RecycleAware)r).onRecycled();
        }
    }
    
    public abstract XMLStreamReader doCreate(final String p0, final InputStream p1, final boolean p2);
    
    private XMLStreamReader doCreate(final String systemId, final InputStream in, @NotNull final String encoding, final boolean rejectDTDs) {
        Reader reader;
        try {
            reader = new InputStreamReader(in, encoding);
        }
        catch (final UnsupportedEncodingException ue) {
            throw new XMLReaderException("stax.cantCreate", new Object[] { ue });
        }
        return this.doCreate(systemId, reader, rejectDTDs);
    }
    
    public abstract XMLStreamReader doCreate(final String p0, final Reader p1, final boolean p2);
    
    public abstract void doRecycle(final XMLStreamReader p0);
    
    private static int buildIntegerValue(final String propertyName, final int defaultValue) {
        final String propVal = System.getProperty(propertyName);
        if (propVal != null && propVal.length() > 0) {
            try {
                final Integer value = Integer.parseInt(propVal);
                if (value > 0) {
                    return value;
                }
            }
            catch (final NumberFormatException nfe) {
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.WARNING)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_INTEGER(propertyName, propVal, Integer.toString(defaultValue)), nfe);
                }
            }
        }
        return defaultValue;
    }
    
    private static long buildLongValue(final String propertyName, final long defaultValue) {
        final String propVal = System.getProperty(propertyName);
        if (propVal != null && propVal.length() > 0) {
            try {
                final long value = Long.parseLong(propVal);
                if (value > 0L) {
                    return value;
                }
            }
            catch (final NumberFormatException nfe) {
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.WARNING)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.WARNING, StreamingMessages.INVALID_PROPERTY_VALUE_LONG(propertyName, propVal, Long.toString(defaultValue)), nfe);
                }
            }
        }
        return defaultValue;
    }
    
    private static Boolean getProperty(final String prop) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String value = System.getProperty(prop);
                return (value != null) ? Boolean.valueOf(value) : Boolean.FALSE;
            }
        });
    }
    
    static {
        LOGGER = Logger.getLogger(XMLStreamReaderFactory.class.getName());
        XMLStreamReaderFactory.streamReader = new ContextClassloaderLocal<XMLStreamReaderFactory>() {
            @Override
            protected XMLStreamReaderFactory initialValue() {
                final XMLInputFactory xif = getXMLInputFactory();
                XMLStreamReaderFactory f = null;
                if (!getProperty(XMLStreamReaderFactory.class.getName() + ".noPool")) {
                    f = Zephyr.newInstance(xif);
                }
                if (f == null && xif.getClass().getName().equals("com.ctc.wstx.stax.WstxInputFactory")) {
                    f = new Woodstox(xif);
                }
                if (f == null) {
                    f = new Default();
                }
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "XMLStreamReaderFactory instance is = {0}", f);
                }
                return f;
            }
        };
    }
    
    private static final class Zephyr extends XMLStreamReaderFactory
    {
        private final XMLInputFactory xif;
        private final ThreadLocal<XMLStreamReader> pool;
        private final Method setInputSourceMethod;
        private final Method resetMethod;
        private final Class zephyrClass;
        
        @Nullable
        public static XMLStreamReaderFactory newInstance(final XMLInputFactory xif) {
            try {
                final Class<?> clazz = xif.createXMLStreamReader(new StringReader("<foo/>")).getClass();
                if (!clazz.getName().startsWith("com.sun.xml.internal.stream.")) {
                    return null;
                }
                return new Zephyr(xif, clazz);
            }
            catch (final NoSuchMethodException e) {
                return null;
            }
            catch (final XMLStreamException e2) {
                return null;
            }
        }
        
        public Zephyr(final XMLInputFactory xif, final Class clazz) throws NoSuchMethodException {
            this.pool = new ThreadLocal<XMLStreamReader>();
            this.zephyrClass = clazz;
            this.setInputSourceMethod = clazz.getMethod("setInputSource", InputSource.class);
            this.resetMethod = clazz.getMethod("reset", (Class[])new Class[0]);
            try {
                xif.setProperty("reuse-instance", false);
            }
            catch (final IllegalArgumentException ex) {}
            this.xif = xif;
        }
        
        @Nullable
        private XMLStreamReader fetch() {
            final XMLStreamReader sr = this.pool.get();
            if (sr == null) {
                return null;
            }
            this.pool.set(null);
            return sr;
        }
        
        @Override
        public void doRecycle(final XMLStreamReader r) {
            if (this.zephyrClass.isInstance(r)) {
                this.pool.set(r);
            }
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final InputStream in, final boolean rejectDTDs) {
            try {
                final XMLStreamReader xsr = this.fetch();
                if (xsr == null) {
                    return this.xif.createXMLStreamReader(systemId, in);
                }
                final InputSource is = new InputSource(systemId);
                is.setByteStream(in);
                this.reuse(xsr, is);
                return xsr;
            }
            catch (final IllegalAccessException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
            catch (final InvocationTargetException e2) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e2 });
            }
            catch (final XMLStreamException e3) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e3 });
            }
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final Reader in, final boolean rejectDTDs) {
            try {
                final XMLStreamReader xsr = this.fetch();
                if (xsr == null) {
                    return this.xif.createXMLStreamReader(systemId, in);
                }
                final InputSource is = new InputSource(systemId);
                is.setCharacterStream(in);
                this.reuse(xsr, is);
                return xsr;
            }
            catch (final IllegalAccessException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
            catch (final InvocationTargetException e2) {
                Throwable cause = e2.getCause();
                if (cause == null) {
                    cause = e2;
                }
                throw new XMLReaderException("stax.cantCreate", new Object[] { cause });
            }
            catch (final XMLStreamException e3) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e3 });
            }
        }
        
        private void reuse(final XMLStreamReader xsr, final InputSource in) throws IllegalAccessException, InvocationTargetException {
            this.resetMethod.invoke(xsr, new Object[0]);
            this.setInputSourceMethod.invoke(xsr, in);
        }
    }
    
    public static final class Default extends XMLStreamReaderFactory
    {
        private final ThreadLocal<XMLInputFactory> xif;
        
        public Default() {
            this.xif = new ThreadLocal<XMLInputFactory>() {
                public XMLInputFactory initialValue() {
                    return getXMLInputFactory();
                }
            };
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final InputStream in, final boolean rejectDTDs) {
            try {
                return this.xif.get().createXMLStreamReader(systemId, in);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final Reader in, final boolean rejectDTDs) {
            try {
                return this.xif.get().createXMLStreamReader(systemId, in);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public void doRecycle(final XMLStreamReader r) {
        }
    }
    
    public static class NoLock extends XMLStreamReaderFactory
    {
        private final XMLInputFactory xif;
        
        public NoLock(final XMLInputFactory xif) {
            this.xif = xif;
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final InputStream in, final boolean rejectDTDs) {
            try {
                return this.xif.createXMLStreamReader(systemId, in);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final Reader in, final boolean rejectDTDs) {
            try {
                return this.xif.createXMLStreamReader(systemId, in);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public void doRecycle(final XMLStreamReader r) {
        }
    }
    
    public static final class Woodstox extends NoLock
    {
        public static final String PROPERTY_MAX_ATTRIBUTES_PER_ELEMENT = "xml.ws.maximum.AttributesPerElement";
        public static final String PROPERTY_MAX_ATTRIBUTE_SIZE = "xml.ws.maximum.AttributeSize";
        public static final String PROPERTY_MAX_CHILDREN_PER_ELEMENT = "xml.ws.maximum.ChildrenPerElement";
        public static final String PROPERTY_MAX_ELEMENT_COUNT = "xml.ws.maximum.ElementCount";
        public static final String PROPERTY_MAX_ELEMENT_DEPTH = "xml.ws.maximum.ElementDepth";
        public static final String PROPERTY_MAX_CHARACTERS = "xml.ws.maximum.Characters";
        private static final int DEFAULT_MAX_ATTRIBUTES_PER_ELEMENT = 500;
        private static final int DEFAULT_MAX_ATTRIBUTE_SIZE = 524288;
        private static final int DEFAULT_MAX_CHILDREN_PER_ELEMENT = Integer.MAX_VALUE;
        private static final int DEFAULT_MAX_ELEMENT_DEPTH = 500;
        private static final long DEFAULT_MAX_ELEMENT_COUNT = 2147483647L;
        private static final long DEFAULT_MAX_CHARACTERS = Long.MAX_VALUE;
        private int maxAttributesPerElement;
        private int maxAttributeSize;
        private int maxChildrenPerElement;
        private int maxElementDepth;
        private long maxElementCount;
        private long maxCharacters;
        private static final String P_MAX_ATTRIBUTES_PER_ELEMENT = "com.ctc.wstx.maxAttributesPerElement";
        private static final String P_MAX_ATTRIBUTE_SIZE = "com.ctc.wstx.maxAttributeSize";
        private static final String P_MAX_CHILDREN_PER_ELEMENT = "com.ctc.wstx.maxChildrenPerElement";
        private static final String P_MAX_ELEMENT_COUNT = "com.ctc.wstx.maxElementCount";
        private static final String P_MAX_ELEMENT_DEPTH = "com.ctc.wstx.maxElementDepth";
        private static final String P_MAX_CHARACTERS = "com.ctc.wstx.maxCharacters";
        private static final String P_INTERN_NSURIS = "org.codehaus.stax2.internNsUris";
        
        public Woodstox(final XMLInputFactory xif) {
            super(xif);
            this.maxAttributesPerElement = 500;
            this.maxAttributeSize = 524288;
            this.maxChildrenPerElement = Integer.MAX_VALUE;
            this.maxElementDepth = 500;
            this.maxElementCount = 2147483647L;
            this.maxCharacters = Long.MAX_VALUE;
            if (xif.isPropertySupported("org.codehaus.stax2.internNsUris")) {
                xif.setProperty("org.codehaus.stax2.internNsUris", true);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "org.codehaus.stax2.internNsUris is {0}", true);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxAttributesPerElement")) {
                this.maxAttributesPerElement = buildIntegerValue("xml.ws.maximum.AttributesPerElement", 500);
                xif.setProperty("com.ctc.wstx.maxAttributesPerElement", this.maxAttributesPerElement);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributesPerElement is {0}", this.maxAttributesPerElement);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxAttributeSize")) {
                this.maxAttributeSize = buildIntegerValue("xml.ws.maximum.AttributeSize", 524288);
                xif.setProperty("com.ctc.wstx.maxAttributeSize", this.maxAttributeSize);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxAttributeSize is {0}", this.maxAttributeSize);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxChildrenPerElement")) {
                this.maxChildrenPerElement = buildIntegerValue("xml.ws.maximum.ChildrenPerElement", Integer.MAX_VALUE);
                xif.setProperty("com.ctc.wstx.maxChildrenPerElement", this.maxChildrenPerElement);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxChildrenPerElement is {0}", this.maxChildrenPerElement);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxElementDepth")) {
                this.maxElementDepth = buildIntegerValue("xml.ws.maximum.ElementDepth", 500);
                xif.setProperty("com.ctc.wstx.maxElementDepth", this.maxElementDepth);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementDepth is {0}", this.maxElementDepth);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxElementCount")) {
                this.maxElementCount = buildLongValue("xml.ws.maximum.ElementCount", 2147483647L);
                xif.setProperty("com.ctc.wstx.maxElementCount", this.maxElementCount);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxElementCount is {0}", this.maxElementCount);
                }
            }
            if (xif.isPropertySupported("com.ctc.wstx.maxCharacters")) {
                this.maxCharacters = buildLongValue("xml.ws.maximum.Characters", Long.MAX_VALUE);
                xif.setProperty("com.ctc.wstx.maxCharacters", this.maxCharacters);
                if (XMLStreamReaderFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamReaderFactory.LOGGER.log(Level.FINE, "com.ctc.wstx.maxCharacters is {0}", this.maxCharacters);
                }
            }
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final InputStream in, final boolean rejectDTDs) {
            return super.doCreate(systemId, in, rejectDTDs);
        }
        
        @Override
        public XMLStreamReader doCreate(final String systemId, final Reader in, final boolean rejectDTDs) {
            return super.doCreate(systemId, in, rejectDTDs);
        }
    }
    
    public interface RecycleAware
    {
        void onRecycled();
    }
}
