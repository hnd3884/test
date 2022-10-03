package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.File;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
@Singleton
public final class FileProvider extends AbstractMessageReaderWriterProvider<File>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return File.class == type;
    }
    
    public File readFrom(final Class<File> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        final File file = Utils.createTempFile();
        final OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        try {
            AbstractMessageReaderWriterProvider.writeTo(entityStream, stream);
        }
        finally {
            stream.close();
        }
        return file;
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return File.class.isAssignableFrom(type);
    }
    
    public void writeTo(final File t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final InputStream stream = new BufferedInputStream(new FileInputStream(t), ReaderWriter.BUFFER_SIZE);
        try {
            AbstractMessageReaderWriterProvider.writeTo(stream, entityStream);
        }
        finally {
            stream.close();
        }
    }
    
    @Override
    public long getSize(final File t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return t.length();
    }
}
