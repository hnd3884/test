package com.theorem.radius3;

public class EAPException extends Exception
{
    public EAPException(final String s) {
        super(s);
    }
    
    public EAPException(final Exception ex) {
        super(ex.getMessage());
    }
}
