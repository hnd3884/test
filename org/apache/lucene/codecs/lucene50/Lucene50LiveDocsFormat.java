package org.apache.lucene.codecs.lucene50;

import java.util.Collection;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.store.ChecksumIndexInput;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.MutableBits;
import org.apache.lucene.codecs.LiveDocsFormat;

public final class Lucene50LiveDocsFormat extends LiveDocsFormat
{
    private static final String EXTENSION = "liv";
    private static final String CODEC_NAME = "Lucene50LiveDocs";
    private static final int VERSION_START = 0;
    private static final int VERSION_CURRENT = 0;
    
    @Override
    public MutableBits newLiveDocs(final int size) throws IOException {
        final FixedBitSet bits = new FixedBitSet(size);
        bits.set(0, size);
        return bits;
    }
    
    @Override
    public MutableBits newLiveDocs(final Bits existing) throws IOException {
        final FixedBitSet fbs = (FixedBitSet)existing;
        return fbs.clone();
    }
    
    @Override
    public Bits readLiveDocs(final Directory dir, final SegmentCommitInfo info, final IOContext context) throws IOException {
        final long gen = info.getDelGen();
        final String name = IndexFileNames.fileNameFromGeneration(info.info.name, "liv", gen);
        final int length = info.info.maxDoc();
        try (final ChecksumIndexInput input = dir.openChecksumInput(name, context)) {
            Throwable priorE = null;
            try {
                CodecUtil.checkIndexHeader(input, "Lucene50LiveDocs", 0, 0, info.info.getId(), Long.toString(gen, 36));
                final long[] data = new long[FixedBitSet.bits2words(length)];
                for (int i = 0; i < data.length; ++i) {
                    data[i] = input.readLong();
                }
                final FixedBitSet fbs = new FixedBitSet(data, length);
                if (fbs.length() - fbs.cardinality() != info.getDelCount()) {
                    throw new CorruptIndexException("bits.deleted=" + (fbs.length() - fbs.cardinality()) + " info.delcount=" + info.getDelCount(), input);
                }
                return fbs;
            }
            catch (final Throwable exception) {
                priorE = exception;
                CodecUtil.checkFooter(input, priorE);
            }
        }
        throw new AssertionError();
    }
    
    @Override
    public void writeLiveDocs(final MutableBits bits, final Directory dir, final SegmentCommitInfo info, final int newDelCount, final IOContext context) throws IOException {
        final long gen = info.getNextDelGen();
        final String name = IndexFileNames.fileNameFromGeneration(info.info.name, "liv", gen);
        final FixedBitSet fbs = (FixedBitSet)bits;
        if (fbs.length() - fbs.cardinality() != info.getDelCount() + newDelCount) {
            throw new CorruptIndexException("bits.deleted=" + (fbs.length() - fbs.cardinality()) + " info.delcount=" + info.getDelCount() + " newdelcount=" + newDelCount, name);
        }
        final long[] data = fbs.getBits();
        try (final IndexOutput output = dir.createOutput(name, context)) {
            CodecUtil.writeIndexHeader(output, "Lucene50LiveDocs", 0, info.info.getId(), Long.toString(gen, 36));
            for (int i = 0; i < data.length; ++i) {
                output.writeLong(data[i]);
            }
            CodecUtil.writeFooter(output);
        }
    }
    
    @Override
    public void files(final SegmentCommitInfo info, final Collection<String> files) throws IOException {
        if (info.hasDeletions()) {
            files.add(IndexFileNames.fileNameFromGeneration(info.info.name, "liv", info.getDelGen()));
        }
    }
}
