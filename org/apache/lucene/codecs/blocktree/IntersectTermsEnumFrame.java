package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.ArrayUtil;
import java.io.IOException;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.store.ByteArrayDataInput;

final class IntersectTermsEnumFrame
{
    final int ord;
    long fp;
    long fpOrig;
    long fpEnd;
    long lastSubFP;
    int state;
    int lastState;
    int metaDataUpto;
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
    int numFollowFloorBlocks;
    int nextFloorLabel;
    final Transition transition;
    int transitionIndex;
    int transitionCount;
    final boolean versionAutoPrefix;
    FST.Arc<BytesRef> arc;
    final BlockTermState termState;
    final long[] longs;
    byte[] bytes;
    final ByteArrayDataInput bytesReader;
    BytesRef outputPrefix;
    int startBytePos;
    int suffix;
    int floorSuffixLeadStart;
    int floorSuffixLeadEnd;
    boolean isAutoPrefixTerm;
    private final IntersectTermsEnum ite;
    
    public IntersectTermsEnumFrame(final IntersectTermsEnum ite, final int ord) throws IOException {
        this.suffixBytes = new byte[128];
        this.suffixesReader = new ByteArrayDataInput();
        this.statBytes = new byte[64];
        this.statsReader = new ByteArrayDataInput();
        this.floorData = new byte[32];
        this.floorDataReader = new ByteArrayDataInput();
        this.transition = new Transition();
        this.bytes = new byte[32];
        this.bytesReader = new ByteArrayDataInput();
        this.ite = ite;
        this.ord = ord;
        this.termState = ite.fr.parent.postingsReader.newTermState();
        this.termState.totalTermFreq = -1L;
        this.longs = new long[ite.fr.longsSize];
        this.versionAutoPrefix = ite.fr.parent.anyAutoPrefixTerms;
    }
    
    void loadNextFloorBlock() throws IOException {
        assert this.numFollowFloorBlocks > 0 : "nextFloorLabel=" + this.nextFloorLabel;
        do {
            this.fp = this.fpOrig + (this.floorDataReader.readVLong() >>> 1);
            --this.numFollowFloorBlocks;
            if (this.numFollowFloorBlocks != 0) {
                this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
            }
            else {
                this.nextFloorLabel = 256;
            }
        } while (this.numFollowFloorBlocks != 0 && this.nextFloorLabel <= this.transition.min);
        this.load(null);
    }
    
    public void setState(final int state) {
        this.state = state;
        this.transitionIndex = 0;
        this.transitionCount = this.ite.automaton.getNumTransitions(state);
        if (this.transitionCount != 0) {
            this.ite.automaton.initTransition(state, this.transition);
            this.ite.automaton.getNextTransition(this.transition);
        }
        else {
            this.transition.min = -1;
            this.transition.max = -1;
        }
    }
    
    void load(final BytesRef frameIndexData) throws IOException {
        if (frameIndexData != null) {
            this.floorDataReader.reset(frameIndexData.bytes, frameIndexData.offset, frameIndexData.length);
            final long code = this.floorDataReader.readVLong();
            if ((code & 0x1L) != 0x0L) {
                this.numFollowFloorBlocks = this.floorDataReader.readVInt();
                this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
                if (!this.ite.runAutomaton.isAccept(this.state) && this.transitionCount != 0) {
                    assert this.transitionIndex == 0 : "transitionIndex=" + this.transitionIndex;
                    while (this.numFollowFloorBlocks != 0 && this.nextFloorLabel <= this.transition.min) {
                        this.fp = this.fpOrig + (this.floorDataReader.readVLong() >>> 1);
                        --this.numFollowFloorBlocks;
                        if (this.numFollowFloorBlocks != 0) {
                            this.nextFloorLabel = (this.floorDataReader.readByte() & 0xFF);
                        }
                        else {
                            this.nextFloorLabel = 256;
                        }
                    }
                }
            }
        }
        this.ite.in.seek(this.fp);
        int code2 = this.ite.in.readVInt();
        this.entCount = code2 >>> 1;
        assert this.entCount > 0;
        this.isLastInFloor = ((code2 & 0x1) != 0x0);
        code2 = this.ite.in.readVInt();
        this.isLeafBlock = ((code2 & 0x1) != 0x0);
        int numBytes = code2 >>> 1;
        if (this.suffixBytes.length < numBytes) {
            this.suffixBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ite.in.readBytes(this.suffixBytes, 0, numBytes);
        this.suffixesReader.reset(this.suffixBytes, 0, numBytes);
        numBytes = this.ite.in.readVInt();
        if (this.statBytes.length < numBytes) {
            this.statBytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ite.in.readBytes(this.statBytes, 0, numBytes);
        this.statsReader.reset(this.statBytes, 0, numBytes);
        this.metaDataUpto = 0;
        this.termState.termBlockOrd = 0;
        this.nextEnt = 0;
        numBytes = this.ite.in.readVInt();
        if (this.bytes.length < numBytes) {
            this.bytes = new byte[ArrayUtil.oversize(numBytes, 1)];
        }
        this.ite.in.readBytes(this.bytes, 0, numBytes);
        this.bytesReader.reset(this.bytes, 0, numBytes);
        if (!this.isLastInFloor) {
            this.fpEnd = this.ite.in.getFilePointer();
        }
        this.isAutoPrefixTerm = false;
    }
    
    public boolean next() {
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
        this.suffixesReader.skipBytes(this.suffix);
    }
    
    public boolean nextNonLeaf() {
        assert this.nextEnt != -1 && this.nextEnt < this.entCount : "nextEnt=" + this.nextEnt + " entCount=" + this.entCount + " fp=" + this.fp;
        ++this.nextEnt;
        final int code = this.suffixesReader.readVInt();
        if (!this.versionAutoPrefix) {
            this.suffix = code >>> 1;
            this.startBytePos = this.suffixesReader.getPosition();
            this.suffixesReader.skipBytes(this.suffix);
            if ((code & 0x1) == 0x0) {
                final BlockTermState termState = this.termState;
                ++termState.termBlockOrd;
                return false;
            }
            this.lastSubFP = this.fp - this.suffixesReader.readVLong();
            return true;
        }
        else {
            this.suffix = code >>> 2;
            this.startBytePos = this.suffixesReader.getPosition();
            this.suffixesReader.skipBytes(this.suffix);
            switch (code & 0x3) {
                case 0: {
                    this.isAutoPrefixTerm = false;
                    final BlockTermState termState2 = this.termState;
                    ++termState2.termBlockOrd;
                    return false;
                }
                case 1: {
                    this.isAutoPrefixTerm = false;
                    this.lastSubFP = this.fp - this.suffixesReader.readVLong();
                    return true;
                }
                case 2: {
                    this.floorSuffixLeadStart = -1;
                    final BlockTermState termState3 = this.termState;
                    ++termState3.termBlockOrd;
                    this.floorSuffixLeadEnd = (this.suffixesReader.readByte() & 0xFF);
                    if (this.floorSuffixLeadEnd == 255) {
                        this.floorSuffixLeadEnd = -1;
                    }
                    this.isAutoPrefixTerm = true;
                    return false;
                }
                case 3: {
                    if (this.suffix == 0) {
                        assert this.ord > 0;
                        final IntersectTermsEnumFrame parent = this.ite.stack[this.ord - 1];
                        this.floorSuffixLeadStart = (parent.suffixBytes[parent.startBytePos + parent.suffix - 1] & 0xFF);
                    }
                    else {
                        this.floorSuffixLeadStart = (this.suffixBytes[this.startBytePos + this.suffix - 1] & 0xFF);
                    }
                    final BlockTermState termState4 = this.termState;
                    ++termState4.termBlockOrd;
                    this.isAutoPrefixTerm = true;
                    this.floorSuffixLeadEnd = (this.suffixesReader.readByte() & 0xFF);
                    return false;
                }
                default: {
                    assert false;
                    return false;
                }
            }
        }
    }
    
    public int getTermBlockOrd() {
        return this.isLeafBlock ? this.nextEnt : this.termState.termBlockOrd;
    }
    
    public void decodeMetaData() throws IOException {
        final int limit = this.getTermBlockOrd();
        boolean absolute = this.metaDataUpto == 0;
        assert limit > 0;
        while (this.metaDataUpto < limit) {
            this.termState.docFreq = this.statsReader.readVInt();
            if (this.ite.fr.fieldInfo.getIndexOptions() != IndexOptions.DOCS) {
                this.termState.totalTermFreq = this.termState.docFreq + this.statsReader.readVLong();
            }
            for (int i = 0; i < this.ite.fr.longsSize; ++i) {
                this.longs[i] = this.bytesReader.readVLong();
            }
            this.ite.fr.parent.postingsReader.decodeTerm(this.longs, this.bytesReader, this.ite.fr.fieldInfo, this.termState, absolute);
            ++this.metaDataUpto;
            absolute = false;
        }
        this.termState.termBlockOrd = this.metaDataUpto;
    }
}
