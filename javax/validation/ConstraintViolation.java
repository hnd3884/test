package javax.validation;

import javax.validation.metadata.ConstraintDescriptor;

public interface ConstraintViolation<T>
{
    String getMessage();
    
    String getMessageTemplate();
    
    T getRootBean();
    
    Class<T> getRootBeanClass();
    
    Object getLeafBean();
    
    Object[] getExecutableParameters();
    
    Object getExecutableReturnValue();
    
    Path getPropertyPath();
    
    Object getInvalidValue();
    
    ConstraintDescriptor<?> getConstraintDescriptor();
    
     <U> U unwrap(final Class<U> p0);
}
