package org.apache.lucene.search.suggest.fst;

import java.util.Comparator;
import org.apache.lucene.util.BytesRefIterator;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;

public interface BytesRefSorter
{
    void add(final BytesRef p0) throws IOException, IllegalStateException;
    
    BytesRefIterator iterator() throws IOException;
    
    Comparator<BytesRef> getComparator();
}
