package org.glassfish.hk2.api.messaging;

import org.glassfish.hk2.api.Metadata;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.inject.Qualifier;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface MessageReceiver {
    public static final String EVENT_RECEIVER_TYPES = "org.glassfish.hk2.messaging.messageReceiverTypes";
    
    @Metadata("org.glassfish.hk2.messaging.messageReceiverTypes")
    Class<?>[] value() default {};
}
