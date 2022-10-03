package com.lowagie.text.pdf.parser;

public interface TextAssembler
{
    void process(final FinalText p0, final String p1);
    
    void process(final Word p0, final String p1);
    
    void process(final ParsedText p0, final String p1);
    
    void renderText(final FinalText p0);
    
    void renderText(final ParsedTextImpl p0);
    
    FinalText endParsingContext(final String p0);
    
    String getWordId();
    
    void setPage(final int p0);
    
    void reset();
}
