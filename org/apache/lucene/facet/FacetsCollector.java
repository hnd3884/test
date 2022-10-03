package org.apache.lucene.facet;

import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.MultiCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.search.DocIdSet;
import java.io.IOException;
import org.apache.lucene.util.FixedBitSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.SimpleCollector;

public class FacetsCollector extends SimpleCollector implements Collector
{
    private LeafReaderContext context;
    private Scorer scorer;
    private int totalHits;
    private float[] scores;
    private final boolean keepScores;
    private final List<MatchingDocs> matchingDocs;
    private Docs docs;
    
    public FacetsCollector() {
        this(false);
    }
    
    public FacetsCollector(final boolean keepScores) {
        this.matchingDocs = new ArrayList<MatchingDocs>();
        this.keepScores = keepScores;
    }
    
    protected Docs createDocs(final int maxDoc) {
        return new Docs() {
            private final FixedBitSet bits = new FixedBitSet(maxDoc);
            
            @Override
            public void addDoc(final int docId) throws IOException {
                this.bits.set(docId);
            }
            
            @Override
            public DocIdSet getDocIdSet() {
                return (DocIdSet)new BitDocIdSet((BitSet)this.bits);
            }
        };
    }
    
    public final boolean getKeepScores() {
        return this.keepScores;
    }
    
    public List<MatchingDocs> getMatchingDocs() {
        if (this.docs != null) {
            this.matchingDocs.add(new MatchingDocs(this.context, this.docs.getDocIdSet(), this.totalHits, this.scores));
            this.docs = null;
            this.scores = null;
            this.context = null;
        }
        return this.matchingDocs;
    }
    
    public final void collect(final int doc) throws IOException {
        this.docs.addDoc(doc);
        if (this.keepScores) {
            if (this.totalHits >= this.scores.length) {
                final float[] newScores = new float[ArrayUtil.oversize(this.totalHits + 1, 4)];
                System.arraycopy(this.scores, 0, newScores, 0, this.totalHits);
                this.scores = newScores;
            }
            this.scores[this.totalHits] = this.scorer.score();
        }
        ++this.totalHits;
    }
    
    public boolean needsScores() {
        return true;
    }
    
    public final void setScorer(final Scorer scorer) throws IOException {
        this.scorer = scorer;
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        if (this.docs != null) {
            this.matchingDocs.add(new MatchingDocs(this.context, this.docs.getDocIdSet(), this.totalHits, this.scores));
        }
        this.docs = this.createDocs(context.reader().maxDoc());
        this.totalHits = 0;
        if (this.keepScores) {
            this.scores = new float[64];
        }
        this.context = context;
    }
    
    public static TopDocs search(final IndexSearcher searcher, final Query q, final int n, final Collector fc) throws IOException {
        return doSearch(searcher, null, q, n, null, false, false, fc);
    }
    
    public static TopFieldDocs search(final IndexSearcher searcher, final Query q, final int n, final Sort sort, final Collector fc) throws IOException {
        if (sort == null) {
            throw new IllegalArgumentException("sort must not be null");
        }
        return (TopFieldDocs)doSearch(searcher, null, q, n, sort, false, false, fc);
    }
    
    public static TopFieldDocs search(final IndexSearcher searcher, final Query q, final int n, final Sort sort, final boolean doDocScores, final boolean doMaxScore, final Collector fc) throws IOException {
        if (sort == null) {
            throw new IllegalArgumentException("sort must not be null");
        }
        return (TopFieldDocs)doSearch(searcher, null, q, n, sort, doDocScores, doMaxScore, fc);
    }
    
    public static TopDocs searchAfter(final IndexSearcher searcher, final ScoreDoc after, final Query q, final int n, final Collector fc) throws IOException {
        return doSearch(searcher, after, q, n, null, false, false, fc);
    }
    
    public static TopDocs searchAfter(final IndexSearcher searcher, final ScoreDoc after, final Query q, final int n, final Sort sort, final Collector fc) throws IOException {
        if (sort == null) {
            throw new IllegalArgumentException("sort must not be null");
        }
        return doSearch(searcher, after, q, n, sort, false, false, fc);
    }
    
    public static TopDocs searchAfter(final IndexSearcher searcher, final ScoreDoc after, final Query q, final int n, final Sort sort, final boolean doDocScores, final boolean doMaxScore, final Collector fc) throws IOException {
        if (sort == null) {
            throw new IllegalArgumentException("sort must not be null");
        }
        return doSearch(searcher, after, q, n, sort, doDocScores, doMaxScore, fc);
    }
    
    private static TopDocs doSearch(final IndexSearcher searcher, final ScoreDoc after, final Query q, int n, final Sort sort, final boolean doDocScores, final boolean doMaxScore, final Collector fc) throws IOException {
        int limit = searcher.getIndexReader().maxDoc();
        if (limit == 0) {
            limit = 1;
        }
        n = Math.min(n, limit);
        if (after != null && after.doc >= limit) {
            throw new IllegalArgumentException("after.doc exceeds the number of documents in the reader: after.doc=" + after.doc + " limit=" + limit);
        }
        TopDocsCollector<?> hitsCollector;
        if (sort != null) {
            if (after != null && !(after instanceof FieldDoc)) {
                throw new IllegalArgumentException("after must be a FieldDoc; got " + after);
            }
            final boolean fillFields = true;
            hitsCollector = (TopDocsCollector<?>)TopFieldCollector.create(sort, n, (FieldDoc)after, fillFields, doDocScores, doMaxScore);
        }
        else {
            hitsCollector = (TopDocsCollector<?>)TopScoreDocCollector.create(n, after);
        }
        searcher.search(q, MultiCollector.wrap(new Collector[] { (Collector)hitsCollector, fc }));
        return hitsCollector.topDocs();
    }
    
    protected abstract static class Docs
    {
        public Docs() {
        }
        
        public abstract void addDoc(final int p0) throws IOException;
        
        public abstract DocIdSet getDocIdSet();
    }
    
    public static final class MatchingDocs
    {
        public final LeafReaderContext context;
        public final DocIdSet bits;
        public final float[] scores;
        public final int totalHits;
        
        public MatchingDocs(final LeafReaderContext context, final DocIdSet bits, final int totalHits, final float[] scores) {
            this.context = context;
            this.bits = bits;
            this.scores = scores;
            this.totalHits = totalHits;
        }
    }
}
