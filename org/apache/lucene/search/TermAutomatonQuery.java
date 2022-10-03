package org.apache.lucene.search;

import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.PostingsEnum;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.Transition;
import java.util.HashMap;
import org.apache.lucene.util.BytesRef;
import java.util.Map;
import org.apache.lucene.util.automaton.Automaton;

public class TermAutomatonQuery extends Query
{
    private final String field;
    private final Automaton.Builder builder;
    Automaton det;
    private final Map<BytesRef, Integer> termToID;
    private final Map<Integer, BytesRef> idToTerm;
    private int anyTermID;
    
    public TermAutomatonQuery(final String field) {
        this.termToID = new HashMap<BytesRef, Integer>();
        this.idToTerm = new HashMap<Integer, BytesRef>();
        this.anyTermID = -1;
        this.field = field;
        this.builder = new Automaton.Builder();
    }
    
    public int createState() {
        return this.builder.createState();
    }
    
    public void setAccept(final int state, final boolean accept) {
        this.builder.setAccept(state, accept);
    }
    
    public void addTransition(final int source, final int dest, final String term) {
        this.addTransition(source, dest, new BytesRef((CharSequence)term));
    }
    
    public void addTransition(final int source, final int dest, final BytesRef term) {
        if (term == null) {
            throw new NullPointerException("term should not be null");
        }
        this.builder.addTransition(source, dest, this.getTermID(term));
    }
    
    public void addAnyTransition(final int source, final int dest) {
        this.builder.addTransition(source, dest, this.getTermID(null));
    }
    
    public void finish() {
        this.finish(10000);
    }
    
    public void finish(final int maxDeterminizedStates) {
        Automaton automaton = this.builder.finish();
        final Transition t = new Transition();
        if (this.anyTermID != -1) {
            for (int count = automaton.initTransition(0, t), i = 0; i < count; ++i) {
                automaton.getNextTransition(t);
                if (this.anyTermID >= t.min && this.anyTermID <= t.max) {
                    throw new IllegalStateException("automaton cannot lead with an ANY transition");
                }
            }
            final int numStates = automaton.getNumStates();
            for (int j = 0; j < numStates; ++j) {
                for (int count = automaton.initTransition(j, t), k = 0; k < count; ++k) {
                    automaton.getNextTransition(t);
                    if (automaton.isAccept(t.dest) && this.anyTermID >= t.min && this.anyTermID <= t.max) {
                        throw new IllegalStateException("automaton cannot end with an ANY transition");
                    }
                }
            }
            final int termCount = this.termToID.size();
            final Automaton newAutomaton = new Automaton();
            for (int l = 0; l < numStates; ++l) {
                newAutomaton.createState();
                newAutomaton.setAccept(l, automaton.isAccept(l));
            }
            for (int l = 0; l < numStates; ++l) {
                for (int count = automaton.initTransition(l, t), m = 0; m < count; ++m) {
                    automaton.getNextTransition(t);
                    int min;
                    int max;
                    if (t.min <= this.anyTermID && this.anyTermID <= t.max) {
                        min = 0;
                        max = termCount - 1;
                    }
                    else {
                        min = t.min;
                        max = t.max;
                    }
                    newAutomaton.addTransition(t.source, t.dest, min, max);
                }
            }
            newAutomaton.finishState();
            automaton = newAutomaton;
        }
        this.det = Operations.removeDeadStates(Operations.determinize(automaton, maxDeterminizedStates));
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final IndexReaderContext context = searcher.getTopReaderContext();
        final Map<Integer, TermContext> termStates = new HashMap<Integer, TermContext>();
        for (final Map.Entry<BytesRef, Integer> ent : this.termToID.entrySet()) {
            if (ent.getKey() != null) {
                termStates.put(ent.getValue(), TermContext.build(context, new Term(this.field, (BytesRef)ent.getKey())));
            }
        }
        return new TermAutomatonWeight(this.det, searcher, termStates);
    }
    
    public String toString(final String field) {
        final StringBuilder sb = new StringBuilder();
        sb.append("TermAutomatonQuery(field=");
        sb.append(this.field);
        if (this.det != null) {
            sb.append(" numStates=");
            sb.append(this.det.getNumStates());
        }
        sb.append(')');
        return sb.toString();
    }
    
    private int getTermID(BytesRef term) {
        Integer id = this.termToID.get(term);
        if (id == null) {
            id = this.termToID.size();
            if (term != null) {
                term = BytesRef.deepCopyOf(term);
            }
            this.termToID.put(term, id);
            this.idToTerm.put(id, term);
            if (term == null) {
                this.anyTermID = id;
            }
        }
        return id;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof TermAutomatonQuery)) {
            return false;
        }
        final TermAutomatonQuery other = (TermAutomatonQuery)o;
        if (this.det == null) {
            throw new IllegalStateException("please call finish first");
        }
        if (other.det == null) {
            throw new IllegalStateException("please call other.finish first");
        }
        return super.equals(o) && this.termToID.equals(other.termToID) && Operations.sameLanguage(this.det, other.det);
    }
    
    public int hashCode() {
        if (this.det == null) {
            throw new IllegalStateException("please call finish first");
        }
        return super.hashCode() ^ this.termToID.hashCode() + this.det.toDot().hashCode();
    }
    
    public String toDot() {
        final StringBuilder b = new StringBuilder();
        b.append("digraph Automaton {\n");
        b.append("  rankdir = LR\n");
        final int numStates = this.det.getNumStates();
        if (numStates > 0) {
            b.append("  initial [shape=plaintext,label=\"0\"]\n");
            b.append("  initial -> 0\n");
        }
        final Transition t = new Transition();
        for (int state = 0; state < numStates; ++state) {
            b.append("  ");
            b.append(state);
            if (this.det.isAccept(state)) {
                b.append(" [shape=doublecircle,label=\"" + state + "\"]\n");
            }
            else {
                b.append(" [shape=circle,label=\"" + state + "\"]\n");
            }
            for (int numTransitions = this.det.initTransition(state, t), i = 0; i < numTransitions; ++i) {
                this.det.getNextTransition(t);
                assert t.max >= t.min;
                for (int j = t.min; j <= t.max; ++j) {
                    b.append("  ");
                    b.append(state);
                    b.append(" -> ");
                    b.append(t.dest);
                    b.append(" [label=\"");
                    if (j == this.anyTermID) {
                        b.append('*');
                    }
                    else {
                        b.append(this.idToTerm.get(j).utf8ToString());
                    }
                    b.append("\"]\n");
                }
            }
        }
        b.append('}');
        return b.toString();
    }
    
    static class EnumAndScorer
    {
        public final int termID;
        public final PostingsEnum posEnum;
        public int posLeft;
        public int pos;
        
        public EnumAndScorer(final int termID, final PostingsEnum posEnum) {
            this.termID = termID;
            this.posEnum = posEnum;
        }
    }
    
    final class TermAutomatonWeight extends Weight
    {
        private final IndexSearcher searcher;
        final Automaton automaton;
        private final Map<Integer, TermContext> termStates;
        private final Similarity.SimWeight stats;
        private final Similarity similarity;
        
        public TermAutomatonWeight(final Automaton automaton, final IndexSearcher searcher, final Map<Integer, TermContext> termStates) throws IOException {
            super((Query)TermAutomatonQuery.this);
            this.automaton = automaton;
            this.searcher = searcher;
            this.termStates = termStates;
            this.similarity = searcher.getSimilarity(true);
            final List<TermStatistics> allTermStats = new ArrayList<TermStatistics>();
            for (final Map.Entry<Integer, BytesRef> ent : TermAutomatonQuery.this.idToTerm.entrySet()) {
                final Integer termID = ent.getKey();
                if (ent.getValue() != null) {
                    allTermStats.add(searcher.termStatistics(new Term(TermAutomatonQuery.this.field, (BytesRef)ent.getValue()), (TermContext)termStates.get(termID)));
                }
            }
            this.stats = this.similarity.computeWeight(searcher.collectionStatistics(TermAutomatonQuery.this.field), (TermStatistics[])allTermStats.toArray(new TermStatistics[allTermStats.size()]));
        }
        
        public void extractTerms(final Set<Term> terms) {
            for (final BytesRef text : TermAutomatonQuery.this.termToID.keySet()) {
                if (text != null) {
                    terms.add(new Term(TermAutomatonQuery.this.field, text));
                }
            }
        }
        
        public String toString() {
            return "weight(" + TermAutomatonQuery.this + ")";
        }
        
        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }
        
        public void normalize(final float queryNorm, final float boost) {
            this.stats.normalize(queryNorm, boost);
        }
        
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            final EnumAndScorer[] enums = new EnumAndScorer[TermAutomatonQuery.this.idToTerm.size()];
            boolean any = false;
            for (final Map.Entry<Integer, TermContext> ent : this.termStates.entrySet()) {
                final TermContext termContext = ent.getValue();
                assert termContext.topReaderContext == ReaderUtil.getTopLevelContext((IndexReaderContext)context) : "The top-reader used to create Weight (" + termContext.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext((IndexReaderContext)context);
                final BytesRef term = TermAutomatonQuery.this.idToTerm.get(ent.getKey());
                final TermState state = termContext.get(context.ord);
                if (state == null) {
                    continue;
                }
                final TermsEnum termsEnum = context.reader().terms(TermAutomatonQuery.this.field).iterator();
                termsEnum.seekExact(term, state);
                enums[ent.getKey()] = new EnumAndScorer(ent.getKey(), termsEnum.postings((PostingsEnum)null, 24));
                any = true;
            }
            if (any) {
                return new TermAutomatonScorer(this, enums, TermAutomatonQuery.this.anyTermID, TermAutomatonQuery.this.idToTerm, this.similarity.simScorer(this.stats, context));
            }
            return null;
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            return null;
        }
    }
}
