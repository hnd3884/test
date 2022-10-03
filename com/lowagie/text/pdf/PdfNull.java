package com.lowagie.text.pdf;

public class PdfNull extends PdfObject
{
    public static final PdfNull PDFNULL;
    private static final String CONTENT = "null";
    
    public PdfNull() {
        super(8, "null");
    }
    
    @Override
    public String toString() {
        return "null";
    }
    
    static {
        PDFNULL = new PdfNull();
    }
}
