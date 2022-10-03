package com.unboundid.ldap.sdk.persist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface LDAPObject {
    boolean requestAllAttributes() default false;
    
    String defaultParentDN() default "";
    
    String postDecodeMethod() default "";
    
    String postEncodeMethod() default "";
    
    String structuralClass() default "";
    
    String[] auxiliaryClass() default {};
    
    String[] superiorClass() default {};
}
