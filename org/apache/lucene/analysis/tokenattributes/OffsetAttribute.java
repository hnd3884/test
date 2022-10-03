package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface OffsetAttribute extends Attribute
{
    int startOffset();
    
    void setOffset(final int p0, final int p1);
    
    int endOffset();
}
