package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfMediaClipData extends PdfDictionary
{
    PdfMediaClipData(final String file, final PdfFileSpecification fs, final String mimeType) throws IOException {
        this.put(PdfName.TYPE, new PdfName("MediaClip"));
        this.put(PdfName.S, new PdfName("MCD"));
        this.put(PdfName.N, new PdfString("Media clip for " + file));
        this.put(new PdfName("CT"), new PdfString(mimeType));
        final PdfDictionary dic = new PdfDictionary();
        dic.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
        this.put(new PdfName("P"), dic);
        this.put(PdfName.D, fs.getReference());
    }
}
