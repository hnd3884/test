package org.bouncycastle.i18n;

import java.util.Locale;

public class LocalizedException extends Exception
{
    protected ErrorBundle message;
    private Throwable cause;
    
    public LocalizedException(final ErrorBundle message) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
    }
    
    public LocalizedException(final ErrorBundle message, final Throwable cause) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
        this.cause = cause;
    }
    
    public ErrorBundle getErrorMessage() {
        return this.message;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
