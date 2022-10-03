package com.lowagie.text.pdf;

public class BadPdfFormatException extends PdfException
{
    private static final long serialVersionUID = 1802317735708833538L;
    
    BadPdfFormatException() {
    }
    
    BadPdfFormatException(final String message) {
        super(message);
    }
}
