package com.lowagie.text.pdf;

public interface HyphenationEvent
{
    String getHyphenSymbol();
    
    String getHyphenatedWordPre(final String p0, final BaseFont p1, final float p2, final float p3);
    
    String getHyphenatedWordPost();
}
