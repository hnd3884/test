package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@ContentType
@Label("Timestamp")
@Description("A point in time")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD })
@Exported
public @interface Timestamp {
    public static final String MILLISECONDS_SINCE_EPOCH = "MILLISECONDS_SINCE_EPOCH";
    public static final String TICKS = "TICKS";
    
    String value() default "MILLISECONDS_SINCE_EPOCH";
}
