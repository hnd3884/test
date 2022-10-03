package org.apache.lucene.search;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Collection;
import java.util.Set;
import org.apache.lucene.index.IndexReaderContext;
import java.util.HashMap;
import org.apache.lucene.index.TermContext;
import java.util.Map;
import org.apache.lucene.search.similarities.Similarity;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.lucene.index.Term;
import java.util.ArrayList;

public class MultiPhraseQuery extends Query
{
    private String field;
    private final ArrayList<Term[]> termArrays;
    private final ArrayList<Integer> positions;
    private int slop;
    
    public MultiPhraseQuery() {
        this.termArrays = new ArrayList<Term[]>();
        this.positions = new ArrayList<Integer>();
        this.slop = 0;
    }
    
    public void setSlop(final int s) {
        if (s < 0) {
            throw new IllegalArgumentException("slop value cannot be negative");
        }
        this.slop = s;
    }
    
    public int getSlop() {
        return this.slop;
    }
    
    public void add(final Term term) {
        this.add(new Term[] { term });
    }
    
    public void add(final Term[] terms) {
        int position = 0;
        if (this.positions.size() > 0) {
            position = this.positions.get(this.positions.size() - 1) + 1;
        }
        this.add(terms, position);
    }
    
    public void add(final Term[] terms, final int position) {
        Objects.requireNonNull(terms, "Term array must not be null");
        if (this.termArrays.size() == 0) {
            this.field = terms[0].field();
        }
        for (final Term term : terms) {
            if (!term.field().equals(this.field)) {
                throw new IllegalArgumentException("All phrase terms must be in the same field (" + this.field + "): " + term);
            }
        }
        this.termArrays.add(terms);
        this.positions.add(position);
    }
    
    public List<Term[]> getTermArrays() {
        return Collections.unmodifiableList((List<? extends Term[]>)this.termArrays);
    }
    
    public int[] getPositions() {
        final int[] result = new int[this.positions.size()];
        for (int i = 0; i < this.positions.size(); ++i) {
            result[i] = this.positions.get(i);
        }
        return result;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        if (this.termArrays.isEmpty()) {
            return new MatchNoDocsQuery();
        }
        if (this.termArrays.size() == 1) {
            final Term[] terms = this.termArrays.get(0);
            final BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.setDisableCoord(true);
            for (final Term term : terms) {
                builder.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
            }
            return builder.build();
        }
        return super.rewrite(reader);
    }
    
    @Override
    public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        return new MultiPhraseWeight(searcher, needsScores);
    }
    
    @Override
    public final String toString(final String f) {
        final StringBuilder buffer = new StringBuilder();
        if (this.field == null || !this.field.equals(f)) {
            buffer.append(this.field);
            buffer.append(":");
        }
        buffer.append("\"");
        int k = 0;
        final Iterator<Term[]> i = this.termArrays.iterator();
        int lastPos = -1;
        boolean first = true;
        while (i.hasNext()) {
            final Term[] terms = i.next();
            final int position = this.positions.get(k);
            if (first) {
                first = false;
            }
            else {
                buffer.append(" ");
                for (int j = 1; j < position - lastPos; ++j) {
                    buffer.append("? ");
                }
            }
            if (terms.length > 1) {
                buffer.append("(");
                for (int j = 0; j < terms.length; ++j) {
                    buffer.append(terms[j].text());
                    if (j < terms.length - 1) {
                        buffer.append(" ");
                    }
                }
                buffer.append(")");
            }
            else {
                buffer.append(terms[0].text());
            }
            lastPos = position;
            ++k;
        }
        buffer.append("\"");
        if (this.slop != 0) {
            buffer.append("~");
            buffer.append(this.slop);
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MultiPhraseQuery)) {
            return false;
        }
        final MultiPhraseQuery other = (MultiPhraseQuery)o;
        return super.equals(o) && this.slop == other.slop && this.termArraysEquals(this.termArrays, other.termArrays) && this.positions.equals(other.positions);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.slop ^ this.termArraysHashCode() ^ this.positions.hashCode();
    }
    
    private int termArraysHashCode() {
        int hashCode = 1;
        for (final Term[] termArray : this.termArrays) {
            hashCode = 31 * hashCode + ((termArray == null) ? 0 : Arrays.hashCode(termArray));
        }
        return hashCode;
    }
    
    private boolean termArraysEquals(final List<Term[]> termArrays1, final List<Term[]> termArrays2) {
        if (termArrays1.size() != termArrays2.size()) {
            return false;
        }
        final ListIterator<Term[]> iterator1 = termArrays1.listIterator();
        final ListIterator<Term[]> iterator2 = termArrays2.listIterator();
        while (iterator1.hasNext()) {
            final Term[] termArray1 = iterator1.next();
            final Term[] termArray2 = iterator2.next();
            if (termArray1 == null) {
                if (termArray2 == null) {
                    continue;
                }
                return false;
            }
            else {
                if (!Arrays.equals(termArray1, termArray2)) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    private class MultiPhraseWeight extends Weight
    {
        private final Similarity similarity;
        private final Similarity.SimWeight stats;
        private final Map<Term, TermContext> termContexts;
        private final boolean needsScores;
        
        public MultiPhraseWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            super(MultiPhraseQuery.this);
            this.termContexts = new HashMap<Term, TermContext>();
            this.needsScores = needsScores;
            this.similarity = searcher.getSimilarity(needsScores);
            final IndexReaderContext context = searcher.getTopReaderContext();
            final ArrayList<TermStatistics> allTermStats = new ArrayList<TermStatistics>();
            for (final Term[] arr$ : MultiPhraseQuery.this.termArrays) {
                final Term[] terms = arr$;
                for (final Term term : arr$) {
                    TermContext termContext = this.termContexts.get(term);
                    if (termContext == null) {
                        termContext = TermContext.build(context, term);
                        this.termContexts.put(term, termContext);
                    }
                    allTermStats.add(searcher.termStatistics(term, termContext));
                }
            }
            this.stats = this.similarity.computeWeight(searcher.collectionStatistics(MultiPhraseQuery.this.field), (TermStatistics[])allTermStats.toArray(new TermStatistics[allTermStats.size()]));
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            for (final Term[] arr : MultiPhraseQuery.this.termArrays) {
                Collections.addAll(terms, arr);
            }
        }
        
        @Override
        public float getValueForNormalization() {
            return this.stats.getValueForNormalization();
        }
        
        @Override
        public void normalize(final float queryNorm, final float boost) {
            this.stats.normalize(queryNorm, boost);
        }
        
        @Override
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            assert !MultiPhraseQuery.this.termArrays.isEmpty();
            final LeafReader reader = context.reader();
            final PhraseQuery.PostingsAndFreq[] postingsFreqs = new PhraseQuery.PostingsAndFreq[MultiPhraseQuery.this.termArrays.size()];
            final Terms fieldTerms = reader.terms(MultiPhraseQuery.this.field);
            if (fieldTerms == null) {
                return null;
            }
            if (!fieldTerms.hasPositions()) {
                throw new IllegalStateException("field \"" + MultiPhraseQuery.this.field + "\" was indexed without position data;" + " cannot run MultiPhraseQuery (phrase=" + this.getQuery() + ")");
            }
            final TermsEnum termsEnum = fieldTerms.iterator();
            float totalMatchCost = 0.0f;
            for (int pos = 0; pos < postingsFreqs.length; ++pos) {
                final Term[] terms = MultiPhraseQuery.this.termArrays.get(pos);
                final List<PostingsEnum> postings = new ArrayList<PostingsEnum>();
                for (final Term term : terms) {
                    final TermState termState = this.termContexts.get(term).get(context.ord);
                    if (termState != null) {
                        termsEnum.seekExact(term.bytes(), termState);
                        postings.add(termsEnum.postings(null, 24));
                        totalMatchCost += PhraseQuery.termPositionsCost(termsEnum);
                    }
                }
                if (postings.isEmpty()) {
                    return null;
                }
                PostingsEnum postingsEnum;
                if (postings.size() == 1) {
                    postingsEnum = postings.get(0);
                }
                else {
                    postingsEnum = new UnionPostingsEnum(postings);
                }
                postingsFreqs[pos] = new PhraseQuery.PostingsAndFreq(postingsEnum, MultiPhraseQuery.this.positions.get(pos), terms);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                ArrayUtil.timSort(postingsFreqs);
            }
            if (MultiPhraseQuery.this.slop == 0) {
                return new ExactPhraseScorer(this, postingsFreqs, this.similarity.simScorer(this.stats, context), this.needsScores, totalMatchCost);
            }
            return new SloppyPhraseScorer(this, postingsFreqs, MultiPhraseQuery.this.slop, this.similarity.simScorer(this.stats, context), this.needsScores, totalMatchCost);
        }
        
        @Override
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final Scorer scorer = this.scorer(context);
            if (scorer != null) {
                final int newDoc = scorer.iterator().advance(doc);
                if (newDoc == doc) {
                    final float freq = (MultiPhraseQuery.this.slop == 0) ? ((float)scorer.freq()) : ((SloppyPhraseScorer)scorer).sloppyFreq();
                    final Similarity.SimScorer docScorer = this.similarity.simScorer(this.stats, context);
                    final Explanation freqExplanation = Explanation.match(freq, "phraseFreq=" + freq, new Explanation[0]);
                    final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    return Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", scoreExplanation);
                }
            }
            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
    }
    
    static class UnionPostingsEnum extends PostingsEnum
    {
        final DocsQueue docsQueue;
        final long cost;
        final PositionsQueue posQueue;
        int posQueueDoc;
        final PostingsEnum[] subs;
        
        UnionPostingsEnum(final Collection<PostingsEnum> subs) {
            this.posQueue = new PositionsQueue();
            this.posQueueDoc = -2;
            this.docsQueue = new DocsQueue(subs.size());
            long cost = 0L;
            for (final PostingsEnum sub : subs) {
                this.docsQueue.add(sub);
                cost += sub.cost();
            }
            this.cost = cost;
            this.subs = subs.toArray(new PostingsEnum[subs.size()]);
        }
        
        @Override
        public int freq() throws IOException {
            final int doc = this.docID();
            if (doc != this.posQueueDoc) {
                this.posQueue.clear();
                for (final PostingsEnum sub : this.subs) {
                    if (sub.docID() == doc) {
                        for (int freq = sub.freq(), i = 0; i < freq; ++i) {
                            this.posQueue.add(sub.nextPosition());
                        }
                    }
                }
                this.posQueue.sort();
                this.posQueueDoc = doc;
            }
            return this.posQueue.size();
        }
        
        @Override
        public int nextPosition() throws IOException {
            return this.posQueue.next();
        }
        
        @Override
        public int docID() {
            return this.docsQueue.top().docID();
        }
        
        @Override
        public int nextDoc() throws IOException {
            PostingsEnum top = this.docsQueue.top();
            final int doc = top.docID();
            do {
                top.nextDoc();
                top = this.docsQueue.updateTop();
            } while (top.docID() == doc);
            return top.docID();
        }
        
        @Override
        public int advance(final int target) throws IOException {
            PostingsEnum top = this.docsQueue.top();
            do {
                top.advance(target);
                top = this.docsQueue.updateTop();
            } while (top.docID() < target);
            return top.docID();
        }
        
        @Override
        public long cost() {
            return this.cost;
        }
        
        @Override
        public int startOffset() throws IOException {
            return -1;
        }
        
        @Override
        public int endOffset() throws IOException {
            return -1;
        }
        
        @Override
        public BytesRef getPayload() throws IOException {
            return null;
        }
        
        static class DocsQueue extends PriorityQueue<PostingsEnum>
        {
            DocsQueue(final int size) {
                super(size);
            }
            
            public final boolean lessThan(final PostingsEnum a, final PostingsEnum b) {
                return a.docID() < b.docID();
            }
        }
        
        static class PositionsQueue
        {
            private int arraySize;
            private int index;
            private int size;
            private int[] array;
            
            PositionsQueue() {
                this.arraySize = 16;
                this.index = 0;
                this.size = 0;
                this.array = new int[this.arraySize];
            }
            
            void add(final int i) {
                if (this.size == this.arraySize) {
                    this.growArray();
                }
                this.array[this.size++] = i;
            }
            
            int next() {
                return this.array[this.index++];
            }
            
            void sort() {
                Arrays.sort(this.array, this.index, this.size);
            }
            
            void clear() {
                this.index = 0;
                this.size = 0;
            }
            
            int size() {
                return this.size;
            }
            
            private void growArray() {
                final int[] newArray = new int[this.arraySize * 2];
                System.arraycopy(this.array, 0, newArray, 0, this.arraySize);
                this.array = newArray;
                this.arraySize *= 2;
            }
        }
    }
}
