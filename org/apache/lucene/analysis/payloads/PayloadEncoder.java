package org.apache.lucene.analysis.payloads;

import org.apache.lucene.util.BytesRef;

public interface PayloadEncoder
{
    BytesRef encode(final char[] p0);
    
    BytesRef encode(final char[] p0, final int p1, final int p2);
}
