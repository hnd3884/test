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
public @interface Threshold {
    public static final String NAME = "threshold";
    
    String value() default "0 ns";
}
