package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@ContentType
@Label("Flag")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD })
@Exported
public @interface BooleanFlag {
}
