package com.azul.crs.com.fasterxml.jackson.databind.annotation;

import com.azul.crs.com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.azul.crs.com.fasterxml.jackson.annotation.JacksonAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonNaming {
    Class<? extends PropertyNamingStrategy> value() default PropertyNamingStrategy.class;
}
