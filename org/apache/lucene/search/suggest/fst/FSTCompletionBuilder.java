package org.apache.lucene.search.suggest.fst;

import org.apache.lucene.util.BytesRefIterator;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.NoOutputs;
import java.io.Closeable;
import java.io.IOException;
import java.util.Comparator;
import org.apache.lucene.search.suggest.InMemorySorter;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.fst.FST;

public class FSTCompletionBuilder
{
    public static final int DEFAULT_BUCKETS = 10;
    private final int buckets;
    FST<Object> automaton;
    private final BytesRefSorter sorter;
    private final BytesRefBuilder scratch;
    private final int shareMaxTailLength;
    
    public FSTCompletionBuilder() {
        this(10, new InMemorySorter(BytesRef.getUTF8SortedAsUnicodeComparator()), Integer.MAX_VALUE);
    }
    
    public FSTCompletionBuilder(final int buckets, final BytesRefSorter sorter, final int shareMaxTailLength) {
        this.scratch = new BytesRefBuilder();
        if (buckets < 1 || buckets > 255) {
            throw new IllegalArgumentException("Buckets must be >= 1 and <= 255: " + buckets);
        }
        if (sorter == null) {
            throw new IllegalArgumentException("BytesRefSorter must not be null.");
        }
        this.sorter = sorter;
        this.buckets = buckets;
        this.shareMaxTailLength = shareMaxTailLength;
    }
    
    public void add(final BytesRef utf8, final int bucket) throws IOException {
        if (bucket < 0 || bucket >= this.buckets) {
            throw new IllegalArgumentException("Bucket outside of the allowed range [0, " + this.buckets + "): " + bucket);
        }
        this.scratch.grow(utf8.length + 10);
        this.scratch.clear();
        this.scratch.append((byte)bucket);
        this.scratch.append(utf8);
        this.sorter.add(this.scratch.get());
    }
    
    public FSTCompletion build() throws IOException {
        this.automaton = this.buildAutomaton(this.sorter);
        if (this.sorter instanceof Closeable) {
            ((Closeable)this.sorter).close();
        }
        return new FSTCompletion(this.automaton);
    }
    
    private FST<Object> buildAutomaton(final BytesRefSorter sorter) throws IOException {
        final Outputs<Object> outputs = (Outputs<Object>)NoOutputs.getSingleton();
        final Object empty = outputs.getNoOutput();
        final Builder<Object> builder = (Builder<Object>)new Builder(FST.INPUT_TYPE.BYTE1, 0, 0, true, true, this.shareMaxTailLength, (Outputs)outputs, false, 0.25f, true, 15);
        final BytesRefBuilder scratch = new BytesRefBuilder();
        final IntsRefBuilder scratchIntsRef = new IntsRefBuilder();
        int count = 0;
        final BytesRefIterator iter = sorter.iterator();
        BytesRef entry;
        while ((entry = iter.next()) != null) {
            ++count;
            if (scratch.get().compareTo(entry) != 0) {
                builder.add(Util.toIntsRef(entry, scratchIntsRef), empty);
                scratch.copyBytes(entry);
            }
        }
        return (FST<Object>)((count == 0) ? null : builder.finish());
    }
}
