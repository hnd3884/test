package org.glassfish.jersey.message;

import javax.ws.rs.ext.WriterInterceptor;
import java.io.OutputStream;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.PropertiesDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public interface MessageBodyWorkers
{
    Map<MediaType, List<MessageBodyReader>> getReaders(final MediaType p0);
    
    Map<MediaType, List<MessageBodyWriter>> getWriters(final MediaType p0);
    
    String readersToString(final Map<MediaType, List<MessageBodyReader>> p0);
    
    String writersToString(final Map<MediaType, List<MessageBodyWriter>> p0);
    
     <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3, final PropertiesDelegate p4);
    
     <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3, final PropertiesDelegate p4);
    
    List<MediaType> getMessageBodyReaderMediaTypes(final Class<?> p0, final Type p1, final Annotation[] p2);
    
    List<MediaType> getMessageBodyReaderMediaTypesByType(final Class<?> p0);
    
    List<MessageBodyReader> getMessageBodyReadersForType(final Class<?> p0);
    
    List<ReaderModel> getReaderModelsForType(final Class<?> p0);
    
    List<MediaType> getMessageBodyWriterMediaTypes(final Class<?> p0, final Type p1, final Annotation[] p2);
    
    List<MediaType> getMessageBodyWriterMediaTypesByType(final Class<?> p0);
    
    List<MessageBodyWriter> getMessageBodyWritersForType(final Class<?> p0);
    
    List<WriterModel> getWritersModelsForType(final Class<?> p0);
    
    MediaType getMessageBodyWriterMediaType(final Class<?> p0, final Type p1, final Annotation[] p2, final List<MediaType> p3);
    
    Object readFrom(final Class<?> p0, final Type p1, final Annotation[] p2, final MediaType p3, final MultivaluedMap<String, String> p4, final PropertiesDelegate p5, final InputStream p6, final Iterable<ReaderInterceptor> p7, final boolean p8) throws WebApplicationException, IOException;
    
    OutputStream writeTo(final Object p0, final Class<?> p1, final Type p2, final Annotation[] p3, final MediaType p4, final MultivaluedMap<String, Object> p5, final PropertiesDelegate p6, final OutputStream p7, final Iterable<WriterInterceptor> p8) throws IOException, WebApplicationException;
}
