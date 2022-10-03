package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfTransition;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfName;

public interface PdfPageActions
{
    void setPageAction(final PdfName p0, final PdfAction p1) throws DocumentException;
    
    void setDuration(final int p0);
    
    void setTransition(final PdfTransition p0);
}
