package com.lowagie.text;

public class DocumentException extends RuntimeException
{
    private static final long serialVersionUID = -2191131489390840739L;
    
    public DocumentException(final Exception ex) {
        super(ex);
    }
    
    public DocumentException() {
    }
    
    public DocumentException(final String message) {
        super(message);
    }
}
