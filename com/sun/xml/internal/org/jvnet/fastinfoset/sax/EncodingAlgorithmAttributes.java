package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.Attributes;

public interface EncodingAlgorithmAttributes extends Attributes
{
    String getAlgorithmURI(final int p0);
    
    int getAlgorithmIndex(final int p0);
    
    Object getAlgorithmData(final int p0);
    
    String getAlpababet(final int p0);
    
    boolean getToIndex(final int p0);
}
