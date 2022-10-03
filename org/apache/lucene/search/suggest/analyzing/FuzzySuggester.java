package org.apache.lucene.search.suggest.analyzing;

import org.apache.lucene.util.IntsRef;
import java.util.Collection;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.FiniteStringsIterator;
import java.util.ArrayList;
import org.apache.lucene.analysis.TokenStreamToAutomaton;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.UTF32ToUTF8;
import java.io.IOException;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;

public final class FuzzySuggester extends AnalyzingSuggester
{
    private final int maxEdits;
    private final boolean transpositions;
    private final int nonFuzzyPrefix;
    private final int minFuzzyLength;
    private final boolean unicodeAware;
    public static final boolean DEFAULT_UNICODE_AWARE = false;
    public static final int DEFAULT_MIN_FUZZY_LENGTH = 3;
    public static final int DEFAULT_NON_FUZZY_PREFIX = 1;
    public static final int DEFAULT_MAX_EDITS = 1;
    public static final boolean DEFAULT_TRANSPOSITIONS = true;
    
    public FuzzySuggester(final Analyzer analyzer) {
        this(analyzer, analyzer);
    }
    
    public FuzzySuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer) {
        this(indexAnalyzer, queryAnalyzer, 3, 256, -1, true, 1, true, 1, 3, false);
    }
    
    public FuzzySuggester(final Analyzer indexAnalyzer, final Analyzer queryAnalyzer, final int options, final int maxSurfaceFormsPerAnalyzedForm, final int maxGraphExpansions, final boolean preservePositionIncrements, final int maxEdits, final boolean transpositions, final int nonFuzzyPrefix, final int minFuzzyLength, final boolean unicodeAware) {
        super(indexAnalyzer, queryAnalyzer, options, maxSurfaceFormsPerAnalyzedForm, maxGraphExpansions, preservePositionIncrements);
        if (maxEdits < 0 || maxEdits > 2) {
            throw new IllegalArgumentException("maxEdits must be between 0 and 2");
        }
        if (nonFuzzyPrefix < 0) {
            throw new IllegalArgumentException("nonFuzzyPrefix must not be >= 0 (got " + nonFuzzyPrefix + ")");
        }
        if (minFuzzyLength < 0) {
            throw new IllegalArgumentException("minFuzzyLength must not be >= 0 (got " + minFuzzyLength + ")");
        }
        this.maxEdits = maxEdits;
        this.transpositions = transpositions;
        this.nonFuzzyPrefix = nonFuzzyPrefix;
        this.minFuzzyLength = minFuzzyLength;
        this.unicodeAware = unicodeAware;
    }
    
    @Override
    protected List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> getFullPrefixPaths(final List<FSTUtil.Path<PairOutputs.Pair<Long, BytesRef>>> prefixPaths, final Automaton lookupAutomaton, final FST<PairOutputs.Pair<Long, BytesRef>> fst) throws IOException {
        final Automaton levA = this.convertAutomaton(this.toLevenshteinAutomata(lookupAutomaton));
        return FSTUtil.intersectPrefixPaths(levA, fst);
    }
    
    @Override
    protected Automaton convertAutomaton(final Automaton a) {
        if (this.unicodeAware) {
            Automaton utf8automaton = new UTF32ToUTF8().convert(a);
            utf8automaton = Operations.determinize(utf8automaton, 10000);
            return utf8automaton;
        }
        return a;
    }
    
    @Override
    TokenStreamToAutomaton getTokenStreamToAutomaton() {
        final TokenStreamToAutomaton tsta = super.getTokenStreamToAutomaton();
        tsta.setUnicodeArcs(this.unicodeAware);
        return tsta;
    }
    
    Automaton toLevenshteinAutomata(final Automaton automaton) {
        final List<Automaton> subs = new ArrayList<Automaton>();
        final FiniteStringsIterator finiteStrings = new FiniteStringsIterator(automaton);
        IntsRef string;
        while ((string = finiteStrings.next()) != null) {
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
        return Operations.determinize(a, 10000);
    }
}
