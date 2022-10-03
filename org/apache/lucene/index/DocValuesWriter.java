package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.codecs.DocValuesConsumer;

abstract class DocValuesWriter
{
    abstract void finish(final int p0);
    
    abstract void flush(final SegmentWriteState p0, final DocValuesConsumer p1) throws IOException;
}
