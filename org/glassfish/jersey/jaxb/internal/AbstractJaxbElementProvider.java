package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.WebApplicationException;
import java.nio.charset.Charset;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
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
import java.lang.reflect.ParameterizedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBElement;

public abstract class AbstractJaxbElementProvider extends AbstractJaxbProvider<JAXBElement<?>>
{
    public AbstractJaxbElementProvider(final Providers providers) {
        super(providers);
    }
    
    public AbstractJaxbElementProvider(final Providers providers, final MediaType resolverMediaType) {
        super(providers, resolverMediaType);
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == JAXBElement.class && genericType instanceof ParameterizedType && this.isSupported(mediaType);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return JAXBElement.class.isAssignableFrom(type) && this.isSupported(mediaType);
    }
    
    public final JAXBElement<?> readFrom(final Class<JAXBElement<?>> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream) throws IOException {
        final EntityInputStream entityStream = EntityInputStream.create(inputStream);
        if (entityStream.isEmpty()) {
            throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
        }
        final ParameterizedType pt = (ParameterizedType)genericType;
        final Class ta = (Class)pt.getActualTypeArguments()[0];
        try {
            return this.readFrom(ta, mediaType, this.getUnmarshaller(ta, mediaType), (InputStream)entityStream);
        }
        catch (final UnmarshalException ex) {
            throw new BadRequestException((Throwable)ex);
        }
        catch (final JAXBException ex2) {
            throw new InternalServerErrorException((Throwable)ex2);
        }
    }
    
    protected abstract JAXBElement<?> readFrom(final Class<?> p0, final MediaType p1, final Unmarshaller p2, final InputStream p3) throws JAXBException;
    
    public final void writeTo(final JAXBElement<?> t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final Marshaller m = this.getMarshaller(t.getDeclaredType(), mediaType);
            final Charset c = getCharset(mediaType);
            if (c != AbstractJaxbElementProvider.UTF8) {
                m.setProperty("jaxb.encoding", c.name());
            }
            this.setHeader(m, annotations);
            this.writeTo(t, mediaType, c, m, entityStream);
        }
        catch (final JAXBException ex) {
            throw new InternalServerErrorException((Throwable)ex);
        }
    }
    
    protected abstract void writeTo(final JAXBElement<?> p0, final MediaType p1, final Charset p2, final Marshaller p3, final OutputStream p4) throws JAXBException;
}
