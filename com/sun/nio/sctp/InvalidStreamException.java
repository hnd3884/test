package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class InvalidStreamException extends IllegalArgumentException
{
    private static final long serialVersionUID = -9172703378046665558L;
    
    public InvalidStreamException() {
    }
    
    public InvalidStreamException(final String s) {
        super(s);
    }
}
