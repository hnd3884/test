package javax.validation;

import java.lang.annotation.Annotation;

public interface ConstraintValidator<A extends Annotation, T>
{
    default void initialize(final A constraintAnnotation) {
    }
    
    boolean isValid(final T p0, final ConstraintValidatorContext p1);
}
