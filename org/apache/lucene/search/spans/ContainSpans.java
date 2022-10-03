package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Objects;
import java.util.Arrays;

abstract class ContainSpans extends ConjunctionSpans
{
    Spans sourceSpans;
    Spans bigSpans;
    Spans littleSpans;
    
    ContainSpans(final Spans bigSpans, final Spans littleSpans, final Spans sourceSpans) {
        super(Arrays.asList(bigSpans, littleSpans));
        this.bigSpans = Objects.requireNonNull(bigSpans);
        this.littleSpans = Objects.requireNonNull(littleSpans);
        this.sourceSpans = Objects.requireNonNull(sourceSpans);
    }
    
    @Override
    public int startPosition() {
        return this.atFirstInCurrentDoc ? -1 : (this.oneExhaustedInCurrentDoc ? Integer.MAX_VALUE : this.sourceSpans.startPosition());
    }
    
    @Override
    public int endPosition() {
        return this.atFirstInCurrentDoc ? -1 : (this.oneExhaustedInCurrentDoc ? Integer.MAX_VALUE : this.sourceSpans.endPosition());
    }
    
    @Override
    public int width() {
        return this.sourceSpans.width();
    }
    
    @Override
    public void collect(final SpanCollector collector) throws IOException {
        this.bigSpans.collect(collector);
        this.littleSpans.collect(collector);
    }
}
