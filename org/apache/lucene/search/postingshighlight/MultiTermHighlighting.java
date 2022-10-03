package org.apache.lucene.search.postingshighlight;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanQuery;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.automaton.LevenshteinAutomata;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanPositionCheckQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FilteredQuery;
import java.util.Collection;
import java.util.Arrays;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import java.util.ArrayList;
import org.apache.lucene.util.automaton.CharacterRunAutomaton;
import org.apache.lucene.search.Query;

class MultiTermHighlighting
{
    static CharacterRunAutomaton[] extractAutomata(final Query query, final String field) {
        final List<CharacterRunAutomaton> list = new ArrayList<CharacterRunAutomaton>();
        if (query instanceof BooleanQuery) {
            for (final BooleanClause clause : (BooleanQuery)query) {
                if (!clause.isProhibited()) {
                    list.addAll(Arrays.asList(extractAutomata(clause.getQuery(), field)));
                }
            }
        }
        else if (query instanceof FilteredQuery) {
            list.addAll(Arrays.asList(extractAutomata(((FilteredQuery)query).getQuery(), field)));
        }
        else if (query instanceof ConstantScoreQuery) {
            list.addAll(Arrays.asList(extractAutomata(((ConstantScoreQuery)query).getQuery(), field)));
        }
        else if (query instanceof DisjunctionMaxQuery) {
            for (final Query sub : ((DisjunctionMaxQuery)query).getDisjuncts()) {
                list.addAll(Arrays.asList(extractAutomata(sub, field)));
            }
        }
        else if (query instanceof SpanOrQuery) {
            for (final Query sub2 : ((SpanOrQuery)query).getClauses()) {
                list.addAll(Arrays.asList(extractAutomata(sub2, field)));
            }
        }
        else if (query instanceof SpanNearQuery) {
            for (final Query sub2 : ((SpanNearQuery)query).getClauses()) {
                list.addAll(Arrays.asList(extractAutomata(sub2, field)));
            }
        }
        else if (query instanceof SpanNotQuery) {
            list.addAll(Arrays.asList(extractAutomata((Query)((SpanNotQuery)query).getInclude(), field)));
        }
        else if (query instanceof SpanPositionCheckQuery) {
            list.addAll(Arrays.asList(extractAutomata((Query)((SpanPositionCheckQuery)query).getMatch(), field)));
        }
        else if (query instanceof SpanMultiTermQueryWrapper) {
            list.addAll(Arrays.asList(extractAutomata(((SpanMultiTermQueryWrapper)query).getWrappedQuery(), field)));
        }
        else if (query instanceof AutomatonQuery) {
            final AutomatonQuery aq = (AutomatonQuery)query;
            if (aq.getField().equals(field)) {
                list.add(new CharacterRunAutomaton(aq.getAutomaton()) {
                    public String toString() {
                        return aq.toString();
                    }
                });
            }
        }
        else if (query instanceof PrefixQuery) {
            final PrefixQuery pq = (PrefixQuery)query;
            final Term prefix = pq.getPrefix();
            if (prefix.field().equals(field)) {
                list.add(new CharacterRunAutomaton(Operations.concatenate(Automata.makeString(prefix.text()), Automata.makeAnyString())) {
                    public String toString() {
                        return pq.toString();
                    }
                });
            }
        }
        else if (query instanceof FuzzyQuery) {
            final FuzzyQuery fq = (FuzzyQuery)query;
            if (fq.getField().equals(field)) {
                final String utf16 = fq.getTerm().text();
                final int[] termText = new int[utf16.codePointCount(0, utf16.length())];
                int i = 0;
                int j = 0;
                while (i < utf16.length()) {
                    final int cp = termText[j++] = utf16.codePointAt(i);
                    i += Character.charCount(cp);
                }
                final int termLength = termText.length;
                final int prefixLength = Math.min(fq.getPrefixLength(), termLength);
                final String suffix = UnicodeUtil.newString(termText, prefixLength, termText.length - prefixLength);
                final LevenshteinAutomata builder = new LevenshteinAutomata(suffix, fq.getTranspositions());
                final String prefix2 = UnicodeUtil.newString(termText, 0, prefixLength);
                final Automaton automaton = builder.toAutomaton(fq.getMaxEdits(), prefix2);
                list.add(new CharacterRunAutomaton(automaton) {
                    public String toString() {
                        return fq.toString();
                    }
                });
            }
        }
        else if (query instanceof TermRangeQuery) {
            final TermRangeQuery tq = (TermRangeQuery)query;
            if (tq.getField().equals(field)) {
                CharsRef lowerBound;
                if (tq.getLowerTerm() == null) {
                    lowerBound = null;
                }
                else {
                    lowerBound = new CharsRef(tq.getLowerTerm().utf8ToString());
                }
                CharsRef upperBound;
                if (tq.getUpperTerm() == null) {
                    upperBound = null;
                }
                else {
                    upperBound = new CharsRef(tq.getUpperTerm().utf8ToString());
                }
                final boolean includeLower = tq.includesLower();
                final boolean includeUpper = tq.includesUpper();
                final CharsRef scratch = new CharsRef();
                final Comparator<CharsRef> comparator = CharsRef.getUTF16SortedAsUTF8Comparator();
                list.add(new CharacterRunAutomaton(Automata.makeEmpty()) {
                    public boolean run(final char[] s, final int offset, final int length) {
                        scratch.chars = s;
                        scratch.offset = offset;
                        scratch.length = length;
                        if (lowerBound != null) {
                            final int cmp = comparator.compare(scratch, lowerBound);
                            if (cmp < 0 || (!includeLower && cmp == 0)) {
                                return false;
                            }
                        }
                        if (upperBound != null) {
                            final int cmp = comparator.compare(scratch, upperBound);
                            if (cmp > 0 || (!includeUpper && cmp == 0)) {
                                return false;
                            }
                        }
                        return true;
                    }
                    
                    public String toString() {
                        return tq.toString();
                    }
                });
            }
        }
        return list.toArray(new CharacterRunAutomaton[list.size()]);
    }
    
    static PostingsEnum getDocsEnum(final TokenStream ts, final CharacterRunAutomaton[] matchers) throws IOException {
        final CharTermAttribute charTermAtt = (CharTermAttribute)ts.addAttribute((Class)CharTermAttribute.class);
        final OffsetAttribute offsetAtt = (OffsetAttribute)ts.addAttribute((Class)OffsetAttribute.class);
        ts.reset();
        return new PostingsEnum() {
            int currentDoc = -1;
            int currentMatch = -1;
            int currentStartOffset = -1;
            int currentEndOffset = -1;
            TokenStream stream = ts;
            final BytesRef[] matchDescriptions = new BytesRef[matchers.length];
            
            public int nextPosition() throws IOException {
                if (this.stream != null) {
                    while (this.stream.incrementToken()) {
                        for (int i = 0; i < matchers.length; ++i) {
                            if (matchers[i].run(charTermAtt.buffer(), 0, charTermAtt.length())) {
                                this.currentStartOffset = offsetAtt.startOffset();
                                this.currentEndOffset = offsetAtt.endOffset();
                                this.currentMatch = i;
                                return 0;
                            }
                        }
                    }
                    this.stream.end();
                    this.stream.close();
                    this.stream = null;
                }
                final int n = Integer.MAX_VALUE;
                this.currentEndOffset = n;
                this.currentStartOffset = n;
                return Integer.MAX_VALUE;
            }
            
            public int freq() throws IOException {
                return Integer.MAX_VALUE;
            }
            
            public int startOffset() throws IOException {
                assert this.currentStartOffset >= 0;
                return this.currentStartOffset;
            }
            
            public int endOffset() throws IOException {
                assert this.currentEndOffset >= 0;
                return this.currentEndOffset;
            }
            
            public BytesRef getPayload() throws IOException {
                if (this.matchDescriptions[this.currentMatch] == null) {
                    this.matchDescriptions[this.currentMatch] = new BytesRef((CharSequence)matchers[this.currentMatch].toString());
                }
                return this.matchDescriptions[this.currentMatch];
            }
            
            public int docID() {
                return this.currentDoc;
            }
            
            public int nextDoc() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            public int advance(final int target) throws IOException {
                return this.currentDoc = target;
            }
            
            public long cost() {
                return 0L;
            }
        };
    }
}
