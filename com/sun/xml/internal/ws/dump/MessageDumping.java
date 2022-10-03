package com.sun.xml.internal.ws.dump;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id = "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature", bean = MessageDumpingFeature.class)
public @interface MessageDumping {
    boolean enabled() default true;
    
    String messageLoggingRoot() default "com.sun.xml.internal.ws.messagedump";
    
    String messageLoggingLevel() default "FINE";
    
    boolean storeMessages() default false;
}
