package org.apache.lucene.search.suggest;

import java.util.HashSet;
import java.util.Iterator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.ByteArrayDataOutput;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.store.ByteArrayDataInput;
import java.io.IOException;
import java.util.Set;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.BytesRef;
import java.util.Comparator;
import org.apache.lucene.util.OfflineSorter;
import java.nio.file.Path;

public class SortedInputIterator implements InputIterator
{
    private final InputIterator source;
    private Path tempInput;
    private Path tempSorted;
    private final OfflineSorter.ByteSequencesReader reader;
    private final Comparator<BytesRef> comparator;
    private final boolean hasPayloads;
    private final boolean hasContexts;
    private boolean done;
    private long weight;
    private final BytesRefBuilder scratch;
    private BytesRef payload;
    private Set<BytesRef> contexts;
    private final Comparator<BytesRef> tieBreakByCostComparator;
    
    public SortedInputIterator(final InputIterator source) throws IOException {
        this(source, BytesRef.getUTF8SortedAsUnicodeComparator());
    }
    
    public SortedInputIterator(final InputIterator source, final Comparator<BytesRef> comparator) throws IOException {
        this.done = false;
        this.scratch = new BytesRefBuilder();
        this.payload = new BytesRef();
        this.contexts = null;
        this.tieBreakByCostComparator = new Comparator<BytesRef>() {
            private final BytesRef leftScratch = new BytesRef();
            private final BytesRef rightScratch = new BytesRef();
            private final ByteArrayDataInput input = new ByteArrayDataInput();
            
            @Override
            public int compare(final BytesRef left, final BytesRef right) {
                this.leftScratch.bytes = left.bytes;
                this.leftScratch.offset = left.offset;
                this.leftScratch.length = left.length;
                this.rightScratch.bytes = right.bytes;
                this.rightScratch.offset = right.offset;
                this.rightScratch.length = right.length;
                final long leftCost = SortedInputIterator.this.decode(this.leftScratch, this.input);
                final long rightCost = SortedInputIterator.this.decode(this.rightScratch, this.input);
                if (SortedInputIterator.this.hasPayloads) {
                    SortedInputIterator.this.decodePayload(this.leftScratch, this.input);
                    SortedInputIterator.this.decodePayload(this.rightScratch, this.input);
                }
                if (SortedInputIterator.this.hasContexts) {
                    SortedInputIterator.this.decodeContexts(this.leftScratch, this.input);
                    SortedInputIterator.this.decodeContexts(this.rightScratch, this.input);
                }
                final int cmp = SortedInputIterator.this.comparator.compare(this.leftScratch, this.rightScratch);
                if (cmp != 0) {
                    return cmp;
                }
                return Long.compare(leftCost, rightCost);
            }
        };
        this.hasPayloads = source.hasPayloads();
        this.hasContexts = source.hasContexts();
        this.source = source;
        this.comparator = comparator;
        this.reader = this.sort();
    }
    
    public BytesRef next() throws IOException {
        boolean success = false;
        if (this.done) {
            return null;
        }
        try {
            final ByteArrayDataInput input = new ByteArrayDataInput();
            if (this.reader.read(this.scratch)) {
                final BytesRef bytes = this.scratch.get();
                this.weight = this.decode(bytes, input);
                if (this.hasPayloads) {
                    this.payload = this.decodePayload(bytes, input);
                }
                if (this.hasContexts) {
                    this.contexts = this.decodeContexts(bytes, input);
                }
                success = true;
                return bytes;
            }
            this.close();
            final boolean done = true;
            this.done = done;
            success = done;
            return null;
        }
        finally {
            if (!success) {
                this.done = true;
                this.close();
            }
        }
    }
    
    @Override
    public long weight() {
        return this.weight;
    }
    
    @Override
    public BytesRef payload() {
        if (this.hasPayloads) {
            return this.payload;
        }
        return null;
    }
    
    @Override
    public boolean hasPayloads() {
        return this.hasPayloads;
    }
    
    @Override
    public Set<BytesRef> contexts() {
        return this.contexts;
    }
    
    @Override
    public boolean hasContexts() {
        return this.hasContexts;
    }
    
    private OfflineSorter.ByteSequencesReader sort() throws IOException {
        final String prefix = this.getClass().getSimpleName();
        final Path directory = OfflineSorter.getDefaultTempDir();
        this.tempInput = Files.createTempFile(directory, prefix, ".input", (FileAttribute<?>[])new FileAttribute[0]);
        this.tempSorted = Files.createTempFile(directory, prefix, ".sorted", (FileAttribute<?>[])new FileAttribute[0]);
        final OfflineSorter.ByteSequencesWriter writer = new OfflineSorter.ByteSequencesWriter(this.tempInput);
        boolean success = false;
        try {
            final byte[] buffer = new byte[0];
            final ByteArrayDataOutput output = new ByteArrayDataOutput(buffer);
            BytesRef spare;
            while ((spare = this.source.next()) != null) {
                this.encode(writer, output, buffer, spare, this.source.payload(), this.source.contexts(), this.source.weight());
            }
            writer.close();
            new OfflineSorter((Comparator)this.tieBreakByCostComparator).sort(this.tempInput, this.tempSorted);
            final OfflineSorter.ByteSequencesReader reader = new OfflineSorter.ByteSequencesReader(this.tempSorted);
            success = true;
            return reader;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)writer });
            }
            else {
                try {
                    IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)writer });
                }
                finally {
                    this.close();
                }
            }
        }
    }
    
    private void close() throws IOException {
        boolean success = false;
        try {
            IOUtils.close(new Closeable[] { (Closeable)this.reader });
            success = true;
        }
        finally {
            if (success) {
                IOUtils.deleteFilesIfExist(new Path[] { this.tempInput, this.tempSorted });
            }
            else {
                IOUtils.deleteFilesIgnoringExceptions(new Path[] { this.tempInput, this.tempSorted });
            }
        }
    }
    
    protected void encode(final OfflineSorter.ByteSequencesWriter writer, final ByteArrayDataOutput output, byte[] buffer, final BytesRef spare, final BytesRef payload, final Set<BytesRef> contexts, final long weight) throws IOException {
        int requiredLength = spare.length + 8 + (this.hasPayloads ? (2 + payload.length) : 0);
        if (this.hasContexts) {
            for (final BytesRef ctx : contexts) {
                requiredLength += 2 + ctx.length;
            }
            requiredLength += 2;
        }
        if (requiredLength >= buffer.length) {
            buffer = ArrayUtil.grow(buffer, requiredLength);
        }
        output.reset(buffer);
        output.writeBytes(spare.bytes, spare.offset, spare.length);
        if (this.hasContexts) {
            for (final BytesRef ctx : contexts) {
                output.writeBytes(ctx.bytes, ctx.offset, ctx.length);
                output.writeShort((short)ctx.length);
            }
            output.writeShort((short)contexts.size());
        }
        if (this.hasPayloads) {
            output.writeBytes(payload.bytes, payload.offset, payload.length);
            output.writeShort((short)payload.length);
        }
        output.writeLong(weight);
        writer.write(buffer, 0, output.getPosition());
    }
    
    protected long decode(final BytesRef scratch, final ByteArrayDataInput tmpInput) {
        tmpInput.reset(scratch.bytes);
        tmpInput.skipBytes((long)(scratch.length - 8));
        scratch.length -= 8;
        return tmpInput.readLong();
    }
    
    protected Set<BytesRef> decodeContexts(final BytesRef scratch, final ByteArrayDataInput tmpInput) {
        tmpInput.reset(scratch.bytes);
        tmpInput.skipBytes((long)(scratch.length - 2));
        final short ctxSetSize = tmpInput.readShort();
        scratch.length -= 2;
        final Set<BytesRef> contextSet = new HashSet<BytesRef>();
        for (short i = 0; i < ctxSetSize; ++i) {
            tmpInput.setPosition(scratch.length - 2);
            final short curContextLength = tmpInput.readShort();
            scratch.length -= 2;
            tmpInput.setPosition(scratch.length - curContextLength);
            final BytesRef contextSpare = new BytesRef((int)curContextLength);
            tmpInput.readBytes(contextSpare.bytes, 0, (int)curContextLength);
            contextSpare.length = curContextLength;
            contextSet.add(contextSpare);
            scratch.length -= curContextLength;
        }
        return contextSet;
    }
    
    protected BytesRef decodePayload(final BytesRef scratch, final ByteArrayDataInput tmpInput) {
        tmpInput.reset(scratch.bytes);
        tmpInput.skipBytes((long)(scratch.length - 2));
        final short payloadLength = tmpInput.readShort();
        tmpInput.setPosition(scratch.length - 2 - payloadLength);
        final BytesRef payloadScratch = new BytesRef((int)payloadLength);
        tmpInput.readBytes(payloadScratch.bytes, 0, (int)payloadLength);
        payloadScratch.length = payloadLength;
        scratch.length -= 2;
        scratch.length -= payloadLength;
        return payloadScratch;
    }
}
