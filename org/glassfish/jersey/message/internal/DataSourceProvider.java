package org.glassfish.jersey.message.internal;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.activation.DataSource;

@Produces({ "application/octet-stream", "*/*" })
@Consumes({ "application/octet-stream", "*/*" })
public class DataSourceProvider extends AbstractMessageReaderWriterProvider<DataSource>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return DataSource.class == type;
    }
    
    public DataSource readFrom(final Class<DataSource> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return new ByteArrayDataSource(entityStream, (mediaType == null) ? null : mediaType.toString());
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return DataSource.class.isAssignableFrom(type);
    }
    
    public void writeTo(final DataSource t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        final InputStream in = t.getInputStream();
        try {
            AbstractMessageReaderWriterProvider.writeTo(in, entityStream);
        }
        finally {
            in.close();
        }
    }
    
    public static class ByteArrayDataSource implements DataSource
    {
        private final String type;
        private byte[] data;
        private int len;
        private String name;
        
        public ByteArrayDataSource(final InputStream is, final String type) throws IOException {
            this.len = -1;
            this.name = "";
            final DSByteArrayOutputStream os = new DSByteArrayOutputStream();
            ReaderWriter.writeTo(is, os);
            this.data = os.getBuf();
            this.len = os.getCount();
            if (this.data.length - this.len > 262144) {
                this.data = os.toByteArray();
                this.len = this.data.length;
            }
            this.type = type;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            if (this.data == null) {
                throw new IOException("no data");
            }
            if (this.len < 0) {
                this.len = this.data.length;
            }
            return new ByteArrayInputStream(this.data, 0, this.len);
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("cannot do this");
        }
        
        @Override
        public String getContentType() {
            return this.type;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        static class DSByteArrayOutputStream extends ByteArrayOutputStream
        {
            public byte[] getBuf() {
                return this.buf;
            }
            
            public int getCount() {
                return this.count;
            }
        }
    }
}
