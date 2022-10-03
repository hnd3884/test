package org.apache.lucene.search;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Term;

public class PrefixQuery extends AutomatonQuery
{
    public PrefixQuery(final Term prefix) {
        super(prefix, toAutomaton(prefix.bytes()), Integer.MAX_VALUE, true);
        if (prefix == null) {
            throw new NullPointerException("prefix cannot be null");
        }
    }
    
    public static Automaton toAutomaton(final BytesRef prefix) {
        final Automaton automaton = new Automaton();
        int lastState = automaton.createState();
        for (int i = 0; i < prefix.length; ++i) {
            final int state = automaton.createState();
            automaton.addTransition(lastState, state, prefix.bytes[prefix.offset + i] & 0xFF);
            lastState = state;
        }
        automaton.setAccept(lastState, true);
        automaton.addTransition(lastState, lastState, 0, 255);
        automaton.finishState();
        assert automaton.isDeterministic();
        return automaton;
    }
    
    public Term getPrefix() {
        return this.term;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append('*');
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.term.hashCode();
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
        final PrefixQuery other = (PrefixQuery)obj;
        return this.term.equals(other.term);
    }
}
