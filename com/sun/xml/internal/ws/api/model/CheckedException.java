package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.bind.api.Bridge;

public interface CheckedException
{
    SEIModel getOwner();
    
    JavaMethod getParent();
    
    Class getExceptionClass();
    
    Class getDetailBean();
    
    @Deprecated
    Bridge getBridge();
    
    ExceptionType getExceptionType();
    
    String getMessageName();
}
