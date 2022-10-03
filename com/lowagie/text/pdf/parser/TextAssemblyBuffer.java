package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;

public interface TextAssemblyBuffer
{
    String getText();
    
    FinalText getFinalText(final PdfReader p0, final int p1, final TextAssembler p2, final boolean p3);
    
    void accumulate(final TextAssembler p0, final String p1);
    
    void assemble(final TextAssembler p0);
}
