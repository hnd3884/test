package com.sun.org.apache.xml.internal.serializer.utils;

public final class WrappedRuntimeException extends RuntimeException
{
    static final long serialVersionUID = 7140414456714658073L;
    private Exception m_exception;
    
    public WrappedRuntimeException(final Exception e) {
        super(e.getMessage());
        this.m_exception = e;
    }
    
    public WrappedRuntimeException(final String msg, final Exception e) {
        super(msg);
        this.m_exception = e;
    }
    
    public Exception getException() {
        return this.m_exception;
    }
}
