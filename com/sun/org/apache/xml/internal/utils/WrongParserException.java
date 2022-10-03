package com.sun.org.apache.xml.internal.utils;

public class WrongParserException extends RuntimeException
{
    static final long serialVersionUID = 6481643018533043846L;
    
    public WrongParserException(final String message) {
        super(message);
    }
}
