package javax.ws.rs.ext;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MessageBodyReader<T>
{
    boolean isReadable(final Class<?> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
    T readFrom(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3, final MultivaluedMap<String, String> p4, final InputStream p5) throws IOException, WebApplicationException;
}
