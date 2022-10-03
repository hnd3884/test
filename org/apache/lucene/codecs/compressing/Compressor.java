package org.apache.lucene.codecs.compressing;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;

public abstract class Compressor
{
    protected Compressor() {
    }
    
    public abstract void compress(final byte[] p0, final int p1, final int p2, final DataOutput p3) throws IOException;
}
