package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;

public class MatchNoDocsQuery extends Query
{
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        return new BooleanQuery.Builder().build();
    }
    
    @Override
    public String toString(final String field) {
        return "";
    }
}
