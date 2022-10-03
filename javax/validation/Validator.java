package javax.validation;

import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;
import java.util.Set;

public interface Validator
{
     <T> Set<ConstraintViolation<T>> validate(final T p0, final Class<?>... p1);
    
     <T> Set<ConstraintViolation<T>> validateProperty(final T p0, final String p1, final Class<?>... p2);
    
     <T> Set<ConstraintViolation<T>> validateValue(final Class<T> p0, final String p1, final Object p2, final Class<?>... p3);
    
    BeanDescriptor getConstraintsForClass(final Class<?> p0);
    
     <T> T unwrap(final Class<T> p0);
    
    ExecutableValidator forExecutables();
}
