package org.msgpack.annotation;

import org.msgpack.template.FieldOption;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Beans {
    FieldOption value() default FieldOption.DEFAULT;
}
