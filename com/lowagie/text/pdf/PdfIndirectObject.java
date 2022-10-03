package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.DocWriter;
import java.io.OutputStream;

public class PdfIndirectObject
{
    protected int number;
    protected int generation;
    static final byte[] STARTOBJ;
    static final byte[] ENDOBJ;
    static final int SIZEOBJ;
    PdfObject object;
    PdfWriter writer;
    
    PdfIndirectObject(final int number, final PdfObject object, final PdfWriter writer) {
        this(number, 0, object, writer);
    }
    
    PdfIndirectObject(final PdfIndirectReference ref, final PdfObject object, final PdfWriter writer) {
        this(ref.getNumber(), ref.getGeneration(), object, writer);
    }
    
    PdfIndirectObject(final int number, final int generation, final PdfObject object, final PdfWriter writer) {
        this.generation = 0;
        this.writer = writer;
        this.number = number;
        this.generation = generation;
        this.object = object;
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null) {
            crypto.setHashKey(number, generation);
        }
    }
    
    public PdfIndirectReference getIndirectReference() {
        return new PdfIndirectReference(this.object.type(), this.number, this.generation);
    }
    
    void writeTo(final OutputStream os) throws IOException {
        os.write(DocWriter.getISOBytes(String.valueOf(this.number)));
        os.write(32);
        os.write(DocWriter.getISOBytes(String.valueOf(this.generation)));
        os.write(PdfIndirectObject.STARTOBJ);
        this.object.toPdf(this.writer, os);
        os.write(PdfIndirectObject.ENDOBJ);
    }
    
    static {
        STARTOBJ = DocWriter.getISOBytes(" obj\n");
        ENDOBJ = DocWriter.getISOBytes("\nendobj\n");
        SIZEOBJ = PdfIndirectObject.STARTOBJ.length + PdfIndirectObject.ENDOBJ.length;
    }
}
