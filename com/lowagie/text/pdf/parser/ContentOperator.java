package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfObject;
import java.util.ArrayList;

public interface ContentOperator
{
    void invoke(final ArrayList<PdfObject> p0, final PdfContentStreamHandler p1, final PdfDictionary p2);
    
    String getOperatorName();
}
