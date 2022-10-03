package com.sun.xml.internal.ws.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR })
public @interface FeatureConstructor {
    String[] value() default {};
}
