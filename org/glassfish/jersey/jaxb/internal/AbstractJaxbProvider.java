package org.glassfish.jersey.jaxb.internal;

import java.util.WeakHashMap;
import javax.xml.bind.PropertyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.message.XmlHeader;
import java.lang.annotation.Annotation;
import org.xml.sax.InputSource;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.bind.JAXBException;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.util.collection.Values;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;

public abstract class AbstractJaxbProvider<T> extends AbstractMessageReaderWriterProvider<T>
{
    private static final Map<Class<?>, WeakReference<JAXBContext>> jaxbContexts;
    private final Providers jaxrsProviders;
    private final boolean fixedResolverMediaType;
    private final Value<ContextResolver<JAXBContext>> mtContext;
    private final Value<ContextResolver<Unmarshaller>> mtUnmarshaller;
    private final Value<ContextResolver<Marshaller>> mtMarshaller;
    private Value<Boolean> formattedOutput;
    private Value<Boolean> xmlRootElementProcessing;
    
    public AbstractJaxbProvider(final Providers providers) {
        this(providers, null);
    }
    
    public AbstractJaxbProvider(final Providers providers, final MediaType resolverMediaType) {
        this.formattedOutput = (Value<Boolean>)Values.of((Object)Boolean.FALSE);
        this.xmlRootElementProcessing = (Value<Boolean>)Values.of((Object)Boolean.FALSE);
        this.jaxrsProviders = providers;
        this.fixedResolverMediaType = (resolverMediaType != null);
        if (this.fixedResolverMediaType) {
            this.mtContext = (Value<ContextResolver<JAXBContext>>)Values.lazy((Value)new Value<ContextResolver<JAXBContext>>() {
                public ContextResolver<JAXBContext> get() {
                    return (ContextResolver<JAXBContext>)providers.getContextResolver((Class)JAXBContext.class, resolverMediaType);
                }
            });
            this.mtUnmarshaller = (Value<ContextResolver<Unmarshaller>>)Values.lazy((Value)new Value<ContextResolver<Unmarshaller>>() {
                public ContextResolver<Unmarshaller> get() {
                    return (ContextResolver<Unmarshaller>)providers.getContextResolver((Class)Unmarshaller.class, resolverMediaType);
                }
            });
            this.mtMarshaller = (Value<ContextResolver<Marshaller>>)Values.lazy((Value)new Value<ContextResolver<Marshaller>>() {
                public ContextResolver<Marshaller> get() {
                    return (ContextResolver<Marshaller>)providers.getContextResolver((Class)Marshaller.class, resolverMediaType);
                }
            });
        }
        else {
            this.mtContext = null;
            this.mtUnmarshaller = null;
            this.mtMarshaller = null;
        }
    }
    
    @Context
    public void setConfiguration(final Configuration config) {
        this.formattedOutput = (Value<Boolean>)Values.lazy((Value)new Value<Boolean>() {
            public Boolean get() {
                return PropertiesHelper.isProperty(config.getProperty("jersey.config.xml.formatOutput"));
            }
        });
        this.xmlRootElementProcessing = (Value<Boolean>)Values.lazy((Value)new Value<Boolean>() {
            public Boolean get() {
                return PropertiesHelper.isProperty(config.getProperty("jersey.config.jaxb.collections.processXmlRootElement"));
            }
        });
    }
    
    protected boolean isSupported(final MediaType mediaType) {
        return true;
    }
    
    protected final Unmarshaller getUnmarshaller(final Class type, final MediaType mediaType) throws JAXBException {
        if (this.fixedResolverMediaType) {
            return this.getUnmarshaller(type);
        }
        final ContextResolver<Unmarshaller> unmarshallerResolver = (ContextResolver<Unmarshaller>)this.jaxrsProviders.getContextResolver((Class)Unmarshaller.class, mediaType);
        if (unmarshallerResolver != null) {
            final Unmarshaller u = (Unmarshaller)unmarshallerResolver.getContext(type);
            if (u != null) {
                return u;
            }
        }
        final JAXBContext ctx = this.getJAXBContext(type, mediaType);
        return (ctx == null) ? null : ctx.createUnmarshaller();
    }
    
    private Unmarshaller getUnmarshaller(final Class type) throws JAXBException {
        final ContextResolver<Unmarshaller> resolver = (ContextResolver<Unmarshaller>)this.mtUnmarshaller.get();
        if (resolver != null) {
            final Unmarshaller u = (Unmarshaller)resolver.getContext(type);
            if (u != null) {
                return u;
            }
        }
        final JAXBContext ctx = this.getJAXBContext(type);
        return (ctx == null) ? null : ctx.createUnmarshaller();
    }
    
    protected final Marshaller getMarshaller(final Class type, final MediaType mediaType) throws JAXBException {
        if (this.fixedResolverMediaType) {
            return this.getMarshaller(type);
        }
        final ContextResolver<Marshaller> mcr = (ContextResolver<Marshaller>)this.jaxrsProviders.getContextResolver((Class)Marshaller.class, mediaType);
        if (mcr != null) {
            final Marshaller m = (Marshaller)mcr.getContext(type);
            if (m != null) {
                return m;
            }
        }
        final JAXBContext ctx = this.getJAXBContext(type, mediaType);
        if (ctx == null) {
            return null;
        }
        final Marshaller i = ctx.createMarshaller();
        if (this.formattedOutput.get()) {
            i.setProperty("jaxb.formatted.output", this.formattedOutput.get());
        }
        return i;
    }
    
    private Marshaller getMarshaller(final Class type) throws JAXBException {
        final ContextResolver<Marshaller> resolver = (ContextResolver<Marshaller>)this.mtMarshaller.get();
        if (resolver != null) {
            final Marshaller u = (Marshaller)resolver.getContext(type);
            if (u != null) {
                return u;
            }
        }
        final JAXBContext ctx = this.getJAXBContext(type);
        if (ctx == null) {
            return null;
        }
        final Marshaller m = ctx.createMarshaller();
        if (this.formattedOutput.get()) {
            m.setProperty("jaxb.formatted.output", this.formattedOutput.get());
        }
        return m;
    }
    
    private JAXBContext getJAXBContext(final Class type, final MediaType mt) throws JAXBException {
        final ContextResolver<JAXBContext> cr = (ContextResolver<JAXBContext>)this.jaxrsProviders.getContextResolver((Class)JAXBContext.class, mt);
        if (cr != null) {
            final JAXBContext c = (JAXBContext)cr.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJaxbContext(type);
    }
    
    private JAXBContext getJAXBContext(final Class type) throws JAXBException {
        final ContextResolver<JAXBContext> resolver = (ContextResolver<JAXBContext>)this.mtContext.get();
        if (resolver != null) {
            final JAXBContext c = (JAXBContext)resolver.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJaxbContext(type);
    }
    
    protected JAXBContext getStoredJaxbContext(final Class type) throws JAXBException {
        synchronized (AbstractJaxbProvider.jaxbContexts) {
            final WeakReference<JAXBContext> ref = AbstractJaxbProvider.jaxbContexts.get(type);
            JAXBContext c = (ref != null) ? ref.get() : null;
            if (c == null) {
                c = JAXBContext.newInstance(type);
                AbstractJaxbProvider.jaxbContexts.put(type, new WeakReference<JAXBContext>(c));
            }
            return c;
        }
    }
    
    protected static SAXSource getSAXSource(final SAXParserFactory spf, final InputStream entityStream) throws JAXBException {
        try {
            return new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(entityStream));
        }
        catch (final Exception ex) {
            throw new JAXBException("Error creating SAXSource", ex);
        }
    }
    
    protected boolean isFormattedOutput() {
        return (boolean)this.formattedOutput.get();
    }
    
    protected boolean isXmlRootElementProcessing() {
        return (boolean)this.xmlRootElementProcessing.get();
    }
    
    protected void setHeader(final Marshaller marshaller, final Annotation[] annotations) {
        for (final Annotation a : annotations) {
            if (a instanceof XmlHeader) {
                try {
                    marshaller.setProperty("com.sun.xml.bind.xmlHeaders", ((XmlHeader)a).value());
                }
                catch (final PropertyException e) {
                    try {
                        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", ((XmlHeader)a).value());
                    }
                    catch (final PropertyException ex) {
                        Logger.getLogger(AbstractJaxbProvider.class.getName()).log(Level.WARNING, "@XmlHeader annotation is not supported with this JAXB implementation. Please use JAXB RI if you need this feature.");
                    }
                }
                break;
            }
        }
    }
    
    static {
        jaxbContexts = new WeakHashMap<Class<?>, WeakReference<JAXBContext>>();
    }
}
