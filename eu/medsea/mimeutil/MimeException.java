package eu.medsea.mimeutil;

public class MimeException extends RuntimeException
{
    private static final long serialVersionUID = -1931354615779382666L;
    
    public MimeException(final String message) {
        super(message);
    }
    
    public MimeException(final Throwable t) {
        super(t);
    }
    
    public MimeException(final String message, final Throwable t) {
        super(message, t);
    }
}
