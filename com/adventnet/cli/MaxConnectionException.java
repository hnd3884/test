package com.adventnet.cli;

public class MaxConnectionException extends Exception
{
    public MaxConnectionException() {
    }
    
    public MaxConnectionException(final String s) {
        super(s);
    }
}
