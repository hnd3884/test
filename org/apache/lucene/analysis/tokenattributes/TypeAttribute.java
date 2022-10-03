package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public interface TypeAttribute extends Attribute
{
    public static final String DEFAULT_TYPE = "word";
    
    String type();
    
    void setType(final String p0);
}
