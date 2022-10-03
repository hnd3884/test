package javax.ws.rs.ext;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MessageBodyWriter<T>
{
    boolean isWriteable(final Class<?> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
    default long getSize(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1L;
    }
    
    void writeTo(final T p0, final Class<?> p1, final Type p2, final Annotation[] p3, final MediaType p4, final MultivaluedMap<String, Object> p5, final OutputStream p6) throws IOException, WebApplicationException;
}
