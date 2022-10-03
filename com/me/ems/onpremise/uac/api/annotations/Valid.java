package com.me.ems.onpremise.uac.api.annotations;

import javax.validation.Payload;
import java.lang.annotation.Documented;
import com.me.ems.onpremise.uac.validators.UserDetailValidator;
import javax.validation.Constraint;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { UserDetailValidator.class })
@Documented
public @interface Valid {
    String message() default "Not a valid User Role. Found: ${validatedValue}";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
