package org.apache.poi.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface SuppressForbidden {
    String value() default "";
}
