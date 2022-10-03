package com.me.ems.framework.common.api.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

@NameBinding
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowEntityFilter {
    boolean enableFieldValidation() default true;
    
    boolean defaultEntity() default false;
    
    String[] defaultFields() default {};
}
