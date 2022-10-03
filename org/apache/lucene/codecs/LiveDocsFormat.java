package org.apache.lucene.codecs;

import java.util.Collection;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.MutableBits;

public abstract class LiveDocsFormat
{
    protected LiveDocsFormat() {
    }
    
    public abstract MutableBits newLiveDocs(final int p0) throws IOException;
    
    public abstract MutableBits newLiveDocs(final Bits p0) throws IOException;
    
    public abstract Bits readLiveDocs(final Directory p0, final SegmentCommitInfo p1, final IOContext p2) throws IOException;
    
    public abstract void writeLiveDocs(final MutableBits p0, final Directory p1, final SegmentCommitInfo p2, final int p3, final IOContext p4) throws IOException;
    
    public abstract void files(final SegmentCommitInfo p0, final Collection<String> p1) throws IOException;
}
