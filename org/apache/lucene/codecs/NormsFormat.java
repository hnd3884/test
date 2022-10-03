package org.apache.lucene.codecs;

import org.apache.lucene.index.SegmentReadState;
import java.io.IOException;
import org.apache.lucene.index.SegmentWriteState;

public abstract class NormsFormat
{
    protected NormsFormat() {
    }
    
    public abstract NormsConsumer normsConsumer(final SegmentWriteState p0) throws IOException;
    
    public abstract NormsProducer normsProducer(final SegmentReadState p0) throws IOException;
}
