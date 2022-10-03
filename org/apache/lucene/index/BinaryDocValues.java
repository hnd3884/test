package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

public abstract class BinaryDocValues
{
    protected BinaryDocValues() {
    }
    
    public abstract BytesRef get(final int p0);
}
