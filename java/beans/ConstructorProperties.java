package java.beans;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConstructorProperties {
    String[] value();
}
