package com.lowagie.text;

public class BadElementException extends DocumentException
{
    private static final long serialVersionUID = -799006030723822254L;
    
    public BadElementException(final Exception ex) {
        super(ex);
    }
    
    BadElementException() {
    }
    
    public BadElementException(final String message) {
        super(message);
    }
}
