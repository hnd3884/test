package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.store.DataInput;

public abstract class Decompressor implements Cloneable
{
    protected Decompressor() {
    }
    
    public abstract void decompress(final DataInput p0, final int p1, final int p2, final int p3, final BytesRef p4) throws IOException;
    
    public abstract Decompressor clone();
}
