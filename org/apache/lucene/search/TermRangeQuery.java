package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

public class TermRangeQuery extends AutomatonQuery
{
    private final BytesRef lowerTerm;
    private final BytesRef upperTerm;
    private final boolean includeLower;
    private final boolean includeUpper;
    
    public TermRangeQuery(final String field, final BytesRef lowerTerm, final BytesRef upperTerm, final boolean includeLower, final boolean includeUpper) {
        super(new Term(field, lowerTerm), toAutomaton(lowerTerm, upperTerm, includeLower, includeUpper), Integer.MAX_VALUE, true);
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
        this.includeLower = includeLower;
        this.includeUpper = includeUpper;
    }
    
    public static Automaton toAutomaton(final BytesRef lowerTerm, final BytesRef upperTerm, boolean includeLower, boolean includeUpper) {
        if (lowerTerm == null) {
            includeLower = true;
        }
        if (upperTerm == null) {
            includeUpper = true;
        }
        return Automata.makeBinaryInterval(lowerTerm, includeLower, upperTerm, includeUpper);
    }
    
    public static TermRangeQuery newStringRange(final String field, final String lowerTerm, final String upperTerm, final boolean includeLower, final boolean includeUpper) {
        final BytesRef lower = (lowerTerm == null) ? null : new BytesRef(lowerTerm);
        final BytesRef upper = (upperTerm == null) ? null : new BytesRef(upperTerm);
        return new TermRangeQuery(field, lower, upper, includeLower, includeUpper);
    }
    
    public BytesRef getLowerTerm() {
        return this.lowerTerm;
    }
    
    public BytesRef getUpperTerm() {
        return this.upperTerm;
    }
    
    public boolean includesLower() {
        return this.includeLower;
    }
    
    public boolean includesUpper() {
        return this.includeUpper;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.includeLower ? '[' : '{');
        buffer.append((this.lowerTerm != null) ? ("*".equals(Term.toString(this.lowerTerm)) ? "\\*" : Term.toString(this.lowerTerm)) : "*");
        buffer.append(" TO ");
        buffer.append((this.upperTerm != null) ? ("*".equals(Term.toString(this.upperTerm)) ? "\\*" : Term.toString(this.upperTerm)) : "*");
        buffer.append(this.includeUpper ? ']' : '}');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.includeLower ? 1231 : 1237);
        result = 31 * result + (this.includeUpper ? 1231 : 1237);
        result = 31 * result + ((this.lowerTerm == null) ? 0 : this.lowerTerm.hashCode());
        result = 31 * result + ((this.upperTerm == null) ? 0 : this.upperTerm.hashCode());
        return result;
    }
    
    @Override
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
        final TermRangeQuery other = (TermRangeQuery)obj;
        if (this.includeLower != other.includeLower) {
            return false;
        }
        if (this.includeUpper != other.includeUpper) {
            return false;
        }
        if (this.lowerTerm == null) {
            if (other.lowerTerm != null) {
                return false;
            }
        }
        else if (!this.lowerTerm.equals(other.lowerTerm)) {
            return false;
        }
        if (this.upperTerm == null) {
            if (other.upperTerm != null) {
                return false;
            }
        }
        else if (!this.upperTerm.equals(other.upperTerm)) {
            return false;
        }
        return true;
    }
}
