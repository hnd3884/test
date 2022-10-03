package org.apache.poi;

public class EncryptedDocumentException extends IllegalStateException
{
    private static final long serialVersionUID = 7276950444540469193L;
    
    public EncryptedDocumentException(final String s) {
        super(s);
    }
    
    public EncryptedDocumentException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public EncryptedDocumentException(final Throwable cause) {
        super(cause);
    }
}
