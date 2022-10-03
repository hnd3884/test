package com.theorem.radius3.module;

public class RADIUSModuleException extends Exception
{
    public RADIUSModuleException(final String s) {
        super(s);
    }
    
    public RADIUSModuleException(final Exception ex) {
        super(ex.getMessage());
    }
}
