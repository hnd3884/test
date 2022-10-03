package jdk.jfr;

import jdk.Exported;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;

@MetadataDefinition
@Label("Experimental")
@Description("Element is not to be shown to a user by default")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Exported
public @interface Experimental {
}
