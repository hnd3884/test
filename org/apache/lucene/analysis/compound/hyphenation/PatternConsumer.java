package org.apache.lucene.analysis.compound.hyphenation;

import java.util.ArrayList;

public interface PatternConsumer
{
    void addClass(final String p0);
    
    void addException(final String p0, final ArrayList<Object> p1);
    
    void addPattern(final String p0, final String p1);
}
