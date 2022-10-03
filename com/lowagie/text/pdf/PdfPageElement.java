package com.lowagie.text.pdf;

interface PdfPageElement
{
    void setParent(final PdfIndirectReference p0);
    
    boolean isParent();
}
