package com.sun.xml.internal.ws.api.model;

import javax.jws.WebParam;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.namespace.QName;

public interface Parameter
{
    SEIModel getOwner();
    
    JavaMethod getParent();
    
    QName getName();
    
    @Deprecated
    Bridge getBridge();
    
    WebParam.Mode getMode();
    
    int getIndex();
    
    boolean isWrapperStyle();
    
    boolean isReturnValue();
    
    ParameterBinding getBinding();
    
    ParameterBinding getInBinding();
    
    ParameterBinding getOutBinding();
    
    boolean isIN();
    
    boolean isOUT();
    
    boolean isINOUT();
    
    boolean isResponse();
    
    Object getHolderValue(final Object p0);
    
    String getPartName();
}
