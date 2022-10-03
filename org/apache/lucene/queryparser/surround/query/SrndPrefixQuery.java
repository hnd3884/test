package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

public class SrndPrefixQuery extends SimpleTerm
{
    private final BytesRef prefixRef;
    private final String prefix;
    private final char truncator;
    
    public SrndPrefixQuery(final String prefix, final boolean quoted, final char truncator) {
        super(quoted);
        this.prefix = prefix;
        this.prefixRef = new BytesRef((CharSequence)prefix);
        this.truncator = truncator;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public char getSuffixOperator() {
        return this.truncator;
    }
    
    public Term getLucenePrefixTerm(final String fieldName) {
        return new Term(fieldName, this.getPrefix());
    }
    
    @Override
    public String toStringUnquoted() {
        return this.getPrefix();
    }
    
    @Override
    protected void suffixToString(final StringBuilder r) {
        r.append(this.getSuffixOperator());
    }
    
    @Override
    public void visitMatchingTerms(final IndexReader reader, final String fieldName, final MatchingTermVisitor mtv) throws IOException {
        final Terms terms = MultiFields.getTerms(reader, fieldName);
        if (terms != null) {
            final TermsEnum termsEnum = terms.iterator();
            boolean skip = false;
            final TermsEnum.SeekStatus status = termsEnum.seekCeil(new BytesRef((CharSequence)this.getPrefix()));
            if (status == TermsEnum.SeekStatus.FOUND) {
                mtv.visitMatchingTerm(this.getLucenePrefixTerm(fieldName));
            }
            else if (status == TermsEnum.SeekStatus.NOT_FOUND) {
                if (StringHelper.startsWith(termsEnum.term(), this.prefixRef)) {
                    mtv.visitMatchingTerm(new Term(fieldName, termsEnum.term().utf8ToString()));
                }
                else {
                    skip = true;
                }
            }
            else {
                skip = true;
            }
            if (!skip) {
                while (true) {
                    final BytesRef text = termsEnum.next();
                    if (text == null || !StringHelper.startsWith(text, this.prefixRef)) {
                        break;
                    }
                    mtv.visitMatchingTerm(new Term(fieldName, text.utf8ToString()));
                }
            }
        }
    }
}
