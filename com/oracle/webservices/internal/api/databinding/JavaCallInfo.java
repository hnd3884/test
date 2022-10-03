package com.oracle.webservices.internal.api.databinding;

import java.lang.reflect.Method;

public interface JavaCallInfo
{
    Method getMethod();
    
    Object[] getParameters();
    
    Object getReturnValue();
    
    void setReturnValue(final Object p0);
    
    Throwable getException();
    
    void setException(final Throwable p0);
}
