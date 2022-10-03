package com.azul.crs.com.fasterxml.jackson.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonView {
    Class<?>[] value() default {};
}
