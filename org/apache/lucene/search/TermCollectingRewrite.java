package org.apache.lucene.search;

import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.io.IOException;

abstract class TermCollectingRewrite<B> extends MultiTermQuery.RewriteMethod
{
    protected abstract B getTopLevelBuilder() throws IOException;
    
    protected abstract Query build(final B p0);
    
    protected final void addClause(final B topLevel, final Term term, final int docCount, final float boost) throws IOException {
        this.addClause(topLevel, term, docCount, boost, null);
    }
    
    protected abstract void addClause(final B p0, final Term p1, final int p2, final float p3, final TermContext p4) throws IOException;
    
    final void collectTerms(final IndexReader reader, final MultiTermQuery query, final TermCollector collector) throws IOException {
        final IndexReaderContext topReaderContext = reader.getContext();
        for (final LeafReaderContext context : topReaderContext.leaves()) {
            final Terms terms = context.reader().terms(query.field);
            if (terms == null) {
                continue;
            }
            final TermsEnum termsEnum = this.getTermsEnum(query, terms, collector.attributes);
            assert termsEnum != null;
            if (termsEnum == TermsEnum.EMPTY) {
                continue;
            }
            collector.setReaderContext(topReaderContext, context);
            collector.setNextEnum(termsEnum);
            BytesRef bytes;
            while ((bytes = termsEnum.next()) != null) {
                if (!collector.collect(bytes)) {
                    return;
                }
            }
        }
    }
    
    abstract static class TermCollector
    {
        protected LeafReaderContext readerContext;
        protected IndexReaderContext topReaderContext;
        public final AttributeSource attributes;
        
        TermCollector() {
            this.attributes = new AttributeSource();
        }
        
        public void setReaderContext(final IndexReaderContext topReaderContext, final LeafReaderContext readerContext) {
            this.readerContext = readerContext;
            this.topReaderContext = topReaderContext;
        }
        
        public abstract boolean collect(final BytesRef p0) throws IOException;
        
        public abstract void setNextEnum(final TermsEnum p0) throws IOException;
    }
}
