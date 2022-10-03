package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.fst.BytesRefFSTEnum;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.index.Fields;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.codecs.blocktree.BlockTreeTermsWriter;
import java.util.ArrayList;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.store.RAMOutputStream;
import java.util.List;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.PairOutputs;
import org.apache.lucene.codecs.FieldsConsumer;

public final class VersionBlockTreeTermsWriter extends FieldsConsumer
{
    static final PairOutputs<BytesRef, Long> FST_OUTPUTS;
    static final PairOutputs.Pair<BytesRef, Long> NO_OUTPUT;
    public static final int DEFAULT_MIN_BLOCK_SIZE = 25;
    public static final int DEFAULT_MAX_BLOCK_SIZE = 48;
    static final int OUTPUT_FLAGS_NUM_BITS = 2;
    static final int OUTPUT_FLAGS_MASK = 3;
    static final int OUTPUT_FLAG_IS_FLOOR = 1;
    static final int OUTPUT_FLAG_HAS_TERMS = 2;
    static final String TERMS_EXTENSION = "tiv";
    static final String TERMS_CODEC_NAME = "VersionBlockTreeTermsDict";
    public static final int VERSION_START = 1;
    public static final int VERSION_CURRENT = 1;
    static final String TERMS_INDEX_EXTENSION = "tipv";
    static final String TERMS_INDEX_CODEC_NAME = "VersionBlockTreeTermsIndex";
    private final IndexOutput out;
    private final IndexOutput indexOut;
    final int maxDoc;
    final int minItemsInBlock;
    final int maxItemsInBlock;
    final PostingsWriterBase postingsWriter;
    final FieldInfos fieldInfos;
    private final List<FieldMetaData> fields;
    private final RAMOutputStream scratchBytes;
    private final IntsRefBuilder scratchIntsRef;
    private boolean closed;
    
    public VersionBlockTreeTermsWriter(final SegmentWriteState state, final PostingsWriterBase postingsWriter, final int minItemsInBlock, final int maxItemsInBlock) throws IOException {
        this.fields = new ArrayList<FieldMetaData>();
        this.scratchBytes = new RAMOutputStream();
        this.scratchIntsRef = new IntsRefBuilder();
        BlockTreeTermsWriter.validateSettings(minItemsInBlock, maxItemsInBlock);
        this.maxDoc = state.segmentInfo.maxDoc();
        final String termsFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tiv");
        this.out = state.directory.createOutput(termsFileName, state.context);
        boolean success = false;
        IndexOutput indexOut = null;
        try {
            this.fieldInfos = state.fieldInfos;
            this.minItemsInBlock = minItemsInBlock;
            this.maxItemsInBlock = maxItemsInBlock;
            CodecUtil.writeIndexHeader((DataOutput)this.out, "VersionBlockTreeTermsDict", 1, state.segmentInfo.getId(), state.segmentSuffix);
            final String termsIndexFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tipv");
            indexOut = state.directory.createOutput(termsIndexFileName, state.context);
            CodecUtil.writeIndexHeader((DataOutput)indexOut, "VersionBlockTreeTermsIndex", 1, state.segmentInfo.getId(), state.segmentSuffix);
            (this.postingsWriter = postingsWriter).init(this.out, state);
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.out, (Closeable)indexOut });
            }
        }
        this.indexOut = indexOut;
    }
    
    private void writeTrailer(final IndexOutput out, final long dirStart) throws IOException {
        out.writeLong(dirStart);
    }
    
    private void writeIndexTrailer(final IndexOutput indexOut, final long dirStart) throws IOException {
        indexOut.writeLong(dirStart);
    }
    
    public void write(final Fields fields) throws IOException {
        String lastField = null;
        for (final String field : fields) {
            assert lastField.compareTo(field) < 0;
            lastField = field;
            final Terms terms = fields.terms(field);
            if (terms == null) {
                continue;
            }
            final TermsEnum termsEnum = terms.iterator();
            final TermsWriter termsWriter = new TermsWriter(this.fieldInfos.fieldInfo(field));
            while (true) {
                final BytesRef term = termsEnum.next();
                if (term == null) {
                    break;
                }
                termsWriter.write(term, termsEnum);
            }
            termsWriter.finish();
        }
    }
    
    static long encodeOutput(final long fp, final boolean hasTerms, final boolean isFloor) {
        assert fp < 4611686018427387904L;
        return fp << 2 | (long)(hasTerms ? 2 : 0) | (long)(isFloor ? 1 : 0);
    }
    
    static String brToString(final BytesRef b) {
        try {
            return b.utf8ToString() + " " + b;
        }
        catch (final Throwable t) {
            return b.toString();
        }
    }
    
    static String brToString(final byte[] b) {
        return brToString(new BytesRef(b));
    }
    
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        boolean success = false;
        try {
            final long dirStart = this.out.getFilePointer();
            final long indexDirStart = this.indexOut.getFilePointer();
            this.out.writeVInt(this.fields.size());
            for (final FieldMetaData field : this.fields) {
                this.out.writeVInt(field.fieldInfo.number);
                assert field.numTerms > 0L;
                this.out.writeVLong(field.numTerms);
                this.out.writeVInt(((BytesRef)field.rootCode.output1).length);
                this.out.writeBytes(((BytesRef)field.rootCode.output1).bytes, ((BytesRef)field.rootCode.output1).offset, ((BytesRef)field.rootCode.output1).length);
                this.out.writeVLong((long)field.rootCode.output2);
                this.out.writeVInt(field.longsSize);
                this.indexOut.writeVLong(field.indexStartFP);
                writeBytesRef(this.out, field.minTerm);
                writeBytesRef(this.out, field.maxTerm);
            }
            this.writeTrailer(this.out, dirStart);
            CodecUtil.writeFooter(this.out);
            this.writeIndexTrailer(this.indexOut, indexDirStart);
            CodecUtil.writeFooter(this.indexOut);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)this.out, (Closeable)this.indexOut, (Closeable)this.postingsWriter });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.out, (Closeable)this.indexOut, (Closeable)this.postingsWriter });
            }
        }
    }
    
    private static void writeBytesRef(final IndexOutput out, final BytesRef bytes) throws IOException {
        out.writeVInt(bytes.length);
        out.writeBytes(bytes.bytes, bytes.offset, bytes.length);
    }
    
    static {
        FST_OUTPUTS = new PairOutputs((Outputs)ByteSequenceOutputs.getSingleton(), (Outputs)PositiveIntOutputs.getSingleton());
        NO_OUTPUT = VersionBlockTreeTermsWriter.FST_OUTPUTS.getNoOutput();
    }
    
    private static class FieldMetaData
    {
        public final FieldInfo fieldInfo;
        public final PairOutputs.Pair<BytesRef, Long> rootCode;
        public final long numTerms;
        public final long indexStartFP;
        private final int longsSize;
        public final BytesRef minTerm;
        public final BytesRef maxTerm;
        
        public FieldMetaData(final FieldInfo fieldInfo, final PairOutputs.Pair<BytesRef, Long> rootCode, final long numTerms, final long indexStartFP, final int longsSize, final BytesRef minTerm, final BytesRef maxTerm) {
            assert numTerms > 0L;
            this.fieldInfo = fieldInfo;
            assert rootCode != null : "field=" + fieldInfo.name + " numTerms=" + numTerms;
            this.rootCode = rootCode;
            this.indexStartFP = indexStartFP;
            this.numTerms = numTerms;
            this.longsSize = longsSize;
            this.minTerm = minTerm;
            this.maxTerm = maxTerm;
        }
    }
    
    private static class PendingEntry
    {
        public final boolean isTerm;
        
        protected PendingEntry(final boolean isTerm) {
            this.isTerm = isTerm;
        }
    }
    
    private static final class PendingTerm extends PendingEntry
    {
        public final byte[] termBytes;
        public final BlockTermState state;
        
        public PendingTerm(final BytesRef term, final BlockTermState state) {
            super(true);
            this.termBytes = new byte[term.length];
            System.arraycopy(term.bytes, term.offset, this.termBytes, 0, term.length);
            this.state = state;
        }
        
        @Override
        public String toString() {
            return VersionBlockTreeTermsWriter.brToString(this.termBytes);
        }
    }
    
    private static final class PendingBlock extends PendingEntry
    {
        public final BytesRef prefix;
        public final long fp;
        public FST<PairOutputs.Pair<BytesRef, Long>> index;
        public List<FST<PairOutputs.Pair<BytesRef, Long>>> subIndices;
        public final boolean hasTerms;
        public final boolean isFloor;
        public final int floorLeadByte;
        private final long maxVersion;
        
        public PendingBlock(final BytesRef prefix, final long maxVersion, final long fp, final boolean hasTerms, final boolean isFloor, final int floorLeadByte, final List<FST<PairOutputs.Pair<BytesRef, Long>>> subIndices) {
            super(false);
            this.prefix = prefix;
            this.maxVersion = maxVersion;
            this.fp = fp;
            this.hasTerms = hasTerms;
            this.isFloor = isFloor;
            this.floorLeadByte = floorLeadByte;
            this.subIndices = subIndices;
        }
        
        @Override
        public String toString() {
            return "BLOCK: " + VersionBlockTreeTermsWriter.brToString(this.prefix);
        }
        
        public void compileIndex(final List<PendingBlock> blocks, final RAMOutputStream scratchBytes, final IntsRefBuilder scratchIntsRef) throws IOException {
            assert !this.isFloor && blocks.size() == 1 : "isFloor=" + this.isFloor + " blocks=" + blocks;
            assert this == blocks.get(0);
            assert scratchBytes.getFilePointer() == 0L;
            long maxVersionIndex = this.maxVersion;
            scratchBytes.writeVLong(VersionBlockTreeTermsWriter.encodeOutput(this.fp, this.hasTerms, this.isFloor));
            if (this.isFloor) {
                scratchBytes.writeVInt(blocks.size() - 1);
                for (int i = 1; i < blocks.size(); ++i) {
                    final PendingBlock sub = blocks.get(i);
                    maxVersionIndex = Math.max(maxVersionIndex, sub.maxVersion);
                    scratchBytes.writeByte((byte)sub.floorLeadByte);
                    assert sub.fp > this.fp;
                    scratchBytes.writeVLong(sub.fp - this.fp << 1 | (long)(sub.hasTerms ? 1 : 0));
                }
            }
            final Builder<PairOutputs.Pair<BytesRef, Long>> indexBuilder = (Builder<PairOutputs.Pair<BytesRef, Long>>)new Builder(FST.INPUT_TYPE.BYTE1, 0, 0, true, false, Integer.MAX_VALUE, (Outputs)VersionBlockTreeTermsWriter.FST_OUTPUTS, false, 0.0f, true, 15);
            final byte[] bytes = new byte[(int)scratchBytes.getFilePointer()];
            assert bytes.length > 0;
            scratchBytes.writeTo(bytes, 0);
            indexBuilder.add(Util.toIntsRef(this.prefix, scratchIntsRef), (Object)VersionBlockTreeTermsWriter.FST_OUTPUTS.newPair((Object)new BytesRef(bytes, 0, bytes.length), (Object)(Long.MAX_VALUE - maxVersionIndex)));
            scratchBytes.reset();
            for (final PendingBlock block : blocks) {
                if (block.subIndices != null) {
                    for (final FST<PairOutputs.Pair<BytesRef, Long>> subIndex : block.subIndices) {
                        this.append(indexBuilder, subIndex, scratchIntsRef);
                    }
                    block.subIndices = null;
                }
            }
            this.index = (FST<PairOutputs.Pair<BytesRef, Long>>)indexBuilder.finish();
            assert this.subIndices == null;
        }
        
        private void append(final Builder<PairOutputs.Pair<BytesRef, Long>> builder, final FST<PairOutputs.Pair<BytesRef, Long>> subIndex, final IntsRefBuilder scratchIntsRef) throws IOException {
            final BytesRefFSTEnum<PairOutputs.Pair<BytesRef, Long>> subIndexEnum = (BytesRefFSTEnum<PairOutputs.Pair<BytesRef, Long>>)new BytesRefFSTEnum((FST)subIndex);
            BytesRefFSTEnum.InputOutput<PairOutputs.Pair<BytesRef, Long>> indexEnt;
            while ((indexEnt = (BytesRefFSTEnum.InputOutput<PairOutputs.Pair<BytesRef, Long>>)subIndexEnum.next()) != null) {
                builder.add(Util.toIntsRef(indexEnt.input, scratchIntsRef), indexEnt.output);
            }
        }
    }
    
    class TermsWriter
    {
        private final FieldInfo fieldInfo;
        private final int longsSize;
        private long numTerms;
        final FixedBitSet docsSeen;
        long indexStartFP;
        private final BytesRefBuilder lastTerm;
        private int[] prefixStarts;
        private final long[] longs;
        private final List<PendingEntry> pending;
        private final List<PendingBlock> newBlocks;
        private PendingTerm firstPendingTerm;
        private PendingTerm lastPendingTerm;
        private final RAMOutputStream suffixWriter;
        private final RAMOutputStream metaWriter;
        private final RAMOutputStream bytesWriter;
        
        void writeBlocks(final int prefixLength, final int count) throws IOException {
            assert count > 0;
            assert count == this.pending.size();
            int lastSuffixLeadLabel = -1;
            boolean hasTerms = false;
            boolean hasSubBlocks = false;
            final int start = this.pending.size() - count;
            final int end = this.pending.size();
            int nextBlockStart = start;
            int nextFloorLeadLabel = -1;
            for (int i = start; i < end; ++i) {
                final PendingEntry ent = this.pending.get(i);
                int suffixLeadLabel;
                if (ent.isTerm) {
                    final PendingTerm term = (PendingTerm)ent;
                    if (term.termBytes.length == prefixLength) {
                        assert lastSuffixLeadLabel == -1;
                        suffixLeadLabel = -1;
                    }
                    else {
                        suffixLeadLabel = (term.termBytes[prefixLength] & 0xFF);
                    }
                }
                else {
                    final PendingBlock block = (PendingBlock)ent;
                    assert block.prefix.length > prefixLength;
                    suffixLeadLabel = (block.prefix.bytes[block.prefix.offset + prefixLength] & 0xFF);
                }
                if (suffixLeadLabel != lastSuffixLeadLabel) {
                    final int itemsInBlock = i - nextBlockStart;
                    if (itemsInBlock >= VersionBlockTreeTermsWriter.this.minItemsInBlock && end - nextBlockStart > VersionBlockTreeTermsWriter.this.maxItemsInBlock) {
                        final boolean isFloor = itemsInBlock < count;
                        this.newBlocks.add(this.writeBlock(prefixLength, isFloor, nextFloorLeadLabel, nextBlockStart, i, hasTerms, hasSubBlocks));
                        hasTerms = false;
                        hasSubBlocks = false;
                        nextFloorLeadLabel = suffixLeadLabel;
                        nextBlockStart = i;
                    }
                    lastSuffixLeadLabel = suffixLeadLabel;
                }
                if (ent.isTerm) {
                    hasTerms = true;
                }
                else {
                    hasSubBlocks = true;
                }
            }
            if (nextBlockStart < end) {
                final int itemsInBlock2 = end - nextBlockStart;
                final boolean isFloor2 = itemsInBlock2 < count;
                this.newBlocks.add(this.writeBlock(prefixLength, isFloor2, nextFloorLeadLabel, nextBlockStart, end, hasTerms, hasSubBlocks));
            }
            assert !this.newBlocks.isEmpty();
            final PendingBlock firstBlock = this.newBlocks.get(0);
            assert this.newBlocks.size() == 1;
            firstBlock.compileIndex(this.newBlocks, VersionBlockTreeTermsWriter.this.scratchBytes, VersionBlockTreeTermsWriter.this.scratchIntsRef);
            this.pending.subList(this.pending.size() - count, this.pending.size()).clear();
            this.pending.add(firstBlock);
            this.newBlocks.clear();
        }
        
        private PendingBlock writeBlock(final int prefixLength, final boolean isFloor, final int floorLeadLabel, final int start, final int end, final boolean hasTerms, final boolean hasSubBlocks) throws IOException {
            assert end > start;
            final long startFP = VersionBlockTreeTermsWriter.this.out.getFilePointer();
            final boolean hasFloorLeadLabel = isFloor && floorLeadLabel != -1;
            final BytesRef prefix = new BytesRef(prefixLength + (hasFloorLeadLabel ? 1 : 0));
            System.arraycopy(this.lastTerm.bytes(), 0, prefix.bytes, 0, prefixLength);
            prefix.length = prefixLength;
            final int numEntries = end - start;
            int code = numEntries << 1;
            if (end == this.pending.size()) {
                code |= 0x1;
            }
            VersionBlockTreeTermsWriter.this.out.writeVInt(code);
            final boolean isLeafBlock = !hasSubBlocks;
            boolean absolute = true;
            long maxVersionInBlock = -1L;
            List<FST<PairOutputs.Pair<BytesRef, Long>>> subIndices;
            if (isLeafBlock) {
                subIndices = null;
                for (int i = start; i < end; ++i) {
                    final PendingEntry ent = this.pending.get(i);
                    assert ent.isTerm : "i=" + i;
                    final PendingTerm term = (PendingTerm)ent;
                    assert StringHelper.startsWith(term.termBytes, prefix) : "term.term=" + term.termBytes + " prefix=" + prefix;
                    final BlockTermState state = term.state;
                    maxVersionInBlock = Math.max(maxVersionInBlock, ((IDVersionTermState)state).idVersion);
                    final int suffix = term.termBytes.length - prefixLength;
                    this.suffixWriter.writeVInt(suffix);
                    this.suffixWriter.writeBytes(term.termBytes, prefixLength, suffix);
                    assert (term.termBytes[prefixLength] & 0xFF) >= floorLeadLabel;
                    VersionBlockTreeTermsWriter.this.postingsWriter.encodeTerm(this.longs, (DataOutput)this.bytesWriter, this.fieldInfo, state, absolute);
                    for (int pos = 0; pos < this.longsSize; ++pos) {
                        assert this.longs[pos] >= 0L;
                        this.metaWriter.writeVLong(this.longs[pos]);
                    }
                    this.bytesWriter.writeTo((DataOutput)this.metaWriter);
                    this.bytesWriter.reset();
                    absolute = false;
                }
            }
            else {
                subIndices = new ArrayList<FST<PairOutputs.Pair<BytesRef, Long>>>();
                for (int i = start; i < end; ++i) {
                    final PendingEntry ent = this.pending.get(i);
                    if (ent.isTerm) {
                        final PendingTerm term = (PendingTerm)ent;
                        assert StringHelper.startsWith(term.termBytes, prefix) : "term.term=" + term.termBytes + " prefix=" + prefix;
                        final BlockTermState state = term.state;
                        maxVersionInBlock = Math.max(maxVersionInBlock, ((IDVersionTermState)state).idVersion);
                        final int suffix = term.termBytes.length - prefixLength;
                        this.suffixWriter.writeVInt(suffix << 1);
                        this.suffixWriter.writeBytes(term.termBytes, prefixLength, suffix);
                        assert (term.termBytes[prefixLength] & 0xFF) >= floorLeadLabel;
                        VersionBlockTreeTermsWriter.this.postingsWriter.encodeTerm(this.longs, (DataOutput)this.bytesWriter, this.fieldInfo, state, absolute);
                        for (int pos = 0; pos < this.longsSize; ++pos) {
                            assert this.longs[pos] >= 0L;
                            this.metaWriter.writeVLong(this.longs[pos]);
                        }
                        this.bytesWriter.writeTo((DataOutput)this.metaWriter);
                        this.bytesWriter.reset();
                        absolute = false;
                    }
                    else {
                        final PendingBlock block = (PendingBlock)ent;
                        maxVersionInBlock = Math.max(maxVersionInBlock, block.maxVersion);
                        assert StringHelper.startsWith(block.prefix, prefix);
                        final int suffix2 = block.prefix.length - prefixLength;
                        assert suffix2 > 0;
                        this.suffixWriter.writeVInt(suffix2 << 1 | 0x1);
                        this.suffixWriter.writeBytes(block.prefix.bytes, prefixLength, suffix2);
                        assert (block.prefix.bytes[prefixLength] & 0xFF) >= floorLeadLabel;
                        assert block.fp < startFP;
                        this.suffixWriter.writeVLong(startFP - block.fp);
                        subIndices.add(block.index);
                    }
                }
                assert subIndices.size() != 0;
            }
            VersionBlockTreeTermsWriter.this.out.writeVInt((int)(this.suffixWriter.getFilePointer() << 1) | (isLeafBlock ? 1 : 0));
            this.suffixWriter.writeTo((DataOutput)VersionBlockTreeTermsWriter.this.out);
            this.suffixWriter.reset();
            VersionBlockTreeTermsWriter.this.out.writeVInt((int)this.metaWriter.getFilePointer());
            this.metaWriter.writeTo((DataOutput)VersionBlockTreeTermsWriter.this.out);
            this.metaWriter.reset();
            if (hasFloorLeadLabel) {
                prefix.bytes[prefix.length++] = (byte)floorLeadLabel;
            }
            return new PendingBlock(prefix, maxVersionInBlock, startFP, hasTerms, isFloor, floorLeadLabel, subIndices);
        }
        
        TermsWriter(final FieldInfo fieldInfo) {
            this.lastTerm = new BytesRefBuilder();
            this.prefixStarts = new int[8];
            this.pending = new ArrayList<PendingEntry>();
            this.newBlocks = new ArrayList<PendingBlock>();
            this.suffixWriter = new RAMOutputStream();
            this.metaWriter = new RAMOutputStream();
            this.bytesWriter = new RAMOutputStream();
            this.fieldInfo = fieldInfo;
            this.docsSeen = new FixedBitSet(VersionBlockTreeTermsWriter.this.maxDoc);
            this.longsSize = VersionBlockTreeTermsWriter.this.postingsWriter.setField(fieldInfo);
            this.longs = new long[this.longsSize];
        }
        
        public void write(final BytesRef text, final TermsEnum termsEnum) throws IOException {
            final BlockTermState state = VersionBlockTreeTermsWriter.this.postingsWriter.writeTerm(text, termsEnum, this.docsSeen);
            if (state != null && ((IDVersionPostingsWriter)VersionBlockTreeTermsWriter.this.postingsWriter).lastDocID != -1) {
                assert state.docFreq != 0;
                assert state.totalTermFreq >= state.docFreq : "postingsWriter=" + VersionBlockTreeTermsWriter.this.postingsWriter;
                this.pushTerm(text);
                final PendingTerm term = new PendingTerm(BytesRef.deepCopyOf(text), state);
                this.pending.add(term);
                ++this.numTerms;
                if (this.firstPendingTerm == null) {
                    this.firstPendingTerm = term;
                }
                this.lastPendingTerm = term;
            }
        }
        
        private void pushTerm(final BytesRef text) throws IOException {
            int limit;
            int pos;
            for (limit = Math.min(this.lastTerm.length(), text.length), pos = 0; pos < limit && this.lastTerm.byteAt(pos) == text.bytes[text.offset + pos]; ++pos) {}
            for (int i = this.lastTerm.length() - 1; i >= pos; --i) {
                final int prefixTopSize = this.pending.size() - this.prefixStarts[i];
                if (prefixTopSize >= VersionBlockTreeTermsWriter.this.minItemsInBlock) {
                    this.writeBlocks(i + 1, prefixTopSize);
                    final int[] prefixStarts = this.prefixStarts;
                    final int n = i;
                    prefixStarts[n] -= prefixTopSize - 1;
                }
            }
            if (this.prefixStarts.length < text.length) {
                this.prefixStarts = ArrayUtil.grow(this.prefixStarts, text.length);
            }
            for (int i = pos; i < text.length; ++i) {
                this.prefixStarts[i] = this.pending.size();
            }
            this.lastTerm.copyBytes(text);
        }
        
        public void finish() throws IOException {
            if (this.numTerms > 0L) {
                this.writeBlocks(0, this.pending.size());
                assert this.pending.size() == 1 && !this.pending.get(0).isTerm : "pending.size()=" + this.pending.size() + " pending=" + this.pending;
                final PendingBlock root = this.pending.get(0);
                assert root.prefix.length == 0;
                assert root.index.getEmptyOutput() != null;
                this.indexStartFP = VersionBlockTreeTermsWriter.this.indexOut.getFilePointer();
                root.index.save((DataOutput)VersionBlockTreeTermsWriter.this.indexOut);
                assert this.firstPendingTerm != null;
                final BytesRef minTerm = new BytesRef(this.firstPendingTerm.termBytes);
                assert this.lastPendingTerm != null;
                final BytesRef maxTerm = new BytesRef(this.lastPendingTerm.termBytes);
                VersionBlockTreeTermsWriter.this.fields.add(new FieldMetaData(this.fieldInfo, (PairOutputs.Pair<BytesRef, Long>)this.pending.get(0).index.getEmptyOutput(), this.numTerms, this.indexStartFP, this.longsSize, minTerm, maxTerm));
            }
        }
    }
}
