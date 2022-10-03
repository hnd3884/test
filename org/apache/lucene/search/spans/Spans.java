package org.apache.lucene.search.spans;

import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;

public abstract class Spans extends DocIdSetIterator
{
    public static final int NO_MORE_POSITIONS = Integer.MAX_VALUE;
    
    public abstract int nextStartPosition() throws IOException;
    
    public abstract int startPosition();
    
    public abstract int endPosition();
    
    public abstract int width();
    
    public abstract void collect(final SpanCollector p0) throws IOException;
    
    public abstract float positionsCost();
    
    public TwoPhaseIterator asTwoPhaseIterator() {
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Class<? extends Spans> clazz = this.getClass();
        sb.append(clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName());
        sb.append("(doc=").append(this.docID());
        sb.append(",start=").append(this.startPosition());
        sb.append(",end=").append(this.endPosition());
        sb.append(")");
        return sb.toString();
    }
    
    protected void doStartCurrentDoc() throws IOException {
    }
    
    protected void doCurrentSpans() throws IOException {
    }
}
