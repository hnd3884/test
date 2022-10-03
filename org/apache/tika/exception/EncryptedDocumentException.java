package org.apache.tika.exception;

public class EncryptedDocumentException extends TikaException
{
    public EncryptedDocumentException() {
        super("Unable to process: document is encrypted");
    }
    
    public EncryptedDocumentException(final Throwable th) {
        super("Unable to process: document is encrypted", th);
    }
    
    public EncryptedDocumentException(final String info) {
        super(info);
    }
    
    public EncryptedDocumentException(final String info, final Throwable th) {
        super(info, th);
    }
}
