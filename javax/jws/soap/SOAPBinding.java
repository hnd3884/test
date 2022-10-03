package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface SOAPBinding {
    Style style() default Style.DOCUMENT;
    
    Use use() default Use.LITERAL;
    
    ParameterStyle parameterStyle() default ParameterStyle.WRAPPED;
    
    public enum Style
    {
        DOCUMENT, 
        RPC;
    }
    
    public enum Use
    {
        LITERAL, 
        ENCODED;
    }
    
    public enum ParameterStyle
    {
        BARE, 
        WRAPPED;
    }
}
