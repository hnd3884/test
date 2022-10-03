package jdk.nashorn.internal.runtime.logging;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {
    String name() default "";
}
