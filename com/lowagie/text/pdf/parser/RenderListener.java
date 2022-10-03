package com.lowagie.text.pdf.parser;

public interface RenderListener extends TextAssembler
{
    void reset();
    
    String getResultantText();
}
