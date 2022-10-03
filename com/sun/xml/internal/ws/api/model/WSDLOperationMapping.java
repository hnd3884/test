package com.sun.xml.internal.ws.api.model;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;

public interface WSDLOperationMapping
{
    WSDLBoundOperation getWSDLBoundOperation();
    
    JavaMethod getJavaMethod();
    
    QName getOperationName();
}
