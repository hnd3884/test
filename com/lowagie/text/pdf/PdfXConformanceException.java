package com.lowagie.text.pdf;

public class PdfXConformanceException extends RuntimeException
{
    private static final long serialVersionUID = 9199144538884293397L;
    
    public PdfXConformanceException() {
    }
    
    public PdfXConformanceException(final String s) {
        super(s);
    }
}
