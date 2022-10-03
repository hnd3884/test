package com.lowagie.text.pdf;

import java.awt.Font;

public interface FontMapper
{
    BaseFont awtToPdf(final Font p0);
    
    Font pdfToAwt(final BaseFont p0, final int p1);
}
