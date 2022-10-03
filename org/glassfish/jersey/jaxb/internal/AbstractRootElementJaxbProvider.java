package org.glassfish.jersey.jaxb.internal;

import java.nio.charset.Charset;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.NoContentException;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.EntityInputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

public abstract class AbstractRootElementJaxbProvider extends AbstractJaxbProvider<Object>
{
    public AbstractRootElementJaxbProvider(final Providers providers) {
        super(providers);
    }
    
    public AbstractRootElementJaxbProvider(final Providers providers, final MediaType resolverMediaType) {
        super(providers, resolverMediaType);
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return (type.getAnnotation(XmlRootElement.class) != null || type.getAnnotation(XmlType.class) != null) && this.isSupported(mediaType);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type.getAnnotation(XmlRootElement.class) != null && this.isSupported(mediaType);
    }
    
    public final Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream) throws IOException {
        try {
            final EntityInputStream entityStream = EntityInputStream.create(inputStream);
            if (entityStream.isEmpty()) {
                throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
            }
            return this.readFrom(type, mediaType, this.getUnmarshaller(type, mediaType), (InputStream)entityStream);
        }
        catch (final UnmarshalException ex) {
            throw new BadRequestException((Throwable)ex);
        }
        catch (final JAXBException ex2) {
            throw new InternalServerErrorException((Throwable)ex2);
        }
    }
    
    protected Object readFrom(final Class<Object> type, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws JAXBException {
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal(entityStream);
        }
        return u.unmarshal(new StreamSource(entityStream), type).getValue();
    }
    
    public final void writeTo(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final Marshaller m = this.getMarshaller(type, mediaType);
            final Charset c = getCharset(mediaType);
            if (c != AbstractRootElementJaxbProvider.UTF8) {
                m.setProperty("jaxb.encoding", c.name());
            }
            this.setHeader(m, annotations);
            this.writeTo(t, mediaType, c, m, entityStream);
        }
        catch (final JAXBException ex) {
            throw new InternalServerErrorException((Throwable)ex);
        }
    }
    
    protected void writeTo(final Object t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }
}
