package org.apache.lucene.search.suggest.document;

import org.apache.lucene.store.DataOutput;
import java.util.Iterator;
import org.apache.lucene.util.fst.Util;
import java.io.IOException;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import java.util.PriorityQueue;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;

final class NRTSuggesterBuilder
{
    public static final int PAYLOAD_SEP = 31;
    public static final int END_BYTE = 0;
    private final PairOutputs<Long, BytesRef> outputs;
    private final Builder<PairOutputs.Pair<Long, BytesRef>> builder;
    private final IntsRefBuilder scratchInts;
    private final BytesRefBuilder analyzed;
    private final PriorityQueue<Entry> entries;
    private final int payloadSep;
    private final int endByte;
    private int maxAnalyzedPathsPerOutput;
    
    public NRTSuggesterBuilder() {
        this.scratchInts = new IntsRefBuilder();
        this.analyzed = new BytesRefBuilder();
        this.maxAnalyzedPathsPerOutput = 0;
        this.payloadSep = 31;
        this.endByte = 0;
        this.outputs = (PairOutputs<Long, BytesRef>)new PairOutputs((Outputs)PositiveIntOutputs.getSingleton(), (Outputs)ByteSequenceOutputs.getSingleton());
        this.entries = new PriorityQueue<Entry>();
        this.builder = (Builder<PairOutputs.Pair<Long, BytesRef>>)new Builder(FST.INPUT_TYPE.BYTE1, (Outputs)this.outputs);
    }
    
    public void startTerm(final BytesRef analyzed) {
        this.analyzed.copyBytes(analyzed);
        this.analyzed.append((byte)this.endByte);
    }
    
    public void addEntry(final int docID, final BytesRef surfaceForm, final long weight) throws IOException {
        final BytesRef payloadRef = NRTSuggester.PayLoadProcessor.make(surfaceForm, docID, this.payloadSep);
        this.entries.add(new Entry(payloadRef, NRTSuggester.encode(weight)));
    }
    
    public void finishTerm() throws IOException {
        int numArcs = 0;
        int numDedupBytes = 1;
        this.analyzed.grow(this.analyzed.length() + 1);
        this.analyzed.setLength(this.analyzed.length() + 1);
        for (final Entry entry : this.entries) {
            if (numArcs == maxNumArcsForDedupByte(numDedupBytes)) {
                this.analyzed.setByteAt(this.analyzed.length() - 1, (byte)numArcs);
                this.analyzed.grow(this.analyzed.length() + 1);
                this.analyzed.setLength(this.analyzed.length() + 1);
                numArcs = 0;
                ++numDedupBytes;
            }
            this.analyzed.setByteAt(this.analyzed.length() - 1, (byte)(numArcs++));
            Util.toIntsRef(this.analyzed.get(), this.scratchInts);
            this.builder.add(this.scratchInts.get(), (Object)this.outputs.newPair((Object)entry.weight, (Object)entry.payload));
        }
        this.maxAnalyzedPathsPerOutput = Math.max(this.maxAnalyzedPathsPerOutput, this.entries.size());
        this.entries.clear();
    }
    
    public boolean store(final DataOutput output) throws IOException {
        final FST<PairOutputs.Pair<Long, BytesRef>> build = (FST<PairOutputs.Pair<Long, BytesRef>>)this.builder.finish();
        if (build == null) {
            return false;
        }
        build.save(output);
        assert this.maxAnalyzedPathsPerOutput > 0;
        output.writeVInt(this.maxAnalyzedPathsPerOutput);
        output.writeVInt(0);
        output.writeVInt(31);
        return true;
    }
    
    private static int maxNumArcsForDedupByte(final int currentNumDedupBytes) {
        int maxArcs = 1 + 2 * currentNumDedupBytes;
        if (currentNumDedupBytes > 5) {
            maxArcs *= currentNumDedupBytes;
        }
        return Math.min(maxArcs, 255);
    }
    
    private static final class Entry implements Comparable<Entry>
    {
        final BytesRef payload;
        final long weight;
        
        public Entry(final BytesRef payload, final long weight) {
            this.payload = payload;
            this.weight = weight;
        }
        
        @Override
        public int compareTo(final Entry o) {
            return Long.compare(this.weight, o.weight);
        }
    }
}
