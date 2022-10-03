package com.fasterxml.jackson.dataformat.xml.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JacksonXmlElementWrapper {
    public static final String USE_PROPERTY_NAME = "";
    
    String namespace() default "";
    
    String localName() default "";
    
    boolean useWrapping() default true;
}
