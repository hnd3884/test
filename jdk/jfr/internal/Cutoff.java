package jdk.jfr.internal;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import jdk.jfr.MetadataDefinition;

@MetadataDefinition
@Target({ ElementType.TYPE })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Cutoff {
    public static final String NAME = "cutoff";
    public static final String INIFITY = "infinity";
    
    String value() default "inifity";
}
