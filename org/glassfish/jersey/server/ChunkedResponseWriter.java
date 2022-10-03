package org.glassfish.jersey.server;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.MessageBodyWriter;

public final class ChunkedResponseWriter implements MessageBodyWriter<ChunkedOutput<?>>
{
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return ChunkedOutput.class.isAssignableFrom(type);
    }
    
    public long getSize(final ChunkedOutput<?> chunkedOutput, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    public void writeTo(final ChunkedOutput<?> chunkedOutput, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
    }
}
