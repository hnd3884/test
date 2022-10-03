package com.lowagie.text.exceptions;

public class IllegalPdfSyntaxException extends IllegalArgumentException
{
    private static final long serialVersionUID = -643024246596031671L;
    
    public IllegalPdfSyntaxException(final String message) {
        super(message);
    }
}
