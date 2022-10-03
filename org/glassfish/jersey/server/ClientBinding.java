package org.glassfish.jersey.server;

import javax.ws.rs.core.Configuration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
@Documented
public @interface ClientBinding {
    Class<? extends Configuration> configClass() default Configuration.class;
    
    boolean inheritServerProviders() default true;
    
    String baseUri() default "";
}
