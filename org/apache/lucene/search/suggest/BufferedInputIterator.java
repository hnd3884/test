package org.apache.lucene.search.suggest;

import java.io.IOException;
import org.apache.lucene.util.ArrayUtil;
import java.util.ArrayList;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import java.util.Set;
import java.util.List;
import org.apache.lucene.util.BytesRefArray;

public class BufferedInputIterator implements InputIterator
{
    protected BytesRefArray entries;
    protected BytesRefArray payloads;
    protected List<Set<BytesRef>> contextSets;
    protected int curPos;
    protected long[] freqs;
    private final BytesRefBuilder spare;
    private final BytesRefBuilder payloadSpare;
    private final boolean hasPayloads;
    private final boolean hasContexts;
    
    public BufferedInputIterator(final InputIterator source) throws IOException {
        this.entries = new BytesRefArray(Counter.newCounter());
        this.payloads = new BytesRefArray(Counter.newCounter());
        this.contextSets = new ArrayList<Set<BytesRef>>();
        this.curPos = -1;
        this.freqs = new long[1];
        this.spare = new BytesRefBuilder();
        this.payloadSpare = new BytesRefBuilder();
        int freqIndex = 0;
        this.hasPayloads = source.hasPayloads();
        this.hasContexts = source.hasContexts();
        BytesRef spare;
        while ((spare = source.next()) != null) {
            this.entries.append(spare);
            if (this.hasPayloads) {
                this.payloads.append(source.payload());
            }
            if (this.hasContexts) {
                this.contextSets.add(source.contexts());
            }
            if (freqIndex >= this.freqs.length) {
                this.freqs = ArrayUtil.grow(this.freqs, this.freqs.length + 1);
            }
            this.freqs[freqIndex++] = source.weight();
        }
    }
    
    @Override
    public long weight() {
        return this.freqs[this.curPos];
    }
    
    public BytesRef next() throws IOException {
        if (++this.curPos < this.entries.size()) {
            this.entries.get(this.spare, this.curPos);
            return this.spare.get();
        }
        return null;
    }
    
    @Override
    public BytesRef payload() {
        if (this.hasPayloads && this.curPos < this.payloads.size()) {
            return this.payloads.get(this.payloadSpare, this.curPos);
        }
        return null;
    }
    
    @Override
    public boolean hasPayloads() {
        return this.hasPayloads;
    }
    
    @Override
    public Set<BytesRef> contexts() {
        if (this.hasContexts && this.curPos < this.contextSets.size()) {
            return this.contextSets.get(this.curPos);
        }
        return null;
    }
    
    @Override
    public boolean hasContexts() {
        return this.hasContexts;
    }
}
