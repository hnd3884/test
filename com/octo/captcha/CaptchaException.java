package com.octo.captcha;

public class CaptchaException extends RuntimeException
{
    private Throwable cause;
    
    public CaptchaException() {
    }
    
    public CaptchaException(final String s) {
        super(s);
    }
    
    public CaptchaException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CaptchaException(final Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
