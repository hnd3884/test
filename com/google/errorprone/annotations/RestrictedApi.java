package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface RestrictedApi {
    String explanation();
    
    String link();
    
    String allowedOnPath() default "";
    
    Class<? extends Annotation>[] whitelistAnnotations() default {};
    
    Class<? extends Annotation>[] whitelistWithWarningAnnotations() default {};
}
