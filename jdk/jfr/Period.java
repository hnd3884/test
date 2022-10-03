package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.TYPE })
@Exported
public @interface Period {
    public static final String NAME = "period";
    
    String value() default "everyChunk";
}
