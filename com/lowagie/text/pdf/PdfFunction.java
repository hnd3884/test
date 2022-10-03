package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.ExceptionConverter;

public class PdfFunction
{
    protected PdfWriter writer;
    protected PdfIndirectReference reference;
    protected PdfDictionary dictionary;
    
    protected PdfFunction(final PdfWriter writer) {
        this.writer = writer;
    }
    
    PdfIndirectReference getReference() {
        try {
            if (this.reference == null) {
                this.reference = this.writer.addToBody(this.dictionary).getIndirectReference();
            }
        }
        catch (final IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return this.reference;
    }
    
    public static PdfFunction type0(final PdfWriter writer, final float[] domain, final float[] range, final int[] size, final int bitsPerSample, final int order, final float[] encode, final float[] decode, final byte[] stream) {
        final PdfFunction func = new PdfFunction(writer);
        func.dictionary = new PdfStream(stream);
        ((PdfStream)func.dictionary).flateCompress(writer.getCompressionLevel());
        func.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(0));
        func.dictionary.put(PdfName.DOMAIN, new PdfArray(domain));
        func.dictionary.put(PdfName.RANGE, new PdfArray(range));
        func.dictionary.put(PdfName.SIZE, new PdfArray(size));
        func.dictionary.put(PdfName.BITSPERSAMPLE, new PdfNumber(bitsPerSample));
        if (order != 1) {
            func.dictionary.put(PdfName.ORDER, new PdfNumber(order));
        }
        if (encode != null) {
            func.dictionary.put(PdfName.ENCODE, new PdfArray(encode));
        }
        if (decode != null) {
            func.dictionary.put(PdfName.DECODE, new PdfArray(decode));
        }
        return func;
    }
    
    public static PdfFunction type2(final PdfWriter writer, final float[] domain, final float[] range, final float[] c0, final float[] c1, final float n) {
        final PdfFunction func = new PdfFunction(writer);
        (func.dictionary = new PdfDictionary()).put(PdfName.FUNCTIONTYPE, new PdfNumber(2));
        func.dictionary.put(PdfName.DOMAIN, new PdfArray(domain));
        if (range != null) {
            func.dictionary.put(PdfName.RANGE, new PdfArray(range));
        }
        if (c0 != null) {
            func.dictionary.put(PdfName.C0, new PdfArray(c0));
        }
        if (c1 != null) {
            func.dictionary.put(PdfName.C1, new PdfArray(c1));
        }
        func.dictionary.put(PdfName.N, new PdfNumber(n));
        return func;
    }
    
    public static PdfFunction type3(final PdfWriter writer, final float[] domain, final float[] range, final PdfFunction[] functions, final float[] bounds, final float[] encode) {
        final PdfFunction func = new PdfFunction(writer);
        (func.dictionary = new PdfDictionary()).put(PdfName.FUNCTIONTYPE, new PdfNumber(3));
        func.dictionary.put(PdfName.DOMAIN, new PdfArray(domain));
        if (range != null) {
            func.dictionary.put(PdfName.RANGE, new PdfArray(range));
        }
        final PdfArray array = new PdfArray();
        for (int k = 0; k < functions.length; ++k) {
            array.add(functions[k].getReference());
        }
        func.dictionary.put(PdfName.FUNCTIONS, array);
        func.dictionary.put(PdfName.BOUNDS, new PdfArray(bounds));
        func.dictionary.put(PdfName.ENCODE, new PdfArray(encode));
        return func;
    }
    
    public static PdfFunction type4(final PdfWriter writer, final float[] domain, final float[] range, final String postscript) {
        final byte[] b = new byte[postscript.length()];
        for (int k = 0; k < b.length; ++k) {
            b[k] = (byte)postscript.charAt(k);
        }
        final PdfFunction func = new PdfFunction(writer);
        func.dictionary = new PdfStream(b);
        ((PdfStream)func.dictionary).flateCompress(writer.getCompressionLevel());
        func.dictionary.put(PdfName.FUNCTIONTYPE, new PdfNumber(4));
        func.dictionary.put(PdfName.DOMAIN, new PdfArray(domain));
        func.dictionary.put(PdfName.RANGE, new PdfArray(range));
        return func;
    }
}
