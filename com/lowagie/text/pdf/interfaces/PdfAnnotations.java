package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAcroForm;

public interface PdfAnnotations
{
    PdfAcroForm getAcroForm();
    
    void addAnnotation(final PdfAnnotation p0);
    
    void addCalculationOrder(final PdfFormField p0);
    
    void setSigFlags(final int p0);
}
