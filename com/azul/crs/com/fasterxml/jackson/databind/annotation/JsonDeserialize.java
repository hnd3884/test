package com.azul.crs.com.fasterxml.jackson.databind.annotation;

import com.azul.crs.com.fasterxml.jackson.databind.util.Converter;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonDeserialize {
    Class<? extends JsonDeserializer> using() default JsonDeserializer.None.class;
    
    Class<? extends JsonDeserializer> contentUsing() default JsonDeserializer.None.class;
    
    Class<? extends KeyDeserializer> keyUsing() default KeyDeserializer.None.class;
    
    Class<?> builder() default Void.class;
    
    Class<? extends Converter> converter() default Converter.None.class;
    
    Class<? extends Converter> contentConverter() default Converter.None.class;
    
    Class<?> as() default Void.class;
    
    Class<?> keyAs() default Void.class;
    
    Class<?> contentAs() default Void.class;
}