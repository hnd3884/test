package org.tanukisoftware.wrapper;

public class WrapperJNIError extends Error
{
    private static final long serialVersionUID = 4163224795268336447L;
    
    WrapperJNIError(final String message) {
        super(message);
    }
    
    WrapperJNIError(final byte[] message) {
        this(new String(message));
    }
    
    public String toString() {
        return this.getClass().getName() + " " + this.getMessage();
    }
}
