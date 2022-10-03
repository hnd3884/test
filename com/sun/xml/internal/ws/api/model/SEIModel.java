package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.NotNull;
import java.util.Collection;
import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.util.Pool;

public interface SEIModel
{
    Pool.Marshaller getMarshallerPool();
    
    @Deprecated
    JAXBContext getJAXBContext();
    
    JavaMethod getJavaMethod(final Method p0);
    
    JavaMethod getJavaMethod(final QName p0);
    
    JavaMethod getJavaMethodForWsdlOperation(final QName p0);
    
    Collection<? extends JavaMethod> getJavaMethods();
    
    @NotNull
    String getWSDLLocation();
    
    @NotNull
    QName getServiceQName();
    
    @NotNull
    WSDLPort getPort();
    
    @NotNull
    QName getPortName();
    
    @NotNull
    QName getPortTypeName();
    
    @NotNull
    QName getBoundPortTypeName();
    
    @NotNull
    String getTargetNamespace();
}
