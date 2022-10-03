package com.octo.captcha.engine;

public class CaptchaEngineException extends RuntimeException
{
    private Throwable cause;
    
    public CaptchaEngineException() {
    }
    
    public CaptchaEngineException(final String s) {
        super(s);
    }
    
    public CaptchaEngineException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CaptchaEngineException(final Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
