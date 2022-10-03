package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.regex.Matcher;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.IndexReader;
import java.util.regex.Pattern;
import org.apache.lucene.util.BytesRef;

public class SrndTruncQuery extends SimpleTerm
{
    private final String truncated;
    private final char unlimited;
    private final char mask;
    private String prefix;
    private BytesRef prefixRef;
    private Pattern pattern;
    
    public SrndTruncQuery(final String truncated, final char unlimited, final char mask) {
        super(false);
        this.truncated = truncated;
        this.unlimited = unlimited;
        this.mask = mask;
        this.truncatedToPrefixAndPattern();
    }
    
    public String getTruncated() {
        return this.truncated;
    }
    
    @Override
    public String toStringUnquoted() {
        return this.getTruncated();
    }
    
    protected boolean matchingChar(final char c) {
        return c != this.unlimited && c != this.mask;
    }
    
    protected void appendRegExpForChar(final char c, final StringBuilder re) {
        if (c == this.unlimited) {
            re.append(".*");
        }
        else if (c == this.mask) {
            re.append(".");
        }
        else {
            re.append(c);
        }
    }
    
    protected void truncatedToPrefixAndPattern() {
        int i;
        for (i = 0; i < this.truncated.length() && this.matchingChar(this.truncated.charAt(i)); ++i) {}
        this.prefix = this.truncated.substring(0, i);
        this.prefixRef = new BytesRef((CharSequence)this.prefix);
        final StringBuilder re = new StringBuilder();
        while (i < this.truncated.length()) {
            this.appendRegExpForChar(this.truncated.charAt(i), re);
            ++i;
        }
        this.pattern = Pattern.compile(re.toString());
    }
    
    @Override
    public void visitMatchingTerms(final IndexReader reader, final String fieldName, final MatchingTermVisitor mtv) throws IOException {
        final int prefixLength = this.prefix.length();
        final Terms terms = MultiFields.getTerms(reader, fieldName);
        if (terms != null) {
            final Matcher matcher = this.pattern.matcher("");
            try {
                final TermsEnum termsEnum = terms.iterator();
                final TermsEnum.SeekStatus status = termsEnum.seekCeil(this.prefixRef);
                BytesRef text;
                if (status == TermsEnum.SeekStatus.FOUND) {
                    text = this.prefixRef;
                }
                else if (status == TermsEnum.SeekStatus.NOT_FOUND) {
                    text = termsEnum.term();
                }
                else {
                    text = null;
                }
                while (text != null && text != null && StringHelper.startsWith(text, this.prefixRef)) {
                    final String textString = text.utf8ToString();
                    matcher.reset(textString.substring(prefixLength));
                    if (matcher.matches()) {
                        mtv.visitMatchingTerm(new Term(fieldName, textString));
                    }
                    text = termsEnum.next();
                }
            }
            finally {
                matcher.reset();
            }
        }
    }
}
