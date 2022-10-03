package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Deprecated
public @interface SOAPMessageHandlers {
    SOAPMessageHandler[] value();
}
