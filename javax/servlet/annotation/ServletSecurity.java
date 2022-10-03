package javax.servlet.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Inherited;

@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServletSecurity {
    HttpConstraint value() default @HttpConstraint;
    
    HttpMethodConstraint[] httpMethodConstraints() default {};
    
    public enum EmptyRoleSemantic
    {
        PERMIT, 
        DENY;
    }
    
    public enum TransportGuarantee
    {
        NONE, 
        CONFIDENTIAL;
    }
}
