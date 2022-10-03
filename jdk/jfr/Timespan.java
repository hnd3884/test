package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@ContentType
@Label("Timespan")
@Description("A duration, measured in nanoseconds by default")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD })
@Exported
public @interface Timespan {
    public static final String TICKS = "TICKS";
    public static final String SECONDS = "SECONDS";
    public static final String MILLISECONDS = "MILLISECONDS";
    public static final String NANOSECONDS = "NANOSECONDS";
    public static final String MICROSECONDS = "MICROSECONDS";
    
    String value() default "NANOSECONDS";
}
