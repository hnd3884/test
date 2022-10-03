package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;

public class SearcherFactory
{
    public IndexSearcher newSearcher(final IndexReader reader, final IndexReader previousReader) throws IOException {
        return new IndexSearcher(reader);
    }
}
