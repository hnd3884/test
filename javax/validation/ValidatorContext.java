package javax.validation;

import javax.validation.valueextraction.ValueExtractor;

public interface ValidatorContext
{
    ValidatorContext messageInterpolator(final MessageInterpolator p0);
    
    ValidatorContext traversableResolver(final TraversableResolver p0);
    
    ValidatorContext constraintValidatorFactory(final ConstraintValidatorFactory p0);
    
    ValidatorContext parameterNameProvider(final ParameterNameProvider p0);
    
    ValidatorContext clockProvider(final ClockProvider p0);
    
    ValidatorContext addValueExtractor(final ValueExtractor<?> p0);
    
    Validator getValidator();
}
