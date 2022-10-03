package net.oauth;

public class OAuthException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    protected OAuthException() {
    }
    
    public OAuthException(final String message) {
        super(message);
    }
    
    public OAuthException(final Throwable cause) {
        super(cause);
    }
    
    public OAuthException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
