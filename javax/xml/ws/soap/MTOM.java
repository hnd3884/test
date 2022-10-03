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
@WebServiceFeatureAnnotation(id = "http://www.w3.org/2004/08/soap/features/http-optimization", bean = MTOMFeature.class)
public @interface MTOM {
    boolean enabled() default true;
    
    int threshold() default 0;
}
