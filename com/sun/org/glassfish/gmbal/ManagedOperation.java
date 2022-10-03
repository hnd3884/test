package com.sun.org.glassfish.gmbal;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedOperation {
    String id() default "";
    
    Impact impact() default Impact.UNKNOWN;
}
