package com.sun.tracing.dtrace;

import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Attributes {
    StabilityLevel name() default StabilityLevel.PRIVATE;
    
    StabilityLevel data() default StabilityLevel.PRIVATE;
    
    DependencyClass dependency() default DependencyClass.UNKNOWN;
}
