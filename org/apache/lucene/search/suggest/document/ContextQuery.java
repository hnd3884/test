package org.apache.lucene.search.suggest.document;

import java.io.IOException;
import org.apache.lucene.util.automaton.Automaton;
import java.util.TreeSet;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.util.Iterator;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.BytesRef;
import java.util.HashMap;
import org.apache.lucene.util.IntsRef;
import java.util.Map;
import org.apache.lucene.util.IntsRefBuilder;

public class ContextQuery extends CompletionQuery
{
    private IntsRefBuilder scratch;
    private Map<IntsRef, ContextMetaData> contexts;
    private boolean matchAllContexts;
    protected CompletionQuery innerQuery;
    
    public ContextQuery(final CompletionQuery query) {
        super(query.getTerm(), query.getFilter());
        this.scratch = new IntsRefBuilder();
        this.matchAllContexts = false;
        if (query instanceof ContextQuery) {
            throw new IllegalArgumentException("'query' parameter must not be of type " + this.getClass().getSimpleName());
        }
        this.innerQuery = query;
        this.contexts = new HashMap<IntsRef, ContextMetaData>();
    }
    
    public void addContext(final CharSequence context) {
        this.addContext(context, 1.0f, true);
    }
    
    public void addContext(final CharSequence context, final float boost) {
        this.addContext(context, boost, true);
    }
    
    public void addContext(final CharSequence context, final float boost, final boolean exact) {
        if (boost < 0.0f) {
            throw new IllegalArgumentException("'boost' must be >= 0");
        }
        for (int i = 0; i < context.length(); ++i) {
            if ('\u001d' == context.charAt(i)) {
                throw new IllegalArgumentException("Illegal value [" + (Object)context + "] UTF-16 codepoint [0x" + Integer.toHexString(context.charAt(i)) + "] at position " + i + " is a reserved character");
            }
        }
        this.contexts.put(IntsRef.deepCopyOf(Util.toIntsRef(new BytesRef(context), this.scratch)), new ContextMetaData(boost, exact));
    }
    
    public void addAllContexts() {
        this.matchAllContexts = true;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        final BytesRefBuilder scratch = new BytesRefBuilder();
        for (final IntsRef context : this.contexts.keySet()) {
            if (buffer.length() != 0) {
                buffer.append(",");
            }
            else {
                buffer.append("contexts");
                buffer.append(":[");
            }
            buffer.append(Util.toBytesRef(context, scratch).utf8ToString());
            final ContextMetaData metaData = this.contexts.get(context);
            if (!metaData.exact) {
                buffer.append("*");
            }
            if (metaData.boost != 0.0f) {
                buffer.append("^");
                buffer.append(Float.toString(metaData.boost));
            }
        }
        if (buffer.length() != 0) {
            buffer.append("]");
            buffer.append(",");
        }
        return buffer.toString() + this.innerQuery.toString(field);
    }
    
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final CompletionWeight innerWeight = (CompletionWeight)this.innerQuery.createWeight(searcher, needsScores);
        final Automaton optionalSepLabel = Operations.optional(Automata.makeChar(31));
        final Automaton prefixAutomaton = Operations.concatenate(optionalSepLabel, innerWeight.getAutomaton());
        Automaton contextsAutomaton = Operations.concatenate(toContextAutomaton(this.contexts, this.matchAllContexts), prefixAutomaton);
        contextsAutomaton = Operations.determinize(contextsAutomaton, 10000);
        final Map<IntsRef, Float> contextMap = new HashMap<IntsRef, Float>(this.contexts.size());
        final TreeSet<Integer> contextLengths = new TreeSet<Integer>();
        for (final Map.Entry<IntsRef, ContextMetaData> entry : this.contexts.entrySet()) {
            final ContextMetaData contextMetaData = entry.getValue();
            contextMap.put(entry.getKey(), contextMetaData.boost);
            contextLengths.add(entry.getKey().length);
        }
        final int[] contextLengthArray = new int[contextLengths.size()];
        final Iterator<Integer> iterator = contextLengths.descendingIterator();
        int i = 0;
        while (iterator.hasNext()) {
            contextLengthArray[i] = iterator.next();
            ++i;
        }
        return new ContextCompletionWeight(this, contextsAutomaton, innerWeight, contextMap, contextLengthArray);
    }
    
    private static Automaton toContextAutomaton(final Map<IntsRef, ContextMetaData> contexts, final boolean matchAllContexts) {
        final Automaton matchAllAutomaton = Operations.repeat(Automata.makeAnyString());
        final Automaton sep = Automata.makeChar(29);
        if (matchAllContexts || contexts.size() == 0) {
            return Operations.concatenate(matchAllAutomaton, sep);
        }
        Automaton contextsAutomaton = null;
        for (final Map.Entry<IntsRef, ContextMetaData> entry : contexts.entrySet()) {
            final ContextMetaData contextMetaData = entry.getValue();
            final IntsRef ref = entry.getKey();
            Automaton contextAutomaton = Automata.makeString(ref.ints, ref.offset, ref.length);
            if (!contextMetaData.exact) {
                contextAutomaton = Operations.concatenate(contextAutomaton, matchAllAutomaton);
            }
            contextAutomaton = Operations.concatenate(contextAutomaton, sep);
            if (contextsAutomaton == null) {
                contextsAutomaton = contextAutomaton;
            }
            else {
                contextsAutomaton = Operations.union(contextsAutomaton, contextAutomaton);
            }
        }
        return contextsAutomaton;
    }
    
    private static class ContextMetaData
    {
        private final float boost;
        private final boolean exact;
        
        private ContextMetaData(final float boost, final boolean exact) {
            this.boost = boost;
            this.exact = exact;
        }
    }
    
    private class ContextCompletionWeight extends CompletionWeight
    {
        private final Map<IntsRef, Float> contextMap;
        private final int[] contextLengths;
        private final CompletionWeight innerWeight;
        private final BytesRefBuilder scratch;
        private float currentBoost;
        private CharSequence currentContext;
        
        public ContextCompletionWeight(final CompletionQuery query, final Automaton automaton, final CompletionWeight innerWeight, final Map<IntsRef, Float> contextMap, final int[] contextLengths) throws IOException {
            super(query, automaton);
            this.scratch = new BytesRefBuilder();
            this.contextMap = contextMap;
            this.contextLengths = contextLengths;
            this.innerWeight = innerWeight;
        }
        
        @Override
        protected void setNextMatch(final IntsRef pathPrefix) {
            final IntsRef ref = pathPrefix.clone();
            for (final int contextLength : this.contextLengths) {
                if (contextLength <= pathPrefix.length) {
                    ref.length = contextLength;
                    if (this.contextMap.containsKey(ref)) {
                        this.currentBoost = this.contextMap.get(ref);
                        ref.length = pathPrefix.length;
                        this.setInnerWeight(ref, contextLength);
                        return;
                    }
                }
            }
            ref.length = pathPrefix.length;
            this.currentBoost = 0.0f;
            this.setInnerWeight(ref, 0);
        }
        
        private void setInnerWeight(final IntsRef ref, final int offset) {
            final IntsRefBuilder refBuilder = new IntsRefBuilder();
            int i = offset;
            while (i < ref.length) {
                if (ref.ints[ref.offset + i] == 29) {
                    if (i > 0) {
                        refBuilder.copyInts(ref.ints, ref.offset, i);
                        this.currentContext = Util.toBytesRef(refBuilder.get(), this.scratch).utf8ToString();
                    }
                    else {
                        this.currentContext = null;
                    }
                    ref.offset = ++i;
                    assert ref.offset < ref.length : "input should not end with the context separator";
                    if (ref.ints[i] == 31) {
                        ++ref.offset;
                        assert ref.offset < ref.length : "input should not end with a context separator followed by SEP_LABEL";
                    }
                    ref.length -= ref.offset;
                    refBuilder.copyInts(ref.ints, ref.offset, ref.length);
                    this.innerWeight.setNextMatch(refBuilder.get());
                }
                else {
                    ++i;
                }
            }
        }
        
        @Override
        protected CharSequence context() {
            return this.currentContext;
        }
        
        @Override
        protected float boost() {
            return this.currentBoost + this.innerWeight.boost();
        }
    }
}
