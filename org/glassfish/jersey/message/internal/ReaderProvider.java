package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.glassfish.jersey.message.MessageUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.Reader;

@Produces({ "text/plain", "*/*" })
@Consumes({ "text/plain", "*/*" })
@Singleton
public final class ReaderProvider extends AbstractMessageReaderWriterProvider<Reader>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Reader.class == type;
    }
    
    public Reader readFrom(final Class<Reader> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream) throws IOException {
        final EntityInputStream entityStream = EntityInputStream.create(inputStream);
        if (entityStream.isEmpty()) {
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[0]), MessageUtils.getCharset(mediaType)));
        }
        return new BufferedReader(new InputStreamReader(entityStream, AbstractMessageReaderWriterProvider.getCharset(mediaType)));
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Reader.class.isAssignableFrom(type);
    }
    
    public void writeTo(final Reader t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final OutputStreamWriter out = new OutputStreamWriter(entityStream, AbstractMessageReaderWriterProvider.getCharset(mediaType));
            AbstractMessageReaderWriterProvider.writeTo(t, out);
            out.flush();
        }
        finally {
            t.close();
        }
    }
}
