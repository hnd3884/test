package javax.xml.ws.soap;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id = "http://www.w3.org/2005/08/addressing/module", bean = AddressingFeature.class)
public @interface Addressing {
    boolean enabled() default true;
    
    boolean required() default false;
    
    AddressingFeature.Responses responses() default AddressingFeature.Responses.ALL;
}
