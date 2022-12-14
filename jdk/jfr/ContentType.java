package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@MetadataDefinition
@Label("Content Type")
@Description("Semantic meaning of a value")
@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Exported
public @interface ContentType {
}
