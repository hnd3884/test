package com.sun.xml.internal.ws.api.databinding;

import java.lang.reflect.Method;

public class JavaCallInfo implements com.oracle.webservices.internal.api.databinding.JavaCallInfo
{
    private Method method;
    private Object[] parameters;
    private Object returnValue;
    private Throwable exception;
    
    public JavaCallInfo() {
    }
    
    public JavaCallInfo(final Method m, final Object[] args) {
        this.method = m;
        this.parameters = args;
    }
    
    @Override
    public Method getMethod() {
        return this.method;
    }
    
    public void setMethod(final Method method) {
        this.method = method;
    }
    
    @Override
    public Object[] getParameters() {
        return this.parameters;
    }
    
    public void setParameters(final Object[] parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public Object getReturnValue() {
        return this.returnValue;
    }
    
    @Override
    public void setReturnValue(final Object returnValue) {
        this.returnValue = returnValue;
    }
    
    @Override
    public Throwable getException() {
        return this.exception;
    }
    
    @Override
    public void setException(final Throwable exception) {
        this.exception = exception;
    }
}
