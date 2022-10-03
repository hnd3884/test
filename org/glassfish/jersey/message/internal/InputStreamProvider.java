package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.InputStream;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
@Singleton
public final class InputStreamProvider extends AbstractMessageReaderWriterProvider<InputStream>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return InputStream.class == type;
    }
    
    public InputStream readFrom(final Class<InputStream> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return ReaderInterceptorExecutor.closeableInputStream(entityStream);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return InputStream.class.isAssignableFrom(type);
    }
    
    @Override
    public long getSize(final InputStream t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        if (t instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream)t).available();
        }
        return -1L;
    }
    
    public void writeTo(final InputStream t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            AbstractMessageReaderWriterProvider.writeTo(t, entityStream);
        }
        finally {
            t.close();
        }
    }
}
