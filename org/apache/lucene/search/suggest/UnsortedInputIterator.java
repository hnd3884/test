package org.apache.lucene.search.suggest;

import java.util.Set;
import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import java.util.Random;
import org.apache.lucene.util.BytesRefBuilder;

public class UnsortedInputIterator extends BufferedInputIterator
{
    private final int[] ords;
    private int currentOrd;
    private final BytesRefBuilder spare;
    private final BytesRefBuilder payloadSpare;
    
    public UnsortedInputIterator(final InputIterator source) throws IOException {
        super(source);
        this.currentOrd = -1;
        this.spare = new BytesRefBuilder();
        this.payloadSpare = new BytesRefBuilder();
        this.ords = new int[this.entries.size()];
        final Random random = new Random();
        for (int i = 0; i < this.ords.length; ++i) {
            this.ords[i] = i;
        }
        for (int i = 0; i < this.ords.length; ++i) {
            final int randomPosition = random.nextInt(this.ords.length);
            final int temp = this.ords[i];
            this.ords[i] = this.ords[randomPosition];
            this.ords[randomPosition] = temp;
        }
    }
    
    @Override
    public long weight() {
        assert this.currentOrd == this.ords[this.curPos];
        return this.freqs[this.currentOrd];
    }
    
    @Override
    public BytesRef next() throws IOException {
        if (++this.curPos < this.entries.size()) {
            this.currentOrd = this.ords[this.curPos];
            return this.entries.get(this.spare, this.currentOrd);
        }
        return null;
    }
    
    @Override
    public BytesRef payload() {
        if (!this.hasPayloads() || this.curPos >= this.payloads.size()) {
            return null;
        }
        assert this.currentOrd == this.ords[this.curPos];
        return this.payloads.get(this.payloadSpare, this.currentOrd);
    }
    
    @Override
    public Set<BytesRef> contexts() {
        if (!this.hasContexts() || this.curPos >= this.contextSets.size()) {
            return null;
        }
        assert this.currentOrd == this.ords[this.curPos];
        return this.contextSets.get(this.currentOrd);
    }
}
