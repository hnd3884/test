package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

@Produces({ "text/plain", "*/*" })
@Consumes({ "text/plain", "*/*" })
@Singleton
final class StringMessageProvider extends AbstractMessageReaderWriterProvider<String>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == String.class;
    }
    
    public String readFrom(final Class<String> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == String.class;
    }
    
    @Override
    public long getSize(final String s, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return s.length();
    }
    
    public void writeTo(final String t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        AbstractMessageReaderWriterProvider.writeToAsString(t, entityStream, mediaType);
    }
}
