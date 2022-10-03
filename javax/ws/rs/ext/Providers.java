package javax.ws.rs.ext;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Providers
{
     <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T extends Throwable> ExceptionMapper<T> getExceptionMapper(final Class<T> p0);
    
     <T> ContextResolver<T> getContextResolver(final Class<T> p0, final MediaType p1);
}
