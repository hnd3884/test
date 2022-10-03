package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface PositionIncrementAttribute extends Attribute
{
    void setPositionIncrement(final int p0);
    
    int getPositionIncrement();
}
