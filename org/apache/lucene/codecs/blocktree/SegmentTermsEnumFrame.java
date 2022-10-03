package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;

final class SegmentTermsEnumFrame
{
    final int ord;
    boolean hasTerms;
    boolean hasTermsOrig;
    boolean isFloor;
    FST.Arc<BytesRef> arc;
    final boolean versionAutoPrefix;
    long fp;
    long fpOrig;
    long fpEnd;
    byte[] suffixBytes;
    final ByteArrayDataInput suffixesReader;
    byte[] statBytes;
    final ByteArrayDataInput statsReader;
    byte[] floorData;
    final ByteArrayDataInput floorDataReader;
    int prefix;
    int entCount;
    int nextEnt;
    boolean isLastInFloor;
    boolean isLeafBlock;
    long lastSubFP;
    int nextFloorLabel;
    int numFollowFloorBlocks;
    int metaDataUpto;
    final BlockTermState state;
    final long[] longs;
    byte[] bytes;
    final ByteArrayDataInput bytesReader;
    private final SegmentTermsEnum ste;
    private int startBytePos;
    private int suffix;
    private long subCode;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public SegmentTermsEnumFrame(final SegmentTermsEnum ste, final int ord) throws IOException {
        this.suffixBytes = new byte[128];
        this.suffixesReader = new ByteArrayDataInput();
        this.statBytes = new byte[64];
        this.statsReader = new ByteArrayDataInput();
        this.floorData = new byte[32];
        this.floorDataReader = new ByteArrayDataInput();
        this.bytes = new byte[32];
        this.bytesReader = new ByteArrayDataInput();
        this.ste = ste;
        this.ord = ord;
        this.state = ste.fr.parent.postingsReader.newTermState();
        this.state.totalTermFreq = -1L;
        this.longs = new long[ste.fr.longsSize];
        this.versionAutoPrefix = ste.fr.parent.anyAutoPrefixTerms;
    }
    
    public void setFloorData(final ByteArrayDataInput in, final BytesRef source) {
        final int numBytes = source.length - (in.getPosition() - source.offset);
        if (numBytes > this.floorData.length) {
            this.floorData = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        System.arraycopy(source.bytes, source.offset + in.getPosition(), this.floorData, 0, numBytes);
        this.floorDataReader.reset(this.floorData, 0, numBytes);
        this.numFollowFloorBlocks = this.floorDataReader.readVInt();
        this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
    }
    
    public int getTermBlockOrd() {
        return this.isLeafBlock ? this.nextEnt : this.state.termBlockOrd;
    }
    
    void loadNextFloorBlock() throws IOException {
        assert !(!this.isFloor) : "arc=" + this.arc + " isFloor=" + this.isFloor;
        this.fp = this.fpEnd;
        this.nextEnt = -1;
        this.loadBlock();
    }
    
    void loadBlock() throws IOException {
        this.ste.initIndexInput();
        if (this.nextEnt != -1) {
            return;
        }
        this.ste.in.seek(this.fp);
        int code = this.ste.in.readVInt();
        this.entCount = code >>> 1;
        assert this.entCount > 0;
        this.isLastInFloor = ((code & 0x1) != 0x0);
        assert !(!this.isFloor) : "fp=" + this.fp + " arc=" + this.arc + " isFloor=" + this.isFloor + " isLastInFloor=" + this.isLastInFloor;
        code = this.ste.in.readVInt();
        this.isLeafBlock = ((code & 0x1) != 0x0);
        int numBytes = code >>> 1;
        if (this.suffixBytes.length < numBytes) {
            this.suffixBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ste.in.readBytes(this.suffixBytes, 0, numBytes);
        this.suffixesReader.reset(this.suffixBytes, 0, numBytes);
        numBytes = this.ste.in.readVInt();
        if (this.statBytes.length < numBytes) {
            this.statBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ste.in.readBytes(this.statBytes, 0, numBytes);
        this.statsReader.reset(this.statBytes, 0, numBytes);
        this.metaDataUpto = 0;
        this.state.termBlockOrd = 0;
        this.nextEnt = 0;
        this.lastSubFP = -1L;
        numBytes = this.ste.in.readVInt();
        if (this.bytes.length < numBytes) {
            this.bytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ste.in.readBytes(this.bytes, 0, numBytes);
        this.bytesReader.reset(this.bytes, 0, numBytes);
        this.fpEnd = this.ste.in.getFilePointer();
    }
    
    void rewind() {
        this.fp = this.fpOrig;
        this.nextEnt = -1;
        this.hasTerms = this.hasTermsOrig;
        if (this.isFloor) {
            this.floorDataReader.rewind();
            this.numFollowFloorBlocks = this.floorDataReader.readVInt();
            assert this.numFollowFloorBlocks > 0;
            this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
        }
    }
    
    public boolean next() throws IOException {
        if (this.isLeafBlock) {
            this.nextLeaf();
            return false;
        }
        return this.nextNonLeaf();
    }
    
    public void nextLeaf() {
        assert this.nextEnt != -1 && this.nextEnt < this.entCount : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
        ++this.nextEnt;
        this.suffix = this.suffixesReader.readVInt();
        this.startBytePos = this.suffixesReader.getPosition();
        this.ste.term.setLength(this.prefix + this.suffix);
        this.ste.term.grow(this.ste.term.length());
        this.suffixesReader.readBytes(this.ste.term.bytes(), this.prefix, this.suffix);
        this.ste.termExists = true;
    }
    
    public boolean nextNonLeaf() throws IOException {
        while (true) {
            if (this.nextEnt == this.entCount) {
                assert this.isFloor && !this.isLastInFloor : "isFloor=" + this.isFloor + " isLastInFloor=" + this.isLastInFloor;
                this.loadNextFloorBlock();
                if (this.isLeafBlock) {
                    this.nextLeaf();
                    return false;
                }
                continue;
            }
            else {
                assert this.nextEnt != -1 && this.nextEnt < this.entCount : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
                ++this.nextEnt;
                final int code = this.suffixesReader.readVInt();
                if (!this.versionAutoPrefix) {
                    this.suffix = code >>> 1;
                    this.startBytePos = this.suffixesReader.getPosition();
                    this.ste.term.setLength(this.prefix + this.suffix);
                    this.ste.term.grow(this.ste.term.length());
                    this.suffixesReader.readBytes(this.ste.term.bytes(), this.prefix, this.suffix);
                    if ((code & 0x1) == 0x0) {
                        this.ste.termExists = true;
                        this.subCode = 0L;
                        final BlockTermState state = this.state;
                        ++state.termBlockOrd;
                        return false;
                    }
                    this.ste.termExists = false;
                    this.subCode = this.suffixesReader.readVLong();
                    this.lastSubFP = this.fp - this.subCode;
                    return true;
                }
                else {
                    this.suffix = code >>> 2;
                    this.startBytePos = this.suffixesReader.getPosition();
                    this.ste.term.setLength(this.prefix + this.suffix);
                    this.ste.term.grow(this.ste.term.length());
                    this.suffixesReader.readBytes(this.ste.term.bytes(), this.prefix, this.suffix);
                    switch (code & 0x3) {
                        case 0: {
                            this.ste.termExists = true;
                            this.subCode = 0L;
                            final BlockTermState state2 = this.state;
                            ++state2.termBlockOrd;
                            return false;
                        }
                        case 1: {
                            this.ste.termExists = false;
                            this.subCode = this.suffixesReader.readVLong();
                            this.lastSubFP = this.fp - this.subCode;
                            return true;
                        }
                        case 2:
                        case 3: {
                            final BlockTermState state3 = this.state;
                            ++state3.termBlockOrd;
                            this.suffixesReader.readByte();
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public void scanToFloorFrame(final BytesRef target) {
        if (!this.isFloor || target.length <= this.prefix) {
            return;
        }
        final int targetLabel = target.bytes[target.offset + this.prefix] & 0xFF;
        if (targetLabel < this.nextFloorLabel) {
            return;
        }
        assert this.numFollowFloorBlocks != 0;
        long newFP = this.fpOrig;
        do {
            final long code = this.floorDataReader.readVLong();
            newFP = this.fpOrig + (code >>> 1);
            this.hasTerms = ((code & 0x1L) != 0x0L);
            this.isLastInFloor = (this.numFollowFloorBlocks == 1);
            --this.numFollowFloorBlocks;
            if (this.isLastInFloor) {
                this.nextFloorLabel = 256;
                break;
            }
            this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
        } while (targetLabel >= this.nextFloorLabel);
        if (newFP != this.fp) {
            this.nextEnt = -1;
            this.fp = newFP;
        }
    }
    
    public void decodeMetaData() throws IOException {
        final int limit = this.getTermBlockOrd();
        boolean absolute = this.metaDataUpto == 0;
        assert limit > 0;
        while (this.metaDataUpto < limit) {
            this.state.docFreq = this.statsReader.readVInt();
            if (this.ste.fr.fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                this.state.totalTermFreq = this.state.docFreq + this.statsReader.readVLong();
            }
            for (int i = 0; i < this.ste.fr.longsSize; ++i) {
                this.longs[i] = this.bytesReader.readVLong();
            }
            this.ste.fr.parent.postingsReader.decodeTerm(this.longs, this.bytesReader, this.ste.fr.fieldInfo, this.state, absolute);
            ++this.metaDataUpto;
            absolute = false;
        }
        this.state.termBlockOrd = this.metaDataUpto;
    }
    
    private boolean prefixMatches(final BytesRef target) {
        for (int bytePos = 0; bytePos < this.prefix; ++bytePos) {
            if (target.bytes[target.offset + bytePos] != this.ste.term.byteAt(bytePos)) {
                return false;
            }
        }
        return true;
    }
    
    public void scanToSubBlock(final long subFP) {
        assert !this.isLeafBlock;
        if (this.lastSubFP == subFP) {
            return;
        }
        assert subFP < this.fp : "fp=" + this.fp + " subFP=" + subFP;
        final long targetSubCode = this.fp - subFP;
        while (SegmentTermsEnumFrame.$assertionsDisabled || this.nextEnt < this.entCount) {
            ++this.nextEnt;
            final int code = this.suffixesReader.readVInt();
            if (!this.versionAutoPrefix) {
                this.suffixesReader.skipBytes(code >>> 1);
                if ((code & 0x1) != 0x0) {
                    final long subCode = this.suffixesReader.readVLong();
                    if (targetSubCode == subCode) {
                        this.lastSubFP = subFP;
                        return;
                    }
                    continue;
                }
                else {
                    final BlockTermState state = this.state;
                    ++state.termBlockOrd;
                }
            }
            else {
                final int flag = code & 0x3;
                this.suffixesReader.skipBytes(code >>> 2);
                if (flag == 1) {
                    final long subCode2 = this.suffixesReader.readVLong();
                    if (targetSubCode == subCode2) {
                        this.lastSubFP = subFP;
                        return;
                    }
                    continue;
                }
                else {
                    final BlockTermState state2 = this.state;
                    ++state2.termBlockOrd;
                    if (flag != 2 && flag != 3) {
                        continue;
                    }
                    this.suffixesReader.readByte();
                }
            }
        }
        throw new AssertionError();
    }
    
    public TermsEnum.SeekStatus scanToTerm(final BytesRef target, final boolean exactOnly) throws IOException {
        return this.isLeafBlock ? this.scanToTermLeaf(target, exactOnly) : this.scanToTermNonLeaf(target, exactOnly);
    }
    
    public TermsEnum.SeekStatus scanToTermLeaf(final BytesRef target, final boolean exactOnly) throws IOException {
        assert this.nextEnt != -1;
        this.ste.termExists = true;
        this.subCode = 0L;
        if (this.nextEnt == this.entCount) {
            if (exactOnly) {
                this.fillTerm();
            }
            return TermsEnum.SeekStatus.END;
        }
        assert this.prefixMatches(target);
        while (true) {
            ++this.nextEnt;
            this.suffix = this.suffixesReader.readVInt();
            final int termLen = this.prefix + this.suffix;
            this.startBytePos = this.suffixesReader.getPosition();
            this.suffixesReader.skipBytes(this.suffix);
            final int targetLimit = target.offset + ((target.length < termLen) ? target.length : termLen);
            int targetPos = target.offset + this.prefix;
            int bytePos = this.startBytePos;
            while (true) {
                int cmp;
                boolean stop;
                if (targetPos < targetLimit) {
                    cmp = (this.suffixBytes[bytePos++] & 0xFF) - (target.bytes[targetPos++] & 0xFF);
                    stop = false;
                }
                else {
                    assert targetPos == targetLimit;
                    cmp = termLen - target.length;
                    stop = true;
                }
                if (cmp < 0) {
                    if (this.nextEnt == this.entCount) {
                        if (exactOnly) {
                            this.fillTerm();
                        }
                        return TermsEnum.SeekStatus.END;
                    }
                    break;
                }
                else {
                    if (cmp > 0) {
                        this.fillTerm();
                        return TermsEnum.SeekStatus.NOT_FOUND;
                    }
                    if (!stop) {
                        continue;
                    }
                    assert this.ste.termExists;
                    this.fillTerm();
                    return TermsEnum.SeekStatus.FOUND;
                }
            }
        }
    }
    
    public TermsEnum.SeekStatus scanToTermNonLeaf(final BytesRef target, final boolean exactOnly) throws IOException {
        assert this.nextEnt != -1;
        if (this.nextEnt == this.entCount) {
            if (exactOnly) {
                this.fillTerm();
                this.ste.termExists = (this.subCode == 0L);
            }
            return TermsEnum.SeekStatus.END;
        }
        assert this.prefixMatches(target);
        while (this.nextEnt < this.entCount) {
            ++this.nextEnt;
            final int code = this.suffixesReader.readVInt();
            if (!this.versionAutoPrefix) {
                this.suffix = code >>> 1;
            }
            else {
                this.suffix = code >>> 2;
            }
            final int termLen = this.prefix + this.suffix;
            this.startBytePos = this.suffixesReader.getPosition();
            this.suffixesReader.skipBytes(this.suffix);
            if (!this.versionAutoPrefix) {
                this.ste.termExists = ((code & 0x1) == 0x0);
                if (this.ste.termExists) {
                    final BlockTermState state = this.state;
                    ++state.termBlockOrd;
                    this.subCode = 0L;
                }
                else {
                    this.subCode = this.suffixesReader.readVLong();
                    this.lastSubFP = this.fp - this.subCode;
                }
            }
            else {
                switch (code & 0x3) {
                    case 0: {
                        this.ste.termExists = true;
                        final BlockTermState state2 = this.state;
                        ++state2.termBlockOrd;
                        this.subCode = 0L;
                        break;
                    }
                    case 1: {
                        this.ste.termExists = false;
                        this.subCode = this.suffixesReader.readVLong();
                        this.lastSubFP = this.fp - this.subCode;
                        break;
                    }
                    case 2:
                    case 3: {
                        this.suffixesReader.readByte();
                        this.ste.termExists = false;
                        final BlockTermState state3 = this.state;
                        ++state3.termBlockOrd;
                        continue;
                    }
                }
            }
            final int targetLimit = target.offset + ((target.length < termLen) ? target.length : termLen);
            int targetPos = target.offset + this.prefix;
            int bytePos = this.startBytePos;
            while (true) {
                int cmp;
                boolean stop;
                if (targetPos < targetLimit) {
                    cmp = (this.suffixBytes[bytePos++] & 0xFF) - (target.bytes[targetPos++] & 0xFF);
                    stop = false;
                }
                else {
                    assert targetPos == targetLimit;
                    cmp = termLen - target.length;
                    stop = true;
                }
                if (cmp < 0) {
                    break;
                }
                if (cmp > 0) {
                    this.fillTerm();
                    if (!exactOnly && !this.ste.termExists) {
                        (this.ste.currentFrame = this.ste.pushFrame(null, this.ste.currentFrame.lastSubFP, termLen)).loadBlock();
                        while (this.ste.currentFrame.next()) {
                            (this.ste.currentFrame = this.ste.pushFrame(null, this.ste.currentFrame.lastSubFP, this.ste.term.length())).loadBlock();
                        }
                    }
                    return TermsEnum.SeekStatus.NOT_FOUND;
                }
                if (!stop) {
                    continue;
                }
                assert this.ste.termExists;
                this.fillTerm();
                return TermsEnum.SeekStatus.FOUND;
            }
        }
        if (exactOnly) {
            this.fillTerm();
        }
        return TermsEnum.SeekStatus.END;
    }
    
    private void fillTerm() {
        final int termLength = this.prefix + this.suffix;
        this.ste.term.setLength(termLength);
        this.ste.term.grow(termLength);
        System.arraycopy(this.suffixBytes, this.startBytePos, this.ste.term.bytes(), this.prefix, this.suffix);
    }
}
