package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@MetadataDefinition
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Exported
public @interface StackTrace {
    public static final String NAME = "stackTrace";
    
    boolean value() default true;
}
