package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class PdfLiteral extends PdfObject
{
    private int position;
    
    public PdfLiteral(final String text) {
        super(0, text);
    }
    
    public PdfLiteral(final byte[] b) {
        super(0, b);
    }
    
    public PdfLiteral(final int size) {
        super(0, (byte[])null);
        Arrays.fill(this.bytes = new byte[size], (byte)32);
    }
    
    public PdfLiteral(final int type, final String text) {
        super(type, text);
    }
    
    public PdfLiteral(final int type, final byte[] b) {
        super(type, b);
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        if (os instanceof OutputStreamCounter) {
            this.position = ((OutputStreamCounter)os).getCounter();
        }
        super.toPdf(writer, os);
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public int getPosLength() {
        if (this.bytes != null) {
            return this.bytes.length;
        }
        return 0;
    }
}
