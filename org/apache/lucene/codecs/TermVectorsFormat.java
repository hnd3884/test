package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;

public abstract class TermVectorsFormat
{
    protected TermVectorsFormat() {
    }
    
    public abstract TermVectorsReader vectorsReader(final Directory p0, final SegmentInfo p1, final FieldInfos p2, final IOContext p3) throws IOException;
    
    public abstract TermVectorsWriter vectorsWriter(final Directory p0, final SegmentInfo p1, final IOContext p2) throws IOException;
}
