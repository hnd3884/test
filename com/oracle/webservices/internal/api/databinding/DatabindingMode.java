package com.oracle.webservices.internal.api.databinding;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id = "", bean = DatabindingModeFeature.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabindingMode {
    String value();
}
