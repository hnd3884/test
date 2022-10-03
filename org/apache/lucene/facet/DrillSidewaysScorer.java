package org.apache.lucene.facet;

import org.apache.lucene.search.TwoPhaseIterator;
import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.FixedBitSet;
import java.io.IOException;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.BulkScorer;

class DrillSidewaysScorer extends BulkScorer
{
    private final Collector drillDownCollector;
    private LeafCollector drillDownLeafCollector;
    private final DocsAndCost[] dims;
    private final Scorer baseScorer;
    private final DocIdSetIterator baseIterator;
    private final LeafReaderContext context;
    final boolean scoreSubDocsAtOnce;
    private static final int CHUNK = 2048;
    private static final int MASK = 2047;
    private int collectDocID;
    private float collectScore;
    
    DrillSidewaysScorer(final LeafReaderContext context, final Scorer baseScorer, final Collector drillDownCollector, final DocsAndCost[] dims, final boolean scoreSubDocsAtOnce) {
        this.collectDocID = -1;
        this.dims = dims;
        this.context = context;
        this.baseScorer = baseScorer;
        this.baseIterator = baseScorer.iterator();
        this.drillDownCollector = drillDownCollector;
        this.scoreSubDocsAtOnce = scoreSubDocsAtOnce;
    }
    
    public long cost() {
        return this.baseIterator.cost();
    }
    
    public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int maxDoc) throws IOException {
        if (min != 0) {
            throw new IllegalArgumentException("min must be 0, got " + min);
        }
        if (maxDoc != Integer.MAX_VALUE) {
            throw new IllegalArgumentException("maxDoc must be Integer.MAX_VALUE");
        }
        final FakeScorer scorer = new FakeScorer();
        collector.setScorer((Scorer)scorer);
        if (this.drillDownCollector != null) {
            (this.drillDownLeafCollector = this.drillDownCollector.getLeafCollector(this.context)).setScorer((Scorer)scorer);
        }
        else {
            this.drillDownLeafCollector = null;
        }
        for (final DocsAndCost dim : this.dims) {
            (dim.sidewaysLeafCollector = dim.sidewaysCollector.getLeafCollector(this.context)).setScorer((Scorer)scorer);
        }
        final long baseQueryCost = this.baseIterator.cost();
        final int numDims = this.dims.length;
        long drillDownCost = 0L;
        for (int dim2 = 0; dim2 < numDims; ++dim2) {
            drillDownCost += this.dims[dim2].approximation.cost();
        }
        long drillDownAdvancedCost = 0L;
        if (numDims > 1) {
            drillDownAdvancedCost = this.dims[1].approximation.cost();
        }
        this.baseIterator.nextDoc();
        for (final DocsAndCost dim3 : this.dims) {
            dim3.approximation.nextDoc();
        }
        if (this.scoreSubDocsAtOnce || baseQueryCost < drillDownCost / 10L) {
            this.doQueryFirstScoring(acceptDocs, collector, this.dims);
        }
        else if (numDims > 1 && drillDownAdvancedCost < baseQueryCost / 10L) {
            this.doDrillDownAdvanceScoring(acceptDocs, collector, this.dims);
        }
        else {
            this.doUnionScoring(acceptDocs, collector, this.dims);
        }
        return Integer.MAX_VALUE;
    }
    
    private void doQueryFirstScoring(final Bits acceptDocs, final LeafCollector collector, final DocsAndCost[] dims) throws IOException {
        int docID = this.baseScorer.docID();
    Label_0009:
        while (docID != Integer.MAX_VALUE) {
            if (acceptDocs != null && !acceptDocs.get(docID)) {
                docID = this.baseIterator.nextDoc();
            }
            else {
                LeafCollector failedCollector = null;
                for (final DocsAndCost dim : dims) {
                    if (dim.approximation.docID() < docID) {
                        dim.approximation.advance(docID);
                    }
                    boolean matches = false;
                    if (dim.approximation.docID() == docID) {
                        matches = (dim.twoPhase == null || dim.twoPhase.matches());
                    }
                    if (!matches) {
                        if (failedCollector != null) {
                            docID = this.baseIterator.nextDoc();
                            continue Label_0009;
                        }
                        failedCollector = dim.sidewaysLeafCollector;
                    }
                }
                this.collectDocID = docID;
                this.collectScore = this.baseScorer.score();
                if (failedCollector == null) {
                    this.collectHit(collector, dims);
                }
                else {
                    this.collectNearMiss(failedCollector);
                }
                docID = this.baseIterator.nextDoc();
            }
        }
    }
    
    private void doDrillDownAdvanceScoring(final Bits acceptDocs, final LeafCollector collector, final DocsAndCost[] dims) throws IOException {
        final int maxDoc = this.context.reader().maxDoc();
        final int numDims = dims.length;
        final int[] filledSlots = new int[2048];
        final int[] docIDs = new int[2048];
        final float[] scores = new float[2048];
        final int[] missingDims = new int[2048];
        final int[] counts = new int[2048];
        docIDs[0] = -1;
        int nextChunkStart = 2048;
        final FixedBitSet seen = new FixedBitSet(2048);
        while (true) {
            DocsAndCost dc = dims[0];
            for (int docID = dc.approximation.docID(); docID < nextChunkStart; docID = dc.approximation.nextDoc()) {
                if (acceptDocs == null || acceptDocs.get(docID)) {
                    final int slot = docID & 0x7FF;
                    if (docIDs[slot] != docID && (dc.twoPhase == null || dc.twoPhase.matches())) {
                        seen.set(slot);
                        docIDs[slot] = docID;
                        counts[slot] = (missingDims[slot] = 1);
                    }
                }
            }
            dc = dims[1];
            for (int docID = dc.approximation.docID(); docID < nextChunkStart; docID = dc.approximation.nextDoc()) {
                if (acceptDocs == null || (acceptDocs.get(docID) && (dc.twoPhase == null || dc.twoPhase.matches()))) {
                    final int slot = docID & 0x7FF;
                    if (docIDs[slot] != docID) {
                        seen.set(slot);
                        docIDs[slot] = docID;
                        missingDims[slot] = 0;
                        counts[slot] = 1;
                    }
                    else if (missingDims[slot] >= 1) {
                        counts[slot] = (missingDims[slot] = 2);
                    }
                    else {
                        counts[slot] = 1;
                    }
                }
            }
            int filledCount = 0;
            for (int slot2 = 0; slot2 < 2048 && (slot2 = seen.nextSetBit(slot2)) != Integer.MAX_VALUE; ++slot2) {
                final int ddDocID = docIDs[slot2];
                assert ddDocID != -1;
                int baseDocID = this.baseIterator.docID();
                if (baseDocID < ddDocID) {
                    baseDocID = this.baseIterator.advance(ddDocID);
                }
                if (baseDocID == ddDocID) {
                    scores[slot2] = this.baseScorer.score();
                    filledSlots[filledCount++] = slot2;
                    final int[] array = counts;
                    final int n = slot2;
                    ++array[n];
                }
                else {
                    docIDs[slot2] = -1;
                }
            }
            seen.clear(0, 2048);
            if (filledCount == 0) {
                if (nextChunkStart >= maxDoc) {
                    break;
                }
                nextChunkStart += 2048;
            }
            else {
                for (int dim = 2; dim < numDims; ++dim) {
                    dc = dims[dim];
                    for (int docID = dc.approximation.docID(); docID < nextChunkStart; docID = dc.approximation.nextDoc()) {
                        final int slot3 = docID & 0x7FF;
                        if (docIDs[slot3] == docID && counts[slot3] >= dim && (dc.twoPhase == null || dc.twoPhase.matches())) {
                            if (missingDims[slot3] >= dim) {
                                missingDims[slot3] = dim + 1;
                                counts[slot3] = dim + 2;
                            }
                            else {
                                counts[slot3] = dim + 1;
                            }
                        }
                    }
                }
                for (int i = 0; i < filledCount; ++i) {
                    final int slot3 = filledSlots[i];
                    this.collectDocID = docIDs[slot3];
                    this.collectScore = scores[slot3];
                    if (counts[slot3] == 1 + numDims) {
                        this.collectHit(collector, dims);
                    }
                    else if (counts[slot3] == numDims) {
                        this.collectNearMiss(dims[missingDims[slot3]].sidewaysLeafCollector);
                    }
                }
                if (nextChunkStart >= maxDoc) {
                    break;
                }
                nextChunkStart += 2048;
            }
        }
    }
    
    private void doUnionScoring(final Bits acceptDocs, final LeafCollector collector, final DocsAndCost[] dims) throws IOException {
        final int maxDoc = this.context.reader().maxDoc();
        final int numDims = dims.length;
        final int[] filledSlots = new int[2048];
        final int[] docIDs = new int[2048];
        final float[] scores = new float[2048];
        final int[] missingDims = new int[2048];
        final int[] counts = new int[2048];
        docIDs[0] = -1;
        int nextChunkStart = 2048;
        while (true) {
            int filledCount = 0;
            for (int docID = this.baseIterator.docID(); docID < nextChunkStart; docID = this.baseIterator.nextDoc()) {
                if (acceptDocs == null || acceptDocs.get(docID)) {
                    final int slot = docID & 0x7FF;
                    assert docIDs[slot] != docID : "slot=" + slot + " docID=" + docID;
                    docIDs[slot] = docID;
                    scores[slot] = this.baseScorer.score();
                    missingDims[filledSlots[filledCount++] = slot] = 0;
                    counts[slot] = 1;
                }
            }
            if (filledCount == 0) {
                if (nextChunkStart >= maxDoc) {
                    break;
                }
                nextChunkStart += 2048;
            }
            else {
                final DocsAndCost dc = dims[0];
                for (int docID = dc.approximation.docID(); docID < nextChunkStart; docID = dc.approximation.nextDoc()) {
                    final int slot2 = docID & 0x7FF;
                    if (docIDs[slot2] == docID && (dc.twoPhase == null || dc.twoPhase.matches())) {
                        missingDims[slot2] = 1;
                        counts[slot2] = 2;
                    }
                }
                for (int dim = 1; dim < numDims; ++dim) {
                    final DocsAndCost dc2 = dims[dim];
                    for (int docID = dc2.approximation.docID(); docID < nextChunkStart; docID = dc2.approximation.nextDoc()) {
                        final int slot3 = docID & 0x7FF;
                        if (docIDs[slot3] == docID && counts[slot3] >= dim && (dc2.twoPhase == null || dc2.twoPhase.matches())) {
                            if (missingDims[slot3] >= dim) {
                                missingDims[slot3] = dim + 1;
                                counts[slot3] = dim + 2;
                            }
                            else {
                                counts[slot3] = dim + 1;
                            }
                        }
                    }
                }
                for (int i = 0; i < filledCount; ++i) {
                    final int slot2 = filledSlots[i];
                    this.collectDocID = docIDs[slot2];
                    this.collectScore = scores[slot2];
                    if (counts[slot2] == 1 + numDims) {
                        this.collectHit(collector, dims);
                    }
                    else if (counts[slot2] == numDims) {
                        this.collectNearMiss(dims[missingDims[slot2]].sidewaysLeafCollector);
                    }
                }
                if (nextChunkStart >= maxDoc) {
                    break;
                }
                nextChunkStart += 2048;
            }
        }
    }
    
    private void collectHit(final LeafCollector collector, final DocsAndCost[] dims) throws IOException {
        collector.collect(this.collectDocID);
        if (this.drillDownCollector != null) {
            this.drillDownLeafCollector.collect(this.collectDocID);
        }
        for (final DocsAndCost dim : dims) {
            dim.sidewaysLeafCollector.collect(this.collectDocID);
        }
    }
    
    private void collectNearMiss(final LeafCollector sidewaysCollector) throws IOException {
        sidewaysCollector.collect(this.collectDocID);
    }
    
    private final class FakeScorer extends Scorer
    {
        public FakeScorer() {
            super((Weight)null);
        }
        
        public int docID() {
            return DrillSidewaysScorer.this.collectDocID;
        }
        
        public int freq() {
            return 1 + DrillSidewaysScorer.this.dims.length;
        }
        
        public DocIdSetIterator iterator() {
            throw new UnsupportedOperationException("FakeScorer doesn't support nextDoc()");
        }
        
        public float score() {
            return DrillSidewaysScorer.this.collectScore;
        }
        
        public Collection<Scorer.ChildScorer> getChildren() {
            return Collections.singletonList(new Scorer.ChildScorer(DrillSidewaysScorer.this.baseScorer, "MUST"));
        }
        
        public Weight getWeight() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class DocsAndCost
    {
        final DocIdSetIterator approximation;
        final TwoPhaseIterator twoPhase;
        final Collector sidewaysCollector;
        LeafCollector sidewaysLeafCollector;
        
        DocsAndCost(final Scorer scorer, final Collector sidewaysCollector) {
            final TwoPhaseIterator twoPhase = scorer.twoPhaseIterator();
            if (twoPhase == null) {
                this.approximation = scorer.iterator();
                this.twoPhase = null;
            }
            else {
                this.approximation = twoPhase.approximation();
                this.twoPhase = twoPhase;
            }
            this.sidewaysCollector = sidewaysCollector;
        }
    }
}
