package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.fst.BytesRefFSTEnum;
import org.apache.lucene.util.fst.Util;
import org.apache.lucene.util.fst.Outputs;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.ByteSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.Fields;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.util.BitSet;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.store.RAMOutputStream;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.FixedBitSet;
import java.util.List;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.codecs.PostingsWriterBase;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.FieldsConsumer;

public final class BlockTreeTermsWriter extends FieldsConsumer
{
    public static final int DEFAULT_MIN_BLOCK_SIZE = 25;
    public static final int DEFAULT_MAX_BLOCK_SIZE = 48;
    private final IndexOutput termsOut;
    private final IndexOutput indexOut;
    final int maxDoc;
    final int minItemsInBlock;
    final int maxItemsInBlock;
    final int minItemsInAutoPrefix;
    final int maxItemsInAutoPrefix;
    final PostingsWriterBase postingsWriter;
    final FieldInfos fieldInfos;
    private final List<FieldMetaData> fields;
    final FixedBitSet prefixDocs;
    final BitSetTermsEnum prefixFixedBitsTermsEnum;
    private TermsEnum prefixTermsEnum;
    private PostingsEnum prefixDocsEnum;
    private final RAMOutputStream scratchBytes;
    private final IntsRefBuilder scratchIntsRef;
    static final BytesRef EMPTY_BYTES_REF;
    private boolean closed;
    
    public BlockTreeTermsWriter(final SegmentWriteState state, final PostingsWriterBase postingsWriter, final int minItemsInBlock, final int maxItemsInBlock) throws IOException {
        this(state, postingsWriter, minItemsInBlock, maxItemsInBlock, 0, 0);
    }
    
    public BlockTreeTermsWriter(final SegmentWriteState state, final PostingsWriterBase postingsWriter, final int minItemsInBlock, final int maxItemsInBlock, final int minItemsInAutoPrefix, final int maxItemsInAutoPrefix) throws IOException {
        this.fields = new ArrayList<FieldMetaData>();
        this.scratchBytes = new RAMOutputStream();
        this.scratchIntsRef = new IntsRefBuilder();
        validateSettings(minItemsInBlock, maxItemsInBlock);
        this.minItemsInBlock = minItemsInBlock;
        this.maxItemsInBlock = maxItemsInBlock;
        validateAutoPrefixSettings(minItemsInAutoPrefix, maxItemsInAutoPrefix);
        if (minItemsInAutoPrefix != 0) {
            this.prefixDocs = new FixedBitSet(state.segmentInfo.maxDoc());
            this.prefixFixedBitsTermsEnum = new BitSetTermsEnum(this.prefixDocs);
        }
        else {
            this.prefixDocs = null;
            this.prefixFixedBitsTermsEnum = null;
        }
        this.minItemsInAutoPrefix = minItemsInAutoPrefix;
        this.maxItemsInAutoPrefix = maxItemsInAutoPrefix;
        this.maxDoc = state.segmentInfo.maxDoc();
        this.fieldInfos = state.fieldInfos;
        this.postingsWriter = postingsWriter;
        final String termsName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tim");
        this.termsOut = state.directory.createOutput(termsName, state.context);
        boolean success = false;
        IndexOutput indexOut = null;
        try {
            CodecUtil.writeIndexHeader(this.termsOut, "BlockTreeTermsDict", 2, state.segmentInfo.getId(), state.segmentSuffix);
            if (minItemsInAutoPrefix == 0) {
                this.termsOut.writeByte((byte)0);
            }
            else {
                this.termsOut.writeByte((byte)1);
            }
            final String indexName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "tip");
            indexOut = state.directory.createOutput(indexName, state.context);
            CodecUtil.writeIndexHeader(indexOut, "BlockTreeTermsIndex", 2, state.segmentInfo.getId(), state.segmentSuffix);
            postingsWriter.init(this.termsOut, state);
            this.indexOut = indexOut;
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(this.termsOut, indexOut);
            }
        }
    }
    
    private void writeTrailer(final IndexOutput out, final long dirStart) throws IOException {
        out.writeLong(dirStart);
    }
    
    private void writeIndexTrailer(final IndexOutput indexOut, final long dirStart) throws IOException {
        indexOut.writeLong(dirStart);
    }
    
    public static void validateSettings(final int minItemsInBlock, final int maxItemsInBlock) {
        if (minItemsInBlock <= 1) {
            throw new IllegalArgumentException("minItemsInBlock must be >= 2; got " + minItemsInBlock);
        }
        if (minItemsInBlock > maxItemsInBlock) {
            throw new IllegalArgumentException("maxItemsInBlock must be >= minItemsInBlock; got maxItemsInBlock=" + maxItemsInBlock + " minItemsInBlock=" + minItemsInBlock);
        }
        if (2 * (minItemsInBlock - 1) > maxItemsInBlock) {
            throw new IllegalArgumentException("maxItemsInBlock must be at least 2*(minItemsInBlock-1); got maxItemsInBlock=" + maxItemsInBlock + " minItemsInBlock=" + minItemsInBlock);
        }
    }
    
    public static void validateAutoPrefixSettings(final int minItemsInAutoPrefix, final int maxItemsInAutoPrefix) {
        if (minItemsInAutoPrefix != 0) {
            if (minItemsInAutoPrefix < 2) {
                throw new IllegalArgumentException("minItemsInAutoPrefix must be at least 2; got minItemsInAutoPrefix=" + minItemsInAutoPrefix);
            }
            if (minItemsInAutoPrefix > maxItemsInAutoPrefix) {
                throw new IllegalArgumentException("maxItemsInAutoPrefix must be >= minItemsInAutoPrefix; got maxItemsInAutoPrefix=" + maxItemsInAutoPrefix + " minItemsInAutoPrefix=" + minItemsInAutoPrefix);
            }
            if (2 * (minItemsInAutoPrefix - 1) > maxItemsInAutoPrefix) {
                throw new IllegalArgumentException("maxItemsInAutoPrefix must be at least 2*(minItemsInAutoPrefix-1); got maxItemsInAutoPrefix=" + maxItemsInAutoPrefix + " minItemsInAutoPrefix=" + minItemsInAutoPrefix);
            }
        }
        else if (maxItemsInAutoPrefix != 0) {
            throw new IllegalArgumentException("maxItemsInAutoPrefix must be 0 (disabled) when minItemsInAutoPrefix is 0");
        }
    }
    
    @Override
    public void write(final Fields fields) throws IOException {
        String lastField = null;
        for (final String field : fields) {
            assert lastField.compareTo(field) < 0;
            lastField = field;
            final Terms terms = fields.terms(field);
            if (terms == null) {
                continue;
            }
            final FieldInfo fieldInfo = this.fieldInfos.fieldInfo(field);
            List<AutoPrefixTermsWriter.PrefixTerm> prefixTerms;
            if (this.minItemsInAutoPrefix != 0) {
                if (fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                    throw new IllegalStateException("ranges can only be indexed with IndexOptions.DOCS (field: " + fieldInfo.name + ")");
                }
                prefixTerms = new AutoPrefixTermsWriter(terms, this.minItemsInAutoPrefix, this.maxItemsInAutoPrefix).prefixes;
            }
            else {
                prefixTerms = null;
            }
            final TermsEnum termsEnum = terms.iterator();
            final TermsWriter termsWriter = new TermsWriter(this.fieldInfos.fieldInfo(field));
            int prefixTermUpto = 0;
            while (true) {
                final BytesRef term = termsEnum.next();
                if (prefixTerms != null) {
                    while (prefixTermUpto < prefixTerms.size() && (term == null || prefixTerms.get(prefixTermUpto).compareTo(term) <= 0)) {
                        final AutoPrefixTermsWriter.PrefixTerm prefixTerm = prefixTerms.get(prefixTermUpto);
                        termsWriter.write(prefixTerm.term, this.getAutoPrefixTermsEnum(terms, prefixTerm), prefixTerm);
                        ++prefixTermUpto;
                    }
                }
                if (term == null) {
                    break;
                }
                termsWriter.write(term, termsEnum, null);
            }
            assert prefixTermUpto == prefixTerms.size();
            termsWriter.finish();
        }
    }
    
    private TermsEnum getAutoPrefixTermsEnum(final Terms terms, final AutoPrefixTermsWriter.PrefixTerm prefix) throws IOException {
        assert this.prefixDocs != null;
        this.prefixDocs.clear(0, this.prefixDocs.length());
        this.prefixTermsEnum = prefix.getTermsEnum(terms.iterator());
        while (this.prefixTermsEnum.next() != null) {
            this.prefixDocsEnum = this.prefixTermsEnum.postings(this.prefixDocsEnum, 0);
            this.prefixDocs.or(this.prefixDocsEnum);
        }
        return this.prefixFixedBitsTermsEnum;
    }
    
    static long encodeOutput(final long fp, final boolean hasTerms, final boolean isFloor) {
        assert fp < 4611686018427387904L;
        return fp << 2 | (long)(hasTerms ? 2 : 0) | (long)(isFloor ? 1 : 0);
    }
    
    static String brToString(final BytesRef b) {
        if (b == null) {
            return "(null)";
        }
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
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        boolean success = false;
        try {
            final long dirStart = this.termsOut.getFilePointer();
            final long indexDirStart = this.indexOut.getFilePointer();
            this.termsOut.writeVInt(this.fields.size());
            for (final FieldMetaData field : this.fields) {
                this.termsOut.writeVInt(field.fieldInfo.number);
                assert field.numTerms > 0L;
                this.termsOut.writeVLong(field.numTerms);
                this.termsOut.writeVInt(field.rootCode.length);
                this.termsOut.writeBytes(field.rootCode.bytes, field.rootCode.offset, field.rootCode.length);
                assert field.fieldInfo.getIndexOptions() != IndexOptions.NONE;
                if (field.fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                    this.termsOut.writeVLong(field.sumTotalTermFreq);
                }
                this.termsOut.writeVLong(field.sumDocFreq);
                this.termsOut.writeVInt(field.docCount);
                this.termsOut.writeVInt(field.longsSize);
                this.indexOut.writeVLong(field.indexStartFP);
                writeBytesRef(this.termsOut, field.minTerm);
                writeBytesRef(this.termsOut, field.maxTerm);
            }
            this.writeTrailer(this.termsOut, dirStart);
            CodecUtil.writeFooter(this.termsOut);
            this.writeIndexTrailer(this.indexOut, indexDirStart);
            CodecUtil.writeFooter(this.indexOut);
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(this.termsOut, this.indexOut, this.postingsWriter);
            }
            else {
                IOUtils.closeWhileHandlingException(this.termsOut, this.indexOut, this.postingsWriter);
            }
        }
    }
    
    private static void writeBytesRef(final IndexOutput out, final BytesRef bytes) throws IOException {
        out.writeVInt(bytes.length);
        out.writeBytes(bytes.bytes, bytes.offset, bytes.length);
    }
    
    static {
        EMPTY_BYTES_REF = new BytesRef();
    }
    
    private static class FieldMetaData
    {
        public final FieldInfo fieldInfo;
        public final BytesRef rootCode;
        public final long numTerms;
        public final long indexStartFP;
        public final long sumTotalTermFreq;
        public final long sumDocFreq;
        public final int docCount;
        private final int longsSize;
        public final BytesRef minTerm;
        public final BytesRef maxTerm;
        
        public FieldMetaData(final FieldInfo fieldInfo, final BytesRef rootCode, final long numTerms, final long indexStartFP, final long sumTotalTermFreq, final long sumDocFreq, final int docCount, final int longsSize, final BytesRef minTerm, final BytesRef maxTerm) {
            assert numTerms > 0L;
            this.fieldInfo = fieldInfo;
            assert rootCode != null : "field=" + fieldInfo.name + " numTerms=" + numTerms;
            this.rootCode = rootCode;
            this.indexStartFP = indexStartFP;
            this.numTerms = numTerms;
            this.sumTotalTermFreq = sumTotalTermFreq;
            this.sumDocFreq = sumDocFreq;
            this.docCount = docCount;
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
        public final AutoPrefixTermsWriter.PrefixTerm prefixTerm;
        public PendingTerm other;
        
        public PendingTerm(final BytesRef term, final BlockTermState state, final AutoPrefixTermsWriter.PrefixTerm prefixTerm) {
            super(true);
            this.termBytes = new byte[term.length];
            System.arraycopy(term.bytes, term.offset, this.termBytes, 0, term.length);
            this.state = state;
            this.prefixTerm = prefixTerm;
        }
        
        @Override
        public String toString() {
            return "TERM: " + BlockTreeTermsWriter.brToString(this.termBytes);
        }
    }
    
    private static final class PendingBlock extends PendingEntry
    {
        public final BytesRef prefix;
        public final long fp;
        public FST<BytesRef> index;
        public List<FST<BytesRef>> subIndices;
        public final boolean hasTerms;
        public final boolean isFloor;
        public final int floorLeadByte;
        
        public PendingBlock(final BytesRef prefix, final long fp, final boolean hasTerms, final boolean isFloor, final int floorLeadByte, final List<FST<BytesRef>> subIndices) {
            super(false);
            this.prefix = prefix;
            this.fp = fp;
            this.hasTerms = hasTerms;
            this.isFloor = isFloor;
            this.floorLeadByte = floorLeadByte;
            this.subIndices = subIndices;
        }
        
        @Override
        public String toString() {
            return "BLOCK: prefix=" + BlockTreeTermsWriter.brToString(this.prefix);
        }
        
        public void compileIndex(final List<PendingBlock> blocks, final RAMOutputStream scratchBytes, final IntsRefBuilder scratchIntsRef) throws IOException {
            assert !this.isFloor && blocks.size() == 1 : "isFloor=" + this.isFloor + " blocks=" + blocks;
            assert this == blocks.get(0);
            assert scratchBytes.getFilePointer() == 0L;
            scratchBytes.writeVLong(BlockTreeTermsWriter.encodeOutput(this.fp, this.hasTerms, this.isFloor));
            if (this.isFloor) {
                scratchBytes.writeVInt(blocks.size() - 1);
                for (int i = 1; i < blocks.size(); ++i) {
                    final PendingBlock sub = blocks.get(i);
                    assert sub.floorLeadByte != -1;
                    scratchBytes.writeByte((byte)sub.floorLeadByte);
                    assert sub.fp > this.fp;
                    scratchBytes.writeVLong(sub.fp - this.fp << 1 | (long)(sub.hasTerms ? 1 : 0));
                }
            }
            final ByteSequenceOutputs outputs = ByteSequenceOutputs.getSingleton();
            final Builder<BytesRef> indexBuilder = new Builder<BytesRef>(FST.INPUT_TYPE.BYTE1, 0, 0, true, false, Integer.MAX_VALUE, outputs, false, 0.0f, true, 15);
            final byte[] bytes = new byte[(int)scratchBytes.getFilePointer()];
            assert bytes.length > 0;
            scratchBytes.writeTo(bytes, 0);
            indexBuilder.add(Util.toIntsRef(this.prefix, scratchIntsRef), new BytesRef(bytes, 0, bytes.length));
            scratchBytes.reset();
            for (final PendingBlock block : blocks) {
                if (block.subIndices != null) {
                    for (final FST<BytesRef> subIndex : block.subIndices) {
                        this.append(indexBuilder, subIndex, scratchIntsRef);
                    }
                    block.subIndices = null;
                }
            }
            this.index = indexBuilder.finish();
            assert this.subIndices == null;
        }
        
        private void append(final Builder<BytesRef> builder, final FST<BytesRef> subIndex, final IntsRefBuilder scratchIntsRef) throws IOException {
            final BytesRefFSTEnum<BytesRef> subIndexEnum = new BytesRefFSTEnum<BytesRef>(subIndex);
            BytesRefFSTEnum.InputOutput<BytesRef> indexEnt;
            while ((indexEnt = subIndexEnum.next()) != null) {
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
        long sumTotalTermFreq;
        long sumDocFreq;
        long indexStartFP;
        private final BytesRefBuilder lastTerm;
        private int[] prefixStarts;
        private final long[] longs;
        private final List<PendingEntry> pending;
        private final List<PendingBlock> newBlocks;
        private PendingTerm firstPendingTerm;
        private PendingTerm lastPendingTerm;
        private final RAMOutputStream suffixWriter;
        private final RAMOutputStream statsWriter;
        private final RAMOutputStream metaWriter;
        private final RAMOutputStream bytesWriter;
        
        void writeBlocks(final int prefixLength, final int count) throws IOException {
            assert count > 0;
            assert count == this.pending.size();
            int lastSuffixLeadLabel = -1;
            boolean hasTerms = false;
            boolean hasPrefixTerms = false;
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
                        assert lastSuffixLeadLabel == -1 : "i=" + i + " lastSuffixLeadLabel=" + lastSuffixLeadLabel;
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
                    if (itemsInBlock >= BlockTreeTermsWriter.this.minItemsInBlock && end - nextBlockStart > BlockTreeTermsWriter.this.maxItemsInBlock) {
                        final boolean isFloor = itemsInBlock < count;
                        this.newBlocks.add(this.writeBlock(prefixLength, isFloor, nextFloorLeadLabel, nextBlockStart, i, hasTerms, hasPrefixTerms, hasSubBlocks));
                        hasTerms = false;
                        hasSubBlocks = false;
                        hasPrefixTerms = false;
                        nextFloorLeadLabel = suffixLeadLabel;
                        nextBlockStart = i;
                    }
                    lastSuffixLeadLabel = suffixLeadLabel;
                }
                if (ent.isTerm) {
                    hasTerms = true;
                    hasPrefixTerms |= (((PendingTerm)ent).prefixTerm != null);
                }
                else {
                    hasSubBlocks = true;
                }
            }
            if (nextBlockStart < end) {
                final int itemsInBlock2 = end - nextBlockStart;
                final boolean isFloor2 = itemsInBlock2 < count;
                this.newBlocks.add(this.writeBlock(prefixLength, isFloor2, nextFloorLeadLabel, nextBlockStart, end, hasTerms, hasPrefixTerms, hasSubBlocks));
            }
            assert !this.newBlocks.isEmpty();
            final PendingBlock firstBlock = this.newBlocks.get(0);
            assert this.newBlocks.size() == 1;
            firstBlock.compileIndex(this.newBlocks, BlockTreeTermsWriter.this.scratchBytes, BlockTreeTermsWriter.this.scratchIntsRef);
            this.pending.subList(this.pending.size() - count, this.pending.size()).clear();
            this.pending.add(firstBlock);
            this.newBlocks.clear();
        }
        
        private PendingBlock writeBlock(final int prefixLength, final boolean isFloor, final int floorLeadLabel, final int start, final int end, final boolean hasTerms, final boolean hasPrefixTerms, final boolean hasSubBlocks) throws IOException {
            assert end > start;
            final long startFP = BlockTreeTermsWriter.this.termsOut.getFilePointer();
            final boolean hasFloorLeadLabel = isFloor && floorLeadLabel != -1;
            final BytesRef prefix = new BytesRef(prefixLength + (hasFloorLeadLabel ? 1 : 0));
            System.arraycopy(this.lastTerm.get().bytes, 0, prefix.bytes, 0, prefixLength);
            prefix.length = prefixLength;
            final int numEntries = end - start;
            int code = numEntries << 1;
            if (end == this.pending.size()) {
                code |= 0x1;
            }
            BlockTreeTermsWriter.this.termsOut.writeVInt(code);
            final boolean isLeafBlock = !hasSubBlocks && !hasPrefixTerms;
            boolean absolute = true;
            List<FST<BytesRef>> subIndices;
            if (isLeafBlock) {
                subIndices = null;
                for (int i = start; i < end; ++i) {
                    final PendingEntry ent = this.pending.get(i);
                    assert ent.isTerm : "i=" + i;
                    final PendingTerm term = (PendingTerm)ent;
                    assert term.prefixTerm == null;
                    assert StringHelper.startsWith(term.termBytes, prefix) : "term.term=" + term.termBytes + " prefix=" + prefix;
                    final BlockTermState state = term.state;
                    final int suffix = term.termBytes.length - prefixLength;
                    this.suffixWriter.writeVInt(suffix);
                    this.suffixWriter.writeBytes(term.termBytes, prefixLength, suffix);
                    assert (term.termBytes[prefixLength] & 0xFF) >= floorLeadLabel;
                    this.statsWriter.writeVInt(state.docFreq);
                    if (this.fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                        assert state.totalTermFreq >= state.docFreq : state.totalTermFreq + " vs " + state.docFreq;
                        this.statsWriter.writeVLong(state.totalTermFreq - state.docFreq);
                    }
                    BlockTreeTermsWriter.this.postingsWriter.encodeTerm(this.longs, this.bytesWriter, this.fieldInfo, state, absolute);
                    for (int pos = 0; pos < this.longsSize; ++pos) {
                        assert this.longs[pos] >= 0L;
                        this.metaWriter.writeVLong(this.longs[pos]);
                    }
                    this.bytesWriter.writeTo(this.metaWriter);
                    this.bytesWriter.reset();
                    absolute = false;
                }
            }
            else {
                subIndices = new ArrayList<FST<BytesRef>>();
                boolean sawAutoPrefixTerm = false;
                for (int j = start; j < end; ++j) {
                    final PendingEntry ent2 = this.pending.get(j);
                    if (ent2.isTerm) {
                        final PendingTerm term2 = (PendingTerm)ent2;
                        assert StringHelper.startsWith(term2.termBytes, prefix) : "term.term=" + term2.termBytes + " prefix=" + prefix;
                        final BlockTermState state2 = term2.state;
                        final int suffix2 = term2.termBytes.length - prefixLength;
                        if (BlockTreeTermsWriter.this.minItemsInAutoPrefix == 0) {
                            this.suffixWriter.writeVInt(suffix2 << 1);
                            this.suffixWriter.writeBytes(term2.termBytes, prefixLength, suffix2);
                        }
                        else {
                            code = suffix2 << 2;
                            int floorLeadEnd = -1;
                            if (term2.prefixTerm != null) {
                                assert BlockTreeTermsWriter.this.minItemsInAutoPrefix > 0;
                                sawAutoPrefixTerm = true;
                                final AutoPrefixTermsWriter.PrefixTerm prefixTerm = term2.prefixTerm;
                                floorLeadEnd = prefixTerm.floorLeadEnd;
                                assert floorLeadEnd != -1;
                                if (prefixTerm.floorLeadStart == -2) {
                                    code |= 0x2;
                                }
                                else {
                                    code |= 0x3;
                                }
                            }
                            this.suffixWriter.writeVInt(code);
                            this.suffixWriter.writeBytes(term2.termBytes, prefixLength, suffix2);
                            if (floorLeadEnd != -1) {
                                this.suffixWriter.writeByte((byte)floorLeadEnd);
                            }
                            assert (term2.termBytes[prefixLength] & 0xFF) >= floorLeadLabel;
                        }
                        this.statsWriter.writeVInt(state2.docFreq);
                        if (this.fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                            assert state2.totalTermFreq >= state2.docFreq;
                            this.statsWriter.writeVLong(state2.totalTermFreq - state2.docFreq);
                        }
                        BlockTreeTermsWriter.this.postingsWriter.encodeTerm(this.longs, this.bytesWriter, this.fieldInfo, state2, absolute);
                        for (int pos2 = 0; pos2 < this.longsSize; ++pos2) {
                            assert this.longs[pos2] >= 0L;
                            this.metaWriter.writeVLong(this.longs[pos2]);
                        }
                        this.bytesWriter.writeTo(this.metaWriter);
                        this.bytesWriter.reset();
                        absolute = false;
                    }
                    else {
                        final PendingBlock block = (PendingBlock)ent2;
                        assert StringHelper.startsWith(block.prefix, prefix);
                        final int suffix = block.prefix.length - prefixLength;
                        assert StringHelper.startsWith(block.prefix, prefix);
                        assert suffix > 0;
                        if (BlockTreeTermsWriter.this.minItemsInAutoPrefix == 0) {
                            this.suffixWriter.writeVInt(suffix << 1 | 0x1);
                        }
                        else {
                            this.suffixWriter.writeVInt(suffix << 2 | 0x1);
                        }
                        this.suffixWriter.writeBytes(block.prefix.bytes, prefixLength, suffix);
                        assert (block.prefix.bytes[prefixLength] & 0xFF) >= floorLeadLabel : "floorLeadLabel=" + floorLeadLabel + " suffixLead=" + (block.prefix.bytes[prefixLength] & 0xFF);
                        assert block.fp < startFP;
                        this.suffixWriter.writeVLong(startFP - block.fp);
                        subIndices.add(block.index);
                    }
                }
                assert !(!sawAutoPrefixTerm);
            }
            BlockTreeTermsWriter.this.termsOut.writeVInt((int)(this.suffixWriter.getFilePointer() << 1) | (isLeafBlock ? 1 : 0));
            this.suffixWriter.writeTo(BlockTreeTermsWriter.this.termsOut);
            this.suffixWriter.reset();
            BlockTreeTermsWriter.this.termsOut.writeVInt((int)this.statsWriter.getFilePointer());
            this.statsWriter.writeTo(BlockTreeTermsWriter.this.termsOut);
            this.statsWriter.reset();
            BlockTreeTermsWriter.this.termsOut.writeVInt((int)this.metaWriter.getFilePointer());
            this.metaWriter.writeTo(BlockTreeTermsWriter.this.termsOut);
            this.metaWriter.reset();
            if (hasFloorLeadLabel) {
                prefix.bytes[prefix.length++] = (byte)floorLeadLabel;
            }
            return new PendingBlock(prefix, startFP, hasTerms, isFloor, floorLeadLabel, subIndices);
        }
        
        TermsWriter(final FieldInfo fieldInfo) {
            this.lastTerm = new BytesRefBuilder();
            this.prefixStarts = new int[8];
            this.pending = new ArrayList<PendingEntry>();
            this.newBlocks = new ArrayList<PendingBlock>();
            this.suffixWriter = new RAMOutputStream();
            this.statsWriter = new RAMOutputStream();
            this.metaWriter = new RAMOutputStream();
            this.bytesWriter = new RAMOutputStream();
            this.fieldInfo = fieldInfo;
            assert fieldInfo.getIndexOptions() != IndexOptions.NONE;
            this.docsSeen = new FixedBitSet(BlockTreeTermsWriter.this.maxDoc);
            this.longsSize = BlockTreeTermsWriter.this.postingsWriter.setField(fieldInfo);
            this.longs = new long[this.longsSize];
        }
        
        public void write(final BytesRef text, final TermsEnum termsEnum, final AutoPrefixTermsWriter.PrefixTerm prefixTerm) throws IOException {
            final BlockTermState state = BlockTreeTermsWriter.this.postingsWriter.writeTerm(text, termsEnum, this.docsSeen);
            if (state != null) {
                assert state.docFreq != 0;
                assert state.totalTermFreq >= state.docFreq : "postingsWriter=" + BlockTreeTermsWriter.this.postingsWriter;
                this.pushTerm(text);
                final PendingTerm term = new PendingTerm(text, state, prefixTerm);
                this.pending.add(term);
                if (prefixTerm == null) {
                    this.sumDocFreq += state.docFreq;
                    this.sumTotalTermFreq += state.totalTermFreq;
                    ++this.numTerms;
                    if (this.firstPendingTerm == null) {
                        this.firstPendingTerm = term;
                    }
                    this.lastPendingTerm = term;
                }
            }
        }
        
        private void pushTerm(final BytesRef text) throws IOException {
            int limit;
            int pos;
            for (limit = Math.min(this.lastTerm.length(), text.length), pos = 0; pos < limit && this.lastTerm.byteAt(pos) == text.bytes[text.offset + pos]; ++pos) {}
            for (int i = this.lastTerm.length() - 1; i >= pos; --i) {
                final int prefixTopSize = this.pending.size() - this.prefixStarts[i];
                if (prefixTopSize >= BlockTreeTermsWriter.this.minItemsInBlock) {
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
                this.pushTerm(new BytesRef());
                this.pushTerm(new BytesRef());
                this.writeBlocks(0, this.pending.size());
                assert this.pending.size() == 1 && !this.pending.get(0).isTerm : "pending.size()=" + this.pending.size() + " pending=" + this.pending;
                final PendingBlock root = this.pending.get(0);
                assert root.prefix.length == 0;
                assert root.index.getEmptyOutput() != null;
                this.indexStartFP = BlockTreeTermsWriter.this.indexOut.getFilePointer();
                root.index.save(BlockTreeTermsWriter.this.indexOut);
                assert this.firstPendingTerm != null;
                final BytesRef minTerm = new BytesRef(this.firstPendingTerm.termBytes);
                assert this.lastPendingTerm != null;
                final BytesRef maxTerm = new BytesRef(this.lastPendingTerm.termBytes);
                BlockTreeTermsWriter.this.fields.add(new FieldMetaData(this.fieldInfo, this.pending.get(0).index.getEmptyOutput(), this.numTerms, this.indexStartFP, this.sumTotalTermFreq, this.sumDocFreq, this.docsSeen.cardinality(), this.longsSize, minTerm, maxTerm));
            }
            else {
                assert this.fieldInfo.getIndexOptions() == IndexOptions.DOCS && this.sumTotalTermFreq == -1L;
                assert this.sumDocFreq == 0L;
                assert this.docsSeen.cardinality() == 0;
            }
        }
    }
}
