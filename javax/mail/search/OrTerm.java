package javax.mail.search;

import javax.mail.Message;

public final class OrTerm extends SearchTerm
{
    private SearchTerm[] terms;
    private static final long serialVersionUID = 5380534067523646936L;
    
    public OrTerm(final SearchTerm t1, final SearchTerm t2) {
        (this.terms = new SearchTerm[2])[0] = t1;
        this.terms[1] = t2;
    }
    
    public OrTerm(final SearchTerm[] t) {
        this.terms = new SearchTerm[t.length];
        for (int i = 0; i < t.length; ++i) {
            this.terms[i] = t[i];
        }
    }
    
    public SearchTerm[] getTerms() {
        return this.terms.clone();
    }
    
    @Override
    public boolean match(final Message msg) {
        for (int i = 0; i < this.terms.length; ++i) {
            if (this.terms[i].match(msg)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof OrTerm)) {
            return false;
        }
        final OrTerm ot = (OrTerm)obj;
        if (ot.terms.length != this.terms.length) {
            return false;
        }
        for (int i = 0; i < this.terms.length; ++i) {
            if (!this.terms[i].equals(ot.terms[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.terms.length; ++i) {
            hash += this.terms[i].hashCode();
        }
        return hash;
    }
}
