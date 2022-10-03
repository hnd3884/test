package org.apache.lucene.search.spans;

import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import java.util.List;

public class NearSpansOrdered extends ConjunctionSpans
{
    protected int matchStart;
    protected int matchEnd;
    protected int matchWidth;
    private final int allowedSlop;
    
    public NearSpansOrdered(final int allowedSlop, final List<Spans> subSpans) throws IOException {
        super(subSpans);
        this.matchStart = -1;
        this.matchEnd = -1;
        this.matchWidth = -1;
        this.atFirstInCurrentDoc = true;
        this.allowedSlop = allowedSlop;
    }
    
    @Override
    boolean twoPhaseCurrentDocMatches() throws IOException {
        assert this.unpositioned();
        this.oneExhaustedInCurrentDoc = false;
        while (this.subSpans[0].nextStartPosition() != Integer.MAX_VALUE && !this.oneExhaustedInCurrentDoc) {
            if (this.stretchToOrder() && this.matchWidth <= this.allowedSlop) {
                return this.atFirstInCurrentDoc = true;
            }
        }
        return false;
    }
    
    private boolean unpositioned() {
        for (final Spans span : this.subSpans) {
            if (span.startPosition() != -1) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int nextStartPosition() throws IOException {
        if (this.atFirstInCurrentDoc) {
            this.atFirstInCurrentDoc = false;
            return this.matchStart;
        }
        this.oneExhaustedInCurrentDoc = false;
        while (this.subSpans[0].nextStartPosition() != Integer.MAX_VALUE && !this.oneExhaustedInCurrentDoc) {
            if (this.stretchToOrder() && this.matchWidth <= this.allowedSlop) {
                return this.matchStart;
            }
        }
        final int n = Integer.MAX_VALUE;
        this.matchEnd = n;
        return this.matchStart = n;
    }
    
    private boolean stretchToOrder() throws IOException {
        Spans prevSpans = this.subSpans[0];
        this.matchStart = prevSpans.startPosition();
        assert prevSpans.startPosition() != Integer.MAX_VALUE : "prevSpans no start position " + prevSpans;
        assert prevSpans.endPosition() != Integer.MAX_VALUE;
        this.matchWidth = 0;
        for (int i = 1; i < this.subSpans.length; ++i) {
            final Spans spans = this.subSpans[i];
            assert spans.startPosition() != Integer.MAX_VALUE;
            assert spans.endPosition() != Integer.MAX_VALUE;
            if (advancePosition(spans, prevSpans.endPosition()) == Integer.MAX_VALUE) {
                this.oneExhaustedInCurrentDoc = true;
                return false;
            }
            this.matchWidth += spans.startPosition() - prevSpans.endPosition();
            prevSpans = spans;
        }
        this.matchEnd = this.subSpans[this.subSpans.length - 1].endPosition();
        return true;
    }
    
    private static int advancePosition(final Spans spans, final int position) throws IOException {
        if (spans instanceof SpanNearQuery.GapSpans) {
            return ((SpanNearQuery.GapSpans)spans).skipToPosition(position);
        }
        while (spans.startPosition() < position) {
            spans.nextStartPosition();
        }
        return spans.startPosition();
    }
    
    @Override
    public int startPosition() {
        return this.atFirstInCurrentDoc ? -1 : this.matchStart;
    }
    
    @Override
    public int endPosition() {
        return this.atFirstInCurrentDoc ? -1 : this.matchEnd;
    }
    
    @Override
    public int width() {
        return this.matchWidth;
    }
    
    @Override
    public void collect(final SpanCollector collector) throws IOException {
        for (final Spans spans : this.subSpans) {
            spans.collect(collector);
        }
    }
}
