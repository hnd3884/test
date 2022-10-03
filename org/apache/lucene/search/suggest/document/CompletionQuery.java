package org.apache.lucene.search.suggest.document;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReader;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.suggest.BitsProducer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

public abstract class CompletionQuery extends Query
{
    private final Term term;
    private final BitsProducer filter;
    
    protected CompletionQuery(final Term term, final BitsProducer filter) {
        this.validate(term.text());
        this.term = term;
        this.filter = filter;
    }
    
    public BitsProducer getFilter() {
        return this.filter;
    }
    
    public String getField() {
        return this.term.field();
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        byte type = 0;
        boolean first = true;
        for (final LeafReaderContext context : reader.leaves()) {
            final LeafReader leafReader = context.reader();
            Terms terms;
            try {
                if ((terms = leafReader.terms(this.getField())) == null) {
                    continue;
                }
            }
            catch (final IOException e) {
                continue;
            }
            if (terms instanceof CompletionTerms) {
                final CompletionTerms completionTerms = (CompletionTerms)terms;
                final byte t = completionTerms.getType();
                if (first) {
                    type = t;
                    first = false;
                }
                else {
                    if (type != t) {
                        throw new IllegalStateException(this.getField() + " has values of multiple types");
                    }
                    continue;
                }
            }
        }
        if (!first) {
            if (this instanceof ContextQuery) {
                if (type == 0) {
                    throw new IllegalStateException(this.getClass().getSimpleName() + " can not be executed against a non context-enabled SuggestField: " + this.getField());
                }
            }
            else if (type == 1) {
                return new ContextQuery(this);
            }
        }
        return super.rewrite(reader);
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append('*');
        if (this.filter != null) {
            buffer.append(",");
            buffer.append("filter");
            buffer.append(":");
            buffer.append(this.filter.toString());
        }
        return buffer.toString();
    }
    
    private void validate(final String termText) {
        int i = 0;
        while (i < termText.length()) {
            switch (termText.charAt(i)) {
                case '\u001e': {
                    throw new IllegalArgumentException("Term text cannot contain HOLE character U+001E; this character is reserved");
                }
                case '\u001f': {
                    throw new IllegalArgumentException("Term text cannot contain unit separator character U+001F; this character is reserved");
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
    }
}
