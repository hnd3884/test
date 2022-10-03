package org.glassfish.hk2.utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Stub {
    Type value() default Type.VALUES;
    
    public enum Type
    {
        VALUES, 
        EXCEPTIONS;
    }
}
