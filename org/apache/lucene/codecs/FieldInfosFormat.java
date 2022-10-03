package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;

public abstract class FieldInfosFormat
{
    protected FieldInfosFormat() {
    }
    
    public abstract FieldInfos read(final Directory p0, final SegmentInfo p1, final String p2, final IOContext p3) throws IOException;
    
    public abstract void write(final Directory p0, final SegmentInfo p1, final String p2, final FieldInfos p3, final IOContext p4) throws IOException;
}
