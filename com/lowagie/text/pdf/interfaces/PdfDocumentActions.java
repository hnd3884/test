package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfAction;

public interface PdfDocumentActions
{
    void setOpenAction(final String p0);
    
    void setOpenAction(final PdfAction p0);
    
    void setAdditionalAction(final PdfName p0, final PdfAction p1) throws DocumentException;
}
