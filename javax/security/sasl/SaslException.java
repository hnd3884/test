package javax.security.sasl;

import java.io.IOException;

public class SaslException extends IOException
{
    private Throwable _exception;
    private static final long serialVersionUID = 4579784287983423626L;
    
    public SaslException() {
    }
    
    public SaslException(final String s) {
        super(s);
    }
    
    public SaslException(final String s, final Throwable t) {
        super(s);
        if (t != null) {
            this.initCause(t);
        }
    }
    
    @Override
    public Throwable getCause() {
        return this._exception;
    }
    
    @Override
    public Throwable initCause(final Throwable exception) {
        super.initCause(exception);
        this._exception = exception;
        return this;
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        if (this._exception != null && this._exception != this) {
            s = s + " [Caused by " + this._exception.toString() + "]";
        }
        return s;
    }
}
