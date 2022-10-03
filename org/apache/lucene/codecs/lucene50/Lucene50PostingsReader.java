package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import java.util.Arrays;
import org.apache.lucene.util.RamUsageEstimator;
import java.util.Collections;
import org.apache.lucene.util.Accountable;
import java.util.Collection;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.codecs.BlockTermState;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.codecs.PostingsReaderBase;

public final class Lucene50PostingsReader extends PostingsReaderBase
{
    private static final long BASE_RAM_BYTES_USED;
    private final IndexInput docIn;
    private final IndexInput posIn;
    private final IndexInput payIn;
    final ForUtil forUtil;
    private int version;
    
    public Lucene50PostingsReader(final SegmentReadState state) throws IOException {
        boolean success = false;
        IndexInput docIn = null;
        IndexInput posIn = null;
        IndexInput payIn = null;
        final String docName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "zdoc");
        try {
            docIn = state.directory.openInput(docName, state.context);
            this.version = CodecUtil.checkIndexHeader((DataInput)docIn, "Lucene50PostingsWriterDoc", 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
            this.forUtil = new ForUtil((DataInput)docIn);
            CodecUtil.retrieveChecksum(docIn);
            if (state.fieldInfos.hasProx()) {
                final String proxName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pos");
                posIn = state.directory.openInput(proxName, state.context);
                CodecUtil.checkIndexHeader((DataInput)posIn, "Lucene50PostingsWriterPos", this.version, this.version, state.segmentInfo.getId(), state.segmentSuffix);
                CodecUtil.retrieveChecksum(posIn);
                if (state.fieldInfos.hasPayloads() || state.fieldInfos.hasOffsets()) {
                    final String payName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pay");
                    payIn = state.directory.openInput(payName, state.context);
                    CodecUtil.checkIndexHeader((DataInput)payIn, "Lucene50PostingsWriterPay", this.version, this.version, state.segmentInfo.getId(), state.segmentSuffix);
                    CodecUtil.retrieveChecksum(payIn);
                }
            }
            this.docIn = docIn;
            this.posIn = posIn;
            this.payIn = payIn;
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)docIn, (Closeable)posIn, (Closeable)payIn });
            }
        }
    }
    
    public void init(final IndexInput termsIn, final SegmentReadState state) throws IOException {
        CodecUtil.checkIndexHeader((DataInput)termsIn, "Lucene50PostingsWriterTerms", 0, 0, state.segmentInfo.getId(), state.segmentSuffix);
        final int indexBlockSize = termsIn.readVInt();
        if (indexBlockSize != 128) {
            throw new IllegalStateException("index-time BLOCK_SIZE (" + indexBlockSize + ") != read-time BLOCK_SIZE (" + 128 + ")");
        }
    }
    
    static void readVIntBlock(final IndexInput docIn, final int[] docBuffer, final int[] freqBuffer, final int num, final boolean indexHasFreq) throws IOException {
        if (indexHasFreq) {
            for (int i = 0; i < num; ++i) {
                final int code = docIn.readVInt();
                docBuffer[i] = code >>> 1;
                if ((code & 0x1) != 0x0) {
                    freqBuffer[i] = 1;
                }
                else {
                    freqBuffer[i] = docIn.readVInt();
                }
            }
        }
        else {
            for (int i = 0; i < num; ++i) {
                docBuffer[i] = docIn.readVInt();
            }
        }
    }
    
    public BlockTermState newTermState() {
        return (BlockTermState)new Lucene50PostingsFormat.IntBlockTermState();
    }
    
    public void close() throws IOException {
        IOUtils.close(new Closeable[] { (Closeable)this.docIn, (Closeable)this.posIn, (Closeable)this.payIn });
    }
    
    public void decodeTerm(final long[] longs, final DataInput in, final FieldInfo fieldInfo, final BlockTermState blockTermState, final boolean absolute) throws IOException {
        final Lucene50PostingsFormat.IntBlockTermState termState = (Lucene50PostingsFormat.IntBlockTermState)blockTermState;
        final boolean fieldHasPositions = fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        final boolean fieldHasOffsets = fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        final boolean fieldHasPayloads = fieldInfo.hasPayloads();
        if (absolute) {
            termState.docStartFP = 0L;
            termState.posStartFP = 0L;
            termState.payStartFP = 0L;
        }
        final Lucene50PostingsFormat.IntBlockTermState intBlockTermState = termState;
        intBlockTermState.docStartFP += longs[0];
        if (fieldHasPositions) {
            final Lucene50PostingsFormat.IntBlockTermState intBlockTermState2 = termState;
            intBlockTermState2.posStartFP += longs[1];
            if (fieldHasOffsets || fieldHasPayloads) {
                final Lucene50PostingsFormat.IntBlockTermState intBlockTermState3 = termState;
                intBlockTermState3.payStartFP += longs[2];
            }
        }
        if (termState.docFreq == 1) {
            termState.singletonDocID = in.readVInt();
        }
        else {
            termState.singletonDocID = -1;
        }
        if (fieldHasPositions) {
            if (termState.totalTermFreq > 128L) {
                termState.lastPosBlockOffset = in.readVLong();
            }
            else {
                termState.lastPosBlockOffset = -1L;
            }
        }
        if (termState.docFreq > 128) {
            termState.skipOffset = in.readVLong();
        }
        else {
            termState.skipOffset = -1L;
        }
    }
    
    public PostingsEnum postings(final FieldInfo fieldInfo, final BlockTermState termState, final PostingsEnum reuse, final int flags) throws IOException {
        final boolean indexHasPositions = fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0;
        final boolean indexHasOffsets = fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0;
        final boolean indexHasPayloads = fieldInfo.hasPayloads();
        if (PostingsEnum.featureRequested(flags, (short)16384) && !indexHasPositions) {
            return null;
        }
        if (!indexHasPositions || !PostingsEnum.featureRequested(flags, (short)24)) {
            BlockDocsEnum docsEnum;
            if (reuse instanceof BlockDocsEnum) {
                docsEnum = (BlockDocsEnum)reuse;
                if (!docsEnum.canReuse(this.docIn, fieldInfo)) {
                    docsEnum = new BlockDocsEnum(this, fieldInfo);
                }
            }
            else {
                docsEnum = new BlockDocsEnum(this, fieldInfo);
            }
            return docsEnum.reset((Lucene50PostingsFormat.IntBlockTermState)termState, flags);
        }
        if ((!indexHasOffsets || !PostingsEnum.featureRequested(flags, (short)56)) && (!indexHasPayloads || !PostingsEnum.featureRequested(flags, (short)88))) {
            BlockPostingsEnum docsAndPositionsEnum;
            if (reuse instanceof BlockPostingsEnum) {
                docsAndPositionsEnum = (BlockPostingsEnum)reuse;
                if (!docsAndPositionsEnum.canReuse(this.docIn, fieldInfo)) {
                    docsAndPositionsEnum = new BlockPostingsEnum(this, fieldInfo);
                }
            }
            else {
                docsAndPositionsEnum = new BlockPostingsEnum(this, fieldInfo);
            }
            return docsAndPositionsEnum.reset((Lucene50PostingsFormat.IntBlockTermState)termState);
        }
        EverythingEnum everythingEnum;
        if (reuse instanceof EverythingEnum) {
            everythingEnum = (EverythingEnum)reuse;
            if (!everythingEnum.canReuse(this.docIn, fieldInfo)) {
                everythingEnum = new EverythingEnum(this, fieldInfo);
            }
        }
        else {
            everythingEnum = new EverythingEnum(this, fieldInfo);
        }
        return (PostingsEnum)everythingEnum.reset((Lucene50PostingsFormat.IntBlockTermState)termState, flags);
    }
    
    public long ramBytesUsed() {
        return Lucene50PostingsReader.BASE_RAM_BYTES_USED;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public void checkIntegrity() throws IOException {
        if (this.docIn != null) {
            CodecUtil.checksumEntireFile(this.docIn);
        }
        if (this.posIn != null) {
            CodecUtil.checksumEntireFile(this.posIn);
        }
        if (this.payIn != null) {
            CodecUtil.checksumEntireFile(this.payIn);
        }
    }
    
    public String toString() {
        return this.getClass().getSimpleName() + "(positions=" + (this.posIn != null) + ",payloads=" + (this.payIn != null) + ")";
    }
    
    static {
        Lucene50PostingsReader.BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance((Class)Lucene50PostingsReader.class);
    }
    
    final class BlockDocsEnum extends PostingsEnum
    {
        private final byte[] encoded;
        private final int[] docDeltaBuffer;
        private final int[] freqBuffer;
        private int docBufferUpto;
        private Lucene50SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final boolean indexHasFreq;
        final boolean indexHasPos;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private long docTermStartFP;
        private long skipOffset;
        private int nextSkipDoc;
        private boolean needsFreq;
        private int singletonDocID;
        
        public BlockDocsEnum(final Lucene50PostingsReader this$0, final FieldInfo fieldInfo) throws IOException {
            this.this$0 = this$0;
            this.docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.startDocIn = this$0.docIn;
            this.docIn = null;
            this.indexHasFreq = (fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS) >= 0);
            this.indexHasPos = (fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0);
            this.indexHasOffsets = (fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
            this.indexHasPayloads = fieldInfo.hasPayloads();
            this.encoded = new byte[512];
        }
        
        public boolean canReuse(final IndexInput docIn, final FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasFreq == fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS) >= 0 && this.indexHasPos == fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }
        
        public PostingsEnum reset(final Lucene50PostingsFormat.IntBlockTermState termState, final int flags) throws IOException {
            this.docFreq = termState.docFreq;
            this.totalTermFreq = (this.indexHasFreq ? termState.totalTermFreq : this.docFreq);
            this.docTermStartFP = termState.docStartFP;
            this.skipOffset = termState.skipOffset;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.doc = -1;
            this.needsFreq = PostingsEnum.featureRequested(flags, (short)8);
            if (!this.indexHasFreq || !this.needsFreq) {
                Arrays.fill(this.freqBuffer, 1);
            }
            this.accum = 0;
            this.docUpto = 0;
            this.nextSkipDoc = 127;
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }
        
        public int freq() throws IOException {
            return this.freq;
        }
        
        public int nextPosition() throws IOException {
            return -1;
        }
        
        public int startOffset() throws IOException {
            return -1;
        }
        
        public int endOffset() throws IOException {
            return -1;
        }
        
        public BytesRef getPayload() throws IOException {
            return null;
        }
        
        public int docID() {
            return this.doc;
        }
        
        private void refillDocs() throws IOException {
            final int left = this.docFreq - this.docUpto;
            assert left > 0;
            if (left >= 128) {
                this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                if (this.indexHasFreq) {
                    if (this.needsFreq) {
                        this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
                    }
                    else {
                        this.this$0.forUtil.skipBlock(this.docIn);
                    }
                }
            }
            else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            }
            else {
                Lucene50PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, this.indexHasFreq);
            }
            this.docBufferUpto = 0;
        }
        
        public int nextDoc() throws IOException {
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            this.accum += this.docDeltaBuffer[this.docBufferUpto];
            ++this.docUpto;
            this.doc = this.accum;
            this.freq = this.freqBuffer[this.docBufferUpto];
            ++this.docBufferUpto;
            return this.doc;
        }
        
        public int advance(final int target) throws IOException {
            if (this.docFreq > 128 && target > this.nextSkipDoc) {
                if (this.skipper == null) {
                    this.skipper = new Lucene50SkipReader(this.docIn.clone(), 10, this.indexHasPos, this.indexHasOffsets, this.indexHasPayloads);
                }
                if (!this.skipped) {
                    assert this.skipOffset != -1L;
                    this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, 0L, 0L, this.docFreq);
                    this.skipped = true;
                }
                final int newDocUpto = this.skipper.skipTo(target) + 1;
                if (newDocUpto > this.docUpto) {
                    assert newDocUpto % 128 == 0 : "got " + newDocUpto;
                    this.docUpto = newDocUpto;
                    this.docBufferUpto = 128;
                    this.accum = this.skipper.getDoc();
                    this.docIn.seek(this.skipper.getDocPointer());
                }
                this.nextSkipDoc = this.skipper.getNextSkipDoc();
            }
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            do {
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                ++this.docUpto;
                if (this.accum >= target) {
                    this.freq = this.freqBuffer[this.docBufferUpto];
                    ++this.docBufferUpto;
                    return this.doc = this.accum;
                }
                ++this.docBufferUpto;
            } while (this.docUpto != this.docFreq);
            return this.doc = Integer.MAX_VALUE;
        }
        
        public long cost() {
            return this.docFreq;
        }
        
        static {
            BlockDocsEnum.$assertionsDisabled = !Lucene50PostingsReader.class.desiredAssertionStatus();
        }
    }
    
    final class BlockPostingsEnum extends PostingsEnum
    {
        private final byte[] encoded;
        private final int[] docDeltaBuffer;
        private final int[] freqBuffer;
        private final int[] posDeltaBuffer;
        private int docBufferUpto;
        private int posBufferUpto;
        private Lucene50SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final IndexInput posIn;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private int position;
        private int posPendingCount;
        private long posPendingFP;
        private long docTermStartFP;
        private long posTermStartFP;
        private long payTermStartFP;
        private long lastPosBlockFP;
        private long skipOffset;
        private int nextSkipDoc;
        private int singletonDocID;
        
        public BlockPostingsEnum(final Lucene50PostingsReader this$0, final FieldInfo fieldInfo) throws IOException {
            this.this$0 = this$0;
            this.docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.startDocIn = this$0.docIn;
            this.docIn = null;
            this.posIn = this$0.posIn.clone();
            this.encoded = new byte[512];
            this.indexHasOffsets = (fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
            this.indexHasPayloads = fieldInfo.hasPayloads();
        }
        
        public boolean canReuse(final IndexInput docIn, final FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasOffsets == fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }
        
        public PostingsEnum reset(final Lucene50PostingsFormat.IntBlockTermState termState) throws IOException {
            this.docFreq = termState.docFreq;
            this.docTermStartFP = termState.docStartFP;
            this.posTermStartFP = termState.posStartFP;
            this.payTermStartFP = termState.payStartFP;
            this.skipOffset = termState.skipOffset;
            this.totalTermFreq = termState.totalTermFreq;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.posPendingFP = this.posTermStartFP;
            this.posPendingCount = 0;
            if (termState.totalTermFreq < 128L) {
                this.lastPosBlockFP = this.posTermStartFP;
            }
            else if (termState.totalTermFreq == 128L) {
                this.lastPosBlockFP = -1L;
            }
            else {
                this.lastPosBlockFP = this.posTermStartFP + termState.lastPosBlockOffset;
            }
            this.doc = -1;
            this.accum = 0;
            this.docUpto = 0;
            if (this.docFreq > 128) {
                this.nextSkipDoc = 127;
            }
            else {
                this.nextSkipDoc = Integer.MAX_VALUE;
            }
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }
        
        public int freq() throws IOException {
            return this.freq;
        }
        
        public int docID() {
            return this.doc;
        }
        
        private void refillDocs() throws IOException {
            final int left = this.docFreq - this.docUpto;
            assert left > 0;
            if (left >= 128) {
                this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
            }
            else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            }
            else {
                Lucene50PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, true);
            }
            this.docBufferUpto = 0;
        }
        
        private void refillPositions() throws IOException {
            if (this.posIn.getFilePointer() == this.lastPosBlockFP) {
                final int count = (int)(this.totalTermFreq % 128L);
                int payloadLength = 0;
                for (int i = 0; i < count; ++i) {
                    final int code = this.posIn.readVInt();
                    if (this.indexHasPayloads) {
                        if ((code & 0x1) != 0x0) {
                            payloadLength = this.posIn.readVInt();
                        }
                        this.posDeltaBuffer[i] = code >>> 1;
                        if (payloadLength != 0) {
                            this.posIn.seek(this.posIn.getFilePointer() + payloadLength);
                        }
                    }
                    else {
                        this.posDeltaBuffer[i] = code;
                    }
                    if (this.indexHasOffsets && (this.posIn.readVInt() & 0x1) != 0x0) {
                        this.posIn.readVInt();
                    }
                }
            }
            else {
                this.this$0.forUtil.readBlock(this.posIn, this.encoded, this.posDeltaBuffer);
            }
        }
        
        public int nextDoc() throws IOException {
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            this.accum += this.docDeltaBuffer[this.docBufferUpto];
            this.freq = this.freqBuffer[this.docBufferUpto];
            this.posPendingCount += this.freq;
            ++this.docBufferUpto;
            ++this.docUpto;
            this.doc = this.accum;
            this.position = 0;
            return this.doc;
        }
        
        public int advance(final int target) throws IOException {
            if (target > this.nextSkipDoc) {
                if (this.skipper == null) {
                    this.skipper = new Lucene50SkipReader(this.docIn.clone(), 10, true, this.indexHasOffsets, this.indexHasPayloads);
                }
                if (!this.skipped) {
                    assert this.skipOffset != -1L;
                    this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, this.posTermStartFP, this.payTermStartFP, this.docFreq);
                    this.skipped = true;
                }
                final int newDocUpto = this.skipper.skipTo(target) + 1;
                if (newDocUpto > this.docUpto) {
                    assert newDocUpto % 128 == 0 : "got " + newDocUpto;
                    this.docUpto = newDocUpto;
                    this.docBufferUpto = 128;
                    this.accum = this.skipper.getDoc();
                    this.docIn.seek(this.skipper.getDocPointer());
                    this.posPendingFP = this.skipper.getPosPointer();
                    this.posPendingCount = this.skipper.getPosBufferUpto();
                }
                this.nextSkipDoc = this.skipper.getNextSkipDoc();
            }
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            do {
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                this.freq = this.freqBuffer[this.docBufferUpto];
                this.posPendingCount += this.freq;
                ++this.docBufferUpto;
                ++this.docUpto;
                if (this.accum >= target) {
                    this.position = 0;
                    return this.doc = this.accum;
                }
            } while (this.docUpto != this.docFreq);
            return this.doc = Integer.MAX_VALUE;
        }
        
        private void skipPositions() throws IOException {
            int toSkip = this.posPendingCount - this.freq;
            final int leftInBlock = 128 - this.posBufferUpto;
            if (toSkip < leftInBlock) {
                this.posBufferUpto += toSkip;
            }
            else {
                for (toSkip -= leftInBlock; toSkip >= 128; toSkip -= 128) {
                    assert this.posIn.getFilePointer() != this.lastPosBlockFP;
                    this.this$0.forUtil.skipBlock(this.posIn);
                }
                this.refillPositions();
                this.posBufferUpto = toSkip;
            }
            this.position = 0;
        }
        
        public int nextPosition() throws IOException {
            assert this.posPendingCount > 0;
            if (this.posPendingFP != -1L) {
                this.posIn.seek(this.posPendingFP);
                this.posPendingFP = -1L;
                this.posBufferUpto = 128;
            }
            if (this.posPendingCount > this.freq) {
                this.skipPositions();
                this.posPendingCount = this.freq;
            }
            if (this.posBufferUpto == 128) {
                this.refillPositions();
                this.posBufferUpto = 0;
            }
            this.position += this.posDeltaBuffer[this.posBufferUpto++];
            --this.posPendingCount;
            return this.position;
        }
        
        public int startOffset() {
            return -1;
        }
        
        public int endOffset() {
            return -1;
        }
        
        public BytesRef getPayload() {
            return null;
        }
        
        public long cost() {
            return this.docFreq;
        }
        
        static {
            BlockPostingsEnum.$assertionsDisabled = !Lucene50PostingsReader.class.desiredAssertionStatus();
        }
    }
    
    final class EverythingEnum extends PostingsEnum
    {
        private final byte[] encoded;
        private final int[] docDeltaBuffer;
        private final int[] freqBuffer;
        private final int[] posDeltaBuffer;
        private final int[] payloadLengthBuffer;
        private final int[] offsetStartDeltaBuffer;
        private final int[] offsetLengthBuffer;
        private byte[] payloadBytes;
        private int payloadByteUpto;
        private int payloadLength;
        private int lastStartOffset;
        private int startOffset;
        private int endOffset;
        private int docBufferUpto;
        private int posBufferUpto;
        private Lucene50SkipReader skipper;
        private boolean skipped;
        final IndexInput startDocIn;
        IndexInput docIn;
        final IndexInput posIn;
        final IndexInput payIn;
        final BytesRef payload;
        final boolean indexHasOffsets;
        final boolean indexHasPayloads;
        private int docFreq;
        private long totalTermFreq;
        private int docUpto;
        private int doc;
        private int accum;
        private int freq;
        private int position;
        private int posPendingCount;
        private long posPendingFP;
        private long payPendingFP;
        private long docTermStartFP;
        private long posTermStartFP;
        private long payTermStartFP;
        private long lastPosBlockFP;
        private long skipOffset;
        private int nextSkipDoc;
        private boolean needsOffsets;
        private boolean needsPayloads;
        private int singletonDocID;
        
        public EverythingEnum(final Lucene50PostingsReader this$0, final FieldInfo fieldInfo) throws IOException {
            this.this$0 = this$0;
            this.docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
            this.startDocIn = this$0.docIn;
            this.docIn = null;
            this.posIn = this$0.posIn.clone();
            this.payIn = this$0.payIn.clone();
            this.encoded = new byte[512];
            this.indexHasOffsets = (fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0);
            if (this.indexHasOffsets) {
                this.offsetStartDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                this.offsetLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
            }
            else {
                this.offsetStartDeltaBuffer = null;
                this.offsetLengthBuffer = null;
                this.startOffset = -1;
                this.endOffset = -1;
            }
            this.indexHasPayloads = fieldInfo.hasPayloads();
            if (this.indexHasPayloads) {
                this.payloadLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                this.payloadBytes = new byte[128];
                this.payload = new BytesRef();
            }
            else {
                this.payloadLengthBuffer = null;
                this.payloadBytes = null;
                this.payload = null;
            }
        }
        
        public boolean canReuse(final IndexInput docIn, final FieldInfo fieldInfo) {
            return docIn == this.startDocIn && this.indexHasOffsets == fieldInfo.getIndexOptions().compareTo((Enum)IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS) >= 0 && this.indexHasPayloads == fieldInfo.hasPayloads();
        }
        
        public EverythingEnum reset(final Lucene50PostingsFormat.IntBlockTermState termState, final int flags) throws IOException {
            this.docFreq = termState.docFreq;
            this.docTermStartFP = termState.docStartFP;
            this.posTermStartFP = termState.posStartFP;
            this.payTermStartFP = termState.payStartFP;
            this.skipOffset = termState.skipOffset;
            this.totalTermFreq = termState.totalTermFreq;
            this.singletonDocID = termState.singletonDocID;
            if (this.docFreq > 1) {
                if (this.docIn == null) {
                    this.docIn = this.startDocIn.clone();
                }
                this.docIn.seek(this.docTermStartFP);
            }
            this.posPendingFP = this.posTermStartFP;
            this.payPendingFP = this.payTermStartFP;
            this.posPendingCount = 0;
            if (termState.totalTermFreq < 128L) {
                this.lastPosBlockFP = this.posTermStartFP;
            }
            else if (termState.totalTermFreq == 128L) {
                this.lastPosBlockFP = -1L;
            }
            else {
                this.lastPosBlockFP = this.posTermStartFP + termState.lastPosBlockOffset;
            }
            this.needsOffsets = PostingsEnum.featureRequested(flags, (short)56);
            this.needsPayloads = PostingsEnum.featureRequested(flags, (short)88);
            this.doc = -1;
            this.accum = 0;
            this.docUpto = 0;
            if (this.docFreq > 128) {
                this.nextSkipDoc = 127;
            }
            else {
                this.nextSkipDoc = Integer.MAX_VALUE;
            }
            this.docBufferUpto = 128;
            this.skipped = false;
            return this;
        }
        
        public int freq() throws IOException {
            return this.freq;
        }
        
        public int docID() {
            return this.doc;
        }
        
        private void refillDocs() throws IOException {
            final int left = this.docFreq - this.docUpto;
            assert left > 0;
            if (left >= 128) {
                this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.docDeltaBuffer);
                this.this$0.forUtil.readBlock(this.docIn, this.encoded, this.freqBuffer);
            }
            else if (this.docFreq == 1) {
                this.docDeltaBuffer[0] = this.singletonDocID;
                this.freqBuffer[0] = (int)this.totalTermFreq;
            }
            else {
                Lucene50PostingsReader.readVIntBlock(this.docIn, this.docDeltaBuffer, this.freqBuffer, left, true);
            }
            this.docBufferUpto = 0;
        }
        
        private void refillPositions() throws IOException {
            if (this.posIn.getFilePointer() == this.lastPosBlockFP) {
                final int count = (int)(this.totalTermFreq % 128L);
                int payloadLength = 0;
                int offsetLength = 0;
                this.payloadByteUpto = 0;
                for (int i = 0; i < count; ++i) {
                    final int code = this.posIn.readVInt();
                    if (this.indexHasPayloads) {
                        if ((code & 0x1) != 0x0) {
                            payloadLength = this.posIn.readVInt();
                        }
                        this.payloadLengthBuffer[i] = payloadLength;
                        this.posDeltaBuffer[i] = code >>> 1;
                        if (payloadLength != 0) {
                            if (this.payloadByteUpto + payloadLength > this.payloadBytes.length) {
                                this.payloadBytes = ArrayUtil.grow(this.payloadBytes, this.payloadByteUpto + payloadLength);
                            }
                            this.posIn.readBytes(this.payloadBytes, this.payloadByteUpto, payloadLength);
                            this.payloadByteUpto += payloadLength;
                        }
                    }
                    else {
                        this.posDeltaBuffer[i] = code;
                    }
                    if (this.indexHasOffsets) {
                        final int deltaCode = this.posIn.readVInt();
                        if ((deltaCode & 0x1) != 0x0) {
                            offsetLength = this.posIn.readVInt();
                        }
                        this.offsetStartDeltaBuffer[i] = deltaCode >>> 1;
                        this.offsetLengthBuffer[i] = offsetLength;
                    }
                }
                this.payloadByteUpto = 0;
            }
            else {
                this.this$0.forUtil.readBlock(this.posIn, this.encoded, this.posDeltaBuffer);
                if (this.indexHasPayloads) {
                    if (this.needsPayloads) {
                        this.this$0.forUtil.readBlock(this.payIn, this.encoded, this.payloadLengthBuffer);
                        final int numBytes = this.payIn.readVInt();
                        if (numBytes > this.payloadBytes.length) {
                            this.payloadBytes = ArrayUtil.grow(this.payloadBytes, numBytes);
                        }
                        this.payIn.readBytes(this.payloadBytes, 0, numBytes);
                    }
                    else {
                        this.this$0.forUtil.skipBlock(this.payIn);
                        final int numBytes = this.payIn.readVInt();
                        this.payIn.seek(this.payIn.getFilePointer() + numBytes);
                    }
                    this.payloadByteUpto = 0;
                }
                if (this.indexHasOffsets) {
                    if (this.needsOffsets) {
                        this.this$0.forUtil.readBlock(this.payIn, this.encoded, this.offsetStartDeltaBuffer);
                        this.this$0.forUtil.readBlock(this.payIn, this.encoded, this.offsetLengthBuffer);
                    }
                    else {
                        this.this$0.forUtil.skipBlock(this.payIn);
                        this.this$0.forUtil.skipBlock(this.payIn);
                    }
                }
            }
        }
        
        public int nextDoc() throws IOException {
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            this.accum += this.docDeltaBuffer[this.docBufferUpto];
            this.freq = this.freqBuffer[this.docBufferUpto];
            this.posPendingCount += this.freq;
            ++this.docBufferUpto;
            ++this.docUpto;
            this.doc = this.accum;
            this.position = 0;
            this.lastStartOffset = 0;
            return this.doc;
        }
        
        public int advance(final int target) throws IOException {
            if (target > this.nextSkipDoc) {
                if (this.skipper == null) {
                    this.skipper = new Lucene50SkipReader(this.docIn.clone(), 10, true, this.indexHasOffsets, this.indexHasPayloads);
                }
                if (!this.skipped) {
                    assert this.skipOffset != -1L;
                    this.skipper.init(this.docTermStartFP + this.skipOffset, this.docTermStartFP, this.posTermStartFP, this.payTermStartFP, this.docFreq);
                    this.skipped = true;
                }
                final int newDocUpto = this.skipper.skipTo(target) + 1;
                if (newDocUpto > this.docUpto) {
                    assert newDocUpto % 128 == 0 : "got " + newDocUpto;
                    this.docUpto = newDocUpto;
                    this.docBufferUpto = 128;
                    this.accum = this.skipper.getDoc();
                    this.docIn.seek(this.skipper.getDocPointer());
                    this.posPendingFP = this.skipper.getPosPointer();
                    this.payPendingFP = this.skipper.getPayPointer();
                    this.posPendingCount = this.skipper.getPosBufferUpto();
                    this.lastStartOffset = 0;
                    this.payloadByteUpto = this.skipper.getPayloadByteUpto();
                }
                this.nextSkipDoc = this.skipper.getNextSkipDoc();
            }
            if (this.docUpto == this.docFreq) {
                return this.doc = Integer.MAX_VALUE;
            }
            if (this.docBufferUpto == 128) {
                this.refillDocs();
            }
            do {
                this.accum += this.docDeltaBuffer[this.docBufferUpto];
                this.freq = this.freqBuffer[this.docBufferUpto];
                this.posPendingCount += this.freq;
                ++this.docBufferUpto;
                ++this.docUpto;
                if (this.accum >= target) {
                    this.position = 0;
                    this.lastStartOffset = 0;
                    return this.doc = this.accum;
                }
            } while (this.docUpto != this.docFreq);
            return this.doc = Integer.MAX_VALUE;
        }
        
        private void skipPositions() throws IOException {
            int toSkip = this.posPendingCount - this.freq;
            final int leftInBlock = 128 - this.posBufferUpto;
            if (toSkip < leftInBlock) {
                final int end = this.posBufferUpto + toSkip;
                while (this.posBufferUpto < end) {
                    if (this.indexHasPayloads) {
                        this.payloadByteUpto += this.payloadLengthBuffer[this.posBufferUpto];
                    }
                    ++this.posBufferUpto;
                }
            }
            else {
                for (toSkip -= leftInBlock; toSkip >= 128; toSkip -= 128) {
                    assert this.posIn.getFilePointer() != this.lastPosBlockFP;
                    this.this$0.forUtil.skipBlock(this.posIn);
                    if (this.indexHasPayloads) {
                        this.this$0.forUtil.skipBlock(this.payIn);
                        final int numBytes = this.payIn.readVInt();
                        this.payIn.seek(this.payIn.getFilePointer() + numBytes);
                    }
                    if (this.indexHasOffsets) {
                        this.this$0.forUtil.skipBlock(this.payIn);
                        this.this$0.forUtil.skipBlock(this.payIn);
                    }
                }
                this.refillPositions();
                this.payloadByteUpto = 0;
                this.posBufferUpto = 0;
                while (this.posBufferUpto < toSkip) {
                    if (this.indexHasPayloads) {
                        this.payloadByteUpto += this.payloadLengthBuffer[this.posBufferUpto];
                    }
                    ++this.posBufferUpto;
                }
            }
            this.position = 0;
            this.lastStartOffset = 0;
        }
        
        public int nextPosition() throws IOException {
            assert this.posPendingCount > 0;
            if (this.posPendingFP != -1L) {
                this.posIn.seek(this.posPendingFP);
                this.posPendingFP = -1L;
                if (this.payPendingFP != -1L) {
                    this.payIn.seek(this.payPendingFP);
                    this.payPendingFP = -1L;
                }
                this.posBufferUpto = 128;
            }
            if (this.posPendingCount > this.freq) {
                this.skipPositions();
                this.posPendingCount = this.freq;
            }
            if (this.posBufferUpto == 128) {
                this.refillPositions();
                this.posBufferUpto = 0;
            }
            this.position += this.posDeltaBuffer[this.posBufferUpto];
            if (this.indexHasPayloads) {
                this.payloadLength = this.payloadLengthBuffer[this.posBufferUpto];
                this.payload.bytes = this.payloadBytes;
                this.payload.offset = this.payloadByteUpto;
                this.payload.length = this.payloadLength;
                this.payloadByteUpto += this.payloadLength;
            }
            if (this.indexHasOffsets) {
                this.startOffset = this.lastStartOffset + this.offsetStartDeltaBuffer[this.posBufferUpto];
                this.endOffset = this.startOffset + this.offsetLengthBuffer[this.posBufferUpto];
                this.lastStartOffset = this.startOffset;
            }
            ++this.posBufferUpto;
            --this.posPendingCount;
            return this.position;
        }
        
        public int startOffset() {
            return this.startOffset;
        }
        
        public int endOffset() {
            return this.endOffset;
        }
        
        public BytesRef getPayload() {
            if (this.payloadLength == 0) {
                return null;
            }
            return this.payload;
        }
        
        public long cost() {
            return this.docFreq;
        }
        
        static {
            EverythingEnum.$assertionsDisabled = !Lucene50PostingsReader.class.desiredAssertionStatus();
        }
    }
}
