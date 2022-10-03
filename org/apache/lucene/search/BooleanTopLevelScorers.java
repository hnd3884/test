package org.apache.lucene.search;

import java.util.Arrays;
import org.apache.lucene.util.Bits;
import java.util.Collections;
import java.util.Collection;
import java.io.IOException;

class BooleanTopLevelScorers
{
    static class BoostedScorer extends FilterScorer
    {
        final float boost;
        
        BoostedScorer(final Scorer in, final float boost) {
            super(in);
            this.boost = boost;
        }
        
        @Override
        public float score() throws IOException {
            return this.in.score() * this.boost;
        }
        
        @Override
        public Collection<ChildScorer> getChildren() {
            return Collections.singleton(new ChildScorer(this.in, "BOOSTED"));
        }
    }
    
    static class BoostedBulkScorer extends BulkScorer
    {
        final BulkScorer in;
        final float boost;
        
        BoostedBulkScorer(final BulkScorer scorer, final float boost) {
            this.in = scorer;
            this.boost = boost;
        }
        
        @Override
        public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
            final LeafCollector wrapped = new FilterLeafCollector(collector) {
                @Override
                public void setScorer(final Scorer scorer) throws IOException {
                    super.setScorer(new BoostedScorer(scorer, BoostedBulkScorer.this.boost));
                }
            };
            return this.in.score(wrapped, acceptDocs, min, max);
        }
        
        @Override
        public long cost() {
            return this.in.cost();
        }
    }
    
    static class CoordinatingConjunctionScorer extends ConjunctionScorer
    {
        private final float[] coords;
        private final int reqCount;
        private final Scorer req;
        private final Scorer opt;
        
        CoordinatingConjunctionScorer(final Weight weight, final float[] coords, final Scorer req, final int reqCount, final Scorer opt) {
            super(weight, Arrays.asList(req, opt), Arrays.asList(req, opt));
            this.coords = coords;
            this.req = req;
            this.reqCount = reqCount;
            this.opt = opt;
        }
        
        @Override
        public float score() throws IOException {
            return (this.req.score() + this.opt.score()) * this.coords[this.reqCount + this.opt.freq()];
        }
    }
    
    static class ReqSingleOptScorer extends ReqOptSumScorer
    {
        private final float coordReq;
        private final float coordBoth;
        
        public ReqSingleOptScorer(final Scorer reqScorer, final Scorer optScorer, final float coordReq, final float coordBoth) {
            super(reqScorer, optScorer);
            this.coordReq = coordReq;
            this.coordBoth = coordBoth;
        }
        
        @Override
        public float score() throws IOException {
            final int curDoc = this.reqScorer.docID();
            float score = this.reqScorer.score();
            int optScorerDoc = this.optIterator.docID();
            if (optScorerDoc < curDoc) {
                optScorerDoc = this.optIterator.advance(curDoc);
            }
            if (optScorerDoc == curDoc) {
                score = (score + this.optScorer.score()) * this.coordBoth;
            }
            else {
                score *= this.coordReq;
            }
            return score;
        }
    }
    
    static class ReqMultiOptScorer extends ReqOptSumScorer
    {
        private final int requiredCount;
        private final float[] coords;
        
        public ReqMultiOptScorer(final Scorer reqScorer, final Scorer optScorer, final int requiredCount, final float[] coords) {
            super(reqScorer, optScorer);
            this.requiredCount = requiredCount;
            this.coords = coords;
        }
        
        @Override
        public float score() throws IOException {
            final int curDoc = this.reqScorer.docID();
            float score = this.reqScorer.score();
            int optScorerDoc = this.optIterator.docID();
            if (optScorerDoc < curDoc) {
                optScorerDoc = this.optIterator.advance(curDoc);
            }
            if (optScorerDoc == curDoc) {
                score = (score + this.optScorer.score()) * this.coords[this.requiredCount + this.optScorer.freq()];
            }
            else {
                score *= this.coords[this.requiredCount];
            }
            return score;
        }
    }
}
