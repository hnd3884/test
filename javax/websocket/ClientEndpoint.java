package javax.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ClientEndpoint {
    String[] subprotocols() default {};
    
    Class<? extends Decoder>[] decoders() default {};
    
    Class<? extends Encoder>[] encoders() default {};
    
    Class<? extends ClientEndpointConfig.Configurator> configurator() default ClientEndpointConfig.Configurator.class;
}
