package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiTermQuery;

@Deprecated
public class RegexQuery extends MultiTermQuery implements RegexQueryCapable
{
    private RegexCapabilities regexImpl;
    private Term term;
    
    public RegexQuery(final Term term) {
        super(term.field());
        this.regexImpl = new JavaUtilRegexCapabilities();
        this.term = term;
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    public void setRegexImplementation(final RegexCapabilities impl) {
        this.regexImpl = impl;
    }
    
    public RegexCapabilities getRegexImplementation() {
        return this.regexImpl;
    }
    
    protected FilteredTermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        return new RegexTermsEnum(terms.iterator(), this.term, this.regexImpl);
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        return buffer.toString();
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.regexImpl == null) ? 0 : this.regexImpl.hashCode());
        result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final RegexQuery other = (RegexQuery)obj;
        if (this.regexImpl == null) {
            if (other.regexImpl != null) {
                return false;
            }
        }
        else if (!this.regexImpl.equals(other.regexImpl)) {
            return false;
        }
        if (this.term == null) {
            if (other.term != null) {
                return false;
            }
        }
        else if (!this.term.equals((Object)other.term)) {
            return false;
        }
        return true;
    }
}
