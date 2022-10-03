package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfName;

public interface PdfVersion
{
    void setPdfVersion(final char p0);
    
    void setAtLeastPdfVersion(final char p0);
    
    void setPdfVersion(final PdfName p0);
    
    void addDeveloperExtension(final PdfDeveloperExtension p0);
}
