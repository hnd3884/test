package javax.ws.rs.ext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ParamConverterProvider
{
     <T> ParamConverter<T> getConverter(final Class<T> p0, final Type p1, final Annotation[] p2);
}
