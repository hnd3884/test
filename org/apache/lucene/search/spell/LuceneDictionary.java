package org.apache.lucene.search.spell;

import java.io.IOException;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.index.IndexReader;

public class LuceneDictionary implements Dictionary
{
    private IndexReader reader;
    private String field;
    
    public LuceneDictionary(final IndexReader reader, final String field) {
        this.reader = reader;
        this.field = field;
    }
    
    @Override
    public final InputIterator getEntryIterator() throws IOException {
        final Terms terms = MultiFields.getTerms(this.reader, this.field);
        if (terms != null) {
            return new InputIterator.InputIteratorWrapper((BytesRefIterator)terms.iterator());
        }
        return InputIterator.EMPTY;
    }
}
