package com.lowagie.text;

import com.lowagie.text.pdf.PdfChunk;

public interface SplitCharacter
{
    boolean isSplitCharacter(final int p0, final int p1, final int p2, final char[] p3, final PdfChunk[] p4);
}
