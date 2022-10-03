package org.apache.lucene.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermContext;
import java.util.HashMap;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;
import java.util.Map;
import java.util.PriorityQueue;
import org.apache.lucene.index.IndexReader;
import java.util.Comparator;

public abstract class TopTermsRewrite<B> extends TermCollectingRewrite<B>
{
    private final int size;
    private static final Comparator<ScoreTerm> scoreTermSortByTermComp;
    
    public TopTermsRewrite(final int size) {
        this.size = size;
    }
    
    public int getSize() {
        return this.size;
    }
    
    protected abstract int getMaxSize();
    
    @Override
    public final Query rewrite(final IndexReader reader, final MultiTermQuery query) throws IOException {
        final int maxSize = Math.min(this.size, this.getMaxSize());
        final PriorityQueue<ScoreTerm> stQueue = new PriorityQueue<ScoreTerm>();
        this.collectTerms(reader, query, new TermCollector() {
            private final MaxNonCompetitiveBoostAttribute maxBoostAtt = this.attributes.addAttribute(MaxNonCompetitiveBoostAttribute.class);
            private final Map<BytesRef, ScoreTerm> visitedTerms = new HashMap<BytesRef, ScoreTerm>();
            private TermsEnum termsEnum;
            private BoostAttribute boostAtt;
            private ScoreTerm st;
            private BytesRefBuilder lastTerm;
            
            @Override
            public void setNextEnum(final TermsEnum termsEnum) {
                this.termsEnum = termsEnum;
                assert this.compareToLastTerm(null);
                if (this.st == null) {
                    this.st = new ScoreTerm(new TermContext(this.topReaderContext));
                }
                this.boostAtt = termsEnum.attributes().addAttribute(BoostAttribute.class);
            }
            
            private boolean compareToLastTerm(final BytesRef t) {
                if (this.lastTerm == null && t != null) {
                    (this.lastTerm = new BytesRefBuilder()).append(t);
                }
                else if (t == null) {
                    this.lastTerm = null;
                }
                else {
                    assert this.lastTerm.get().compareTo(t) < 0 : "lastTerm=" + this.lastTerm + " t=" + t;
                    this.lastTerm.copyBytes(t);
                }
                return true;
            }
            
            @Override
            public boolean collect(final BytesRef bytes) throws IOException {
                final float boost = this.boostAtt.getBoost();
                assert this.compareToLastTerm(bytes);
                if (stQueue.size() == maxSize) {
                    final ScoreTerm t = stQueue.peek();
                    if (boost < t.boost) {
                        return true;
                    }
                    if (boost == t.boost && bytes.compareTo(t.bytes.get()) > 0) {
                        return true;
                    }
                }
                ScoreTerm t = this.visitedTerms.get(bytes);
                final TermState state = this.termsEnum.termState();
                assert state != null;
                if (t != null) {
                    assert t.boost == boost : "boost should be equal in all segment TermsEnums";
                    t.termState.register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                }
                else {
                    this.st.bytes.copyBytes(bytes);
                    this.st.boost = boost;
                    this.visitedTerms.put(this.st.bytes.get(), this.st);
                    assert this.st.termState.docFreq() == 0;
                    this.st.termState.register(state, this.readerContext.ord, this.termsEnum.docFreq(), this.termsEnum.totalTermFreq());
                    stQueue.offer(this.st);
                    if (stQueue.size() > maxSize) {
                        this.st = stQueue.poll();
                        this.visitedTerms.remove(this.st.bytes.get());
                        this.st.termState.clear();
                    }
                    else {
                        this.st = new ScoreTerm(new TermContext(this.topReaderContext));
                    }
                    assert stQueue.size() <= maxSize : "the PQ size must be limited to maxSize";
                    if (stQueue.size() == maxSize) {
                        t = stQueue.peek();
                        this.maxBoostAtt.setMaxNonCompetitiveBoost(t.boost);
                        this.maxBoostAtt.setCompetitiveTerm(t.bytes.get());
                    }
                }
                return true;
            }
        });
        final B b = this.getTopLevelBuilder();
        final ScoreTerm[] scoreTerms = stQueue.toArray(new ScoreTerm[stQueue.size()]);
        ArrayUtil.timSort(scoreTerms, TopTermsRewrite.scoreTermSortByTermComp);
        for (final ScoreTerm st : scoreTerms) {
            final Term term = new Term(query.field, st.bytes.toBytesRef());
            this.addClause(b, term, st.termState.docFreq(), st.boost, st.termState);
        }
        return this.build(b);
    }
    
    @Override
    public int hashCode() {
        return 31 * this.size;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TopTermsRewrite<?> other = (TopTermsRewrite<?>)obj;
        return this.size == other.size;
    }
    
    static {
        scoreTermSortByTermComp = new Comparator<ScoreTerm>() {
            @Override
            public int compare(final ScoreTerm st1, final ScoreTerm st2) {
                return st1.bytes.get().compareTo(st2.bytes.get());
            }
        };
    }
    
    static final class ScoreTerm implements Comparable<ScoreTerm>
    {
        public final BytesRefBuilder bytes;
        public float boost;
        public final TermContext termState;
        
        public ScoreTerm(final TermContext termState) {
            this.bytes = new BytesRefBuilder();
            this.termState = termState;
        }
        
        @Override
        public int compareTo(final ScoreTerm other) {
            if (this.boost == other.boost) {
                return other.bytes.get().compareTo(this.bytes.get());
            }
            return Float.compare(this.boost, other.boost);
        }
    }
}
