package com.lowagie.text.pdf;

import java.io.IOException;

public class PdfRendition extends PdfDictionary
{
    PdfRendition(final String file, final PdfFileSpecification fs, final String mimeType) throws IOException {
        this.put(PdfName.S, new PdfName("MR"));
        this.put(PdfName.N, new PdfString("Rendition for " + file));
        this.put(PdfName.C, new PdfMediaClipData(file, fs, mimeType));
    }
}
