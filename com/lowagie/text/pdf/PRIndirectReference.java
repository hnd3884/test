package com.lowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PRIndirectReference extends PdfIndirectReference
{
    protected PdfReader reader;
    
    PRIndirectReference(final PdfReader reader, final int number, final int generation) {
        this.type = 10;
        this.number = number;
        this.generation = generation;
        this.reader = reader;
    }
    
    PRIndirectReference(final PdfReader reader, final int number) {
        this(reader, number, 0);
    }
    
    @Override
    public void toPdf(final PdfWriter writer, final OutputStream os) throws IOException {
        final int n = writer.getNewObjectNumber(this.reader, this.number, this.generation);
        os.write(PdfEncodings.convertToBytes(new StringBuffer().append(n).append(" 0 R").toString(), null));
    }
    
    public PdfReader getReader() {
        return this.reader;
    }
    
    public void setNumber(final int number, final int generation) {
        this.number = number;
        this.generation = generation;
    }
}
