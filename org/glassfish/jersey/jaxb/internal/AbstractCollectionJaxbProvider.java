package org.glassfish.jersey.jaxb.internal;

import java.util.Stack;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.lang.reflect.GenericArrayType;
import java.util.Iterator;
import java.lang.reflect.Array;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.BadRequestException;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import javax.ws.rs.core.NoContentException;
import org.glassfish.jersey.message.internal.EntityInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBException;
import javax.ws.rs.InternalServerErrorException;
import java.util.Arrays;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.JAXBElement;
import java.util.logging.Level;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import java.util.logging.Logger;

public abstract class AbstractCollectionJaxbProvider extends AbstractJaxbProvider<Object>
{
    private static final Logger LOGGER;
    private static final Class<?>[] DEFAULT_IMPLS;
    private static final JaxbTypeChecker DefaultJaxbTypeCHECKER;
    private final NounInflector inflector;
    
    public AbstractCollectionJaxbProvider(final Providers ps) {
        super(ps);
        this.inflector = NounInflector.getInstance();
    }
    
    public AbstractCollectionJaxbProvider(final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.inflector = NounInflector.getInstance();
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (verifyCollectionSubclass(type)) {
            return verifyGenericType(genericType) && this.isSupported(mediaType);
        }
        return type.isArray() && verifyArrayType(type) && this.isSupported(mediaType);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (Collection.class.isAssignableFrom(type)) {
            return verifyGenericType(genericType) && this.isSupported(mediaType);
        }
        return type.isArray() && verifyArrayType(type) && this.isSupported(mediaType);
    }
    
    public static boolean verifyCollectionSubclass(final Class<?> type) {
        try {
            if (Collection.class.isAssignableFrom(type)) {
                for (final Class c : AbstractCollectionJaxbProvider.DEFAULT_IMPLS) {
                    if (type.isAssignableFrom(c)) {
                        return true;
                    }
                }
                return !Modifier.isAbstract(type.getModifiers()) && Modifier.isPublic(type.getConstructor((Class<?>[])new Class[0]).getModifiers());
            }
        }
        catch (final NoSuchMethodException ex) {
            AbstractCollectionJaxbProvider.LOGGER.log(Level.WARNING, LocalizationMessages.NO_PARAM_CONSTRUCTOR_MISSING(type.getName()), ex);
        }
        catch (final SecurityException ex2) {
            AbstractCollectionJaxbProvider.LOGGER.log(Level.WARNING, LocalizationMessages.UNABLE_TO_ACCESS_METHODS_OF_CLASS(type.getName()), ex2);
        }
        return false;
    }
    
    private static boolean verifyArrayType(final Class type) {
        return verifyArrayType(type, AbstractCollectionJaxbProvider.DefaultJaxbTypeCHECKER);
    }
    
    public static boolean verifyArrayType(Class type, final JaxbTypeChecker checker) {
        type = type.getComponentType();
        return checker.isJaxbType(type) || JAXBElement.class.isAssignableFrom(type);
    }
    
    private static boolean verifyGenericType(final Type genericType) {
        return verifyGenericType(genericType, AbstractCollectionJaxbProvider.DefaultJaxbTypeCHECKER);
    }
    
    public static boolean verifyGenericType(final Type genericType, final JaxbTypeChecker checker) {
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType pt = (ParameterizedType)genericType;
        if (pt.getActualTypeArguments().length > 1) {
            return false;
        }
        final Type ta = pt.getActualTypeArguments()[0];
        if (ta instanceof ParameterizedType) {
            final ParameterizedType lpt = (ParameterizedType)ta;
            return lpt.getRawType() instanceof Class && JAXBElement.class.isAssignableFrom((Class<?>)lpt.getRawType());
        }
        if (!(pt.getActualTypeArguments()[0] instanceof Class)) {
            return false;
        }
        final Class listClass = (Class)pt.getActualTypeArguments()[0];
        return checker.isJaxbType(listClass);
    }
    
    public final void writeTo(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final Collection c = type.isArray() ? Arrays.asList((Object[])t) : ((Collection)t);
            final Class elementType = getElementClass(type, genericType);
            final Charset charset = getCharset(mediaType);
            final String charsetName = charset.name();
            final Marshaller m = this.getMarshaller(elementType, mediaType);
            m.setProperty("jaxb.fragment", true);
            if (charset != AbstractCollectionJaxbProvider.UTF8) {
                m.setProperty("jaxb.encoding", charsetName);
            }
            this.setHeader(m, annotations);
            this.writeCollection(elementType, c, mediaType, charset, m, entityStream);
        }
        catch (final JAXBException ex) {
            throw new InternalServerErrorException((Throwable)ex);
        }
    }
    
    public abstract void writeCollection(final Class<?> p0, final Collection<?> p1, final MediaType p2, final Charset p3, final Marshaller p4, final OutputStream p5) throws JAXBException, IOException;
    
    public final Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream) throws IOException {
        final EntityInputStream entityStream = EntityInputStream.create(inputStream);
        if (entityStream.isEmpty()) {
            throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
        }
        try {
            final Class<?> elementType = getElementClass(type, genericType);
            final Unmarshaller u = this.getUnmarshaller(elementType, mediaType);
            final XMLStreamReader r = this.getXMLStreamReader(elementType, mediaType, u, (InputStream)entityStream);
            boolean jaxbElement = false;
            Collection<Object> l = null;
            if (type.isArray()) {
                l = new ArrayList<Object>();
            }
            else {
                try {
                    l = type.newInstance();
                }
                catch (final Exception e) {
                    for (final Class<?> c : AbstractCollectionJaxbProvider.DEFAULT_IMPLS) {
                        if (type.isAssignableFrom(c)) {
                            try {
                                l = (Collection)c.newInstance();
                                break;
                            }
                            catch (final InstantiationException ex) {
                                AbstractCollectionJaxbProvider.LOGGER.log(Level.WARNING, LocalizationMessages.UNABLE_TO_INSTANTIATE_CLASS(c.getName()), ex);
                            }
                            catch (final IllegalAccessException ex2) {
                                AbstractCollectionJaxbProvider.LOGGER.log(Level.WARNING, LocalizationMessages.UNABLE_TO_INSTANTIATE_CLASS(c.getName()), ex2);
                            }
                            catch (final SecurityException ex3) {
                                AbstractCollectionJaxbProvider.LOGGER.log(Level.WARNING, LocalizationMessages.UNABLE_TO_INSTANTIATE_CLASS(c.getName()), ex3);
                            }
                        }
                    }
                }
            }
            if (l == null) {
                l = new ArrayList<Object>();
            }
            for (int event = r.next(); event != 1; event = r.next()) {}
            int event;
            for (event = r.next(); event != 1 && event != 8; event = r.next()) {}
            while (event != 8) {
                if (elementType.isAnnotationPresent(XmlRootElement.class)) {
                    l.add(u.unmarshal(r));
                }
                else if (elementType.isAnnotationPresent(XmlType.class)) {
                    l.add(u.unmarshal(r, elementType).getValue());
                }
                else {
                    l.add(u.unmarshal(r, elementType));
                    jaxbElement = true;
                }
                for (event = r.getEventType(); event != 1 && event != 8; event = r.next()) {}
            }
            return type.isArray() ? createArray(l, jaxbElement ? JAXBElement.class : elementType) : l;
        }
        catch (final UnmarshalException ex4) {
            throw new BadRequestException((Throwable)ex4);
        }
        catch (final XMLStreamException ex5) {
            throw new BadRequestException((Throwable)ex5);
        }
        catch (final JAXBException ex6) {
            throw new InternalServerErrorException((Throwable)ex6);
        }
    }
    
    private static Object createArray(final Collection<?> collection, final Class componentType) {
        final Object array = Array.newInstance(componentType, collection.size());
        int i = 0;
        for (final Object value : collection) {
            Array.set(array, i++, value);
        }
        return array;
    }
    
    protected abstract XMLStreamReader getXMLStreamReader(final Class<?> p0, final MediaType p1, final Unmarshaller p2, final InputStream p3) throws XMLStreamException;
    
    protected static Class getElementClass(final Class<?> type, final Type genericType) {
        Type ta;
        if (genericType instanceof ParameterizedType) {
            ta = ((ParameterizedType)genericType).getActualTypeArguments()[0];
        }
        else if (genericType instanceof GenericArrayType) {
            ta = ((GenericArrayType)genericType).getGenericComponentType();
        }
        else {
            ta = type.getComponentType();
        }
        if (ta instanceof ParameterizedType) {
            ta = ((ParameterizedType)ta).getActualTypeArguments()[0];
        }
        return (Class)ta;
    }
    
    private static String convertToXmlName(final String name) {
        return name.replace("$", "_");
    }
    
    protected final String getRootElementName(final Class<?> elementType) {
        if (this.isXmlRootElementProcessing()) {
            return convertToXmlName(this.inflector.pluralize(this.inflector.demodulize(getElementName(elementType))));
        }
        return convertToXmlName(this.inflector.decapitalize(this.inflector.pluralize(this.inflector.demodulize(elementType.getName()))));
    }
    
    protected static String getElementName(final Class<?> elementType) {
        String name = elementType.getName();
        final XmlRootElement xre = elementType.getAnnotation(XmlRootElement.class);
        if (xre != null && !"##default".equals(xre.name())) {
            name = xre.name();
        }
        return name;
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractCollectionJaxbProvider.class.getName());
        DEFAULT_IMPLS = new Class[] { ArrayList.class, LinkedList.class, HashSet.class, TreeSet.class, Stack.class };
        DefaultJaxbTypeCHECKER = new JaxbTypeChecker() {
            @Override
            public boolean isJaxbType(final Class<?> type) {
                return type.isAnnotationPresent(XmlRootElement.class) || type.isAnnotationPresent(XmlType.class);
            }
        };
    }
    
    public interface JaxbTypeChecker
    {
        boolean isJaxbType(final Class<?> p0);
    }
}
