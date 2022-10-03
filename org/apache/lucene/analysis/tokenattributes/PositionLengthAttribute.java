package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface PositionLengthAttribute extends Attribute
{
    void setPositionLength(final int p0);
    
    int getPositionLength();
}
