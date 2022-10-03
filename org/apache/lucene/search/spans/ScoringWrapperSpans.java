package org.apache.lucene.search.spans;

import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import org.apache.lucene.search.similarities.Similarity;

public class ScoringWrapperSpans extends Spans
{
    private final Spans in;
    
    public ScoringWrapperSpans(final Spans spans, final Similarity.SimScorer simScorer) {
        this.in = spans;
    }
    
    @Override
    public int nextStartPosition() throws IOException {
        return this.in.nextStartPosition();
    }
    
    @Override
    public int startPosition() {
        return this.in.startPosition();
    }
    
    @Override
    public int endPosition() {
        return this.in.endPosition();
    }
    
    @Override
    public int width() {
        return this.in.width();
    }
    
    @Override
    public void collect(final SpanCollector collector) throws IOException {
        this.in.collect(collector);
    }
    
    @Override
    public int docID() {
        return this.in.docID();
    }
    
    @Override
    public int nextDoc() throws IOException {
        return this.in.nextDoc();
    }
    
    @Override
    public int advance(final int target) throws IOException {
        return this.in.advance(target);
    }
    
    @Override
    public long cost() {
        return this.in.cost();
    }
    
    @Override
    public TwoPhaseIterator asTwoPhaseIterator() {
        return this.in.asTwoPhaseIterator();
    }
    
    @Override
    public float positionsCost() {
        return this.in.positionsCost();
    }
}
