package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class IllegalUnbindException extends IllegalStateException
{
    private static final long serialVersionUID = -310540883995532224L;
    
    public IllegalUnbindException() {
    }
    
    public IllegalUnbindException(final String s) {
        super(s);
    }
}
