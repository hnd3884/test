package com.sun.xml.internal.ws.developer;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id = "http://java.sun.com/xml/ns/jaxws/2004/08/addressing", bean = MemberSubmissionAddressingFeature.class)
public @interface MemberSubmissionAddressing {
    boolean enabled() default true;
    
    boolean required() default false;
    
    Validation validation() default Validation.LAX;
    
    public enum Validation
    {
        LAX, 
        STRICT;
    }
}
