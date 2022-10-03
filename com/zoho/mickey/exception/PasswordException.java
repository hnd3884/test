package com.zoho.mickey.exception;

public class PasswordException extends Exception
{
    public PasswordException(final String message) {
        super(message);
    }
    
    public PasswordException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
    
    public PasswordException(final Throwable throwable) {
        super(throwable);
    }
}
