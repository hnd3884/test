package org.apache.lucene.index;

import java.io.IOException;

abstract class DocConsumer
{
    abstract void processDocument() throws IOException, AbortingException;
    
    abstract void flush(final SegmentWriteState p0) throws IOException, AbortingException;
    
    abstract void abort();
}
