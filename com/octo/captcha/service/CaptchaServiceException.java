package com.octo.captcha.service;

public class CaptchaServiceException extends RuntimeException
{
    private Throwable cause;
    
    public CaptchaServiceException(final String s) {
        super(s);
    }
    
    public CaptchaServiceException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CaptchaServiceException(final Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
