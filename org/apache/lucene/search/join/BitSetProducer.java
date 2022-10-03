package org.apache.lucene.search.join;

import java.io.IOException;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.LeafReaderContext;

public interface BitSetProducer
{
    BitSet getBitSet(final LeafReaderContext p0) throws IOException;
}
