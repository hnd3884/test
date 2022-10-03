package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;

public class PdfBoolean extends PdfObject
{
    public static final PdfBoolean PDFTRUE;
    public static final PdfBoolean PDFFALSE;
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private boolean value;
    
    public PdfBoolean(final boolean value) {
        super(1);
        if (value) {
            this.setContent("true");
        }
        else {
            this.setContent("false");
        }
        this.value = value;
    }
    
    public PdfBoolean(final String value) throws BadPdfFormatException {
        super(1, value);
        if (value.equals("true")) {
            this.value = true;
        }
        else {
            if (!value.equals("false")) {
                throw new BadPdfFormatException(MessageLocalization.getComposedMessage("the.value.has.to.be.true.of.false.instead.of.1", value));
            }
            this.value = false;
        }
    }
    
    public boolean booleanValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value ? "true" : "false";
    }
    
    static {
        PDFTRUE = new PdfBoolean(true);
        PDFFALSE = new PdfBoolean(false);
    }
}
