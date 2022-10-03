package com.oracle.webservices.internal.api.databinding;

import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;
import javax.xml.transform.Source;
import java.net.URL;
import javax.xml.namespace.QName;
import com.oracle.webservices.internal.api.message.MessageContext;
import java.lang.reflect.Method;

public interface Databinding
{
    JavaCallInfo createJavaCallInfo(final Method p0, final Object[] p1);
    
    MessageContext serializeRequest(final JavaCallInfo p0);
    
    JavaCallInfo deserializeResponse(final MessageContext p0, final JavaCallInfo p1);
    
    JavaCallInfo deserializeRequest(final MessageContext p0);
    
    MessageContext serializeResponse(final JavaCallInfo p0);
    
    public interface Builder
    {
        Builder targetNamespace(final String p0);
        
        Builder serviceName(final QName p0);
        
        Builder portName(final QName p0);
        
        @Deprecated
        Builder wsdlURL(final URL p0);
        
        @Deprecated
        Builder wsdlSource(final Source p0);
        
        @Deprecated
        Builder entityResolver(final EntityResolver p0);
        
        Builder classLoader(final ClassLoader p0);
        
        Builder feature(final WebServiceFeature... p0);
        
        Builder property(final String p0, final Object p1);
        
        Databinding build();
        
        WSDLGenerator createWSDLGenerator();
    }
}
