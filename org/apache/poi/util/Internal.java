package org.apache.poi.util;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal {
    String value() default "";
    
    String since() default "";
}
