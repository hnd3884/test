package org.apache.tomcat.util.codec;

@Deprecated
public class EncoderException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public EncoderException() {
    }
    
    public EncoderException(final String message) {
        super(message);
    }
    
    public EncoderException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public EncoderException(final Throwable cause) {
        super(cause);
    }
}
