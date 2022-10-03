package org.apache.lucene.search;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.util.automaton.RegExp;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.automaton.AutomatonProvider;

public class RegexpQuery extends AutomatonQuery
{
    private static AutomatonProvider defaultProvider;
    
    public RegexpQuery(final Term term) {
        this(term, 65535);
    }
    
    public RegexpQuery(final Term term, final int flags) {
        this(term, flags, RegexpQuery.defaultProvider, 10000);
    }
    
    public RegexpQuery(final Term term, final int flags, final int maxDeterminizedStates) {
        this(term, flags, RegexpQuery.defaultProvider, maxDeterminizedStates);
    }
    
    public RegexpQuery(final Term term, final int flags, final AutomatonProvider provider, final int maxDeterminizedStates) {
        super(term, new RegExp(term.text(), flags).toAutomaton(provider, maxDeterminizedStates), maxDeterminizedStates);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append('/');
        buffer.append(this.term.text());
        buffer.append('/');
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    static {
        RegexpQuery.defaultProvider = new AutomatonProvider() {
            @Override
            public Automaton getAutomaton(final String name) {
                return null;
            }
        };
    }
}
