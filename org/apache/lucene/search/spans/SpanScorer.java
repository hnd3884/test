package org.apache.lucene.search.spans;

import org.apache.lucene.search.DocIdSetIterator;
import java.io.IOException;
import org.apache.lucene.search.TwoPhaseIterator;
import java.util.Objects;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.Scorer;

public class SpanScorer extends Scorer
{
    protected final Spans spans;
    protected final Similarity.SimScorer docScorer;
    private float freq;
    private int numMatches;
    private int lastScoredDoc;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public SpanScorer(final SpanWeight weight, final Spans spans, final Similarity.SimScorer docScorer) {
        super(weight);
        this.lastScoredDoc = -1;
        this.spans = Objects.requireNonNull(spans);
        this.docScorer = docScorer;
    }
    
    public Spans getSpans() {
        return this.spans;
    }
    
    @Override
    public int docID() {
        return this.spans.docID();
    }
    
    @Override
    public Spans iterator() {
        return this.spans;
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return this.spans.asTwoPhaseIterator();
    }
    
    protected float scoreCurrentDoc() throws IOException {
        assert this.docScorer != null : this.getClass() + " has a null docScorer!";
        return this.docScorer.score(this.docID(), this.freq);
    }
    
    protected final void setFreqCurrentDoc() throws IOException {
        this.freq = 0.0f;
        this.numMatches = 0;
        this.spans.doStartCurrentDoc();
        assert this.spans.startPosition() == -1 : "incorrect initial start position, " + this.toString();
        assert this.spans.endPosition() == -1 : "incorrect initial end position, " + this.toString();
        int prevStartPos = -1;
        int prevEndPos = -1;
        int startPos = this.spans.nextStartPosition();
        assert startPos != Integer.MAX_VALUE : "initial startPos NO_MORE_POSITIONS, " + this.toString();
        while (SpanScorer.$assertionsDisabled || startPos >= prevStartPos) {
            final int endPos = this.spans.endPosition();
            assert endPos != Integer.MAX_VALUE;
            assert endPos >= prevEndPos : "decreased endPos=" + endPos;
            ++this.numMatches;
            if (this.docScorer == null) {
                this.freq = 1.0f;
                return;
            }
            this.freq += this.docScorer.computeSlopFactor(this.spans.width());
            this.spans.doCurrentSpans();
            prevStartPos = startPos;
            prevEndPos = endPos;
            startPos = this.spans.nextStartPosition();
            if (startPos != Integer.MAX_VALUE) {
                continue;
            }
            assert this.spans.startPosition() == Integer.MAX_VALUE : "incorrect final start position, " + this.toString();
            assert this.spans.endPosition() == Integer.MAX_VALUE : "incorrect final end position, " + this.toString();
            return;
        }
        throw new AssertionError();
    }
    
    private void ensureFreq() throws IOException {
        final int currentDoc = this.docID();
        if (this.lastScoredDoc != currentDoc) {
            this.setFreqCurrentDoc();
            this.lastScoredDoc = currentDoc;
        }
    }
    
    @Override
    public final float score() throws IOException {
        this.ensureFreq();
        return this.scoreCurrentDoc();
    }
    
    @Override
    public final int freq() throws IOException {
        this.ensureFreq();
        return this.numMatches;
    }
    
    public final float sloppyFreq() throws IOException {
        this.ensureFreq();
        return this.freq;
    }
}
