package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@MetadataDefinition
@Label("Transition From")
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Exported
public @interface TransitionFrom {
}
