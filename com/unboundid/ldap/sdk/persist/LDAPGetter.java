package com.unboundid.ldap.sdk.persist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface LDAPGetter {
    boolean inAdd() default true;
    
    boolean inModify() default true;
    
    boolean inRDN() default false;
    
    Class<? extends ObjectEncoder> encoderClass() default DefaultObjectEncoder.class;
    
    FilterUsage filterUsage() default FilterUsage.CONDITIONALLY_ALLOWED;
    
    String attribute() default "";
    
    String[] objectClass() default {};
}
