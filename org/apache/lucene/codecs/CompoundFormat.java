package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;

public abstract class CompoundFormat
{
    public abstract Directory getCompoundReader(final Directory p0, final SegmentInfo p1, final IOContext p2) throws IOException;
    
    public abstract void write(final Directory p0, final SegmentInfo p1, final IOContext p2) throws IOException;
}
