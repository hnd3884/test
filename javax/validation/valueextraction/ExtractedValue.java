package javax.validation.valueextraction;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE_USE })
@Documented
public @interface ExtractedValue {
    Class<?> type() default void.class;
}
