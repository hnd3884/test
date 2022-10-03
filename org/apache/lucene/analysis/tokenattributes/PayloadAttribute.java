package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Attribute;

public interface PayloadAttribute extends Attribute
{
    BytesRef getPayload();
    
    void setPayload(final BytesRef p0);
}
