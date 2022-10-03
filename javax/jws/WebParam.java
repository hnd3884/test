package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface WebParam {
    String name() default "";
    
    String partName() default "";
    
    String targetNamespace() default "";
    
    Mode mode() default Mode.IN;
    
    boolean header() default false;
    
    public enum Mode
    {
        IN, 
        OUT, 
        INOUT;
    }
}
