package org.apache.lucene.search.suggest.fst;

import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.NoOutputs;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import java.util.Iterator;
import org.apache.lucene.util.CharsRefBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.store.ByteArrayDataOutput;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.util.OfflineSorter;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.search.suggest.Lookup;

public class FSTCompletionLookup extends Lookup implements Accountable
{
    private static int INVALID_BUCKETS_COUNT;
    private static final int sharedTailLength = 5;
    private int buckets;
    private boolean exactMatchFirst;
    private FSTCompletion higherWeightsCompletion;
    private FSTCompletion normalCompletion;
    private long count;
    
    public FSTCompletionLookup() {
        this(10, true);
    }
    
    public FSTCompletionLookup(final int buckets, final boolean exactMatchFirst) {
        this.count = 0L;
        this.buckets = buckets;
        this.exactMatchFirst = exactMatchFirst;
    }
    
    public FSTCompletionLookup(final FSTCompletion completion, final boolean exactMatchFirst) {
        this(FSTCompletionLookup.INVALID_BUCKETS_COUNT, exactMatchFirst);
        this.normalCompletion = new FSTCompletion(completion.getFST(), false, exactMatchFirst);
        this.higherWeightsCompletion = new FSTCompletion(completion.getFST(), true, exactMatchFirst);
    }
    
    @Override
    public void build(final InputIterator iterator) throws IOException {
        if (iterator.hasPayloads()) {
            throw new IllegalArgumentException("this suggester doesn't support payloads");
        }
        if (iterator.hasContexts()) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        final Path tempInput = Files.createTempFile(OfflineSorter.getDefaultTempDir(), FSTCompletionLookup.class.getSimpleName(), ".input", (FileAttribute<?>[])new FileAttribute[0]);
        final Path tempSorted = Files.createTempFile(OfflineSorter.getDefaultTempDir(), FSTCompletionLookup.class.getSimpleName(), ".sorted", (FileAttribute<?>[])new FileAttribute[0]);
        final OfflineSorter.ByteSequencesWriter writer = new OfflineSorter.ByteSequencesWriter(tempInput);
        OfflineSorter.ByteSequencesReader reader = null;
        ExternalRefSorter sorter = null;
        boolean success = false;
        this.count = 0L;
        try {
            byte[] buffer = new byte[0];
            final ByteArrayDataOutput output = new ByteArrayDataOutput(buffer);
            BytesRef spare;
            while ((spare = iterator.next()) != null) {
                if (spare.length + 4 >= buffer.length) {
                    buffer = ArrayUtil.grow(buffer, spare.length + 4);
                }
                output.reset(buffer);
                output.writeInt(encodeWeight(iterator.weight()));
                output.writeBytes(spare.bytes, spare.offset, spare.length);
                writer.write(buffer, 0, output.getPosition());
            }
            writer.close();
            final OfflineSorter.SortInfo info = new OfflineSorter().sort(tempInput, tempSorted);
            Files.delete(tempInput);
            final FSTCompletionBuilder builder = new FSTCompletionBuilder(this.buckets, sorter = new ExternalRefSorter(new OfflineSorter()), 5);
            final int inputLines = info.lines;
            reader = new OfflineSorter.ByteSequencesReader(tempSorted);
            long line = 0L;
            int previousBucket = 0;
            int previousScore = 0;
            final ByteArrayDataInput input = new ByteArrayDataInput();
            final BytesRefBuilder tmp1 = new BytesRefBuilder();
            final BytesRef tmp2 = new BytesRef();
            while (reader.read(tmp1)) {
                input.reset(tmp1.bytes());
                final int currentScore = input.readInt();
                int bucket;
                if (line > 0L && currentScore == previousScore) {
                    bucket = previousBucket;
                }
                else {
                    bucket = (int)(line * this.buckets / inputLines);
                }
                previousScore = currentScore;
                previousBucket = bucket;
                tmp2.bytes = tmp1.bytes();
                tmp2.offset = input.getPosition();
                tmp2.length = tmp1.length() - input.getPosition();
                builder.add(tmp2, bucket);
                ++line;
                ++this.count;
            }
            this.higherWeightsCompletion = builder.build();
            this.normalCompletion = new FSTCompletion(this.higherWeightsCompletion.getFST(), false, this.exactMatchFirst);
            success = true;
        }
        finally {
            IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)reader, (Closeable)writer, sorter });
            if (success) {
                Files.delete(tempSorted);
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { tempInput, tempSorted });
            }
        }
    }
    
    private static int encodeWeight(final long value) {
        if (value < -2147483648L || value > 2147483647L) {
            throw new UnsupportedOperationException("cannot encode value: " + value);
        }
        return (int)value;
    }
    
    @Override
    public List<LookupResult> lookup(final CharSequence key, final Set<BytesRef> contexts, final boolean higherWeightsFirst, final int num) {
        if (contexts != null) {
            throw new IllegalArgumentException("this suggester doesn't support contexts");
        }
        List<FSTCompletion.Completion> completions;
        if (higherWeightsFirst) {
            completions = this.higherWeightsCompletion.lookup(key, num);
        }
        else {
            completions = this.normalCompletion.lookup(key, num);
        }
        final ArrayList<LookupResult> results = new ArrayList<LookupResult>(completions.size());
        final CharsRefBuilder spare = new CharsRefBuilder();
        for (final FSTCompletion.Completion c : completions) {
            spare.copyUTF8Bytes(c.utf8);
            results.add(new LookupResult(spare.toString(), c.bucket));
        }
        return results;
    }
    
    public Object get(final CharSequence key) {
        final int bucket = this.normalCompletion.getBucket(key);
        return (bucket == -1) ? null : Long.valueOf(bucket);
    }
    
    @Override
    public synchronized boolean store(final DataOutput output) throws IOException {
        output.writeVLong(this.count);
        if (this.normalCompletion == null || this.normalCompletion.getFST() == null) {
            return false;
        }
        this.normalCompletion.getFST().save(output);
        return true;
    }
    
    @Override
    public synchronized boolean load(final DataInput input) throws IOException {
        this.count = input.readVLong();
        this.higherWeightsCompletion = new FSTCompletion((FST<Object>)new FST(input, (Outputs)NoOutputs.getSingleton()));
        this.normalCompletion = new FSTCompletion(this.higherWeightsCompletion.getFST(), false, this.exactMatchFirst);
        return true;
    }
    
    public long ramBytesUsed() {
        long mem = RamUsageEstimator.shallowSizeOf((Object)this) + RamUsageEstimator.shallowSizeOf((Object)this.normalCompletion) + RamUsageEstimator.shallowSizeOf((Object)this.higherWeightsCompletion);
        if (this.normalCompletion != null) {
            mem += this.normalCompletion.getFST().ramBytesUsed();
        }
        if (this.higherWeightsCompletion != null && (this.normalCompletion == null || this.normalCompletion.getFST() != this.higherWeightsCompletion.getFST())) {
            mem += this.higherWeightsCompletion.getFST().ramBytesUsed();
        }
        return mem;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        final List<Accountable> resources = new ArrayList<Accountable>();
        if (this.normalCompletion != null) {
            resources.add(Accountables.namedAccountable("fst", (Accountable)this.normalCompletion.getFST()));
        }
        if (this.higherWeightsCompletion != null && (this.normalCompletion == null || this.normalCompletion.getFST() != this.higherWeightsCompletion.getFST())) {
            resources.add(Accountables.namedAccountable("higher weights fst", (Accountable)this.higherWeightsCompletion.getFST()));
        }
        return (Collection<Accountable>)Collections.unmodifiableList((List<?>)resources);
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    static {
        FSTCompletionLookup.INVALID_BUCKETS_COUNT = -1;
    }
}
