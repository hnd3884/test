package com.me.mdm.framework.syncml.annotations;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SyncMLElement {
    String xmlElementName() default "";
    
    String xmlElementNameSpace() default "";
    
    boolean isMandatory() default false;
    
    int orderby() default 0;
}
