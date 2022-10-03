package org.apache.lucene.search;

import org.apache.lucene.util.ToStringUtils;
import java.util.List;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.Automata;
import java.util.ArrayList;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.index.Term;

public class WildcardQuery extends AutomatonQuery
{
    public static final char WILDCARD_STRING = '*';
    public static final char WILDCARD_CHAR = '?';
    public static final char WILDCARD_ESCAPE = '\\';
    
    public WildcardQuery(final Term term) {
        super(term, toAutomaton(term));
    }
    
    public WildcardQuery(final Term term, final int maxDeterminizedStates) {
        super(term, toAutomaton(term), maxDeterminizedStates);
    }
    
    public static Automaton toAutomaton(final Term wildcardquery) {
        final List<Automaton> automata = new ArrayList<Automaton>();
        final String wildcardText = wildcardquery.text();
        int length;
        for (int i = 0; i < wildcardText.length(); i += length) {
            final int c = wildcardText.codePointAt(i);
            length = Character.charCount(c);
            switch (c) {
                case 42: {
                    automata.add(Automata.makeAnyString());
                    continue;
                }
                case 63: {
                    automata.add(Automata.makeAnyChar());
                    continue;
                }
                case 92: {
                    if (i + length < wildcardText.length()) {
                        final int nextChar = wildcardText.codePointAt(i + length);
                        length += Character.charCount(nextChar);
                        automata.add(Automata.makeChar(nextChar));
                        continue;
                    }
                    break;
                }
            }
            automata.add(Automata.makeChar(c));
        }
        return Operations.concatenate(automata);
    }
    
    public Term getTerm() {
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
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}
