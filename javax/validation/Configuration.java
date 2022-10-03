package javax.validation;

import java.io.InputStream;
import javax.validation.valueextraction.ValueExtractor;

public interface Configuration<T extends Configuration<T>>
{
    T ignoreXmlConfiguration();
    
    T messageInterpolator(final MessageInterpolator p0);
    
    T traversableResolver(final TraversableResolver p0);
    
    T constraintValidatorFactory(final ConstraintValidatorFactory p0);
    
    T parameterNameProvider(final ParameterNameProvider p0);
    
    T clockProvider(final ClockProvider p0);
    
    T addValueExtractor(final ValueExtractor<?> p0);
    
    T addMapping(final InputStream p0);
    
    T addProperty(final String p0, final String p1);
    
    MessageInterpolator getDefaultMessageInterpolator();
    
    TraversableResolver getDefaultTraversableResolver();
    
    ConstraintValidatorFactory getDefaultConstraintValidatorFactory();
    
    ParameterNameProvider getDefaultParameterNameProvider();
    
    ClockProvider getDefaultClockProvider();
    
    BootstrapConfiguration getBootstrapConfiguration();
    
    ValidatorFactory buildValidatorFactory();
}
