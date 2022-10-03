package com.oracle.webservices.internal.api;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id = "", bean = EnvelopeStyleFeature.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvelopeStyle {
    Style[] style() default { Style.SOAP11 };
    
    public enum Style
    {
        SOAP11("http://schemas.xmlsoap.org/wsdl/soap/http"), 
        SOAP12("http://www.w3.org/2003/05/soap/bindings/HTTP/"), 
        XML("http://www.w3.org/2004/08/wsdl/http");
        
        public final String bindingId;
        
        private Style(final String id) {
            this.bindingId = id;
        }
        
        public boolean isSOAP11() {
            return this.equals(Style.SOAP11);
        }
        
        public boolean isSOAP12() {
            return this.equals(Style.SOAP12);
        }
        
        public boolean isXML() {
            return this.equals(Style.XML);
        }
    }
}
