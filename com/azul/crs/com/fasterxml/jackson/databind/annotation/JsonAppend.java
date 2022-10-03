package com.azul.crs.com.fasterxml.jackson.databind.annotation;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;
import com.azul.crs.com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.azul.crs.com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAppend {
    Attr[] attrs() default {};
    
    Prop[] props() default {};
    
    boolean prepend() default false;
    
    public @interface Prop {
        Class<? extends VirtualBeanPropertyWriter> value();
        
        String name() default "";
        
        String namespace() default "";
        
        JsonInclude.Include include() default JsonInclude.Include.NON_NULL;
        
        boolean required() default false;
        
        Class<?> type() default Object.class;
    }
    
    public @interface Attr {
        String value();
        
        String propName() default "";
        
        String propNamespace() default "";
        
        JsonInclude.Include include() default JsonInclude.Include.NON_NULL;
        
        boolean required() default false;
    }
}
