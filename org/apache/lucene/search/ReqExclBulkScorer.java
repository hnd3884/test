package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.Bits;

final class ReqExclBulkScorer extends BulkScorer
{
    private final BulkScorer req;
    private final DocIdSetIterator excl;
    
    ReqExclBulkScorer(final BulkScorer req, final DocIdSetIterator excl) {
        this.req = req;
        this.excl = excl;
    }
    
    @Override
    public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
        int upTo = min;
        int exclDoc = this.excl.docID();
        while (upTo < max) {
            if (exclDoc < upTo) {
                exclDoc = this.excl.advance(upTo);
            }
            if (exclDoc == upTo) {
                ++upTo;
                exclDoc = this.excl.nextDoc();
            }
            else {
                upTo = this.req.score(collector, acceptDocs, upTo, Math.min(exclDoc, max));
            }
        }
        if (upTo == max) {
            upTo = this.req.score(collector, acceptDocs, upTo, upTo);
        }
        return upTo;
    }
    
    @Override
    public long cost() {
        return this.req.cost();
    }
}
