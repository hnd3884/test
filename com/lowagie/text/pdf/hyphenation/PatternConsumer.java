package com.lowagie.text.pdf.hyphenation;

import java.util.ArrayList;

public interface PatternConsumer
{
    void addClass(final String p0);
    
    void addException(final String p0, final ArrayList p1);
    
    void addPattern(final String p0, final String p1);
}
