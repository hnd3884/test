package com.sun.org.apache.xpath.internal.functions;

public class WrongNumberArgsException extends Exception
{
    static final long serialVersionUID = -4551577097576242432L;
    
    public WrongNumberArgsException(final String argsExpected) {
        super(argsExpected);
    }
}
