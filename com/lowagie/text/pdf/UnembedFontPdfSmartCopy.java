package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.Iterator;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;
import com.lowagie.text.Document;

public class UnembedFontPdfSmartCopy extends PdfSmartCopy
{
    public UnembedFontPdfSmartCopy(final Document document, final OutputStream os) throws DocumentException {
        super(document, os);
    }
    
    @Override
    protected PdfDictionary copyDictionary(final PdfDictionary in) throws IOException, BadPdfFormatException {
        final PdfDictionary out = new PdfDictionary();
        final PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
        for (final PdfName key : in.getKeys()) {
            final PdfObject value = in.get(key);
            if ((PdfName.FONTFILE.equals(key) || PdfName.FONTFILE2.equals(key) || PdfName.FONTFILE3.equals(key)) && !PdfReader.isFontSubset(PdfReader.getFontNameFromDescriptor(in))) {
                continue;
            }
            out.put(key, this.copyObject(value));
        }
        return out;
    }
}
