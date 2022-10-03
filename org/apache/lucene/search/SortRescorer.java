package org.apache.lucene.search;

import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Arrays;
import java.util.Comparator;

public class SortRescorer extends Rescorer
{
    private final Sort sort;
    
    public SortRescorer(final Sort sort) {
        this.sort = sort;
    }
    
    @Override
    public TopDocs rescore(final IndexSearcher searcher, final TopDocs firstPassTopDocs, final int topN) throws IOException {
        final ScoreDoc[] hits = firstPassTopDocs.scoreDocs.clone();
        Arrays.sort(hits, new Comparator<ScoreDoc>() {
            @Override
            public int compare(final ScoreDoc a, final ScoreDoc b) {
                return a.doc - b.doc;
            }
        });
        final List<LeafReaderContext> leaves = searcher.getIndexReader().leaves();
        final TopFieldCollector collector = TopFieldCollector.create(this.sort, topN, true, true, true);
        int hitUpto = 0;
        int readerUpto = -1;
        int endDoc = 0;
        int docBase = 0;
        LeafCollector leafCollector = null;
        final FakeScorer fakeScorer = new FakeScorer();
        while (hitUpto < hits.length) {
            final ScoreDoc hit = hits[hitUpto];
            int docID;
            LeafReaderContext readerContext;
            for (docID = hit.doc, readerContext = null; docID >= endDoc; endDoc = readerContext.docBase + readerContext.reader().maxDoc()) {
                ++readerUpto;
                readerContext = leaves.get(readerUpto);
            }
            if (readerContext != null) {
                leafCollector = collector.getLeafCollector(readerContext);
                leafCollector.setScorer(fakeScorer);
                docBase = readerContext.docBase;
            }
            fakeScorer.score = hit.score;
            leafCollector.collect(fakeScorer.doc = docID - docBase);
            ++hitUpto;
        }
        return collector.topDocs();
    }
    
    @Override
    public Explanation explain(final IndexSearcher searcher, final Explanation firstPassExplanation, final int docID) throws IOException {
        final TopDocs oneHit = new TopDocs(1, new ScoreDoc[] { new ScoreDoc(docID, firstPassExplanation.getValue()) });
        final TopDocs hits = this.rescore(searcher, oneHit, 1);
        assert hits.totalHits == 1;
        final List<Explanation> subs = new ArrayList<Explanation>();
        final Explanation first = Explanation.match(firstPassExplanation.getValue(), "first pass score", firstPassExplanation);
        subs.add(first);
        final FieldDoc fieldDoc = (FieldDoc)hits.scoreDocs[0];
        final SortField[] sortFields = this.sort.getSort();
        for (int i = 0; i < sortFields.length; ++i) {
            subs.add(Explanation.match(0.0f, "sort field " + sortFields[i].toString() + " value=" + fieldDoc.fields[i], new Explanation[0]));
        }
        return Explanation.match(0.0f, "sort field values for sort=" + this.sort.toString(), subs);
    }
}
