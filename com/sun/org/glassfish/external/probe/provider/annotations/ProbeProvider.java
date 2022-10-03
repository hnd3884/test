package com.sun.org.glassfish.external.probe.provider.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ProbeProvider {
    String providerName() default "";
    
    String moduleProviderName() default "";
    
    String moduleName() default "";
    
    String probeProviderName() default "";
}
