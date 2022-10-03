package com.octo.captcha.module;

public class CaptchaModuleException extends RuntimeException
{
    private Throwable cause;
    
    public CaptchaModuleException() {
    }
    
    public CaptchaModuleException(final String s) {
        super(s);
    }
    
    public CaptchaModuleException(final Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    public CaptchaModuleException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
