package org.apache.lucene.search.suggest.fst;

import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.store.ByteArrayDataOutput;
import org.apache.lucene.util.OfflineSorter;
import org.apache.lucene.search.suggest.SortedInputIterator;
import org.apache.lucene.util.Accountables;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import java.io.IOException;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.suggest.InputIterator;
import java.util.Comparator;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.search.suggest.Lookup;

public class WFSTCompletionLookup extends Lookup
{
    private FST<Long> fst;
    private final boolean exactFirst;
    private long count;
    static final Comparator<Long> weightComparator;
    
    public WFSTCompletionLookup() {
        this(true);
    }
    
    public WFSTCompletionLookup(final boolean exactFirst) {
        this.fst = null;
        this.count = 0L;
        this.exactFirst = exactFirst;
    }
    
    @Override
    public void build(final InputIterator iterator) throws IOException {
        if (iterator.hasPayloads()) {
            throw new IllegalArgumentException("this suggester doesn't support payloads");
        }
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        this.count = 0L;
        BytesRef scratch = new BytesRef();
        final InputIterator iter = new WFSTInputIterator(iterator);
        final IntsRefBuilder scratchInts = new IntsRefBuilder();
        BytesRefBuilder previous = null;
        final PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
        final Builder<Long> builder = (Builder<Long>)new Builder(FST.INPUT_TYPE.BYTE1, (Outputs)outputs);
        while ((scratch = iter.next()) != null) {
            final long cost = iter.weight();
            if (previous == null) {
                previous = new BytesRefBuilder();
            }
            else if (scratch.equals((Object)previous.get())) {
                continue;
            }
            Util.toIntsRef(scratch, scratchInts);
            builder.add(scratchInts.get(), (Object)cost);
            previous.copyBytes(scratch);
            ++this.count;
        }
        this.fst = (FST<Long>)builder.finish();
    }
    
    @Override
    public boolean store(final DataOutput output) throws IOException {
        output.writeVLong(this.count);
        if (this.fst == null) {
            return false;
        }
        this.fst.save(output);
        return true;
    }
    
    @Override
    public boolean load(final DataInput input) throws IOException {
        this.count = input.readVLong();
        this.fst = (FST<Long>)new FST(input, (Outputs)PositiveIntOutputs.getSingleton());
        return true;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean onlyMorePopular, int num) {
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        assert num > 0;
        if (onlyMorePopular) {
            throw new IllegalArgumentException("this suggester only works with onlyMorePopular=false");
        }
        if (this.fst == null) {
            return Collections.emptyList();
        }
        final BytesRefBuilder scratch = new BytesRefBuilder();
        scratch.copyChars(key);
        final int prefixLength = scratch.length();
        final FST.Arc<Long> arc = (FST.Arc<Long>)new FST.Arc();
        Long prefixOutput = null;
        try {
            prefixOutput = this.lookupPrefix(scratch.get(), arc);
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
        if (prefixOutput == null) {
            return Collections.emptyList();
        }
        final List<LookupResult> results = new ArrayList<LookupResult>(num);
        final CharsRefBuilder spare = new CharsRefBuilder();
        if (this.exactFirst && arc.isFinal()) {
            spare.copyUTF8Bytes(scratch.get());
            results.add(new LookupResult(spare.toString(), decodeWeight(prefixOutput + (long)arc.nextFinalOutput)));
            if (--num == 0) {
                return results;
            }
        }
        Util.TopResults<Long> completions = null;
        try {
            completions = (Util.TopResults<Long>)Util.shortestPaths((FST)this.fst, (FST.Arc)arc, (Object)prefixOutput, (Comparator)WFSTCompletionLookup.weightComparator, num, !this.exactFirst);
            assert completions.isComplete;
        }
        catch (final IOException bogus2) {
            throw new RuntimeException(bogus2);
        }
        final BytesRefBuilder suffix = new BytesRefBuilder();
        for (final Util.Result<Long> completion : completions) {
            scratch.setLength(prefixLength);
            Util.toBytesRef(completion.input, suffix);
            scratch.append(suffix);
            spare.copyUTF8Bytes(scratch.get());
            results.add(new LookupResult(spare.toString(), decodeWeight((long)completion.output)));
        }
        return results;
    }
    
    private Long lookupPrefix(final BytesRef scratch, final FST.Arc<Long> arc) throws IOException {
        assert 0L == (long)this.fst.outputs.getNoOutput();
        long output = 0L;
        final FST.BytesReader bytesReader = this.fst.getBytesReader();
        this.fst.getFirstArc((FST.Arc)arc);
        final byte[] bytes = scratch.bytes;
        int pos = scratch.offset;
        final int end = pos + scratch.length;
        while (pos < end) {
            if (this.fst.findTargetArc(bytes[pos++] & 0xFF, (FST.Arc)arc, (FST.Arc)arc, bytesReader) == null) {
                return null;
            }
            output += (long)arc.output;
        }
        return output;
    }
    
    public Object get(final CharSequence key) {
        if (this.fst == null) {
            return null;
        }
        final FST.Arc<Long> arc = (FST.Arc<Long>)new FST.Arc();
        Long result = null;
        try {
            result = this.lookupPrefix(new BytesRef(key), arc);
        }
        catch (final IOException bogus) {
            throw new RuntimeException(bogus);
        }
        if (result == null || !arc.isFinal()) {
            return null;
        }
        return decodeWeight(result + (long)arc.nextFinalOutput);
    }
    
    private static int decodeWeight(final long encoded) {
        return (int)(2147483647L - encoded);
    }
    
    private static int encodeWeight(final long value) {
        if (value < 0L || value > 2147483647L) {
            throw new UnsupportedOperationException("cannot encode value: " + value);
        }
        return Integer.MAX_VALUE - (int)value;
    }
    
    public long ramBytesUsed() {
        return (this.fst == null) ? 0L : this.fst.ramBytesUsed();
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        if (this.fst == null) {
            return (Collection<Accountable>)Collections.emptyList();
        }
        return Collections.singleton(Accountables.namedAccountable("fst", (Accountable)this.fst));
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    static {
        weightComparator = new Comparator<Long>() {
            @Override
            public int compare(final Long left, final Long right) {
                return left.compareTo(right);
            }
        };
    }
    
    private final class WFSTInputIterator extends SortedInputIterator
    {
        WFSTInputIterator(final InputIterator source) throws IOException {
            super(source);
            assert !source.hasPayloads();
        }
        
        @Override
        protected void encode(final OfflineSorter.ByteSequencesWriter writer, final ByteArrayDataOutput output, byte[] buffer, final BytesRef spare, final BytesRef payload, final Set<BytesRef> contexts, final long weight) throws IOException {
            if (spare.length + 4 >= buffer.length) {
                buffer = ArrayUtil.grow(buffer, spare.length + 4);
            }
            output.reset(buffer);
            output.writeBytes(spare.bytes, spare.offset, spare.length);
            output.writeInt(encodeWeight(weight));
            writer.write(buffer, 0, output.getPosition());
        }
        
        @Override
        protected long decode(final BytesRef scratch, final ByteArrayDataInput tmpInput) {
            scratch.length -= 4;
            tmpInput.reset(scratch.bytes, scratch.offset + scratch.length, 4);
            return tmpInput.readInt();
        }
    }
}
