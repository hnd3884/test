package javax.ws.rs.ext;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Collection;

public interface InterceptorContext
{
    Object getProperty(final String p0);
    
    Collection<String> getPropertyNames();
    
    void setProperty(final String p0, final Object p1);
    
    void removeProperty(final String p0);
    
    Annotation[] getAnnotations();
    
    void setAnnotations(final Annotation[] p0);
    
    Class<?> getType();
    
    void setType(final Class<?> p0);
    
    Type getGenericType();
    
    void setGenericType(final Type p0);
    
    MediaType getMediaType();
    
    void setMediaType(final MediaType p0);
}
