package org.apache.lucene.codecs;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import java.io.Closeable;

public abstract class PostingsWriterBase implements Closeable
{
    protected PostingsWriterBase() {
    }
    
    public abstract void init(final IndexOutput p0, final SegmentWriteState p1) throws IOException;
    
    public abstract BlockTermState writeTerm(final BytesRef p0, final TermsEnum p1, final FixedBitSet p2) throws IOException;
    
    public abstract void encodeTerm(final long[] p0, final DataOutput p1, final FieldInfo p2, final BlockTermState p3, final boolean p4) throws IOException;
    
    public abstract int setField(final FieldInfo p0);
    
    @Override
    public abstract void close() throws IOException;
}
