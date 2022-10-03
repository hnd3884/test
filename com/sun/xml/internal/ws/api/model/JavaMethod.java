package com.sun.xml.internal.ws.api.model;

import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.istack.internal.NotNull;
import java.lang.reflect.Method;

public interface JavaMethod
{
    SEIModel getOwner();
    
    @NotNull
    Method getMethod();
    
    @NotNull
    Method getSEIMethod();
    
    MEP getMEP();
    
    SOAPBinding getBinding();
    
    @NotNull
    String getOperationName();
    
    @NotNull
    String getRequestMessageName();
    
    @Nullable
    String getResponseMessageName();
    
    @Nullable
    QName getRequestPayloadName();
    
    @Nullable
    QName getResponsePayloadName();
}
