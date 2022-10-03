package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.Automaton;

public class AutomatonQuery extends MultiTermQuery
{
    protected final Automaton automaton;
    protected final CompiledAutomaton compiled;
    protected final Term term;
    
    public AutomatonQuery(final Term term, final Automaton automaton) {
        this(term, automaton, 10000);
    }
    
    public AutomatonQuery(final Term term, final Automaton automaton, final int maxDeterminizedStates) {
        this(term, automaton, maxDeterminizedStates, false);
    }
    
    public AutomatonQuery(final Term term, final Automaton automaton, final int maxDeterminizedStates, final boolean isBinary) {
        super(term.field());
        this.term = term;
        this.automaton = automaton;
        this.compiled = new CompiledAutomaton(automaton, null, true, maxDeterminizedStates, isBinary);
    }
    
    @Override
    protected TermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        return this.compiled.getTermsEnum(terms);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.compiled.hashCode();
        result = 31 * result + ((this.term == null) ? 0 : this.term.hashCode());
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
        final AutomatonQuery other = (AutomatonQuery)obj;
        if (!this.compiled.equals(other.compiled)) {
            return false;
        }
        if (this.term == null) {
            if (other.term != null) {
                return false;
            }
        }
        else if (!this.term.equals(other.term)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.getClass().getSimpleName());
        buffer.append(" {");
        buffer.append('\n');
        buffer.append(this.automaton.toString());
        buffer.append("}");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    public Automaton getAutomaton() {
        return this.automaton;
    }
}
