package jdk.jfr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import jdk.Exported;

@Exported
@MetadataDefinition
@ContentType
@Label("Frequency")
@Description("Measure of how often something occurs, in Hertz")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Frequency {
}
