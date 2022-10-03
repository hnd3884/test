package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface KeywordAttribute extends Attribute
{
    boolean isKeyword();
    
    void setKeyword(final boolean p0);
}
