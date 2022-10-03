package org.apache.lucene.search.spans;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class NearSpansUnordered extends ConjunctionSpans
{
    private List<SpansCell> subSpanCells;
    private final int allowedSlop;
    private SpanPositionQueue spanPositionQueue;
    private int totalSpanLength;
    private SpansCell maxEndPositionCell;
    
    public NearSpansUnordered(final int allowedSlop, final List<Spans> subSpans) throws IOException {
        super(subSpans);
        this.subSpanCells = new ArrayList<SpansCell>(subSpans.size());
        for (final Spans subSpan : subSpans) {
            this.subSpanCells.add(new SpansCell(subSpan));
        }
        this.spanPositionQueue = new SpanPositionQueue(subSpans.size());
        this.singleCellToPositionQueue();
        this.allowedSlop = allowedSlop;
    }
    
    private void singleCellToPositionQueue() {
        this.maxEndPositionCell = this.subSpanCells.get(0);
        assert this.maxEndPositionCell.docID() == -1;
        assert this.maxEndPositionCell.startPosition() == -1;
        this.spanPositionQueue.add(this.maxEndPositionCell);
    }
    
    private void subSpanCellsToPositionQueue() throws IOException {
        this.spanPositionQueue.clear();
        for (final SpansCell cell : this.subSpanCells) {
            assert cell.startPosition() == -1;
            cell.nextStartPosition();
            assert cell.startPosition() != Integer.MAX_VALUE;
            this.spanPositionQueue.add(cell);
        }
    }
    
    static boolean positionsOrdered(final Spans spans1, final Spans spans2) {
        assert spans1.docID() == spans2.docID() : "doc1 " + spans1.docID() + " != doc2 " + spans2.docID();
        final int start1 = spans1.startPosition();
        final int start2 = spans2.startPosition();
        return (start1 == start2) ? (spans1.endPosition() < spans2.endPosition()) : (start1 < start2);
    }
    
    private SpansCell minPositionCell() {
        return this.spanPositionQueue.top();
    }
    
    private boolean atMatch() {
        assert this.minPositionCell().docID() == this.maxEndPositionCell.docID();
        return this.maxEndPositionCell.endPosition() - this.minPositionCell().startPosition() - this.totalSpanLength <= this.allowedSlop;
    }
    
    @Override
    boolean twoPhaseCurrentDocMatches() throws IOException {
        this.subSpanCellsToPositionQueue();
        while (!this.atMatch()) {
            assert this.minPositionCell().startPosition() != Integer.MAX_VALUE;
            if (this.minPositionCell().nextStartPosition() == Integer.MAX_VALUE) {
                return false;
            }
            this.spanPositionQueue.updateTop();
        }
        this.atFirstInCurrentDoc = true;
        this.oneExhaustedInCurrentDoc = false;
        return true;
    }
    
    @Override
    public int nextStartPosition() throws IOException {
        if (this.atFirstInCurrentDoc) {
            this.atFirstInCurrentDoc = false;
            return this.minPositionCell().startPosition();
        }
        while (this.minPositionCell().startPosition() == -1) {
            this.minPositionCell().nextStartPosition();
            this.spanPositionQueue.updateTop();
        }
        assert this.minPositionCell().startPosition() != Integer.MAX_VALUE;
        while (this.minPositionCell().nextStartPosition() != Integer.MAX_VALUE) {
            this.spanPositionQueue.updateTop();
            if (this.atMatch()) {
                return this.minPositionCell().startPosition();
            }
        }
        this.oneExhaustedInCurrentDoc = true;
        return Integer.MAX_VALUE;
    }
    
    @Override
    public int startPosition() {
        assert this.minPositionCell() != null;
        return this.atFirstInCurrentDoc ? -1 : (this.oneExhaustedInCurrentDoc ? Integer.MAX_VALUE : this.minPositionCell().startPosition());
    }
    
    @Override
    public int endPosition() {
        return this.atFirstInCurrentDoc ? -1 : (this.oneExhaustedInCurrentDoc ? Integer.MAX_VALUE : this.maxEndPositionCell.endPosition());
    }
    
    @Override
    public int width() {
        return this.maxEndPositionCell.startPosition() - this.minPositionCell().startPosition();
    }
    
    @Override
    public void collect(final SpanCollector collector) throws IOException {
        for (final SpansCell cell : this.subSpanCells) {
            cell.collect(collector);
        }
    }
    
    private class SpansCell extends Spans
    {
        private int spanLength;
        final Spans in;
        
        public SpansCell(final Spans spans) {
            this.spanLength = -1;
            this.in = spans;
        }
        
        @Override
        public int nextStartPosition() throws IOException {
            final int res = this.in.nextStartPosition();
            if (res != Integer.MAX_VALUE) {
                this.adjustLength();
            }
            this.adjustMax();
            return res;
        }
        
        private void adjustLength() {
            if (this.spanLength != -1) {
                NearSpansUnordered.this.totalSpanLength -= this.spanLength;
            }
            assert this.in.startPosition() != Integer.MAX_VALUE;
            this.spanLength = this.endPosition() - this.startPosition();
            assert this.spanLength >= 0;
            NearSpansUnordered.this.totalSpanLength += this.spanLength;
        }
        
        private void adjustMax() {
            assert this.docID() == NearSpansUnordered.this.maxEndPositionCell.docID();
            if (this.endPosition() > NearSpansUnordered.this.maxEndPositionCell.endPosition()) {
                NearSpansUnordered.this.maxEndPositionCell = this;
            }
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
        public TwoPhaseIterator asTwoPhaseIterator() {
            return this.in.asTwoPhaseIterator();
        }
        
        @Override
        public float positionsCost() {
            return this.in.positionsCost();
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
        public String toString() {
            return "NearSpansUnordered.SpansCell(" + this.in.toString() + ")";
        }
    }
    
    private static class SpanPositionQueue extends PriorityQueue<SpansCell>
    {
        public SpanPositionQueue(final int size) {
            super(size);
        }
        
        @Override
        protected final boolean lessThan(final SpansCell spans1, final SpansCell spans2) {
            return NearSpansUnordered.positionsOrdered(spans1, spans2);
        }
    }
}
