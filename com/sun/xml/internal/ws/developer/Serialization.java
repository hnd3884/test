package com.sun.xml.internal.ws.developer;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Documented
@WebServiceFeatureAnnotation(id = "http://jax-ws.java.net/features/serialization", bean = SerializationFeature.class)
public @interface Serialization {
    String encoding() default "";
}
