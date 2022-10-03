package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;

public interface PdfPCellEvent
{
    void cellLayout(final PdfPCell p0, final Rectangle p1, final PdfContentByte[] p2);
}
