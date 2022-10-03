package org.apache.lucene.search.suggest.document;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.FiniteStringsIterator;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.util.automaton.Automaton;
import java.util.Set;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.UTF32ToUTF8;
import org.apache.lucene.util.IntsRef;
import java.util.HashSet;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.suggest.BitsProducer;
import org.apache.lucene.index.Term;
import org.apache.lucene.analysis.Analyzer;

public class FuzzyCompletionQuery extends PrefixCompletionQuery
{
    public static final boolean DEFAULT_UNICODE_AWARE = false;
    public static final int DEFAULT_MIN_FUZZY_LENGTH = 3;
    public static final int DEFAULT_NON_FUZZY_PREFIX = 1;
    public static final int DEFAULT_MAX_EDITS = 1;
    public static final boolean DEFAULT_TRANSPOSITIONS = true;
    private final int maxEdits;
    private final boolean transpositions;
    private final int nonFuzzyPrefix;
    private final int minFuzzyLength;
    private final boolean unicodeAware;
    private final int maxDeterminizedStates;
    
    public FuzzyCompletionQuery(final Analyzer analyzer, final Term term) {
        this(analyzer, term, null);
    }
    
    public FuzzyCompletionQuery(final Analyzer analyzer, final Term term, final BitsProducer filter) {
        this(analyzer, term, filter, 1, true, 1, 3, false, 10000);
    }
    
    public FuzzyCompletionQuery(final Analyzer analyzer, final Term term, final BitsProducer filter, final int maxEdits, final boolean transpositions, final int nonFuzzyPrefix, final int minFuzzyLength, final boolean unicodeAware, final int maxDeterminizedStates) {
        super(analyzer, term, filter);
        this.maxEdits = maxEdits;
        this.transpositions = transpositions;
        this.nonFuzzyPrefix = nonFuzzyPrefix;
        this.minFuzzyLength = minFuzzyLength;
        this.unicodeAware = unicodeAware;
        this.maxDeterminizedStates = maxDeterminizedStates;
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final CompletionTokenStream stream = (CompletionTokenStream)this.analyzer.tokenStream(this.getField(), this.getTerm().text());
        final Set<IntsRef> refs = new HashSet<IntsRef>();
        Automaton automaton = this.toLevenshteinAutomata(stream.toAutomaton(this.unicodeAware), refs);
        if (this.unicodeAware) {
            Automaton utf8automaton = new UTF32ToUTF8().convert(automaton);
            utf8automaton = (automaton = Operations.determinize(utf8automaton, this.maxDeterminizedStates));
        }
        return new FuzzyCompletionWeight(this, automaton, refs);
    }
    
    private Automaton toLevenshteinAutomata(final Automaton automaton, final Set<IntsRef> refs) {
        final List<Automaton> subs = new ArrayList<Automaton>();
        final FiniteStringsIterator finiteStrings = new FiniteStringsIterator(automaton);
        IntsRef string;
        while ((string = finiteStrings.next()) != null) {
            refs.add(IntsRef.deepCopyOf(string));
            if (string.length <= this.nonFuzzyPrefix || string.length < this.minFuzzyLength) {
                subs.add(Automata.makeString(string.ints, string.offset, string.length));
            }
            else {
                final int[] ints = new int[string.length - this.nonFuzzyPrefix];
                System.arraycopy(string.ints, string.offset + this.nonFuzzyPrefix, ints, 0, ints.length);
                final LevenshteinAutomata lev = new LevenshteinAutomata(ints, this.unicodeAware ? 1114111 : 255, this.transpositions);
                subs.add(lev.toAutomaton(this.maxEdits, UnicodeUtil.newString(string.ints, string.offset, this.nonFuzzyPrefix)));
            }
        }
        if (subs.isEmpty()) {
            return Automata.makeEmpty();
        }
        if (subs.size() == 1) {
            return subs.get(0);
        }
        final Automaton a = Operations.union((Collection)subs);
        return Operations.determinize(a, this.maxDeterminizedStates);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (!this.getField().equals(field)) {
            buffer.append(this.getField());
            buffer.append(":");
        }
        buffer.append(this.getTerm().text());
        buffer.append('*');
        buffer.append('~');
        buffer.append(Integer.toString(this.maxEdits));
        if (this.getFilter() != null) {
            buffer.append(",");
            buffer.append("filter");
            buffer.append(this.getFilter().toString());
        }
        return buffer.toString();
    }
    
    private static class FuzzyCompletionWeight extends CompletionWeight
    {
        private final Set<IntsRef> refs;
        int currentBoost;
        
        public FuzzyCompletionWeight(final CompletionQuery query, final Automaton automaton, final Set<IntsRef> refs) throws IOException {
            super(query, automaton);
            this.currentBoost = 0;
            this.refs = refs;
        }
        
        @Override
        protected void setNextMatch(final IntsRef pathPrefix) {
            int maxCount = 0;
            for (final IntsRef ref : this.refs) {
                final int minLength = Math.min(ref.length, pathPrefix.length);
                int count = 0;
                for (int i = 0; i < minLength && ref.ints[i + ref.offset] == pathPrefix.ints[i + pathPrefix.offset]; ++i) {
                    ++count;
                }
                maxCount = Math.max(maxCount, count);
            }
            this.currentBoost = maxCount;
        }
        
        @Override
        protected float boost() {
            return (float)this.currentBoost;
        }
    }
}
