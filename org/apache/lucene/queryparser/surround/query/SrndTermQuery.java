package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

public class SrndTermQuery extends SimpleTerm
{
    private final String termText;
    
    public SrndTermQuery(final String termText, final boolean quoted) {
        super(quoted);
        this.termText = termText;
    }
    
    public String getTermText() {
        return this.termText;
    }
    
    public Term getLuceneTerm(final String fieldName) {
        return new Term(fieldName, this.getTermText());
    }
    
    @Override
    public String toStringUnquoted() {
        return this.getTermText();
    }
    
    @Override
    public void visitMatchingTerms(final IndexReader reader, final String fieldName, final MatchingTermVisitor mtv) throws IOException {
        final Terms terms = MultiFields.getTerms(reader, fieldName);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            final TermsEnum.SeekStatus status = termsEnum.seekCeil(new BytesRef((CharSequence)this.getTermText()));
            if (status == TermsEnum.SeekStatus.FOUND) {
                mtv.visitMatchingTerm(this.getLuceneTerm(fieldName));
            }
        }
    }
}
