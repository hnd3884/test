package javax.validation;

public interface ValidatorFactory extends AutoCloseable
{
    Validator getValidator();
    
    ValidatorContext usingContext();
    
    MessageInterpolator getMessageInterpolator();
    
    TraversableResolver getTraversableResolver();
    
    ConstraintValidatorFactory getConstraintValidatorFactory();
    
    ParameterNameProvider getParameterNameProvider();
    
    ClockProvider getClockProvider();
    
     <T> T unwrap(final Class<T> p0);
    
    void close();
}
