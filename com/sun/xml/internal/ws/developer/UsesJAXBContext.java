package com.sun.xml.internal.ws.developer;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id = "http://jax-ws.dev.java.net/features/uses-jaxb-context", bean = UsesJAXBContextFeature.class)
public @interface UsesJAXBContext {
    Class<? extends JAXBContextFactory> value();
}
