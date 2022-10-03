package com.sun.xml.internal.ws.api.streaming;

import com.sun.xml.internal.ws.encoding.HasEncoding;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.ws.WebServiceException;
import java.lang.reflect.InvocationTargetException;
import com.sun.istack.internal.Nullable;
import javax.xml.transform.stream.StreamResult;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.streaming.XMLReaderException;
import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import java.io.Writer;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import com.sun.istack.internal.NotNull;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.logging.Logger;

public abstract class XMLStreamWriterFactory
{
    private static final Logger LOGGER;
    private static volatile ContextClassloaderLocal<XMLStreamWriterFactory> writerFactory;
    
    public abstract XMLStreamWriter doCreate(final OutputStream p0);
    
    public abstract XMLStreamWriter doCreate(final OutputStream p0, final String p1);
    
    public abstract void doRecycle(final XMLStreamWriter p0);
    
    public static void recycle(final XMLStreamWriter r) {
        get().doRecycle(r);
    }
    
    @NotNull
    public static XMLStreamWriterFactory get() {
        return XMLStreamWriterFactory.writerFactory.get();
    }
    
    public static void set(@NotNull final XMLStreamWriterFactory f) {
        if (f == null) {
            throw new IllegalArgumentException();
        }
        XMLStreamWriterFactory.writerFactory.set(f);
    }
    
    public static XMLStreamWriter create(final OutputStream out) {
        return get().doCreate(out);
    }
    
    public static XMLStreamWriter create(final OutputStream out, final String encoding) {
        return get().doCreate(out, encoding);
    }
    
    @Deprecated
    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out) {
        return create(out);
    }
    
    @Deprecated
    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out, final String encoding) {
        return create(out, encoding);
    }
    
    @Deprecated
    public static XMLStreamWriter createXMLStreamWriter(final OutputStream out, final String encoding, final boolean declare) {
        return create(out, encoding);
    }
    
    static {
        LOGGER = Logger.getLogger(XMLStreamWriterFactory.class.getName());
        XMLStreamWriterFactory.writerFactory = new ContextClassloaderLocal<XMLStreamWriterFactory>() {
            @Override
            protected XMLStreamWriterFactory initialValue() {
                XMLOutputFactory xof = null;
                if (Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".woodstox")) {
                    try {
                        xof = (XMLOutputFactory)Class.forName("com.ctc.wstx.stax.WstxOutputFactory").newInstance();
                    }
                    catch (final Exception ex3) {}
                }
                if (xof == null) {
                    xof = XMLOutputFactory.newInstance();
                }
                XMLStreamWriterFactory f = null;
                if (!Boolean.getBoolean(XMLStreamWriterFactory.class.getName() + ".noPool")) {
                    try {
                        final Class<?> clazz = xof.createXMLStreamWriter(new StringWriter()).getClass();
                        if (clazz.getName().startsWith("com.sun.xml.internal.stream.")) {
                            f = new Zephyr(xof, (Class)clazz);
                        }
                    }
                    catch (final XMLStreamException ex) {
                        Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, null, ex);
                    }
                    catch (final NoSuchMethodException ex2) {
                        Logger.getLogger(XMLStreamWriterFactory.class.getName()).log(Level.INFO, null, ex2);
                    }
                }
                if (f == null && xof.getClass().getName().equals("com.ctc.wstx.stax.WstxOutputFactory")) {
                    f = new NoLock(xof);
                }
                if (f == null) {
                    f = new Default(xof);
                }
                if (XMLStreamWriterFactory.LOGGER.isLoggable(Level.FINE)) {
                    XMLStreamWriterFactory.LOGGER.log(Level.FINE, "XMLStreamWriterFactory instance is = {0}", f);
                }
                return f;
            }
        };
    }
    
    public static final class Default extends XMLStreamWriterFactory
    {
        private final XMLOutputFactory xof;
        
        public Default(final XMLOutputFactory xof) {
            this.xof = xof;
        }
        
        @Override
        public XMLStreamWriter doCreate(final OutputStream out) {
            return this.doCreate(out, "UTF-8");
        }
        
        @Override
        public synchronized XMLStreamWriter doCreate(final OutputStream out, final String encoding) {
            try {
                final XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
                return new HasEncodingWriter(writer, encoding);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public void doRecycle(final XMLStreamWriter r) {
        }
    }
    
    public static final class Zephyr extends XMLStreamWriterFactory
    {
        private final XMLOutputFactory xof;
        private final ThreadLocal<XMLStreamWriter> pool;
        private final Method resetMethod;
        private final Method setOutputMethod;
        private final Class zephyrClass;
        
        public static XMLStreamWriterFactory newInstance(final XMLOutputFactory xof) {
            try {
                final Class<?> clazz = xof.createXMLStreamWriter(new StringWriter()).getClass();
                if (!clazz.getName().startsWith("com.sun.xml.internal.stream.")) {
                    return null;
                }
                return new Zephyr(xof, clazz);
            }
            catch (final XMLStreamException e) {
                return null;
            }
            catch (final NoSuchMethodException e2) {
                return null;
            }
        }
        
        private Zephyr(final XMLOutputFactory xof, final Class clazz) throws NoSuchMethodException {
            this.pool = new ThreadLocal<XMLStreamWriter>();
            this.xof = xof;
            this.zephyrClass = clazz;
            this.setOutputMethod = clazz.getMethod("setOutput", StreamResult.class, String.class);
            this.resetMethod = clazz.getMethod("reset", (Class[])new Class[0]);
        }
        
        @Nullable
        private XMLStreamWriter fetch() {
            final XMLStreamWriter sr = this.pool.get();
            if (sr == null) {
                return null;
            }
            this.pool.set(null);
            return sr;
        }
        
        @Override
        public XMLStreamWriter doCreate(final OutputStream out) {
            return this.doCreate(out, "UTF-8");
        }
        
        @Override
        public XMLStreamWriter doCreate(final OutputStream out, final String encoding) {
            XMLStreamWriter xsw = this.fetch();
            if (xsw != null) {
                try {
                    this.resetMethod.invoke(xsw, new Object[0]);
                    this.setOutputMethod.invoke(xsw, new StreamResult(out), encoding);
                    return new HasEncodingWriter(xsw, encoding);
                }
                catch (final IllegalAccessException e) {
                    throw new XMLReaderException("stax.cantCreate", new Object[] { e });
                }
                catch (final InvocationTargetException e2) {
                    throw new XMLReaderException("stax.cantCreate", new Object[] { e2 });
                }
            }
            try {
                xsw = this.xof.createXMLStreamWriter(out, encoding);
            }
            catch (final XMLStreamException e3) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e3 });
            }
            return new HasEncodingWriter(xsw, encoding);
        }
        
        @Override
        public void doRecycle(XMLStreamWriter r) {
            if (r instanceof HasEncodingWriter) {
                r = ((HasEncodingWriter)r).getWriter();
            }
            if (this.zephyrClass.isInstance(r)) {
                try {
                    r.close();
                }
                catch (final XMLStreamException e) {
                    throw new WebServiceException(e);
                }
                this.pool.set(r);
            }
            if (r instanceof RecycleAware) {
                ((RecycleAware)r).onRecycled();
            }
        }
    }
    
    public static final class NoLock extends XMLStreamWriterFactory
    {
        private final XMLOutputFactory xof;
        
        public NoLock(final XMLOutputFactory xof) {
            this.xof = xof;
        }
        
        @Override
        public XMLStreamWriter doCreate(final OutputStream out) {
            return this.doCreate(out, "utf-8");
        }
        
        @Override
        public XMLStreamWriter doCreate(final OutputStream out, final String encoding) {
            try {
                final XMLStreamWriter writer = this.xof.createXMLStreamWriter(out, encoding);
                return new HasEncodingWriter(writer, encoding);
            }
            catch (final XMLStreamException e) {
                throw new XMLReaderException("stax.cantCreate", new Object[] { e });
            }
        }
        
        @Override
        public void doRecycle(final XMLStreamWriter r) {
        }
    }
    
    public static class HasEncodingWriter extends XMLStreamWriterFilter implements HasEncoding
    {
        private final String encoding;
        
        HasEncodingWriter(final XMLStreamWriter writer, final String encoding) {
            super(writer);
            this.encoding = encoding;
        }
        
        @Override
        public String getEncoding() {
            return this.encoding;
        }
        
        public XMLStreamWriter getWriter() {
            return this.writer;
        }
    }
    
    public interface RecycleAware
    {
        void onRecycled();
    }
}
