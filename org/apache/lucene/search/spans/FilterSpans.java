package org.apache.lucene.search.spans;

import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import java.util.Objects;

public abstract class FilterSpans extends Spans
{
    protected final Spans in;
    private boolean atFirstInCurrentDoc;
    private int startPos;
    
    protected FilterSpans(final Spans in) {
        this.atFirstInCurrentDoc = false;
        this.startPos = -1;
        this.in = Objects.requireNonNull(in);
    }
    
    protected abstract AcceptStatus accept(final Spans p0) throws IOException;
    
    @Override
    public final int nextDoc() throws IOException {
        while (true) {
            final int doc = this.in.nextDoc();
            if (doc == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (this.twoPhaseCurrentDocMatches()) {
                return doc;
            }
        }
    }
    
    @Override
    public final int advance(final int target) throws IOException {
        int doc;
        for (doc = this.in.advance(target); doc != Integer.MAX_VALUE && !this.twoPhaseCurrentDocMatches(); doc = this.in.nextDoc()) {}
        return doc;
    }
    
    @Override
    public final int docID() {
        return this.in.docID();
    }
    
    @Override
    public final int nextStartPosition() throws IOException {
        if (this.atFirstInCurrentDoc) {
            this.atFirstInCurrentDoc = false;
            return this.startPos;
        }
        while (true) {
            this.startPos = this.in.nextStartPosition();
            if (this.startPos == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            switch (this.accept(this.in)) {
                case YES: {
                    return this.startPos;
                }
                case NO: {
                    continue;
                }
                case NO_MORE_IN_CURRENT_DOC: {
                    return this.startPos = Integer.MAX_VALUE;
                }
            }
        }
    }
    
    @Override
    public final int startPosition() {
        return this.atFirstInCurrentDoc ? -1 : this.startPos;
    }
    
    @Override
    public final int endPosition() {
        return this.atFirstInCurrentDoc ? -1 : ((this.startPos != Integer.MAX_VALUE) ? this.in.endPosition() : Integer.MAX_VALUE);
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
    public final long cost() {
        return this.in.cost();
    }
    
    @Override
    public String toString() {
        return "Filter(" + this.in.toString() + ")";
    }
    
    @Override
    public final TwoPhaseIterator asTwoPhaseIterator() {
        final TwoPhaseIterator inner = this.in.asTwoPhaseIterator();
        if (inner != null) {
            return new TwoPhaseIterator(inner.approximation()) {
                @Override
                public boolean matches() throws IOException {
                    return inner.matches() && FilterSpans.this.twoPhaseCurrentDocMatches();
                }
                
                @Override
                public float matchCost() {
                    return inner.matchCost();
                }
                
                @Override
                public String toString() {
                    return "FilterSpans@asTwoPhaseIterator(inner=" + inner + ", in=" + FilterSpans.this.in + ")";
                }
            };
        }
        return new TwoPhaseIterator(this.in) {
            @Override
            public boolean matches() throws IOException {
                return FilterSpans.this.twoPhaseCurrentDocMatches();
            }
            
            @Override
            public float matchCost() {
                return FilterSpans.this.in.positionsCost();
            }
            
            @Override
            public String toString() {
                return "FilterSpans@asTwoPhaseIterator(in=" + FilterSpans.this.in + ")";
            }
        };
    }
    
    @Override
    public float positionsCost() {
        throw new UnsupportedOperationException();
    }
    
    private final boolean twoPhaseCurrentDocMatches() throws IOException {
        this.atFirstInCurrentDoc = false;
        this.startPos = this.in.nextStartPosition();
        assert this.startPos != Integer.MAX_VALUE;
    Label_0110:
        while (true) {
            switch (this.accept(this.in)) {
                case YES: {
                    return this.atFirstInCurrentDoc = true;
                }
                case NO: {
                    this.startPos = this.in.nextStartPosition();
                    if (this.startPos != Integer.MAX_VALUE) {
                        continue;
                    }
                    break Label_0110;
                }
                case NO_MORE_IN_CURRENT_DOC: {
                    break Label_0110;
                }
            }
        }
        this.startPos = -1;
        return false;
    }
    
    public enum AcceptStatus
    {
        YES, 
        NO, 
        NO_MORE_IN_CURRENT_DOC;
    }
}
