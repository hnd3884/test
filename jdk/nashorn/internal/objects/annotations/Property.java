package jdk.nashorn.internal.objects.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Property {
    String name() default "";
    
    int attributes() default 0;
    
    String clazz() default "";
    
    Where where() default Where.INSTANCE;
}
