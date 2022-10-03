package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface WebMethod {
    String operationName() default "";
    
    String action() default "";
    
    boolean exclude() default false;
}
