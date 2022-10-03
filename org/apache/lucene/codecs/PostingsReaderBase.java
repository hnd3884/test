package org.apache.lucene.codecs;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.store.DataInput;
import java.io.IOException;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public abstract class PostingsReaderBase implements Closeable, Accountable
{
    protected PostingsReaderBase() {
    }
    
    public abstract void init(final IndexInput p0, final SegmentReadState p1) throws IOException;
    
    public abstract BlockTermState newTermState() throws IOException;
    
    public abstract void decodeTerm(final long[] p0, final DataInput p1, final FieldInfo p2, final BlockTermState p3, final boolean p4) throws IOException;
    
    public abstract PostingsEnum postings(final FieldInfo p0, final BlockTermState p1, final PostingsEnum p2, final int p3) throws IOException;
    
    public abstract void checkIntegrity() throws IOException;
    
    @Override
    public abstract void close() throws IOException;
}
