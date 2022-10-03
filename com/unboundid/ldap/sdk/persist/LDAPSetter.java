package com.unboundid.ldap.sdk.persist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface LDAPSetter {
    boolean failOnInvalidValue() default true;
    
    boolean failOnTooManyValues() default true;
    
    Class<? extends ObjectEncoder> encoderClass() default DefaultObjectEncoder.class;
    
    String attribute() default "";
}
