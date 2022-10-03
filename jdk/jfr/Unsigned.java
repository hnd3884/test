package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@ContentType
@Label("Unsigned Value")
@Description("Value should be interpreted as unsigned data type")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Exported
public @interface Unsigned {
}
