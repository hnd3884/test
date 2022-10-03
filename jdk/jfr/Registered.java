package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Exported
public @interface Registered {
    boolean value() default true;
}
