package org.apache.lucene.search.suggest;

import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

public abstract class BitsProducer
{
    protected BitsProducer() {
    }
    
    public abstract Bits getBits(final LeafReaderContext p0) throws IOException;
}
