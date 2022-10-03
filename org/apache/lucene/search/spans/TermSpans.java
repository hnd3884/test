package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Objects;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.PostingsEnum;

public class TermSpans extends Spans
{
    protected final PostingsEnum postings;
    protected final Term term;
    protected int doc;
    protected int freq;
    protected int count;
    protected int position;
    protected boolean readPayload;
    private final float positionsCost;
    
    public TermSpans(final Similarity.SimScorer scorer, final PostingsEnum postings, final Term term, final float positionsCost) {
        this.postings = Objects.requireNonNull(postings);
        this.term = Objects.requireNonNull(term);
        this.doc = -1;
        this.position = -1;
        assert positionsCost > 0.0f;
        this.positionsCost = positionsCost;
    }
    
    @Override
    public int nextDoc() throws IOException {
        this.doc = this.postings.nextDoc();
        if (this.doc != Integer.MAX_VALUE) {
            this.freq = this.postings.freq();
            assert this.freq >= 1;
            this.count = 0;
        }
        this.position = -1;
        return this.doc;
    }
    
    @Override
    public int advance(final int target) throws IOException {
        assert target > this.doc;
        this.doc = this.postings.advance(target);
        if (this.doc != Integer.MAX_VALUE) {
            this.freq = this.postings.freq();
            assert this.freq >= 1;
            this.count = 0;
        }
        this.position = -1;
        return this.doc;
    }
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int nextStartPosition() throws IOException {
        if (this.count == this.freq) {
            assert this.position != Integer.MAX_VALUE;
            return this.position = Integer.MAX_VALUE;
        }
        else {
            final int prevPosition = this.position;
            this.position = this.postings.nextPosition();
            assert this.position >= prevPosition : "prevPosition=" + prevPosition + " > position=" + this.position;
            assert this.position != Integer.MAX_VALUE;
            ++this.count;
            this.readPayload = false;
            return this.position;
        }
    }
    
    @Override
    public int startPosition() {
        return this.position;
    }
    
    @Override
    public int endPosition() {
        return (this.position == -1) ? -1 : ((this.position != Integer.MAX_VALUE) ? (this.position + 1) : Integer.MAX_VALUE);
    }
    
    @Override
    public int width() {
        return 0;
    }
    
    @Override
    public long cost() {
        return this.postings.cost();
    }
    
    @Override
    public void collect(final SpanCollector collector) throws IOException {
        collector.collectLeaf(this.postings, this.position, this.term);
    }
    
    @Override
    public float positionsCost() {
        return this.positionsCost;
    }
    
    @Override
    public String toString() {
        return "spans(" + this.term.toString() + ")@" + ((this.doc == -1) ? "START" : ((this.doc == Integer.MAX_VALUE) ? "ENDDOC" : (this.doc + " - " + ((this.position == Integer.MAX_VALUE) ? "ENDPOS" : Integer.valueOf(this.position)))));
    }
    
    public PostingsEnum getPostings() {
        return this.postings;
    }
}
