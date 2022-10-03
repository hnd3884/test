package com.sun.corba.se.spi.copyobject;

public class ReflectiveCopyException extends Exception
{
    public ReflectiveCopyException() {
    }
    
    public ReflectiveCopyException(final String s) {
        super(s);
    }
    
    public ReflectiveCopyException(final String s, final Throwable t) {
        super(s, t);
    }
}
