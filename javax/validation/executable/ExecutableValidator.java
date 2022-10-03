package javax.validation.executable;

import java.lang.reflect.Constructor;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.lang.reflect.Method;

public interface ExecutableValidator
{
     <T> Set<ConstraintViolation<T>> validateParameters(final T p0, final Method p1, final Object[] p2, final Class<?>... p3);
    
     <T> Set<ConstraintViolation<T>> validateReturnValue(final T p0, final Method p1, final Object p2, final Class<?>... p3);
    
     <T> Set<ConstraintViolation<T>> validateConstructorParameters(final Constructor<? extends T> p0, final Object[] p1, final Class<?>... p2);
    
     <T> Set<ConstraintViolation<T>> validateConstructorReturnValue(final Constructor<? extends T> p0, final T p1, final Class<?>... p2);
}
