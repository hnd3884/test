package com.sun.org.glassfish.external.arc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PACKAGE })
public @interface Taxonomy {
    Stability stability() default Stability.UNSPECIFIED;
    
    String description() default "";
}
