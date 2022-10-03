package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface CharTermAttribute extends Attribute, CharSequence, Appendable
{
    void copyBuffer(final char[] p0, final int p1, final int p2);
    
    char[] buffer();
    
    char[] resizeBuffer(final int p0);
    
    CharTermAttribute setLength(final int p0);
    
    CharTermAttribute setEmpty();
    
    CharTermAttribute append(final CharSequence p0);
    
    CharTermAttribute append(final CharSequence p0, final int p1, final int p2);
    
    CharTermAttribute append(final char p0);
    
    CharTermAttribute append(final String p0);
    
    CharTermAttribute append(final StringBuilder p0);
    
    CharTermAttribute append(final CharTermAttribute p0);
}
