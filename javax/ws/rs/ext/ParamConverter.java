package javax.ws.rs.ext;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public interface ParamConverter<T>
{
    T fromString(final String p0);
    
    String toString(final T p0);
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface Lazy {
    }
}
