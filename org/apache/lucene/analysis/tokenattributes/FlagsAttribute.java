package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface FlagsAttribute extends Attribute
{
    int getFlags();
    
    void setFlags(final int p0);
}
