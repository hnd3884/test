package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfName;

public interface PdfViewerPreferences
{
    void setViewerPreferences(final int p0);
    
    void addViewerPreference(final PdfName p0, final PdfObject p1);
}
