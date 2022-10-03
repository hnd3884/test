package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.ProcessingException;
import javax.xml.bind.UnmarshalException;
import org.glassfish.jersey.internal.inject.ExtractorException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.ws.rs.ext.ParamConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;
import javax.ws.rs.ext.ParamConverterProvider;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBException;
import org.glassfish.jersey.internal.util.collection.Values;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.ext.ContextResolver;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.xml.bind.JAXBContext;
import java.util.Map;

public class JaxbStringReaderProvider
{
    private static final Map<Class, JAXBContext> jaxbContexts;
    private final Value<ContextResolver<JAXBContext>> mtContext;
    private final Value<ContextResolver<Unmarshaller>> mtUnmarshaller;
    
    public JaxbStringReaderProvider(final Providers ps) {
        this.mtContext = (Value<ContextResolver<JAXBContext>>)Values.lazy((Value)new Value<ContextResolver<JAXBContext>>() {
            public ContextResolver<JAXBContext> get() {
                return (ContextResolver<JAXBContext>)ps.getContextResolver((Class)JAXBContext.class, (MediaType)null);
            }
        });
        this.mtUnmarshaller = (Value<ContextResolver<Unmarshaller>>)Values.lazy((Value)new Value<ContextResolver<Unmarshaller>>() {
            public ContextResolver<Unmarshaller> get() {
                return (ContextResolver<Unmarshaller>)ps.getContextResolver((Class)Unmarshaller.class, (MediaType)null);
            }
        });
    }
    
    protected final Unmarshaller getUnmarshaller(final Class type) throws JAXBException {
        final ContextResolver<Unmarshaller> unmarshallerContextResolver = (ContextResolver<Unmarshaller>)this.mtUnmarshaller.get();
        if (unmarshallerContextResolver != null) {
            final Unmarshaller u = (Unmarshaller)unmarshallerContextResolver.getContext(type);
            if (u != null) {
                return u;
            }
        }
        return this.getJAXBContext(type).createUnmarshaller();
    }
    
    private JAXBContext getJAXBContext(final Class type) throws JAXBException {
        final ContextResolver<JAXBContext> jaxbContextContextResolver = (ContextResolver<JAXBContext>)this.mtContext.get();
        if (jaxbContextContextResolver != null) {
            final JAXBContext c = (JAXBContext)jaxbContextContextResolver.getContext(type);
            if (c != null) {
                return c;
            }
        }
        return this.getStoredJAXBContext(type);
    }
    
    protected JAXBContext getStoredJAXBContext(final Class type) throws JAXBException {
        synchronized (JaxbStringReaderProvider.jaxbContexts) {
            JAXBContext c = JaxbStringReaderProvider.jaxbContexts.get(type);
            if (c == null) {
                c = JAXBContext.newInstance(type);
                JaxbStringReaderProvider.jaxbContexts.put(type, c);
            }
            return c;
        }
    }
    
    static {
        jaxbContexts = new WeakHashMap<Class, JAXBContext>();
    }
    
    public static class RootElementProvider extends JaxbStringReaderProvider implements ParamConverterProvider
    {
        private final Provider<SAXParserFactory> spfProvider;
        
        public RootElementProvider(@Context final Provider<SAXParserFactory> spfProvider, @Context final Providers ps) {
            super(ps);
            this.spfProvider = spfProvider;
        }
        
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            final boolean supported = rawType.getAnnotation(XmlRootElement.class) != null || rawType.getAnnotation(XmlType.class) != null;
            if (!supported) {
                return null;
            }
            return (ParamConverter<T>)new ParamConverter<T>() {
                public T fromString(final String value) {
                    try {
                        final SAXSource source = new SAXSource(((SAXParserFactory)RootElementProvider.this.spfProvider.get()).newSAXParser().getXMLReader(), new InputSource(new StringReader(value)));
                        final Unmarshaller u = RootElementProvider.this.getUnmarshaller(rawType);
                        if (rawType.isAnnotationPresent(XmlRootElement.class)) {
                            return rawType.cast(u.unmarshal(source));
                        }
                        return u.unmarshal(source, rawType).getValue();
                    }
                    catch (final UnmarshalException ex) {
                        throw new ExtractorException(LocalizationMessages.ERROR_UNMARSHALLING_JAXB(rawType), (Throwable)ex);
                    }
                    catch (final JAXBException ex2) {
                        throw new ProcessingException(LocalizationMessages.ERROR_UNMARSHALLING_JAXB(rawType), (Throwable)ex2);
                    }
                    catch (final Exception ex3) {
                        throw new ProcessingException(LocalizationMessages.ERROR_UNMARSHALLING_JAXB(rawType), (Throwable)ex3);
                    }
                }
                
                public String toString(final T value) throws IllegalArgumentException {
                    return "test";
                }
            };
        }
    }
}
