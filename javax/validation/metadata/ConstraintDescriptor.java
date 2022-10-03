package javax.validation.metadata;

import java.util.Map;
import javax.validation.ConstraintValidator;
import java.util.List;
import javax.validation.ConstraintTarget;
import javax.validation.Payload;
import java.util.Set;
import java.lang.annotation.Annotation;

public interface ConstraintDescriptor<T extends Annotation>
{
    T getAnnotation();
    
    String getMessageTemplate();
    
    Set<Class<?>> getGroups();
    
    Set<Class<? extends Payload>> getPayload();
    
    ConstraintTarget getValidationAppliesTo();
    
    List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses();
    
    Map<String, Object> getAttributes();
    
    Set<ConstraintDescriptor<?>> getComposingConstraints();
    
    boolean isReportAsSingleViolation();
    
    ValidateUnwrappedValue getValueUnwrapping();
    
     <U> U unwrap(final Class<U> p0);
}
